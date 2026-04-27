package controller;

import db.MobDefinitionRepository;
import game.GameState;
import game.GameTimeManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import model.CollisionRegistry;
import model.ItemDatabase;
import model.MobEntity;
import model.MobMapState;
import model.Player;
import model.PlayerState;
import model.item;
import model.mobs;
import util.AppDialogs;
import util.CombatRules;
import util.SkillModifiers;

final class MapMobSystem {

    private static final Set<String> SUPPORTED_MOB_TYPES = Set.of(
        "slime",
        "poison_slime",
        "lava_slime",
        "orc",
        "orcb"
    );

    private static final double MOB_DROP_TTL_SECONDS = 12.0 * 60.0 * 60.0;
    private static final double AUTO_GATHER_MARGIN_PIXELS = 2.0;
    private static final double ORC_LOOT_COMMON_WEIGHT = 70.0;
    private static final double ORC_LOOT_UNCOMMON_WEIGHT = 25.0;
    private static final double ORC_LOOT_RARE_WEIGHT = 5.0;

    private static final int SLIME_BASE_MAX_HP = 10;
    private static final int SLIME_BASE_MAX_SP = 30;
    private static final int SLIME_BASE_STR = 3;
    private static final int SLIME_BASE_AGI = 2;
    private static final int SLIME_BASE_VIT = 6;
    private static final int SLIME_BASE_DEX = 1;

    private static final int ORC_BASE_MAX_HP = 20;
    private static final int ORC_BASE_MAX_SP = 25;
    private static final int ORC_BASE_STR = 4;
    private static final int ORC_BASE_AGI = 3;
    private static final int ORC_BASE_VIT = 3;
    private static final int ORC_BASE_DEX = 1;

    private static final class MobVisuals {
        final String idlePath;
        final int idleColumns;
        final String walkPath;
        final int walkColumns;
        final String runPath;
        final int runColumns;
        final String attackPath;
        final int attackColumns;
        final String hurtPath;
        final int hurtColumns;
        final String deathPath;
        final int deathColumns;

        MobVisuals(
            String idlePath,
            int idleColumns,
            String walkPath,
            int walkColumns,
            String runPath,
            int runColumns,
            String attackPath,
            int attackColumns,
            String hurtPath,
            int hurtColumns,
            String deathPath,
            int deathColumns
        ) {
            this.idlePath = idlePath;
            this.idleColumns = idleColumns;
            this.walkPath = walkPath;
            this.walkColumns = walkColumns;
            this.runPath = runPath;
            this.runColumns = runColumns;
            this.attackPath = attackPath;
            this.attackColumns = attackColumns;
            this.hurtPath = hurtPath;
            this.hurtColumns = hurtColumns;
            this.deathPath = deathPath;
            this.deathColumns = deathColumns;
        }
    }

    private static final class MobHandle {
        final String mobId;
        final String mobType;
        final MobEntity.Kind kind;
        final Group node;
        final Rectangle bodyHitbox;
        final ImageView dropsView;
        final Rectangle spawnMarker;
        final double fallbackSpawnX;
        final double fallbackSpawnY;
        final MobEntity entity;
        String renderedDropsKey = null;
        boolean warnedInventoryFull = false;

        MobHandle(
            String mobId,
            String mobType,
            MobEntity.Kind kind,
            Group node,
            Rectangle bodyHitbox,
            ImageView dropsView,
            Rectangle spawnMarker,
            double fallbackSpawnX,
            double fallbackSpawnY,
            MobEntity entity
        ) {
            this.mobId = mobId;
            this.mobType = mobType;
            this.kind = kind;
            this.node = node;
            this.bodyHitbox = bodyHitbox;
            this.dropsView = dropsView;
            this.spawnMarker = spawnMarker;
            this.fallbackSpawnX = fallbackSpawnX;
            this.fallbackSpawnY = fallbackSpawnY;
            this.entity = entity;
        }
    }

    private final GameState gameState;
    private final String mapKey;
    private final Group world;
    private final Group walls;
    private final Player player;
    private final PlayerState playerState;
    private final Rectangle playerBodyHitbox;

    private final Rectangle playerAttackLeft;
    private final Rectangle playerAttackRight;
    private final Rectangle playerAttackUp;
    private final Rectangle playerAttackDown;

    private final Map<String, Node> nodesById = new HashMap<>();
    private final List<MobHandle> mobs = new ArrayList<>();

    private int sandItemId = -1;
    private int stringyMeatItemId = -1;
    private int smallMeatChunkItemId = -1;
    private int steakItemId = -1;
    private final Map<String, mobs> mobDefinitionsByType = new HashMap<>();

    static MapMobSystem tryCreate(
        GameState gameState,
        String mapKey,
        Group world,
        Group walls,
        Player player,
        PlayerState playerState,
        Rectangle playerBodyHitbox
    ) {
        if (gameState == null || mapKey == null || mapKey.isBlank() || world == null) {
            return null;
        }
        MapMobSystem system = new MapMobSystem(gameState, mapKey, world, walls, player, playerState, playerBodyHitbox);
        if (system.mobs.isEmpty()) {
            return null;
        }
        return system;
    }

    private MapMobSystem(
        GameState gameState,
        String mapKey,
        Group world,
        Group walls,
        Player player,
        PlayerState playerState,
        Rectangle playerBodyHitbox
    ) {
        this.gameState = gameState;
        this.mapKey = mapKey;
        this.world = world;
        this.walls = walls;
        this.player = player;
        this.playerState = playerState;
        this.playerBodyHitbox = playerBodyHitbox;

        indexNodes(world, nodesById);

        this.playerAttackLeft = node("human_hitbox_left", Rectangle.class);
        this.playerAttackRight = node("human_hitbox_right", Rectangle.class);
        this.playerAttackUp = node("human_hitbox_up", Rectangle.class);
        this.playerAttackDown = node("human_hitbox_down", Rectangle.class);

        // Ensure attack hitboxes never intercept input (they're intersection-only helpers).
        if (playerAttackLeft != null) playerAttackLeft.setMouseTransparent(true);
        if (playerAttackRight != null) playerAttackRight.setMouseTransparent(true);
        if (playerAttackUp != null) playerAttackUp.setMouseTransparent(true);
        if (playerAttackDown != null) playerAttackDown.setMouseTransparent(true);

        loadMobDefinitions();
        setupMobsFromWorld();
    }

    List<Rectangle> mobHitboxesForPlayerCollision() {
        List<Rectangle> result = new ArrayList<>();
        for (MobHandle handle : mobs) {
            if (handle != null && handle.bodyHitbox != null) {
                result.add(handle.bodyHitbox);
            }
        }
        return result;
    }

    void update(Bounds playerBoundsScene) {
        if (playerBoundsScene == null || playerState == null) {
            return;
        }

        syncDirectionalHitboxes();

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();
        long nowNanos = System.nanoTime();

        for (MobHandle handle : mobs) {
            if (handle == null || handle.entity == null || handle.node == null) {
                continue;
            }

            boolean wasDead = handle.entity.isDead();
            handle.entity.update(nowNanos, currentDay, nowSeconds, playerBoundsScene, playerState);
            if (wasDead && !handle.entity.isDead()) {
                respawnMobAtMarkerOrFallback(handle);
            }
        }

        autoGatherMobDrops(playerBoundsScene);
        renderAllMobDrops();
    }

    void handlePlayerAttackStarted() {
        if (player == null || playerState == null) return;

        Bounds attackBounds = buildPlayerAttackBounds();
        if (attackBounds == null) return;

        long nowNanos = System.nanoTime();
        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();

        int damage = calculatePlayerMobDamage();
        if (damage <= 0) return;

        for (MobHandle handle : mobs) {
            if (handle == null || handle.entity == null || handle.entity.isDead() || handle.entity.getBodyHitbox() == null) {
                continue;
            }

            Bounds mobBounds = handle.entity.getBodyHitbox().localToScene(handle.entity.getBodyHitbox().getBoundsInLocal());
            if (!attackBounds.intersects(mobBounds)) {
                continue;
            }

            boolean killed = handle.entity.applyPlayerDamage(damage, nowNanos);
            if (killed) {
                handleMobKilledByPlayer(handle.entity, nowSeconds);
                renderSingleMobDrops(handle);
            }
        }
    }

    private void setupMobsFromWorld() {
        ItemDatabase.loadItems();

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        MobMapState mapMobs = gameState.getMobState(mapKey);
        List<Rectangle> staticWalls = (walls != null) ? CollisionRegistry.collectWalls(walls) : List.of();

        List<Group> mobGroups = new ArrayList<>();
        for (Node node : nodesById.values()) {
            if (node instanceof Group g) {
                String id = g.getId();
                if (id == null || id.isBlank()) continue;
                String type = baseMobTypeFromMobId(id);
                if (type != null) {
                    mobGroups.add(g);
                }
            }
        }
        mobGroups.sort(Comparator.comparing(g -> g.getId() == null ? "" : g.getId()));

        for (Group mobNode : mobGroups) {
            if (mobNode == null || mobNode.getId() == null || mobNode.getId().isBlank()) continue;

            String mobId = mobNode.getId();
            String mobType = baseMobTypeFromMobId(mobId);
            if (mobType == null) continue;

            Rectangle hitbox = node(mobId + "_hitbox", Rectangle.class);
            ImageView img = node(mobId + "_img", ImageView.class);
            if (hitbox == null || img == null) {
                continue;
            }

            ImageView dropsImg = node(mobId + "_drops_img", ImageView.class);
            Rectangle detect = node(mobId + "_detect", Rectangle.class);
            Rectangle hitboxLeft = node(mobId + "_hitbox_left", Rectangle.class);
            Rectangle hitboxRight = node(mobId + "_hitbox_right", Rectangle.class);
            Rectangle hitboxUp = node(mobId + "_hitbox_up", Rectangle.class);
            Rectangle hitboxDown = node(mobId + "_hitbox_down", Rectangle.class);

            // Mark all mob helper hitboxes as non-interactive.
            hitbox.setMouseTransparent(true);
            if (dropsImg != null) dropsImg.setMouseTransparent(true);
            if (detect != null) detect.setMouseTransparent(true);
            if (hitboxLeft != null) hitboxLeft.setMouseTransparent(true);
            if (hitboxRight != null) hitboxRight.setMouseTransparent(true);
            if (hitboxUp != null) hitboxUp.setMouseTransparent(true);
            if (hitboxDown != null) hitboxDown.setMouseTransparent(true);

            Rectangle marker = node("spawn_" + mobId, Rectangle.class);

            MobEntity.Kind kind = mobType.contains("orc") ? MobEntity.Kind.ORC : MobEntity.Kind.SLIME;

            mobs definition = findMobDefinition(mobType);
            int multiplier = baseStatMultiplier(mobType);
            int maxHp = definition != null ? Math.max(1, definition.getHp()) : (kind == MobEntity.Kind.ORC ? ORC_BASE_MAX_HP : SLIME_BASE_MAX_HP) * multiplier;
            int maxSp = definition != null ? Math.max(1, definition.getStimana()) : (kind == MobEntity.Kind.ORC ? ORC_BASE_MAX_SP : SLIME_BASE_MAX_SP) * multiplier;
            int str = definition != null ? Math.max(0, definition.getStr()) : (kind == MobEntity.Kind.ORC ? ORC_BASE_STR : SLIME_BASE_STR) * multiplier;
            int agi = definition != null ? Math.max(0, definition.getAgi()) : (kind == MobEntity.Kind.ORC ? ORC_BASE_AGI : SLIME_BASE_AGI) * multiplier;
            int vit = definition != null ? Math.max(0, definition.getVid()) : (kind == MobEntity.Kind.ORC ? ORC_BASE_VIT : SLIME_BASE_VIT) * multiplier;
            int dex = definition != null ? Math.max(0, definition.getDex()) : (kind == MobEntity.Kind.ORC ? ORC_BASE_DEX : SLIME_BASE_DEX) * multiplier;

            // Fallback respawn location when no spawn marker is defined.
            // Capture before applying any saved position so respawns stay consistent.
            double fallbackSpawnX = mobNode.getLayoutX();
            double fallbackSpawnY = mobNode.getLayoutY();

            MobMapState.MobData data = mapMobs.getMob(mobId);
            boolean initialized = initializeMobDefaultsIfNeeded(
                data,
                mobType,
                mobNode,
                maxHp,
                maxSp,
                str,
                agi,
                vit,
                dex,
                currentDay,
                nowSeconds
            );

            MobVisuals visuals = visualsFor(mobType);
            if (visuals == null) {
                continue;
            }

            MobEntity entity = new MobEntity(
                kind,
                data,
                mobNode,
                hitbox,
                img,
                detect,
                hitboxLeft,
                hitboxRight,
                hitboxUp,
                hitboxDown,
                staticWalls,
                visuals.idlePath,
                visuals.idleColumns,
                visuals.walkPath,
                visuals.walkColumns,
                visuals.runPath,
                visuals.runColumns,
                visuals.attackPath,
                visuals.attackColumns,
                visuals.hurtPath,
                visuals.hurtColumns,
                visuals.deathPath,
                visuals.deathColumns
            );

            MobHandle handle = new MobHandle(
                mobId,
                mobType,
                kind,
                mobNode,
                hitbox,
                dropsImg,
                marker,
                fallbackSpawnX,
                fallbackSpawnY,
                entity
            );

            mobs.add(handle);

            if (initialized && marker != null) {
                Platform.runLater(() -> applyMobSpawn(marker, mobNode, hitbox, data));
            }
        }

        renderAllMobDrops();
    }

    private void respawnMobAtMarkerOrFallback(MobHandle handle) {
        if (handle == null || handle.node == null || handle.bodyHitbox == null) return;

        MobMapState.MobData data = handle.entity == null ? null : handle.entity.getData();
        if (handle.spawnMarker != null) {
            applyMobSpawn(handle.spawnMarker, handle.node, handle.bodyHitbox, data);
            return;
        }

        handle.node.setLayoutX(handle.fallbackSpawnX);
        handle.node.setLayoutY(handle.fallbackSpawnY);
        if (data != null) {
            data.layoutX = handle.fallbackSpawnX;
            data.layoutY = handle.fallbackSpawnY;
        }
    }

    private void renderAllMobDrops() {
        for (MobHandle handle : mobs) {
            renderSingleMobDrops(handle);
        }
    }

    private void renderSingleMobDrops(MobHandle handle) {
        if (handle == null || handle.entity == null || handle.dropsView == null) return;

        if (handle.entity.isDead() && !handle.entity.isDeathAnimationFinished()) {
            hideMobDropView(handle.dropsView);
            handle.renderedDropsKey = null;
            return;
        }

        handle.renderedDropsKey = renderMobDrops(
            handle.entity.getData(),
            handle.dropsView,
            handle.renderedDropsKey
        );
    }

    private void hideMobDropView(ImageView dropView) {
        if (dropView == null) return;
        dropView.setImage(null);
        dropView.setVisible(false);
        dropView.setManaged(false);
        dropView.setOnMouseClicked(null);
    }

    private String renderMobDrops(MobMapState.MobData mob, ImageView dropView, String renderedKey) {
        if (dropView == null || mob == null) {
            return renderedKey;
        }

        String key = buildMobDropsKey(mob);
        if (key.equals(renderedKey)) {
            return renderedKey;
        }

        MobMapState.MobDrop first = null;
        for (MobMapState.MobDrop d : mob.drops) {
            if (d == null || d.itemId <= 0 || d.quantity <= 0) continue;
            if (ItemDatabase.getImage(d.itemId) == null) continue;
            first = d;
            break;
        }

        if (first == null) {
            dropView.setImage(null);
            dropView.setVisible(false);
            dropView.setManaged(false);
            dropView.setOnMouseClicked(null);
            return key;
        }

        dropView.setImage(ItemDatabase.getImage(first.itemId));
        boolean show = dropView.getImage() != null;
        dropView.setVisible(show);
        dropView.setManaged(show);
        dropView.setPickOnBounds(true);
        dropView.setMouseTransparent(true);
        dropView.setOnMouseClicked(null);

        return key;
    }

    private String buildMobDropsKey(MobMapState.MobData mob) {
        if (mob == null || mob.drops.isEmpty()) {
            return "drops:none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("drops:count=").append(mob.drops.size());
        for (MobMapState.MobDrop d : mob.drops) {
            if (d == null) continue;
            sb.append('#').append(d.itemId).append('x').append(d.quantity);
        }
        return sb.toString();
    }

    private void autoGatherMobDrops(Bounds playerBoundsScene) {
        if (player == null || playerBoundsScene == null) return;

        for (MobHandle handle : mobs) {
            if (handle == null || handle.entity == null || handle.bodyHitbox == null) {
                continue;
            }

            handle.warnedInventoryFull = autoGatherSingleMobDrops(
                handle.entity,
                handle.bodyHitbox,
                playerBoundsScene,
                handle.warnedInventoryFull,
                () -> renderSingleMobDrops(handle)
            );
        }
    }

    private boolean autoGatherSingleMobDrops(
        MobEntity mob,
        Rectangle mobHitbox,
        Bounds playerBoundsScene,
        boolean warnedInventoryFull,
        Runnable rerender
    ) {
        if (mob == null || mobHitbox == null || player == null || playerBoundsScene == null) {
            return false;
        }

        // Drops are rendered only after the death animation finishes; keep pickup timing consistent.
        if (mob.isDead() && !mob.isDeathAnimationFinished()) {
            return warnedInventoryFull;
        }

        MobMapState.MobData data = mob.getData();
        if (data == null || data.drops == null || data.drops.isEmpty()) {
            return warnedInventoryFull;
        }

        boolean inside = intersectsWithMargin(playerBoundsScene, mobHitbox, AUTO_GATHER_MARGIN_PIXELS);
        if (!inside) {
            return false;
        }

        boolean removedAny = false;
        Iterator<MobMapState.MobDrop> it = data.drops.iterator();
        while (it.hasNext()) {
            MobMapState.MobDrop drop = it.next();
            if (drop == null || drop.itemId <= 0 || drop.quantity <= 0) {
                it.remove();
                removedAny = true;
                continue;
            }

            if (!player.getInventory().addItem(drop.itemId, drop.quantity)) {
                if (!warnedInventoryFull) {
                    AppDialogs.showMessageDialog(null, "Not enough inventory space to collect these drops.");
                    warnedInventoryFull = true;
                }
                break;
            }

            it.remove();
            removedAny = true;
        }

        if (removedAny && rerender != null) {
            rerender.run();
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

    private void handleMobKilledByPlayer(MobEntity mob, double nowSeconds) {
        if (mob == null || playerState == null) return;

        int expGain = computeExpGain(mob.getData());
        if (expGain > 0) {
            playerState.addExp(expGain);
        }

        if (gameState != null) {
            gameState.addCombatSkillExp(1);
        }

        int dropItemId = (mob.getKind() == MobEntity.Kind.ORC) ? chooseOrcLootItemId() : resolveSandItemId();
        if (dropItemId <= 0) {
            // Fallback to legacy sand drop if items are missing from the DB.
            dropItemId = resolveSandItemId();
        }

        int dropMultiplier = baseStatMultiplier(mob.getData() == null ? null : mob.getData().mobType);
        dropMultiplier = Math.max(1, dropMultiplier);

        if (dropItemId > 0) {
            mob.spawnDrop(dropItemId, dropMultiplier, nowSeconds, MOB_DROP_TTL_SECONDS);
        }
    }

    private int computeExpGain(MobMapState.MobData mob) {
        if (mob == null || playerState == null) return 0;

        // Spec:
        // - Raw EXP uses mob total status (including HP + SP) vs player total status (excluding HP + SP).
        // - Raw EXP = max(1, player_total - mob_total).
        int mobTotal = Math.max(0, mob.maxHp)
            + Math.max(0, mob.maxSp)
            + Math.max(0, mob.str)
            + Math.max(0, mob.agi)
            + Math.max(0, mob.vit)
            + Math.max(0, mob.dex);

        int playerTotal = Math.max(0, playerState.getStr())
            + Math.max(0, playerState.getAgi())
            + Math.max(0, playerState.getVit())
            + Math.max(0, playerState.getDex());

        long raw = (long) playerTotal - (long) mobTotal;
        return (int) Math.max(1L, raw);
    }

    private int calculatePlayerMobDamage() {
        if (playerState == null || player == null) return 0;

        int playerStr = Math.max(0, playerState.getStr());
        int bonus = 0;
        int equippedId = player.getEquippedItemId();
        if (equippedId > 0) {
            String name = ItemDatabase.getItemName(equippedId);
            if (name != null && !name.isBlank()) {
                String normalized = name.trim().toLowerCase(Locale.ROOT);
                if (normalized.contains("sword")) {
                    bonus += 2;
                }
            }
        }

        int rolled = CombatRules.rollAttackDamage(playerStr, bonus);
        int combatBonus = SkillModifiers.combatBonusDamage(getCombatLevel());
        return Math.max(0, rolled + combatBonus);
    }

    private int getCombatLevel() {
        if (gameState == null || gameState.getPlayerSkills() == null) {
            return 0;
        }
        return Math.max(0, gameState.getPlayerSkills().getCombatLevel());
    }

    private int resolveSandItemId() {
        if (sandItemId > 0) return sandItemId;
        sandItemId = resolveItemId("sand");
        return sandItemId;
    }

    private int resolveStringyMeatItemId() {
        if (stringyMeatItemId > 0) return stringyMeatItemId;
        stringyMeatItemId = resolveItemId("stringy_meat");
        return stringyMeatItemId;
    }

    private int resolveSmallMeatChunkItemId() {
        if (smallMeatChunkItemId > 0) return smallMeatChunkItemId;
        smallMeatChunkItemId = resolveItemId("small_meat_chunk");
        return smallMeatChunkItemId;
    }

    private int resolveSteakItemId() {
        if (steakItemId > 0) return steakItemId;
        steakItemId = resolveItemId("Steak");
        return steakItemId;
    }

    private int resolveItemId(String itemName) {
        ItemDatabase.loadItems();
        item itm = ItemDatabase.getItemByName(itemName);
        return (itm == null) ? -1 : itm.getItem_id();
    }

    private int chooseOrcLootItemId() {
        int stringyId = resolveStringyMeatItemId();
        int chunkId = resolveSmallMeatChunkItemId();
        int steakId = resolveSteakItemId();

        int[] ids = { stringyId, chunkId, steakId };
        double[] weights = { ORC_LOOT_COMMON_WEIGHT, ORC_LOOT_UNCOMMON_WEIGHT, ORC_LOOT_RARE_WEIGHT };

        double total = 0.0;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] > 0 && weights[i] > 0.0) {
                total += weights[i];
            }
        }
        if (total <= 0.0) {
            return -1;
        }

        double roll = ThreadLocalRandom.current().nextDouble(total);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] <= 0 || weights[i] <= 0.0) continue;
            roll -= weights[i];
            if (roll < 0.0) return ids[i];
        }

        for (int i = ids.length - 1; i >= 0; i--) {
            if (ids[i] > 0) return ids[i];
        }
        return -1;
    }

    private Bounds buildPlayerAttackBounds() {
        if (player == null) return null;

        Rectangle directional = resolveActiveDirectionalAttackHitbox();
        if (directional != null) {
            return directional.localToScene(directional.getBoundsInLocal());
        }

        if (playerBodyHitbox == null) return null;
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

    private Rectangle resolveActiveDirectionalAttackHitbox() {
        if (player == null) return null;
        if (playerAttackLeft == null && playerAttackRight == null && playerAttackUp == null && playerAttackDown == null) {
            return null;
        }

        return switch (player.getFacingDirection()) {
            case LEFT -> playerAttackLeft;
            case RIGHT -> playerAttackRight;
            case UP -> playerAttackUp;
            case DOWN -> playerAttackDown;
        };
    }

    private void syncDirectionalHitboxes() {
        if (player == null) return;
        if (playerAttackLeft == null && playerAttackRight == null && playerAttackUp == null && playerAttackDown == null) {
            return;
        }

        var facing = player.getFacingDirection();

        setDirectionalHitboxActive(playerAttackLeft, facing == model.Player.FacingDirection.LEFT);
        setDirectionalHitboxActive(playerAttackRight, facing == model.Player.FacingDirection.RIGHT);
        setDirectionalHitboxActive(playerAttackUp, facing == model.Player.FacingDirection.UP);
        setDirectionalHitboxActive(playerAttackDown, facing == model.Player.FacingDirection.DOWN);
    }

    private void setDirectionalHitboxActive(Rectangle hitbox, boolean active) {
        if (hitbox == null) return;
        hitbox.setDisable(!active);
        hitbox.setVisible(active);
        hitbox.setManaged(active);
    }

    private boolean initializeMobDefaultsIfNeeded(
        MobMapState.MobData mob,
        String mobType,
        Group mobNode,
        int maxHp,
        int maxSp,
        int str,
        int agi,
        int vit,
        int dex,
        int currentDay,
        double nowSeconds
    ) {
        if (mob == null || mobNode == null) return false;

        boolean missingType = mob.mobType == null || mob.mobType.isBlank();
        boolean missingStats = mob.maxHp <= 0 || mob.maxSp <= 0;
        if (missingType || missingStats) {
            mob.mobType = mobType;
            mob.dead = false;
            mob.respawnAtTotalSeconds = -1.0;
            mob.runAttackCooldownUntilTotalSeconds = -1.0;
            mob.livedDays = 0;

            mob.maxHp = Math.max(1, maxHp);
            mob.hp = mob.maxHp;
            mob.maxSp = Math.max(1, maxSp);
            mob.sp = mob.maxSp;

            mob.str = Math.max(0, str);
            mob.agi = Math.max(0, agi);
            mob.vit = Math.max(0, vit);
            mob.dex = Math.max(0, dex);

            int safeDay = Math.max(1, currentDay);
            int worldRolls = Math.max(0, safeDay - 1);
            for (int i = 0; i < worldRolls; i++) {
                applySingleMobDailyGrowthRoll(mob);
            }
            mob.hp = mob.maxHp;
            mob.sp = mob.maxSp;

            mob.layoutX = mobNode.getLayoutX();
            mob.layoutY = mobNode.getLayoutY();
            mob.lastRegenTotalSeconds = nowSeconds;
            mob.lastUpdatedDayNumber = safeDay;
            mob.drops.clear();
            return true;
        }

        mob.mobType = mobType;
        mob.maxHp = Math.max(1, maxHp);
        mob.maxSp = Math.max(1, maxSp);
        mob.str = Math.max(0, str);
        mob.agi = Math.max(0, agi);
        mob.vit = Math.max(0, vit);
        mob.dex = Math.max(0, dex);

        // Apply saved position.
        mobNode.setLayoutX(mob.layoutX);
        mobNode.setLayoutY(mob.layoutY);

        // Clamp HP/SP.
        mob.maxHp = Math.max(1, mob.maxHp);
        mob.maxSp = Math.max(1, mob.maxSp);
        mob.hp = Math.max(0, Math.min(mob.maxHp, mob.hp));
        mob.sp = Math.max(0, Math.min(mob.maxSp, mob.sp));

        if (Double.isNaN(mob.runAttackCooldownUntilTotalSeconds) || Double.isInfinite(mob.runAttackCooldownUntilTotalSeconds)) {
            mob.runAttackCooldownUntilTotalSeconds = -1.0;
        }
        if (mob.runAttackCooldownUntilTotalSeconds < -1.0) {
            mob.runAttackCooldownUntilTotalSeconds = -1.0;
        }

        return false;
    }

    private void loadMobDefinitions() {
        mobDefinitionsByType.clear();
        try {
            mobDefinitionsByType.putAll(new MobDefinitionRepository().findAllByTypeKey());
        } catch (Exception e) {
            System.out.println("Mob definition load fallback: " + e.getMessage());
        }
    }

    private mobs findMobDefinition(String mobType) {
        if (mobType == null || mobType.isBlank()) {
            return null;
        }
        return mobDefinitionsByType.get(MobDefinitionRepository.normalizeType(mobType));
    }

    private void applySingleMobDailyGrowthRoll(MobMapState.MobData mob) {
        if (mob == null) return;

        String type = mob.mobType == null ? "" : mob.mobType.trim().toLowerCase(Locale.ROOT);
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (type.contains("orc")) {
            // Spec (orc): STR 35%, AGI 5%, MAX HP 30%, VIT 30%.
            if (roll < 35) {
                mob.str = Math.max(0, mob.str) + 1;
                return;
            }
            if (roll < 40) {
                mob.agi = Math.max(0, mob.agi) + 1;
                return;
            }
            if (roll < 70) {
                mob.maxHp = Math.max(1, mob.maxHp) + 1;
                mob.hp = Math.min(mob.maxHp, Math.max(0, mob.hp) + 1);
                return;
            }

            mob.vit = Math.max(0, mob.vit) + 1;
            return;
        }

        // Default (slime): STR 10%, AGI 20%, MAX SP 30%, VIT 30%, MAX HP 10%.
        if (roll < 10) {
            mob.str = Math.max(0, mob.str) + 1;
            return;
        }
        if (roll < 30) {
            mob.agi = Math.max(0, mob.agi) + 1;
            return;
        }
        if (roll < 60) {
            mob.maxSp = Math.max(1, mob.maxSp) + 1;
            mob.sp = Math.min(mob.maxSp, Math.max(0, mob.sp) + 1);
            return;
        }
        if (roll < 90) {
            mob.vit = Math.max(0, mob.vit) + 1;
            return;
        }

        mob.maxHp = Math.max(1, mob.maxHp) + 1;
        mob.hp = Math.min(mob.maxHp, Math.max(0, mob.hp) + 1);
    }

    private void applyMobSpawn(Rectangle marker, Group mobNode, Rectangle mobHitbox, MobMapState.MobData mobData) {
        if (marker == null || mobNode == null || mobHitbox == null || mobNode.getParent() == null) {
            return;
        }

        Bounds markerScene = marker.localToScene(marker.getBoundsInLocal());
        Bounds hitboxScene = mobHitbox.localToScene(mobHitbox.getBoundsInLocal());

        Bounds markerInParent = mobNode.getParent().sceneToLocal(markerScene);
        Bounds hitboxInParent = mobNode.getParent().sceneToLocal(hitboxScene);

        double dx = markerInParent.getMinX() - hitboxInParent.getMinX();
        double dy = markerInParent.getMinY() - hitboxInParent.getMinY();

        mobNode.setLayoutX(mobNode.getLayoutX() + dx);
        mobNode.setLayoutY(mobNode.getLayoutY() + dy);

        if (mobData != null) {
            mobData.layoutX = mobNode.getLayoutX();
            mobData.layoutY = mobNode.getLayoutY();
        }
    }

    private static String baseMobTypeFromMobId(String mobId) {
        if (mobId == null || mobId.isBlank()) return null;

        String trimmed = mobId.trim();
        int end = trimmed.length();
        while (end > 0) {
            char c = trimmed.charAt(end - 1);
            if (c < '0' || c > '9') {
                break;
            }
            end--;
        }

        String base = trimmed.substring(0, end);
        if (base.isBlank()) {
            base = trimmed;
        }

        String normalized = base.trim().toLowerCase(Locale.ROOT);
        return SUPPORTED_MOB_TYPES.contains(normalized) ? normalized : null;
    }

    private static int baseStatMultiplier(String mobType) {
        if (mobType == null || mobType.isBlank()) return 1;
        String normalized = mobType.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "poison_slime", "lava_slime" -> 2;
            case "orcb" -> 3;
            default -> 1;
        };
    }

    private static MobVisuals visualsFor(String mobType) {
        if (mobType == null || mobType.isBlank()) return null;

        return switch (mobType.trim().toLowerCase(Locale.ROOT)) {
            case "slime" -> new MobVisuals(
                "/image/mob/Slime/Slime1_Idle_with_shadow.png",
                6,
                "/image/mob/Slime/Slime1_Walk_with_shadow.png",
                8,
                "/image/mob/Slime/Slime1_Run_with_shadow.png",
                8,
                "/image/mob/Slime/Slime1_Attack_with_shadow.png",
                10,
                "/image/mob/Slime/Slime1_Hurt_with_shadow.png",
                5,
                "/image/mob/Slime/Slime1_Death_with_shadow.png",
                10
            );
            case "poison_slime" -> new MobVisuals(
                "/image/mob/Slime/Slime2_Idle_with_shadow.png",
                4,
                "/image/mob/Slime/Slime2_Walk_with_shadow.png",
                6,
                "/image/mob/Slime/Slime2_Run_with_shadow.png",
                8,
                "/image/mob/Slime/Slime2_Attack_with_shadow.png",
                10,
                "/image/mob/Slime/Slime2_Hurt_with_shadow.png",
                5,
                "/image/mob/Slime/Slime2_Death_with_shadow.png",
                10
            );
            case "lava_slime" -> new MobVisuals(
                "/image/mob/Slime/Slime3_Idle_with_shadow.png",
                4,
                "/image/mob/Slime/Slime3_Walk_with_shadow.png",
                6,
                "/image/mob/Slime/Slime3_Run_with_shadow.png",
                8,
                "/image/mob/Slime/Slime3_Attack_with_shadow.png",
                10,
                "/image/mob/Slime/Slime3_Hurt_with_shadow.png",
                5,
                "/image/mob/Slime/Slime3_Death_with_shadow.png",
                10
            );
            case "orc" -> new MobVisuals(
                "/image/mob/Orc/orc1_idle_with_shadow.png",
                4,
                "/image/mob/Orc/orc1_walk_with_shadow.png",
                6,
                "/image/mob/Orc/orc1_run_with_shadow.png",
                8,
                "/image/mob/Orc/orc1_attack_with_shadow.png",
                8,
                "/image/mob/Orc/orc1_hurt_with_shadow.png",
                6,
                "/image/mob/Orc/orc1_death_with_shadow.png",
                8
            );
            case "orcb" -> new MobVisuals(
                "/image/mob/Orc/orc3_idle_with_shadow.png",
                4,
                "/image/mob/Orc/orc3_walk_with_shadow.png",
                6,
                "/image/mob/Orc/orc3_run_with_shadow.png",
                8,
                "/image/mob/Orc/orc3_attack_with_shadow.png",
                8,
                "/image/mob/Orc/orc3_hurt_with_shadow.png",
                6,
                "/image/mob/Orc/orc3_death_with_shadow.png",
                8
            );
            default -> null;
        };
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
