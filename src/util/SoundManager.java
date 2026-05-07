package util;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;

public final class SoundManager {

    public static final double DEFAULT_SFX_VOLUME = 1.0;
    public static final double DEFAULT_BGM_VOLUME = 0.5;

    private static volatile double sfxVolume = DEFAULT_SFX_VOLUME;
    private static volatile double bgmVolume = DEFAULT_BGM_VOLUME;

    public enum Sfx {
        SWORD_CUT,
        BOSS_ENCOUNTER,
        TELEPORT,
        HIT,
        PLAYER_DEFEAT
    }

    private static final ConcurrentHashMap<Sfx, AudioClip> CLIPS = new ConcurrentHashMap<>();

    private SoundManager() {
    }

    public static double getSfxVolume() {
        return sfxVolume;
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = clamp01(volume);
    }

    public static double getBgmVolume() {
        return bgmVolume;
    }

    public static void setBgmVolume(double volume) {
        bgmVolume = clamp01(volume);
    }

    public static void playSfx(Sfx sfx) {
        if (sfx == null) {
            return;
        }

        Runnable play = () -> {
            try {
                AudioClip clip = CLIPS.computeIfAbsent(sfx, SoundManager::loadClip);
                if (clip == null) {
                    return;
                }
                clip.play(getSfxVolume());
            } catch (Exception e) {
                System.err.println("[SoundManager] Failed to play sfx " + sfx + ": " + e.getMessage());
            }
        };

        if (Platform.isFxApplicationThread()) {
            play.run();
        } else {
            Platform.runLater(play);
        }
    }

    private static double clamp01(double value) {
        if (!Double.isFinite(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static AudioClip loadClip(Sfx sfx) {
        String resource = switch (sfx) {
            case SWORD_CUT -> "/mp3/swordCut.mp3";
            case BOSS_ENCOUNTER -> "/mp3/bossEncounter.mp3";
            case TELEPORT -> "/mp3/teleport.mp3";
            case HIT -> "/mp3/hit.mp3";
            case PLAYER_DEFEAT -> "/mp3/PlayerDefeat.mp3";
        };

        URL url = SoundManager.class.getResource(resource);
        if (url == null) {
            System.err.println("[SoundManager] Missing sfx resource: " + resource);
            return null;
        }
        return new AudioClip(url.toExternalForm());
    }
}
