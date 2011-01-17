package group.jinjie.qq.net;

import group.jinjie.qq.R;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class ImageDownloader {
    private static ConcurrentHashMap<String, SoftReference<Bitmap>> mImageCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

    private static final int DEFAULT_THREAD_POOL_SIZE = 3;
    private static ThreadPoolExecutor mExecutor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    public static Bitmap loadImage(final Context context, final String imageUrl, final int channelId,
            final int itemId, final ImageDownloaderCallback imageCallback) {
        if (mImageCache.containsKey(imageUrl)) {
            SoftReference<Bitmap> softReference = mImageCache.get(imageUrl);
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                return bitmap;
            }
        }

        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, channelId,
                        itemId, imageUrl);
            }
        };

        mExecutor.execute(new Runnable() {

            public void run() {
                try {
                    Bitmap bitmap = ImageDownloader.downloadImageFromUrl(context, imageUrl);
                    mImageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));

                    Message message = Message.obtain();
                    message.obj = bitmap;
                    message.arg1 = channelId;
                    message.arg2 = itemId;
                    handler.sendMessage(message);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        return null;
    }

    public static boolean containsUrl(String url) {
        return mImageCache.containsKey(url);
    }

    public interface ImageDownloaderCallback {
        public void imageLoaded(Bitmap bitmap, final int channelId,
                final int itemId, String imageUrl);
    }

    public static Bitmap downloadImageFromUrl(Context context, String imageUrl) {

        URL url = null;
        try {
            url = new URL(imageUrl);
            HttpURLConnection mHttpURLConnection = (HttpURLConnection) url
                    .openConnection();
            mHttpURLConnection.setDoInput(true);
            mHttpURLConnection.connect();

            InputStream in = mHttpURLConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 110, 80, true);
            } else if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.item_default_icon);
                bitmap = Bitmap.createScaledBitmap(bitmap, 110, 80, true);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.item_default_icon);
        }

    }
    
    public static void  shutdownThreadPool() {
        mExecutor.shutdown();
    }

    public static void recycleImageCache(){
        mImageCache.clear();
    }
}
