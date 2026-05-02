package util;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;

/**
 * Small shared cache for JavaFX {@link Image} instances to avoid repeatedly decoding the same
 * sprite sheets / UI assets across scene (map) switches.
 *
 * Uses {@link SoftReference} so the JVM can reclaim image memory under pressure.
 */
public final class ImageCache {

    private static final ConcurrentHashMap<String, SoftReference<Image>> CACHE = new ConcurrentHashMap<>();

    private ImageCache() {
    }

    public static Image get(String resourcePath) {
        if (resourcePath == null) {
            return null;
        }

        String key = resourcePath.trim();
        if (key.isEmpty()) {
            return null;
        }

        Image cached = deref(CACHE.get(key));
        if (cached != null) {
            return cached;
        }

        URL url = ImageCache.class.getResource(key);
        if (url == null && !key.startsWith("/")) {
            url = ImageCache.class.getResource("/" + key);
        }
        if (url == null) {
            return null;
        }

        Image image = new Image(url.toExternalForm());
        CACHE.put(key, new SoftReference<>(image));
        return image;
    }

    public static void clear() {
        CACHE.clear();
    }

    private static Image deref(SoftReference<Image> ref) {
        return ref == null ? null : ref.get();
    }
}

