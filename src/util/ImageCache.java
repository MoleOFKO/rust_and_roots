package util;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;

/**
 * Lightweight shared cache for JavaFX {@link Image} resources.
 *
 * Goals:
 * - Avoid creating duplicate Image instances for the same resource path.
 * - Allow the JVM to reclaim image memory under pressure (SoftReference).
 */
public final class ImageCache {

    private static final ConcurrentHashMap<String, SoftReference<Image>> CACHE = new ConcurrentHashMap<>();

    private ImageCache() {
    }

    public static Image get(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return null;
        }

        String key = normalizeKey(resourcePath);
        SoftReference<Image> ref = CACHE.get(key);
        Image cached = ref == null ? null : ref.get();
        if (cached != null) {
            return cached;
        }

        URL url = ImageCache.class.getResource(resourcePath);
        if (url == null) {
            return null;
        }

        Image image = new Image(url.toExternalForm());
        CACHE.put(key, new SoftReference<>(image));
        return image;
    }

    private static String normalizeKey(String resourcePath) {
        // Treat resource paths case-insensitively to reduce duplicates on Windows dev setups.
        return resourcePath.trim().toLowerCase(Locale.ROOT);
    }
}

