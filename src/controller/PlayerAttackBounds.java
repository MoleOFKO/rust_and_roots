package controller;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import model.Player;

final class PlayerAttackBounds {

    private static final double DEFAULT_ATTACK_WIDTH = 14.0;
    private static final double DEFAULT_ATTACK_HEIGHT = 20.0;
    private static final double SPEAR_RANGE_MULTIPLIER = 2.0;

    private PlayerAttackBounds() {
    }

    static Bounds build(
        Player player,
        Rectangle playerBodyHitbox,
        Rectangle attackLeft,
        Rectangle attackRight,
        Rectangle attackUp,
        Rectangle attackDown
    ) {
        if (player == null) {
            return null;
        }

        Player.FacingDirection facing = player.getFacingDirection();
        Rectangle active = switch (facing) {
            case LEFT -> attackLeft;
            case RIGHT -> attackRight;
            case UP -> attackUp;
            case DOWN -> attackDown;
        };

        double multiplier = rangeMultiplierFor(player);
        if (active != null) {
            return expandDirectionalBounds(
                active.localToScene(active.getBoundsInLocal()),
                facing,
                multiplier
            );
        }

        if (playerBodyHitbox == null) {
            return null;
        }

        return buildFallbackBounds(
            playerBodyHitbox.localToScene(playerBodyHitbox.getBoundsInLocal()),
            facing,
            multiplier
        );
    }

    static Bounds build(
        Player player,
        Rectangle playerBodyHitbox,
        Rectangle activeAttackHitbox
    ) {
        if (player == null) {
            return null;
        }

        double multiplier = rangeMultiplierFor(player);
        if (activeAttackHitbox != null) {
            return expandDirectionalBounds(
                activeAttackHitbox.localToScene(activeAttackHitbox.getBoundsInLocal()),
                player.getFacingDirection(),
                multiplier
            );
        }

        if (playerBodyHitbox == null) {
            return null;
        }

        return buildFallbackBounds(
            playerBodyHitbox.localToScene(playerBodyHitbox.getBoundsInLocal()),
            player.getFacingDirection(),
            multiplier
        );
    }

    private static double rangeMultiplierFor(Player player) {
        return player != null && player.isSpearEquipped() ? SPEAR_RANGE_MULTIPLIER : 1.0;
    }

    private static Bounds expandDirectionalBounds(Bounds base, Player.FacingDirection facing, double multiplier) {
        if (base == null) {
            return null;
        }

        double safeMultiplier = Math.max(1.0, multiplier);
        double extraWidth = base.getWidth() * (safeMultiplier - 1.0);
        double extraHeight = base.getHeight() * (safeMultiplier - 1.0);

        return switch (facing) {
            case LEFT -> new BoundingBox(
                base.getMinX() - extraWidth,
                base.getMinY(),
                base.getWidth() + extraWidth,
                base.getHeight()
            );
            case RIGHT -> new BoundingBox(
                base.getMinX(),
                base.getMinY(),
                base.getWidth() + extraWidth,
                base.getHeight()
            );
            case UP -> new BoundingBox(
                base.getMinX(),
                base.getMinY() - extraHeight,
                base.getWidth(),
                base.getHeight() + extraHeight
            );
            case DOWN -> new BoundingBox(
                base.getMinX(),
                base.getMinY(),
                base.getWidth(),
                base.getHeight() + extraHeight
            );
        };
    }

    private static Bounds buildFallbackBounds(Bounds body, Player.FacingDirection facing, double multiplier) {
        if (body == null) {
            return null;
        }

        double safeMultiplier = Math.max(1.0, multiplier);
        double attackWidth = DEFAULT_ATTACK_WIDTH;
        double attackHeight = DEFAULT_ATTACK_HEIGHT;

        return switch (facing) {
            case LEFT -> new BoundingBox(
                body.getMinX() - (attackWidth * safeMultiplier),
                body.getMinY() + (body.getHeight() - attackHeight) * 0.5,
                attackWidth * safeMultiplier,
                attackHeight
            );
            case RIGHT -> new BoundingBox(
                body.getMaxX(),
                body.getMinY() + (body.getHeight() - attackHeight) * 0.5,
                attackWidth * safeMultiplier,
                attackHeight
            );
            case UP -> new BoundingBox(
                body.getMinX() + (body.getWidth() - attackWidth) * 0.5,
                body.getMinY() - (attackHeight * safeMultiplier),
                attackWidth,
                attackHeight * safeMultiplier
            );
            case DOWN -> new BoundingBox(
                body.getMinX() + (body.getWidth() - attackWidth) * 0.5,
                body.getMaxY(),
                attackWidth,
                attackHeight * safeMultiplier
            );
        };
    }
}
