package controller;

import game.GameState;
import game.GameTimeManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import model.ItemDatabase;
import model.Player;
import model.PlayerState;
import model.TreeMapState;
import model.item;
import util.AppDialogs;
import util.CombatRules;
import util.ImageCache;
import util.SkillModifiers;

final class NaturalResourceSystem {

    private enum ResourceKind {
        TREE,
        STONE,
        ORE
    }

    private static final Set<String> SUPPORTED_ORE_BASE_IDS = Set.of(
        "stone",
        "stone_dungeon",
        "adamantite",
        "adamantite_ore",
        "copper",
        "copper_ore",
        "gold",
        "gold_ore",
        "hihiogane",
        "hihiogane_ore",
        "iron",
        "iron_ore",
        "mithril",
        "mithril_ore",
        "orichalcum",
        "orichalcum_ore",
        "silver",
        "silver_ore"
    );

    private static final double RESOURCE_RESPAWN_SECONDS = 3.0 * 60.0 * 60.0;
    private static final double DROP_TTL_SECONDS = 12.0 * 60.0 * 60.0;
    private static final double AUTO_GATHER_MARGIN_PIXELS = 2.0;

    private static final int STARTING_MAX_HP = 10;
    private static final int STARTING_ORE_VIT = 1;
    private static final int MAX_DROPS_PER_LIFE = 10;
    private static final int TREE_STAGE2_MAX_ALIVE_DROPS = 9;
    private static final int TREE_STAGE3_MAX_ALIVE_DROPS = 8;
    private static final int DROP_ROLL_HP_CHUNK = 10;

    private static final double HP_REGEN_SECONDS = 48.0;
    private static final double HP_CAP_GROW_SECONDS = 1440.0;
    private static final int STAGE3_AFTER_LIVED_DAYS = 1;

    @FunctionalInterface
    private interface ResourceDropSpawner {
        void spawn(TreeMapState.TreeData resource, double nowSeconds);
    }

    @FunctionalInterface
    private interface ResourceLifecycleHandler {
        void handle(TreeMapState.TreeData resource, int currentDay, double nowSeconds);
    }

    private static final class ResourceHandle {
        final String nodeId;
        final String baseId;
        final int suffixNumber;
        final ResourceKind kind;
        final Group group;
        final ImageView sprite;
        final Rectangle hitbox;
        final ImageView dropsView;
        final Group treeLayerGroup;
        final ImageView treeLayerImage;

        TreeMapState.TreeData data;

        String renderedVisualKey = null;
        String renderedLayerVisualKey = null;
        String renderedDropsKey = null;
        boolean warnedInventoryFull = false;
        int cachedDropItemId = -1;

        ResourceHandle(
            String nodeId,
            String baseId,
            int suffixNumber,
            ResourceKind kind,
            Group group,
            ImageView sprite,
            Rectangle hitbox,
            ImageView dropsView,
            Group treeLayerGroup,
            ImageView treeLayerImage
        ) {
            this.nodeId = nodeId == null ? "" : nodeId;
            this.baseId = baseId == null ? "" : baseId;
            this.suffixNumber = suffixNumber;
            this.kind = kind == null ? ResourceKind.ORE : kind;
            this.group = group;
            this.sprite = sprite;
            this.hitbox = hitbox;
            this.dropsView = dropsView;
            this.treeLayerGroup = treeLayerGroup;
            this.treeLayerImage = treeLayerImage;
        }
    }

    private final GameState gameState;
    private final String mapKey;
    private final Group world;
    private final Player player;
    private final PlayerState playerState;
    private final Rectangle playerBodyHitbox;

    private final Rectangle playerAttackLeft;
    private final Rectangle playerAttackRight;
    private final Rectangle playerAttackUp;
    private final Rectangle playerAttackDown;

    private final Map<String, Node> nodesById = new HashMap<>();
    private final List<ResourceHandle> resources = new ArrayList<>();

    private List<String> cachedValidTreeTypeKeys = null;

    private int treeLogItemId = -1;
    private int plantFiberItemId = -1;
    private int stoneItemId = -1;
    private int sandItemId = -1;

    static NaturalResourceSystem tryCreate(
        GameState gameState,
        String mapKey,
        Group world,
        Player player,
        PlayerState playerState,
        Rectangle playerBodyHitbox
    ) {
        if (gameState == null || mapKey == null || mapKey.isBlank() || world == null || player == null) {
            return null;
        }

        NaturalResourceSystem system = new NaturalResourceSystem(gameState, mapKey, world, player, playerState, playerBodyHitbox);
        if (system.resources.isEmpty()) {
            return null;
        }
        return system;
    }

    private NaturalResourceSystem(
        GameState gameState,
        String mapKey,
        Group world,
        Player player,
        PlayerState playerState,
        Rectangle playerBodyHitbox
    ) {
        this.gameState = gameState;
        this.mapKey = mapKey;
        this.world = world;
        this.player = player;
        this.playerState = playerState;
        this.playerBodyHitbox = playerBodyHitbox;

        indexNodes(world, nodesById);

        this.playerAttackLeft = node("human_hitbox_left", Rectangle.class);
        this.playerAttackRight = node("human_hitbox_right", Rectangle.class);
        this.playerAttackUp = node("human_hitbox_up", Rectangle.class);
        this.playerAttackDown = node("human_hitbox_down", Rectangle.class);

        if (playerAttackLeft != null) playerAttackLeft.setMouseTransparent(true);
        if (playerAttackRight != null) playerAttackRight.setMouseTransparent(true);
        if (playerAttackUp != null) playerAttackUp.setMouseTransparent(true);
        if (playerAttackDown != null) playerAttackDown.setMouseTransparent(true);

        setupResourcesFromWorld();
    }

    List<Rectangle> resourceHitboxesForPlayerCollision() {
        List<Rectangle> result = new ArrayList<>();
        for (ResourceHandle handle : resources) {
            if (handle != null && handle.hitbox != null) {
                result.add(handle.hitbox);
            }
        }
        return result;
    }

    void update(Bounds playerBoundsScene) {
        if (playerBoundsScene == null) {
            return;
        }

        syncDirectionalHitboxes();

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        for (ResourceHandle handle : resources) {
            if (handle == null || handle.data == null) {
                continue;
            }

            cleanupExpiredDrops(handle.data, nowSeconds);

            switch (handle.kind) {
                case TREE -> updateTreeState(handle, currentDay, nowSeconds);
                case STONE -> updateStoneState(handle, currentDay, nowSeconds);
                case ORE -> updateOreState(handle, currentDay, nowSeconds);
            }

            renderDrops(handle);
        }

        autoGatherDrops(playerBoundsScene);
    }

    void handlePlayerAttackStarted() {
        if (player == null || playerState == null) {
            return;
        }

        Bounds attackBounds = buildPlayerAttackBounds();
        if (attackBounds == null) {
            return;
        }

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        for (ResourceHandle handle : resources) {
            if (handle == null || handle.data == null || handle.hitbox == null) {
                continue;
            }
            if (handle.data.currentStage == 1) {
                continue;
            }

            Bounds nodeBounds = handle.hitbox.localToScene(handle.hitbox.getBoundsInLocal());
            if (!attackBounds.intersects(nodeBounds)) {
                continue;
            }

            switch (handle.kind) {
                case TREE -> applyPlayerAttackToTree(handle, currentDay, nowSeconds);
                case STONE -> applyPlayerAttackToStone(handle, currentDay, nowSeconds);
                case ORE -> applyPlayerAttackToOre(handle, currentDay, nowSeconds);
            }

            renderDrops(handle);
            break;
        }
    }

    private void setupResourcesFromWorld() {
        ItemDatabase.loadItems();

        TreeMapState mapState = gameState.getTreeState(mapKey);
        int currentDay = GameTimeManager.get().dayNumberProperty().get();
        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();

        List<ResourceHandle> discovered = new ArrayList<>();
        Set<Node> claimedNodes = new HashSet<>();
        for (Map.Entry<String, Node> entry : nodesById.entrySet()) {
            String id = entry == null ? null : entry.getKey();
            Node node = entry == null ? null : entry.getValue();
            if (id == null || id.isBlank() || !(node instanceof Group group)) {
                continue;
            }

            ResourceHandle handle = createResourceHandle(id, group);
            if (handle == null) {
                continue;
            }
            if (!claimResourceNodes(handle, claimedNodes)) {
                continue;
            }

            handle.data = mapState.getTree(handle.nodeId);

            switch (handle.kind) {
                case TREE -> initializeTreeDefaults(handle.data, currentDay, nowSeconds);
                case STONE -> initializeStoneDefaults(handle.data, currentDay, nowSeconds);
                case ORE -> initializeOreDefaults(handle, currentDay, nowSeconds);
            }

            if (handle.hitbox != null) {
                handle.hitbox.setMouseTransparent(true);
            }

            if (handle.treeLayerGroup != null) {
                handle.treeLayerGroup.setMouseTransparent(true);
            }
            if (handle.treeLayerImage != null) {
                handle.treeLayerImage.setMouseTransparent(true);
            }

            discovered.add(handle);
        }

        discovered.sort((left, right) -> {
            if (left == null && right == null) return 0;
            if (left == null) return 1;
            if (right == null) return -1;

            int kindCompare = Integer.compare(kindSortKey(left.kind), kindSortKey(right.kind));
            if (kindCompare != 0) return kindCompare;

            int baseCompare = left.baseId.compareToIgnoreCase(right.baseId);
            if (baseCompare != 0) return baseCompare;

            return Integer.compare(left.suffixNumber, right.suffixNumber);
        });

        resources.clear();
        resources.addAll(discovered);

        for (ResourceHandle handle : resources) {
            renderVisual(handle);
            renderDrops(handle);
        }
    }

    private int kindSortKey(ResourceKind kind) {
        if (kind == null) return 99;
        return switch (kind) {
            case TREE -> 0;
            case STONE -> 1;
            case ORE -> 2;
        };
    }

    private boolean claimResourceNodes(ResourceHandle handle, Set<Node> claimed) {
        if (handle == null || claimed == null) {
            return true;
        }

        if (!claimNodeForHandle(claimed, handle.sprite, handle.nodeId, "sprite")) return false;
        if (!claimNodeForHandle(claimed, handle.hitbox, handle.nodeId, "hitbox")) return false;
        if (!claimNodeForHandle(claimed, handle.dropsView, handle.nodeId, "dropsView")) return false;

        if (handle.kind == ResourceKind.TREE) {
            if (!claimNodeForHandle(claimed, handle.treeLayerGroup, handle.nodeId, "treeLayerGroup")) return false;
            if (!claimNodeForHandle(claimed, handle.treeLayerImage, handle.nodeId, "treeLayerImage")) return false;
        }

        return true;
    }

    private boolean claimNodeForHandle(Set<Node> claimed, Node node, String ownerId, String role) {
        if (node == null) {
            return true;
        }
        if (claimed.add(node)) {
            return true;
        }
        System.err.println(
            "[NaturalResourceSystem] Node reused across multiple resources (owner=" + ownerId + ", role=" + role + ", id=" + node.getId() + ")"
        );
        return false;
    }

    private ResourceHandle createResourceHandle(String id, Group group) {
        if (id == null || id.isBlank() || group == null) {
            return null;
        }

        if (isTreeGroupId(id)) {
            int suffix = extractTrailingNumberOrZero(id);
            String baseId = "tree";
            String suffixStr = suffix <= 0 ? "" : Integer.toString(suffix);

            ImageView sprite = node(baseId + "_img" + suffixStr, ImageView.class);
            Rectangle hitbox = node(baseId + "_hitbox" + suffixStr, Rectangle.class);
            ImageView dropsView = node("drops_img_" + baseId + suffixStr, ImageView.class);

            Group layerGroup = node("treelayorgp" + suffixStr, Group.class);
            ImageView layerImage = node("treelayorimg" + suffixStr, ImageView.class);

            if (sprite == null || hitbox == null) {
                return null;
            }

            return new ResourceHandle(id, baseId, suffix, ResourceKind.TREE, group, sprite, hitbox, dropsView, layerGroup, layerImage);
        }

        String baseId = stripTrailingDigits(id);
        if (baseId.isBlank() || !SUPPORTED_ORE_BASE_IDS.contains(baseId)) {
            return null;
        }

        int suffix = extractTrailingNumberOrZero(id);
        String suffixStr = suffix <= 0 ? "" : Integer.toString(suffix);

        ImageView sprite = node(baseId + "_img" + suffixStr, ImageView.class);
        Rectangle hitbox = node(baseId + "_hitbox" + suffixStr, Rectangle.class);
        ImageView dropsView = node("drops_img_" + baseId + suffixStr, ImageView.class);

        if (sprite == null || hitbox == null) {
            return null;
        }

        ResourceKind kind = baseId.equals("stone") ? ResourceKind.STONE : ResourceKind.ORE;
        return new ResourceHandle(id, baseId, suffix, kind, group, sprite, hitbox, dropsView, null, null);
    }

    private boolean isTreeGroupId(String id) {
        if (id == null) return false;
        if (!id.startsWith("tree")) return false;
        if (id.length() == 4) return true;
        for (int i = 4; i < id.length(); i++) {
            char c = id.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private String stripTrailingDigits(String id) {
        if (id == null || id.isBlank()) return "";
        int i = id.length();
        while (i > 0) {
            char c = id.charAt(i - 1);
            if (c >= '0' && c <= '9') {
                i--;
                continue;
            }
            break;
        }
        return id.substring(0, i);
    }

    private int extractTrailingNumberOrZero(String id) {
        if (id == null || id.isBlank()) return 0;
        int i = id.length();
        while (i > 0) {
            char c = id.charAt(i - 1);
            if (c >= '0' && c <= '9') {
                i--;
                continue;
            }
            break;
        }
        if (i >= id.length()) {
            return 0;
        }
        try {
            return Integer.parseInt(id.substring(i));
        } catch (Exception e) {
            return 0;
        }
    }

    private void initializeTreeDefaults(TreeMapState.TreeData data, int currentDay, double nowSeconds) {
        if (data == null) return;

        if (data.typeKey == null || data.typeKey.isBlank() || !isValidTreeTypeKey(data.typeKey)) {
            data.typeKey = randomTreeTypeKey();
        }

        if (data.lastUpdatedDayNumber <= 0) {
            data.lastUpdatedDayNumber = Math.max(1, currentDay);
        }

        if (data.livedDays < 0) data.livedDays = 0;

        if (data.currentStage < 1 || data.currentStage > 3) {
            data.currentStage = 2;
        }

        if (data.dropRatePity < 0) data.dropRatePity = 0;
        if (data.dropsThisLife < 0) data.dropsThisLife = 0;
        if (data.damageSinceLastDropRoll < 0) data.damageSinceLastDropRoll = 0;
        if (data.damageSinceLastDropRoll >= DROP_ROLL_HP_CHUNK) {
            data.damageSinceLastDropRoll %= DROP_ROLL_HP_CHUNK;
        }

        if (data.currentStage == 1) {
            data.hp = 0;
            data.maxHp = 0;
            data.vit = 0;
            data.lastHpGrowthTotalSeconds = -1;
            if (data.respawnAtTotalSeconds < 0) {
                data.respawnAtTotalSeconds = nowSeconds + RESOURCE_RESPAWN_SECONDS;
            }
        } else {
            data.currentStage = stageFromLivedDays(data.livedDays);
            data.vit = vitFor(data.livedDays);

            if (data.maxHp <= 0) {
                data.maxHp = STARTING_MAX_HP;
            }
            if (data.maxHp < STARTING_MAX_HP) {
                data.maxHp = STARTING_MAX_HP;
            }
            if (data.hp <= 0) {
                data.hp = data.maxHp;
            }
            if (data.hp > data.maxHp) data.hp = data.maxHp;
            if (data.hp < 0) data.hp = 0;

            if (data.lastHpGrowthTotalSeconds < 0 || data.lastHpGrowthTotalSeconds > nowSeconds) {
                data.lastHpGrowthTotalSeconds = nowSeconds;
            }
            data.respawnAtTotalSeconds = -1;
        }
    }

    private void initializeStoneDefaults(TreeMapState.TreeData data, int currentDay, double nowSeconds) {
        if (data == null) return;

        if (data.typeKey == null || data.typeKey.isBlank()) {
            data.typeKey = "stone";
        }

        if (data.lastUpdatedDayNumber <= 0) {
            data.lastUpdatedDayNumber = Math.max(1, currentDay);
        }

        if (data.livedDays < 0) data.livedDays = 0;

        if (data.currentStage < 1 || data.currentStage > 3) {
            data.currentStage = 2;
        }

        if (data.dropRatePity < 0) data.dropRatePity = 0;
        if (data.dropsThisLife < 0) data.dropsThisLife = 0;
        if (data.damageSinceLastDropRoll < 0) data.damageSinceLastDropRoll = 0;
        if (data.damageSinceLastDropRoll >= DROP_ROLL_HP_CHUNK) {
            data.damageSinceLastDropRoll %= DROP_ROLL_HP_CHUNK;
        }

        if (data.currentStage == 1) {
            data.hp = 0;
            data.maxHp = 0;
            data.vit = 0;
            data.lastHpGrowthTotalSeconds = -1;
            if (data.respawnAtTotalSeconds < 0) {
                data.respawnAtTotalSeconds = nowSeconds + RESOURCE_RESPAWN_SECONDS;
            }
        } else {
            data.currentStage = stageFromLivedDays(data.livedDays);
            data.vit = vitFor(data.livedDays);

            if (data.maxHp <= 0) {
                data.maxHp = STARTING_MAX_HP;
            }
            if (data.maxHp < STARTING_MAX_HP) {
                data.maxHp = STARTING_MAX_HP;
            }
            if (data.hp <= 0) {
                data.hp = data.maxHp;
            }
            if (data.hp > data.maxHp) data.hp = data.maxHp;
            if (data.hp < 0) data.hp = 0;

            if (data.lastHpGrowthTotalSeconds < 0 || data.lastHpGrowthTotalSeconds > nowSeconds) {
                data.lastHpGrowthTotalSeconds = nowSeconds;
            }
            data.respawnAtTotalSeconds = -1;
        }
    }

    private void initializeOreDefaults(ResourceHandle handle, int currentDay, double nowSeconds) {
        if (handle == null || handle.data == null) {
            return;
        }

        TreeMapState.TreeData data = handle.data;

        if (data.typeKey == null || data.typeKey.isBlank()) {
            data.typeKey = handle.baseId;
        }

        if (data.lastUpdatedDayNumber <= 0) {
            data.lastUpdatedDayNumber = Math.max(1, currentDay);
        }

        if (data.currentStage < 1 || data.currentStage > 3) {
            data.currentStage = 2;
        }

        if (data.currentStage == 1) {
            data.hp = 0;
            data.maxHp = 0;
            data.vit = 0;
            if (data.respawnAtTotalSeconds < 0) {
                data.respawnAtTotalSeconds = nowSeconds + RESOURCE_RESPAWN_SECONDS;
            }
            return;
        }

        double multiplier = oreStatusMultiplier(handle.baseId);
        int desiredMaxHp = scaledStatus(STARTING_MAX_HP, multiplier);
        int desiredVit = scaledStatus(STARTING_ORE_VIT, multiplier);

        // Enforce variant stats, while keeping the current HP proportion when migrating existing saves.
        if (data.maxHp <= 0) {
            data.maxHp = desiredMaxHp;
        } else if (data.maxHp != desiredMaxHp) {
            double ratio = (double) Math.max(0, data.hp) / (double) Math.max(1, data.maxHp);
            data.maxHp = desiredMaxHp;
            data.hp = (int) Math.round(ratio * desiredMaxHp);
        }

        if (data.hp <= 0) {
            data.hp = data.maxHp;
        }

        // Ores do not use lived-day scaling; VIT is variant-defined.
        if (data.vit != desiredVit) {
            data.vit = desiredVit;
        }

        if (data.hp > data.maxHp) {
            data.hp = data.maxHp;
        }
        if (data.hp < 0) {
            data.hp = 0;
        }

        if (data.dropsThisLife < 0) {
            data.dropsThisLife = 0;
        }
        if (data.dropRatePity < 0) {
            data.dropRatePity = 0;
        }

        data.respawnAtTotalSeconds = -1;
    }

    private void updateTreeState(ResourceHandle handle, int currentDay, double nowSeconds) {
        updateResourceState(handle, currentDay, nowSeconds, this::respawnTree, this::killTree);
        renderTreeVisual(handle);
    }

    private void updateStoneState(ResourceHandle handle, int currentDay, double nowSeconds) {
        updateResourceState(handle, currentDay, nowSeconds, this::respawnStone, this::killStone);
        renderNonTreeVisual(handle);
    }

    private void updateOreState(ResourceHandle handle, int currentDay, double nowSeconds) {
        if (handle == null || handle.data == null) {
            return;
        }

        if (handle.data.currentStage == 1) {
            handle.data.hp = 0;
            handle.data.maxHp = 0;
            handle.data.vit = 0;

            if (handle.data.respawnAtTotalSeconds >= 0 && nowSeconds >= handle.data.respawnAtTotalSeconds) {
                respawnOre(handle, currentDay, nowSeconds);
            }
        }

        renderNonTreeVisual(handle);
    }

    private void updateResourceState(
        ResourceHandle handle,
        int currentDay,
        double nowSeconds,
        ResourceLifecycleHandler respawnHandler,
        ResourceLifecycleHandler killHandler
    ) {
        if (handle == null || handle.data == null) {
            return;
        }

        TreeMapState.TreeData resource = handle.data;

        if (resource.currentStage == 1) {
            resource.hp = 0;
            resource.maxHp = 0;
            resource.vit = 0;

            if (respawnHandler != null && resource.respawnAtTotalSeconds >= 0 && nowSeconds >= resource.respawnAtTotalSeconds) {
                respawnHandler.handle(resource, currentDay, nowSeconds);
            }
            return;
        }

        applyTreeDailyGrowthIfNeeded(resource, currentDay);
        applyTreeHpGrowthIfNeeded(resource, nowSeconds);

        resource.currentStage = stageFromLivedDays(resource.livedDays);
        resource.vit = vitFor(resource.livedDays);

        if (resource.maxHp < STARTING_MAX_HP) {
            resource.maxHp = STARTING_MAX_HP;
        }
        if (resource.hp > resource.maxHp) resource.hp = resource.maxHp;
        if (resource.hp < 0) resource.hp = 0;

        if (killHandler != null && resource.hp == 0) {
            killHandler.handle(resource, currentDay, nowSeconds);
        }
    }

    private void applyTreeDailyGrowthIfNeeded(TreeMapState.TreeData tree, int currentDay) {
        if (tree == null) return;
        if (tree.currentStage == 1) return;
        int safeDay = Math.max(1, currentDay);
        int last = tree.lastUpdatedDayNumber <= 0 ? safeDay : tree.lastUpdatedDayNumber;

        if (safeDay == last) {
            return;
        }

        if (safeDay < last) {
            tree.lastUpdatedDayNumber = safeDay;
            return;
        }

        int delta = safeDay - last;
        tree.lastUpdatedDayNumber = safeDay;
        tree.livedDays = Math.max(0, tree.livedDays + delta);
        tree.currentStage = stageFromLivedDays(tree.livedDays);
        tree.vit = vitFor(tree.livedDays);
    }

    private void applyTreeHpGrowthIfNeeded(TreeMapState.TreeData tree, double nowSeconds) {
        if (tree == null) return;
        if (tree.currentStage == 1) return;

        if (tree.maxHp < STARTING_MAX_HP) {
            tree.maxHp = STARTING_MAX_HP;
        }
        if (tree.hp > tree.maxHp) tree.hp = tree.maxHp;
        if (tree.hp < 0) tree.hp = 0;

        if (tree.lastHpGrowthTotalSeconds < 0 || tree.lastHpGrowthTotalSeconds > nowSeconds) {
            tree.lastHpGrowthTotalSeconds = nowSeconds;
            return;
        }

        double elapsed = nowSeconds - tree.lastHpGrowthTotalSeconds;
        if (elapsed <= 0) {
            return;
        }

        double remaining = elapsed;
        while (true) {
            if (tree.hp < tree.maxHp) {
                long ticks = (long) Math.floor(remaining / HP_REGEN_SECONDS);
                int needed = tree.maxHp - tree.hp;
                long apply = Math.min(ticks, (long) Math.max(0, needed));
                if (apply <= 0) {
                    break;
                }

                tree.hp += (int) apply;
                remaining -= apply * HP_REGEN_SECONDS;
                continue;
            }

            long capTicks = (long) Math.floor(remaining / HP_CAP_GROW_SECONDS);
            if (capTicks <= 0) {
                break;
            }

            int inc = capTicks > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capTicks;
            tree.maxHp += inc;
            tree.hp += inc;
            remaining -= inc * HP_CAP_GROW_SECONDS;
        }

        tree.lastHpGrowthTotalSeconds = nowSeconds - remaining;
    }

    private int stageFromLivedDays(int livedDays) {
        return Math.max(0, livedDays) >= STAGE3_AFTER_LIVED_DAYS ? 3 : 2;
    }

    private int vitFor(int livedDays) {
        int days = Math.max(0, livedDays);
        return Math.max(1, days + 1);
    }

    private void renderVisual(ResourceHandle handle) {
        if (handle == null) return;
        if (handle.kind == ResourceKind.TREE) {
            renderTreeVisual(handle);
        } else {
            renderNonTreeVisual(handle);
        }
    }

    private void renderNonTreeVisual(ResourceHandle handle) {
        if (handle == null || handle.sprite == null || handle.data == null) return;

        boolean alive = handle.data.currentStage != 1;

        handle.sprite.setVisible(alive);
        handle.sprite.setManaged(alive);

        if (handle.hitbox != null) {
            handle.hitbox.setDisable(!alive);
        }
    }

    private void renderTreeVisual(ResourceHandle handle) {
        if (handle == null || handle.sprite == null || handle.data == null) return;

        syncTreeLayerPosition(handle);

        String visualKey;
        if (handle.data.currentStage == 1) {
            visualKey = "stage:1";
        } else {
            visualKey = "stage:" + handle.data.currentStage + "#type:" + (handle.data.typeKey == null ? "null" : handle.data.typeKey);
        }

        boolean visualUnchanged = visualKey.equals(handle.renderedVisualKey);

        handle.sprite.setManaged(true);
        handle.sprite.setVisible(true);
        if (handle.hitbox != null) {
            handle.hitbox.setDisable(handle.data.currentStage == 1);
        }

        if (handle.data.currentStage == 1) {
            handle.sprite.setImage(null);
            handle.sprite.setVisible(false);
            handle.sprite.setManaged(false);
            hideTreeLayerVisual(handle, "stage:1");
            handle.renderedVisualKey = visualKey;
            return;
        }

        if (visualUnchanged) {
            return;
        }
        handle.renderedVisualKey = visualKey;

        String base = (handle.data.typeKey == null) ? "blue" : handle.data.typeKey.trim().toLowerCase(Locale.ROOT);
        String spriteName = base + "_tree" + (handle.data.currentStage == 3 ? "2" : "1");
        item spriteItem = ItemDatabase.getItemByName(spriteName);

        if (spriteItem != null) {
            handle.sprite.setImage(ItemDatabase.getImage(spriteItem.getItem_id()));
        } else {
            handle.sprite.setImage(loadImageOrNull("/image/items/" + spriteName + ".png"));
        }

        if (handle.sprite.getImage() == null) {
            hideTreeLayerVisual(handle, "sprite:missing");
        } else {
            renderTreeLayerVisual(handle, spriteName, visualKey);
        }
    }

    private void renderTreeLayerVisual(ResourceHandle handle, String spriteName, String visualKey) {
        if (handle == null || handle.treeLayerImage == null || handle.treeLayerGroup == null) {
            return;
        }

        syncTreeLayerPosition(handle);

        String key = (visualKey == null || visualKey.isBlank())
            ? ("sprite:" + (spriteName == null ? "null" : spriteName))
            : ("layer:" + visualKey);

        if (Objects.equals(key, handle.renderedLayerVisualKey)) {
            return;
        }
        handle.renderedLayerVisualKey = key;

        if (spriteName == null || spriteName.isBlank()) {
            hideTreeLayerVisual(handle, "sprite:null");
            return;
        }

        handle.treeLayerImage.setFitWidth(handle.sprite.getFitWidth());
        handle.treeLayerImage.setFitHeight(handle.sprite.getFitHeight());
        handle.treeLayerImage.setPreserveRatio(false);
        handle.treeLayerImage.setSmooth(false);
        handle.treeLayerImage.setPickOnBounds(true);

        var upper = loadImageOrNull("/image/items/" + spriteName + "up.png");
        if (upper == null) {
            hideTreeLayerVisual(handle, "sprite:missing_upper");
            return;
        }

        handle.treeLayerImage.setImage(upper);
        handle.treeLayerImage.setVisible(true);
        handle.treeLayerImage.setManaged(true);
        handle.treeLayerGroup.setVisible(true);
        handle.treeLayerGroup.setManaged(true);
    }

    private void hideTreeLayerVisual(ResourceHandle handle, String key) {
        if (handle == null) {
            return;
        }

        if (handle.treeLayerImage == null || handle.treeLayerGroup == null) {
            handle.renderedLayerVisualKey = key;
            return;
        }

        if (key != null && !key.isBlank()) {
            handle.renderedLayerVisualKey = key;
        }

        handle.treeLayerImage.setImage(null);
        handle.treeLayerImage.setVisible(false);
        handle.treeLayerImage.setManaged(false);

        handle.treeLayerGroup.setVisible(false);
        handle.treeLayerGroup.setManaged(false);
    }

    private void syncTreeLayerPosition(ResourceHandle handle) {
        if (handle == null || handle.treeLayerGroup == null || handle.sprite == null) return;
        if (handle.treeLayerGroup.getParent() == null) return;
        if (handle.sprite.getScene() == null || handle.treeLayerGroup.getScene() == null) return;

        Bounds treeBoundsScene = handle.sprite.localToScene(handle.sprite.getBoundsInLocal());
        Bounds treeBoundsInParent = handle.treeLayerGroup.getParent().sceneToLocal(treeBoundsScene);

        handle.treeLayerGroup.setLayoutX(treeBoundsInParent.getMinX());
        handle.treeLayerGroup.setLayoutY(treeBoundsInParent.getMinY());
    }

    private String buildDropsKey(TreeMapState.TreeData data) {
        if (data == null || data.drops == null || data.drops.isEmpty()) {
            return "drops:none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("drops:").append(data.drops.size());
        for (TreeMapState.TreeDrop d : data.drops) {
            if (d == null) {
                continue;
            }
            sb.append('#').append(d.itemId).append('x').append(d.quantity);
        }
        return sb.toString();
    }

    private void renderDrops(ResourceHandle handle) {
        if (handle == null || handle.dropsView == null || handle.data == null) {
            return;
        }

        String key = buildDropsKey(handle.data);
        if (key.equals(handle.renderedDropsKey)) {
            return;
        }
        handle.renderedDropsKey = key;

        TreeMapState.TreeDrop first = null;
        for (TreeMapState.TreeDrop d : handle.data.drops) {
            if (d == null || d.itemId <= 0 || d.quantity <= 0) continue;
            if (ItemDatabase.getImage(d.itemId) == null) continue;
            first = d;
            break;
        }

        if (first == null) {
            handle.dropsView.setImage(null);
            handle.dropsView.setVisible(false);
            handle.dropsView.setManaged(false);
            handle.dropsView.setOnMouseClicked(null);
            return;
        }

        handle.dropsView.setImage(ItemDatabase.getImage(first.itemId));
        boolean show = handle.dropsView.getImage() != null;
        handle.dropsView.setVisible(show);
        handle.dropsView.setManaged(show);
        handle.dropsView.setPickOnBounds(true);
        handle.dropsView.setMouseTransparent(true);
        handle.dropsView.setOnMouseClicked(null);
    }

    private void cleanupExpiredDrops(TreeMapState.TreeData resource, double nowSeconds) {
        if (resource == null || resource.drops == null || resource.drops.isEmpty()) {
            return;
        }

        resource.drops.removeIf(drop -> drop == null
            || drop.itemId <= 0
            || drop.quantity <= 0
            || (drop.expiresAtTotalSeconds > 0 && nowSeconds >= drop.expiresAtTotalSeconds)
        );
    }

    private void autoGatherDrops(Bounds playerBoundsScene) {
        if (player == null || playerBoundsScene == null) {
            return;
        }

        for (ResourceHandle handle : resources) {
            if (handle == null || handle.data == null || handle.hitbox == null) {
                continue;
            }
            handle.warnedInventoryFull = autoGatherResourceDrops(handle.data, handle.hitbox, playerBoundsScene, handle.warnedInventoryFull);
            renderDrops(handle);
        }
    }

    private boolean autoGatherResourceDrops(
        TreeMapState.TreeData resource,
        Rectangle resourceHitbox,
        Bounds playerBoundsScene,
        boolean warnedInventoryFull
    ) {
        if (resource == null || resourceHitbox == null || player == null || playerBoundsScene == null) {
            return warnedInventoryFull;
        }

        boolean inside = intersectsWithMargin(playerBoundsScene, resourceHitbox, AUTO_GATHER_MARGIN_PIXELS);
        if (!inside) {
            return warnedInventoryFull;
        }

        if (resource.drops == null || resource.drops.isEmpty()) {
            return warnedInventoryFull;
        }

        Iterator<TreeMapState.TreeDrop> it = resource.drops.iterator();
        while (it.hasNext()) {
            TreeMapState.TreeDrop drop = it.next();
            if (drop == null || drop.itemId <= 0 || drop.quantity <= 0) {
                it.remove();
                continue;
            }

            if (!player.getInventory().addItem(drop.itemId, drop.quantity)) {
                if (!warnedInventoryFull) {
                    AppDialogs.showMessageDialog(null, "Not enough inventory space to collect these drops.");
                    warnedInventoryFull = true;
                }
                break;
            }

            if (gameState != null) {
                gameState.addGatheringExpFromCollectedItems(drop.quantity);
            }

            it.remove();
        }

        return warnedInventoryFull;
    }

    private boolean intersectsWithMargin(Bounds playerBoundsScene, Rectangle targetHitbox, double marginPixels) {
        if (playerBoundsScene == null || targetHitbox == null) return false;
        Bounds targetBoundsScene = targetHitbox.localToScene(targetHitbox.getBoundsInLocal());
        double margin = Math.max(0.0, marginPixels);

        Bounds expanded = new BoundingBox(
            targetBoundsScene.getMinX() - margin,
            targetBoundsScene.getMinY() - margin,
            targetBoundsScene.getWidth() + (margin * 2.0),
            targetBoundsScene.getHeight() + (margin * 2.0)
        );

        return expanded.intersects(playerBoundsScene);
    }

    private void syncDirectionalHitboxes() {
        if (player == null) {
            return;
        }
        if (playerAttackLeft == null && playerAttackRight == null && playerAttackUp == null && playerAttackDown == null) {
            return;
        }

        Player.FacingDirection facing = player.getFacingDirection();
        setDirectionalHitboxActive(playerAttackLeft, facing == Player.FacingDirection.LEFT);
        setDirectionalHitboxActive(playerAttackRight, facing == Player.FacingDirection.RIGHT);
        setDirectionalHitboxActive(playerAttackUp, facing == Player.FacingDirection.UP);
        setDirectionalHitboxActive(playerAttackDown, facing == Player.FacingDirection.DOWN);
    }

    private void setDirectionalHitboxActive(Rectangle hitbox, boolean active) {
        if (hitbox == null) {
            return;
        }
        hitbox.setDisable(!active);
        hitbox.setVisible(active);
        hitbox.setManaged(active);
    }

    private Rectangle resolveActiveAttackHitbox() {
        if (player == null) {
            return null;
        }

        return switch (player.getFacingDirection()) {
            case LEFT -> playerAttackLeft;
            case RIGHT -> playerAttackRight;
            case UP -> playerAttackUp;
            case DOWN -> playerAttackDown;
        };
    }

    private Bounds buildPlayerAttackBounds() {
        if (player == null) {
            return null;
        }

        Rectangle attackHitbox = resolveActiveAttackHitbox();
        if (attackHitbox != null) {
            return attackHitbox.localToScene(attackHitbox.getBoundsInLocal());
        }

        if (playerBodyHitbox == null) {
            return null;
        }

        Bounds body = playerBodyHitbox.localToScene(playerBodyHitbox.getBoundsInLocal());

        double attackWidth = 14.0;
        double attackHeight = 20.0;

        return switch (player.getFacingDirection()) {
            case LEFT -> new BoundingBox(
                body.getMinX() - attackWidth,
                body.getMinY() + (body.getHeight() - attackHeight) * 0.5,
                attackWidth,
                attackHeight
            );
            case RIGHT -> new BoundingBox(
                body.getMaxX(),
                body.getMinY() + (body.getHeight() - attackHeight) * 0.5,
                attackWidth,
                attackHeight
            );
            case UP -> new BoundingBox(
                body.getMinX() + (body.getWidth() - attackWidth) * 0.5,
                body.getMinY() - attackHeight,
                attackWidth,
                attackHeight
            );
            case DOWN -> new BoundingBox(
                body.getMinX() + (body.getWidth() - attackWidth) * 0.5,
                body.getMaxY(),
                attackWidth,
                attackHeight
            );
        };
    }

    private void applyPlayerAttackToTree(ResourceHandle handle, int currentDay, double nowSeconds) {
        if (handle == null || handle.data == null) {
            return;
        }

        int damage = calculatePlayerTreeDamage();
        if (damage <= 0) {
            return;
        }

        applyPlayerAttackToResource(
            "TREE",
            handle.data,
            currentDay,
            nowSeconds,
            damage,
            resolveTreeLogItemId(),
            resolvePlantFiberItemId(),
            49.0,
            this::spawnAliveTreeDropForStage,
            this::killTree
        );

        renderTreeVisual(handle);
    }

    private void applyPlayerAttackToStone(ResourceHandle handle, int currentDay, double nowSeconds) {
        if (handle == null || handle.data == null) {
            return;
        }

        int damage = calculatePlayerOreDamage();
        if (damage <= 0) {
            return;
        }

        applyPlayerAttackToResource(
            "STONE",
            handle.data,
            currentDay,
            nowSeconds,
            damage,
            resolveStoneItemId(),
            resolveSandItemId(),
            49.0,
            this::spawnAliveStoneDropForStage,
            this::killStone
        );

        renderNonTreeVisual(handle);
    }

    private void applyPlayerAttackToOre(ResourceHandle handle, int currentDay, double nowSeconds) {
        if (handle == null || handle.data == null) {
            return;
        }

        int damage = calculatePlayerOreDamage();
        if (damage <= 0) {
            return;
        }

        int hpBefore = Math.max(0, handle.data.hp);
        int vit = Math.max(0, handle.data.vit);
        int dealt = CombatRules.applyDamageReduction(Math.max(0, damage), vit);
        if (dealt <= 0) {
            return;
        }

        int hpAfter = Math.max(0, hpBefore - dealt);
        handle.data.hp = hpAfter;

        spawnCommonOreDrops(handle, nowSeconds);

        if (hpAfter <= 0) {
            killOre(handle, currentDay, nowSeconds);
        }

        renderNonTreeVisual(handle);
    }

    private void applyPlayerAttackToResource(
        String debugTag,
        TreeMapState.TreeData resource,
        int currentDay,
        double nowSeconds,
        int baseDamage,
        int commonDropItemId,
        int uncommonDropItemId,
        double uncommonDropChancePercent,
        ResourceDropSpawner dropSpawner,
        ResourceLifecycleHandler killHandler
    ) {
        if (resource == null) return;
        if (resource.currentStage == 1) return;

        applyTreeDailyGrowthIfNeeded(resource, currentDay);
        applyTreeHpGrowthIfNeeded(resource, nowSeconds);

        resource.lastHpGrowthTotalSeconds = nowSeconds;
        resource.currentStage = stageFromLivedDays(resource.livedDays);
        resource.vit = vitFor(resource.livedDays);

        int resourceVit = Math.max(0, resource.vit);
        int receivedDamage = CombatRules.applyDamageReduction(baseDamage, resourceVit);
        int reducedDamage = Math.max(0, baseDamage - receivedDamage);

        int hpBefore = Math.max(0, resource.hp);
        int maxHp = Math.max(0, resource.maxHp);
        int actualDamage = Math.min(receivedDamage, hpBefore);
        int hpAfter = Math.max(0, hpBefore - actualDamage);

        System.out.println(
            "[" + (debugTag == null ? "RESOURCE" : debugTag) + "] HP " + hpBefore + "/" + maxHp +
            ", VIT " + resourceVit +
            " | PlayerDamage " + baseDamage +
            " | Reduced " + reducedDamage +
            " | Received " + actualDamage +
            " | HP -> " + hpAfter + "/" + maxHp
        );

        if (actualDamage <= 0) {
            return;
        }

        resource.hp = hpAfter;
        applyResourceDropRollsForDamage(
            resource,
            actualDamage,
            nowSeconds,
            dropSpawner,
            commonDropItemId,
            uncommonDropItemId,
            uncommonDropChancePercent
        );
        if (resource.hp <= 0 && killHandler != null) {
            killHandler.handle(resource, currentDay, nowSeconds);
        }
    }

    private int calculatePlayerTreeDamage() {
        if (playerState == null) return 0;

        int playerStr = Math.max(0, playerState.getStr());
        int bonus = isAxeEquipped() ? 1 : 0;
        return CombatRules.rollAttackDamage(playerStr, bonus);
    }

    private int calculatePlayerOreDamage() {
        if (playerState == null) return 0;

        int playerStr = Math.max(0, playerState.getStr());
        int bonus = isPickaxeEquipped() ? 1 : 0;
        return CombatRules.rollAttackDamage(playerStr, bonus);
    }

    private boolean isAxeEquipped() {
        if (player == null) return false;
        int equippedId = player.getEquippedItemId();
        if (equippedId <= 0) return false;
        String name = ItemDatabase.getItemName(equippedId);
        if (name == null || name.isBlank()) return false;

        String normalized = name.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("axe")) return true;

        if (normalized.contains("pickaxe") || normalized.contains("pick axe")) return false;
        return normalized.contains("axe");
    }

    private boolean isPickaxeEquipped() {
        if (player == null) return false;
        int equippedId = player.getEquippedItemId();
        if (equippedId <= 0) return false;
        String name = ItemDatabase.getItemName(equippedId);
        if (name == null || name.isBlank()) return false;

        String normalized = name.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("pickaxe") || normalized.equals("pick axe")) return true;
        return normalized.contains("pickaxe") || normalized.contains("pick axe");
    }

    private void killTree(TreeMapState.TreeData tree, int currentDay, double nowSeconds) {
        if (tree == null) return;
        if (tree.currentStage == 1) return;

        double respawnSeconds = computeResourceRespawnSeconds(tree);
        tree.currentStage = 1;
        tree.hp = 0;
        tree.maxHp = 0;
        tree.vit = 0;
        tree.respawnAtTotalSeconds = nowSeconds + respawnSeconds;
        tree.livedDays = 0;
        tree.lastUpdatedDayNumber = Math.max(1, currentDay);
        tree.lastHpGrowthTotalSeconds = -1;

        tree.dropRatePity = 0;
        tree.damageSinceLastDropRoll = 0;
    }

    private void respawnTree(TreeMapState.TreeData tree, int currentDay, double nowSeconds) {
        if (tree == null) return;

        tree.typeKey = randomTreeTypeKey();
        tree.livedDays = Math.max(0, currentDay - 1);
        tree.currentStage = stageFromLivedDays(tree.livedDays);
        tree.vit = vitFor(tree.livedDays);
        tree.maxHp = STARTING_MAX_HP;
        tree.hp = STARTING_MAX_HP;
        tree.respawnAtTotalSeconds = -1;
        tree.lastUpdatedDayNumber = Math.max(1, currentDay);
        tree.lastHpGrowthTotalSeconds = nowSeconds;

        tree.dropRatePity = 0;
        tree.dropsThisLife = 0;
        tree.damageSinceLastDropRoll = 0;
    }

    private void killStone(TreeMapState.TreeData stone, int currentDay, double nowSeconds) {
        if (stone == null) return;
        if (stone.currentStage == 1) return;

        double respawnSeconds = computeResourceRespawnSeconds(stone);
        stone.currentStage = 1;
        stone.hp = 0;
        stone.maxHp = 0;
        stone.vit = 0;
        stone.respawnAtTotalSeconds = nowSeconds + respawnSeconds;
        stone.livedDays = 0;
        stone.lastUpdatedDayNumber = Math.max(1, currentDay);
        stone.lastHpGrowthTotalSeconds = -1;

        stone.dropRatePity = 0;
        stone.damageSinceLastDropRoll = 0;
    }

    private void respawnStone(TreeMapState.TreeData stone, int currentDay, double nowSeconds) {
        if (stone == null) return;

        stone.typeKey = "stone";
        stone.livedDays = Math.max(0, currentDay - 1);
        stone.currentStage = stageFromLivedDays(stone.livedDays);
        stone.vit = vitFor(stone.livedDays);
        stone.maxHp = STARTING_MAX_HP;
        stone.hp = STARTING_MAX_HP;
        stone.respawnAtTotalSeconds = -1;
        stone.lastUpdatedDayNumber = Math.max(1, currentDay);
        stone.lastHpGrowthTotalSeconds = nowSeconds;

        stone.dropRatePity = 0;
        stone.dropsThisLife = 0;
        stone.damageSinceLastDropRoll = 0;
    }

    private void respawnOre(ResourceHandle handle, int currentDay, double nowSeconds) {
        if (handle == null || handle.data == null) {
            return;
        }

        double multiplier = oreStatusMultiplier(handle.baseId);
        int desiredMaxHp = scaledStatus(STARTING_MAX_HP, multiplier);
        int desiredVit = scaledStatus(STARTING_ORE_VIT, multiplier);

        handle.data.currentStage = 2;
        handle.data.hp = desiredMaxHp;
        handle.data.maxHp = desiredMaxHp;
        handle.data.vit = desiredVit;
        handle.data.respawnAtTotalSeconds = -1;
        handle.data.lastUpdatedDayNumber = Math.max(1, currentDay);

        handle.data.dropRatePity = 0;
        handle.data.dropsThisLife = 0;
        handle.data.damageSinceLastDropRoll = 0;

        if (handle.hitbox != null) {
            handle.hitbox.setDisable(false);
        }
    }

    private int scaledStatus(int baseValue, double multiplier) {
        int base = Math.max(0, baseValue);
        double safe = Double.isFinite(multiplier) ? multiplier : 1.0;
        if (safe <= 0.0) safe = 1.0;
        return Math.max(1, (int) Math.round(base * safe));
    }

    private double oreStatusMultiplier(String baseId) {
        if (baseId == null || baseId.isBlank()) {
            return 1.0;
        }

        String id = baseId.trim().toLowerCase(Locale.ROOT);
        // Treat *_ore nodes as the same variant as their base metal.
        if (id.endsWith("_ore")) {
            id = id.substring(0, id.length() - "_ore".length());
        }

        return switch (id) {
            case "stone", "stone_dungeon" -> 1.0;
            case "copper", "iron" -> 1.5;
            case "silver" -> 2.0;
            case "gold" -> 2.5;
            case "mithril" -> 3.0;
            case "adamantite" -> 4.0;
            case "orichalcum" -> 5.0;
            case "hihiogane" -> 6.0;
            default -> 1.0;
        };
    }

    private void killOre(ResourceHandle handle, int currentDay, double nowSeconds) {
        if (handle == null || handle.data == null) {
            return;
        }

        handle.data.currentStage = 1;
        handle.data.hp = 0;
        handle.data.maxHp = 0;
        handle.data.vit = 0;
        handle.data.respawnAtTotalSeconds = nowSeconds + RESOURCE_RESPAWN_SECONDS;
        handle.data.livedDays = 0;
        handle.data.lastUpdatedDayNumber = Math.max(1, currentDay);

        handle.data.dropRatePity = 0;
        handle.data.damageSinceLastDropRoll = 0;

        if (handle.hitbox != null) {
            handle.hitbox.setDisable(true);
        }
    }

    private double computeResourceRespawnSeconds(TreeMapState.TreeData resource) {
        if (resource == null) {
            return RESOURCE_RESPAWN_SECONDS;
        }

        int totalStatusPoints = 0;
        totalStatusPoints += Math.max(0, resource.maxHp);
        totalStatusPoints += Math.max(0, resource.vit);

        return Math.max(RESOURCE_RESPAWN_SECONDS, (double) totalStatusPoints);
    }

    private void applyResourceDropRollsForDamage(
        TreeMapState.TreeData resource,
        int hpLost,
        double nowSeconds,
        ResourceDropSpawner dropSpawner,
        int commonDropItemId,
        int uncommonDropItemId,
        double uncommonDropChancePercent
    ) {
        if (resource == null) return;
        if (resource.currentStage != 2 && resource.currentStage != 3) return;
        if (hpLost <= 0) return;

        int gatheringLevel = getGatheringLevel();
        int commonBonus = SkillModifiers.gatheringCommonBonusPercent(gatheringLevel);
        int uncommonBonus = SkillModifiers.gatheringUncommonBonusPercent(gatheringLevel);
        double adjustedUncommonChancePercent = Math.max(0.0, Math.min(100.0, uncommonDropChancePercent + uncommonBonus));

        int remainderBefore = Math.max(0, resource.damageSinceLastDropRoll);
        int hpAfterCall = Math.max(0, resource.hp);
        int hpBeforeCall = hpAfterCall + hpLost;

        resource.damageSinceLastDropRoll = remainderBefore + hpLost;

        int processedRolls = 0;

        while (resource.damageSinceLastDropRoll >= DROP_ROLL_HP_CHUNK) {
            resource.damageSinceLastDropRoll -= DROP_ROLL_HP_CHUNK;
            processedRolls++;

            int chunkDamageFromThisCall = (processedRolls * DROP_ROLL_HP_CHUNK) - remainderBefore;
            if (chunkDamageFromThisCall < 0) {
                chunkDamageFromThisCall = 0;
            }
            chunkDamageFromThisCall = Math.min(hpLost, chunkDamageFromThisCall);

            int hpAfterChunk = Math.max(0, hpBeforeCall - chunkDamageFromThisCall);

            if (hpAfterChunk < 20) {
                trySpawnTreeDrop(resource, commonDropItemId, nowSeconds);
                trySpawnGatheringCommonBonusDrop(resource, commonDropItemId, nowSeconds, commonBonus);
                if (uncommonDropItemId > 0
                    && adjustedUncommonChancePercent > 0.0
                    && ThreadLocalRandom.current().nextDouble(100.0) < adjustedUncommonChancePercent) {
                    trySpawnTreeDrop(resource, uncommonDropItemId, nowSeconds);
                }
                continue;
            }

            if (uncommonDropItemId > 0
                && adjustedUncommonChancePercent > 0.0
                && ThreadLocalRandom.current().nextDouble(100.0) < adjustedUncommonChancePercent) {
                trySpawnTreeDrop(resource, uncommonDropItemId, nowSeconds);
            }

            if (dropSpawner == null || !canSpawnAliveDrop(resource)) {
                continue;
            }

            int roll = rollTreeDropNumber(resource);
            if (roll == 0) {
                continue;
            }
            if (roll == 10) {
                resource.dropRatePity = 0;
                dropSpawner.spawn(resource, nowSeconds);
                continue;
            }

            resource.dropRatePity = Math.min(10, Math.max(0, resource.dropRatePity) + roll);
            if (resource.dropRatePity >= 10) {
                resource.dropRatePity = 0;
                dropSpawner.spawn(resource, nowSeconds);
            }
        }

        if (resource.damageSinceLastDropRoll < 0) resource.damageSinceLastDropRoll = 0;
        if (resource.dropRatePity < 0) resource.dropRatePity = 0;
        if (resource.dropRatePity > 10) resource.dropRatePity = 10;
    }

    private boolean canSpawnAliveDrop(TreeMapState.TreeData tree) {
        if (tree == null) return false;

        int aliveCap = switch (tree.currentStage) {
            case 2 -> TREE_STAGE2_MAX_ALIVE_DROPS;
            case 3 -> TREE_STAGE3_MAX_ALIVE_DROPS;
            default -> 0;
        };

        return tree.dropsThisLife < aliveCap && tree.dropsThisLife < MAX_DROPS_PER_LIFE;
    }

    private int rollTreeDropNumber(TreeMapState.TreeData tree) {
        int treeVit = Math.max(0, tree == null ? 0 : tree.vit);
        int playerDex = Math.max(0, playerState == null ? 0 : playerState.getDex());

        int zeroWeight = calculateZeroRollWeight(playerDex, treeVit);
        int positiveWeightEach = 10;
        int totalWeight = zeroWeight + (10 * positiveWeightEach);

        int pick = ThreadLocalRandom.current().nextInt(totalWeight);

        if (pick < zeroWeight) {
            return 0;
        }

        return ((pick - zeroWeight) / positiveWeightEach) + 1;
    }

    private int calculateZeroRollWeight(int playerDex, int treeVit) {
        int safeDex = Math.max(0, playerDex);
        int safeVit = Math.max(0, treeVit);

        if (safeDex > safeVit * 2) {
            return 0;
        }

        int baseZeroWeight = 10;

        if (safeDex <= safeVit) {
            return baseZeroWeight;
        }

        double progress = (double) (safeDex - safeVit) / Math.max(1.0, safeVit);
        double clamped = Math.max(0.0, Math.min(1.0, progress / 2.0));

        return (int) Math.round(baseZeroWeight * (1.0 - clamped));
    }

    private void spawnAliveTreeDropForStage(TreeMapState.TreeData tree, double nowSeconds) {
        if (tree == null) return;
        if (!canSpawnAliveDrop(tree)) return;

        int logId = resolveTreeLogItemId();
        if (tree.currentStage == 2) {
            trySpawnTreeDrop(tree, logId, nowSeconds);
            trySpawnGatheringCommonBonusDrop(tree, logId, nowSeconds, SkillModifiers.gatheringCommonBonusPercent(getGatheringLevel()));
            return;
        }

        int fruitId = resolveFruitItemIdForTypeKey(tree.typeKey);
        int chosen = chooseValidTreeDropItemId(logId, fruitId);
        trySpawnTreeDrop(tree, chosen, nowSeconds);
        trySpawnGatheringCommonBonusDrop(tree, chosen, nowSeconds, SkillModifiers.gatheringCommonBonusPercent(getGatheringLevel()));
    }

    private void spawnAliveStoneDropForStage(TreeMapState.TreeData stone, double nowSeconds) {
        if (stone == null) return;
        if (!canSpawnAliveDrop(stone)) return;

        int stoneId = resolveStoneItemId();
        trySpawnTreeDrop(stone, stoneId, nowSeconds);
        trySpawnGatheringCommonBonusDrop(stone, stoneId, nowSeconds, SkillModifiers.gatheringCommonBonusPercent(getGatheringLevel()));
    }

    private void spawnCommonOreDrops(ResourceHandle handle, double nowSeconds) {
        if (handle == null || handle.data == null) {
            return;
        }

        int dropItemId = resolveOreDropItemId(handle);
        if (dropItemId <= 0) {
            return;
        }

        if (handle.data.dropsThisLife < 0) {
            handle.data.dropsThisLife = 0;
        }
        if (handle.data.dropsThisLife >= MAX_DROPS_PER_LIFE) {
            return;
        }

        handle.data.drops.add(new TreeMapState.TreeDrop(dropItemId, 1, nowSeconds, nowSeconds + DROP_TTL_SECONDS));
        handle.data.dropsThisLife += 1;

        int gatheringLevel = getGatheringLevel();
        int bonusPercent = SkillModifiers.gatheringCommonBonusPercent(gatheringLevel);
        int chance = Math.max(0, Math.min(100, bonusPercent));
        if (chance > 0 && handle.data.dropsThisLife < MAX_DROPS_PER_LIFE) {
            if (ThreadLocalRandom.current().nextDouble(100.0) < chance) {
                handle.data.drops.add(new TreeMapState.TreeDrop(dropItemId, 1, nowSeconds, nowSeconds + DROP_TTL_SECONDS));
                handle.data.dropsThisLife += 1;
            }
        }
    }

    private int resolveOreDropItemId(ResourceHandle handle) {
        if (handle == null) return -1;
        if (handle.cachedDropItemId > 0) return handle.cachedDropItemId;

        String base = handle.baseId == null ? "" : handle.baseId.trim().toLowerCase(Locale.ROOT);
        if (base.isBlank()) {
            handle.cachedDropItemId = -1;
            return handle.cachedDropItemId;
        }

        String dropBase = base.endsWith("_ore") ? base.substring(0, base.length() - "_ore".length()) : base;
        String dropKey = dropBase + "_drop_item";
        model.item itm = ItemDatabase.getItemByName(dropKey);
        handle.cachedDropItemId = (itm == null) ? -1 : itm.getItem_id();
        return handle.cachedDropItemId;
    }

    private int chooseValidTreeDropItemId(int logId, int fruitId) {
        boolean hasLog = logId > 0;
        boolean hasFruit = fruitId > 0;

        if (hasLog && hasFruit) {
            return ThreadLocalRandom.current().nextBoolean() ? logId : fruitId;
        }
        if (hasLog) return logId;
        return fruitId;
    }

    private boolean trySpawnTreeDrop(TreeMapState.TreeData tree, int itemId, double nowSeconds) {
        if (tree == null) return false;
        if (tree.dropsThisLife >= MAX_DROPS_PER_LIFE) return false;
        if (itemId <= 0) return false;

        tree.drops.add(new TreeMapState.TreeDrop(itemId, 1, nowSeconds, nowSeconds + DROP_TTL_SECONDS));
        tree.dropsThisLife += 1;
        return true;
    }

    private void trySpawnGatheringCommonBonusDrop(TreeMapState.TreeData resource, int itemId, double nowSeconds, int bonusPercent) {
        if (resource == null || itemId <= 0) return;
        int chance = Math.max(0, Math.min(100, bonusPercent));
        if (chance <= 0) return;
        if (ThreadLocalRandom.current().nextDouble(100.0) < chance) {
            trySpawnTreeDrop(resource, itemId, nowSeconds);
        }
    }

    private int resolveTreeLogItemId() {
        if (treeLogItemId > 0) return treeLogItemId;
        treeLogItemId = resolveItemId("tree_log");
        return treeLogItemId;
    }

    private int resolvePlantFiberItemId() {
        if (plantFiberItemId > 0) return plantFiberItemId;
        plantFiberItemId = resolveItemId("plant_fiber");
        return plantFiberItemId;
    }

    private int resolveStoneItemId() {
        if (stoneItemId > 0) return stoneItemId;
        stoneItemId = resolveItemId("stone_drop_item");
        return stoneItemId;
    }

    private int resolveSandItemId() {
        if (sandItemId > 0) return sandItemId;
        sandItemId = resolveItemId("sand");
        return sandItemId;
    }

    private int resolveFruitItemIdForTypeKey(String typeKey) {
        String base = (typeKey == null) ? "" : typeKey.trim().toLowerCase(Locale.ROOT);
        if (base.isBlank()) {
            base = "blue";
        }
        return resolveItemId(base + "_fruit");
    }

    private int resolveItemId(String itemName) {
        item itm = ItemDatabase.getItemByName(itemName);
        return (itm == null) ? -1 : itm.getItem_id();
    }

    private int getGatheringLevel() {
        if (gameState == null || gameState.getPlayerSkills() == null) {
            return 0;
        }
        return Math.max(0, gameState.getPlayerSkills().getGatheringLevel());
    }

    private String randomTreeTypeKey() {
        List<String> keys = getValidTreeTypeKeys();
        if (keys.isEmpty()) {
            return "blue";
        }
        return keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
    }

    private List<String> getValidTreeTypeKeys() {
        if (cachedValidTreeTypeKeys != null) {
            return cachedValidTreeTypeKeys;
        }

        ItemDatabase.loadItems();

        Set<String> keys = new HashSet<>();
        for (item itm : ItemDatabase.getAllItems()) {
            if (itm == null) continue;
            String name = itm.getItem_name();
            if (name == null || name.isBlank()) continue;

            String normalized = name.trim().toLowerCase(Locale.ROOT);
            if (!normalized.endsWith("_tree1")) {
                continue;
            }

            String base = normalized.substring(0, normalized.length() - "_tree1".length());
            if (base.isBlank()) continue;
            if (isValidTreeTypeKey(base)) {
                keys.add(base);
            }
        }

        List<String> list = new ArrayList<>(keys);
        Collections.sort(list);
        cachedValidTreeTypeKeys = list;
        return cachedValidTreeTypeKeys;
    }

    private boolean isValidTreeTypeKey(String typeKey) {
        if (typeKey == null || typeKey.isBlank()) return false;
        String base = typeKey.trim().toLowerCase(Locale.ROOT);

        item tree1 = ItemDatabase.getItemByName(base + "_tree1");
        item tree2 = ItemDatabase.getItemByName(base + "_tree2");
        item fruit = ItemDatabase.getItemByName(base + "_fruit");
        if (tree1 == null || tree2 == null || fruit == null) {
            return false;
        }

        if (!ItemDatabase.isDisplayableItem(tree1.getItem_id())
            || !ItemDatabase.isDisplayableItem(tree2.getItem_id())
            || !ItemDatabase.isDisplayableItem(fruit.getItem_id())) {
            return false;
        }

        String up1 = "/image/items/" + base + "_tree1up.png";
        String up2 = "/image/items/" + base + "_tree2up.png";
        return getClass().getResource(up1) != null && getClass().getResource(up2) != null;
    }

    private Image loadImageOrNull(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        Image image = ImageCache.get(path);
        if (image == null) {
            System.err.println("[NaturalResourceSystem] Missing resource: " + path);
            return null;
        }
        return image;
    }

    private <T extends Node> T node(String id, Class<T> type) {
        if (id == null || id.isBlank() || type == null) return null;
        Node n = nodesById.get(id);
        if (n == null) return null;
        if (!type.isInstance(n)) return null;
        return type.cast(n);
    }

    private static void indexNodes(Node root, Map<String, Node> out) {
        if (root == null || out == null) return;

        String id = root.getId();
        if (id != null && !id.isBlank()) {
            out.putIfAbsent(id, root);
        }

        if (root instanceof Parent p) {
            for (Node child : p.getChildrenUnmodifiable()) {
                indexNodes(child, out);
            }
        }
    }
}

