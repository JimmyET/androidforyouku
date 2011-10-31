package group.jinjie.qq;

import com.ctestore.core.imagecache.ImageLoader;

import android.app.Application;

import java.net.ContentHandler;

import com.ctestore.core.imagecache.BitmapContentHandler;

public class UniversalMediaApp extends Application {
    private static final int IMAGE_TASK_LIMIT = 3;

    // 50% of available memory, up to a maximum of 32MB
    private static final long IMAGE_CACHE_SIZE = Math.min(Runtime.getRuntime().maxMemory() / 2,
            32 * 1024 * 1024);
    
    private ImageLoader mImageLoader;
    
    @Override
    public void onCreate() {
        super.onCreate();
        UniversalMediaCache.install(this);
        ContentHandler bitmapHandler = UniversalMediaCache.capture(new BitmapContentHandler(), null);
        android.util.Log.d("TAG", ""+Runtime.getRuntime().maxMemory());
        mImageLoader = new ImageLoader(IMAGE_TASK_LIMIT, null, bitmapHandler,
                IMAGE_CACHE_SIZE, null);
    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        mImageLoader = null;
        super.onTerminate();
    }

    @Override
    public Object getSystemService(String name) {
        if (ImageLoader.IMAGE_LOADER_SERVICE.equals(name)) {
            return mImageLoader;
        } else {
            return super.getSystemService(name);
        }
    }
}
