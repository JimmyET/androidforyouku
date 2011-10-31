package com.ctestore.core.imagecache;

import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * A bitmap cache with LRU mechanism.
 */
class BitmapCache<K> extends LinkedHashMap<K, Bitmap> {

    /**
     * 
     */
    private static final long serialVersionUID = 3548954352544380038L;

    // Assume a 32-bit image
    private static final long BYTES_PER_PIXEL = 4;

    private static final int INITIAL_CAPACITY = 32;

    private static final float LOAD_FACTOR = 0.75f;

    private final long mMaxBytes;

    private boolean mRemove;

    /**
     * Constructor.
     *
     * @param maxBytes the maximum size of the cache in bytes.
     */
    public BitmapCache(long maxBytes) {
        super(INITIAL_CAPACITY, LOAD_FACTOR, true);
        mMaxBytes = maxBytes;
    }

    static long sizeOf(Bitmap b) {
        return b.getWidth() * b.getHeight() * BYTES_PER_PIXEL;
    }

    private static long sizeOf(Iterable<Bitmap> bitmaps) {
        long total = 0;
        for (Bitmap bitmap : bitmaps) {
            total += sizeOf(bitmap);
        }
        return total;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, Bitmap> eldest) {
        return mRemove;
    }

    /**
     * Removes the eldest entry if the cache is too big.
     */
    private void trimEldest() {
        // Remove null otherwise put() may have no effect
        super.remove(null);

        // Induce LinkedHashMap to remove the eldest element
        mRemove = true;
        try {
            super.put(null, null);
        } finally {
            mRemove = false;
        }

        // Remove null so that it does not appear in the key/entry sets
        super.remove(null);
    }

    /**
     * Removes additional elements until the cache is an acceptable size.
     * <p>
     * This method must be called after each insertion operation.
     */
    private void trimToSize() {
        // This runtime performance of this method is not great,
        // but it's less error-prone than maintaining a counter.
        while (sizeOf(values()) > mMaxBytes) {
            trimEldest();
        }
    }

    private NullPointerException nullKeyException() {
        // Null keys are not permitted because null is used by trim()
        return new NullPointerException("Key is null");
    }

    @Override
    public Bitmap put(K key, Bitmap value) {
        if (key == null) {
            throw nullKeyException();
        }
        
        try {
            return super.put(key, value);
        } finally {
            trimToSize();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends Bitmap> map) {
        if (map.containsKey(null)) {
            throw nullKeyException();
        }
        
        try {
            super.putAll(map);
        } finally {
            trimToSize();
        }
    }

    @Override
    public Bitmap get(Object key) {
        if (key == null) {
            throw nullKeyException();
        }
        return super.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw nullKeyException();
        }
        return super.containsKey(key);
    }

    @Override
    public Bitmap remove(Object key) {
        if (key == null) {
            throw nullKeyException();
        }
        return super.remove(key);
    }
}
