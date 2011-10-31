package com.ctestore.core.imagecache;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ContentHandler;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

/**
 * A helper class to load images asynchronously.
 */
public final class ImageLoader {

    private static final String TAG = "ImageLoader";

    /**
     * The default maximum number of active tasks.
     */
    public static final int DEFAULT_TASK_LIMIT = 3;

    /**
     * The default cache size (in bytes).
     */
    // 25% of available memory, up to a maximum of 16MB
    public static final long DEFAULT_CACHE_SIZE = Math.min(Runtime.getRuntime().maxMemory() / 4,
            16 * 1024 * 1024);

    /**
     * Use with {@link Context#getSystemService(String)} to retrieve an
     * {@link ImageLoader} for loading images.
     * <p>
     * Since {@link ImageLoader} is not a standard system service, you must
     * create a custom {@link Application} subclass implementing
     * {@link Application#getSystemService(String)} and add it to your
     * {@code AndroidManifest.xml}.
     * <p>
     * Using this constant is optional and it is only provided for convenience
     * and to promote consistency across deployments of this component.
     */
    public static final String IMAGE_LOADER_SERVICE = "com.ctestore.imageloader";

    /**
     * Gets the {@link ImageLoader} from a {@link Context}.
     *
     * @throws IllegalStateException if the {@link Application} does not have an
     *             {@link ImageLoader}.
     * @see #IMAGE_LOADER_SERVICE
     */
    public static ImageLoader get(Context context) {
        ImageLoader loader = (ImageLoader) context.getSystemService(IMAGE_LOADER_SERVICE);
        if (loader == null) {
            context = context.getApplicationContext();
            loader = (ImageLoader) context.getSystemService(IMAGE_LOADER_SERVICE);
        }
        if (loader == null) {
            throw new IllegalStateException("ImageLoader not available");
        }
        return loader;
    }

    public static enum BindResult {
        /**
         * Returned when an image is bound to an {@link ImageView} immediately
         * because it was already loaded.
         */
        OK,
        /**
         * Returned when an image needs to be loaded asynchronously.
         * <p>
         * Callers may wish to assign a placeholder or show a progress spinner
         * while the image is being loaded whenever this value is returned.
         */
        LOADING,
        /**
         * Returned when an attempt to load the image has already been made and
         * it failed.
         * <p>
         * Callers may wish to show an error indicator when this value is
         * returned.
         *
         * @see ImageLoader.Callback
         */
        ERROR
    }

    private static String getProtocol(String url) {
        Uri uri = Uri.parse(url);
        return uri.getScheme();
    }

    private final ContentHandler mBitmapContentHandler;

    private final URLStreamHandlerFactory mURLStreamHandlerFactory;

    private final HashMap<String, URLStreamHandler> mStreamHandlers;

    private final LinkedList<ImageRequest> mRequests;

    /**
     * A cache containing least recently used bitmaps.
     * <p>
     * Use soft references so that the application does not run out of memory in
     * the case where one or more of the bitmaps are large.
     */
    private final Map<String, Bitmap> mBitmaps;

    /**
     * Recent errors encountered when loading bitmaps.
     */
    private final Map<String, ImageError> mErrors;

    /**
     * Tracks the last URL that was bound to an {@link ImageView}.
     * <p>
     * This ensures that the right image is shown in the case where a new URL is
     * assigned to an {@link ImageView} before the previous asynchronous task
     * completes.
     * <p>
     * This <em>does not</em> ensure that an image assigned with
     * {@link ImageView#setImageBitmap(Bitmap)},
     * {@link ImageView#setImageDrawable(android.graphics.drawable.Drawable)},
     * {@link ImageView#setImageResource(int)}, or
     * {@link ImageView#setImageURI(android.net.Uri)} is not replaced. This
     * behavior is important because callers may invoke these methods to assign
     * a placeholder when a bind method returns {@link BindResult#LOADING} or
     * {@link BindResult#ERROR}.
     */
    private final Map<ImageView, String> mImageViewBinding;

    /**
     * The maximum number of active tasks.
     */
    private final int mMaxTaskCount;

    /**
     * The current number of active tasks.
     */
    private int mActiveTaskCount;

    public ImageLoader(int taskLimit, URLStreamHandlerFactory streamFactory,
            ContentHandler bitmapHandler, long cacheSize,
            Handler handler) {
        if (taskLimit < 1) {
            throw new IllegalArgumentException("Task limit must be positive");
        }
        if (cacheSize < 1) {
            throw new IllegalArgumentException("Cache size must be positive");
        }
        mMaxTaskCount = taskLimit;
        mURLStreamHandlerFactory = streamFactory;
        mStreamHandlers = streamFactory != null ? new HashMap<String, URLStreamHandler>() : null;
        mBitmapContentHandler = bitmapHandler != null ? bitmapHandler : new BitmapContentHandler();
        mImageViewBinding = new WeakHashMap<ImageView, String>();
        mRequests = new LinkedList<ImageRequest>();

        // Use a LruCache to prevent the set of keys from growing too large.
        // The Maps must be synchronized because they are accessed
        // by the UI thread and by background threads.
        mBitmaps = Collections.synchronizedMap(new BitmapCache<String>(cacheSize));
        mErrors = Collections.synchronizedMap(new LruCache<String, ImageError>());
    }

    private URLStreamHandler getURLStreamHandler(String protocol) {
        URLStreamHandlerFactory factory = mURLStreamHandlerFactory;
        if (factory == null) {
            return null;
        }
        HashMap<String, URLStreamHandler> handlers = mStreamHandlers;
        synchronized (handlers) {
            URLStreamHandler handler = handlers.get(protocol);
            if (handler == null) {
                handler = factory.createURLStreamHandler(protocol);
                if (handler != null) {
                    handlers.put(protocol, handler);
                }
            }
            return handler;
        }
    }

    /**
     * Creates tasks to service any pending requests until {@link #mRequests} is
     * empty or {@link #mMaxTaskCount} is reached.
     */
    void flushRequests() {
        while (mActiveTaskCount < mMaxTaskCount && !mRequests.isEmpty()) {
            new ImageTask().executeOnThreadPool(mRequests.poll());
        }
    }

    private void enqueueRequest(ImageRequest request) {
        mRequests.add(request);
        flushRequests();
    }

    /**
     * Binds a URL to an {@link ImageView} within an {@link android.widget.ListView}.
     *
     * @param adapter the adapter for the {@link android.widget.ListView}.
     * @param view the {@link ImageView}.
     * @param url the image URL.
     * @return a {@link BindResult}.
     * @throws NullPointerException if any of the arguments are {@code null}.
     */
    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int rid) {
        if (adapter == null) {
            throw new NullPointerException("Adapter is null");
        }
        if (view == null) {
            throw new NullPointerException("ImageView is null");
        }
        if (url == null) {
            throw new NullPointerException("URL is null");
        }
        
        mImageViewBinding.put(view, url);
        Bitmap bitmap = getBitmap(url);
        ImageError error = getError(url);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            return BindResult.OK;
        } else {
            // Clear the ImageView by default.
            // The caller can set their own placeholder
            // based on the return value.
            view.setImageResource(rid);

            if (error != null) {
                return BindResult.ERROR;
            } else {
                ImageRequest request = new ImageRequest(adapter, view, url);

                // For adapters, post the latest requests
                // at the front of the queue in case the user
                // has already scrolled past most of the images
                // that are currently in the queue.
                enqueueRequest(request);

                return BindResult.LOADING;
            }
        }
    }

    /**
     * Cancels an asynchronous request to bind an image URL to an
     * {@link ImageView} and clears the {@link ImageView}.
     *
     * @see #bind(ImageView, String, Callback)
     */
    public void unbind(ImageView view) {
        mImageViewBinding.remove(view);
        view.setImageDrawable(null);
    }

    /**
     * Clears any cached errors.
     * <p>
     * Call this method when a network connection is restored, or the user
     * invokes a manual refresh of the screen.
     */
    public void clearErrors() {
        mErrors.clear();
    }

    private void putBitmap(String url, Bitmap bitmap) {
        mBitmaps.put(url, bitmap);
    }

    private void putError(String url, ImageError error) {
        mErrors.put(url, error);
    }

    private Bitmap getBitmap(String url) {
        return mBitmaps.get(url);
    }

    private ImageError getError(String url) {
        ImageError error = mErrors.get(url);
        return error != null && !error.isExpired() ? error : null;
    }

    /**
     * Returns {@code true} if there was an error the last time the given URL
     * was accessed and the error is not expired, {@code false} otherwise.
     */
    private boolean hasError(String url) {
        return getError(url) != null;
    }

    private class ImageRequest {

        private final ImageCallback mCallback;

        private final String mUrl;

        private final boolean mLoadBitmap;

        private Bitmap mBitmap;

        private ImageError mError;

        private ImageRequest(String url, ImageCallback callback, boolean loadBitmap) {
            mUrl = url;
            mCallback = callback;
            mLoadBitmap = loadBitmap;
        }

        /**
         * Creates an {@link ImageTask} to load a {@link Bitmap} for an
         * {@link ImageView} in an {@link android.widget.AdapterView}.
         */
        public ImageRequest(BaseAdapter adapter, ImageView view, String url) {
            this(url, new BaseAdapterCallback(adapter, view), true);
        }

        private Bitmap loadImageByUrl(URL url) throws IOException {
            URLConnection connection = url.openConnection();
            return (Bitmap) mBitmapContentHandler.getContent(connection);
        }

        /**
         * Executes the {@link ImageTask}.
         *
         * @return {@code true} if the result for this {@link ImageTask} should
         *         be posted, {@code false} otherwise.
         */
        public boolean execute() {
            try {
                if (mCallback != null) {
                    if (mCallback.imageRejected()) {
                        return false;
                    }
                }
                // Check if the last attempt to load the URL had an error
                mError = getError(mUrl);
                if (mError != null) {
                    return true;
                }

                // Check if the Bitmap is already cached in memory
                mBitmap = getBitmap(mUrl);
                if (mBitmap != null) {
                    // Keep a hard reference until the view has been notified.
                    return true;
                }

                String protocol = getProtocol(mUrl);
                URLStreamHandler streamHandler = getURLStreamHandler(protocol);
                URL url = new URL(null, mUrl, streamHandler);

                if (mLoadBitmap) {
                    try {
                        mBitmap = loadImageByUrl(url);
                    } catch (OutOfMemoryError e) {
                        // The VM does not always free-up memory as it should,
                        // so manually invoke the garbage collector
                        // and try loading the image again.
                        System.gc();
                        mBitmap = loadImageByUrl(url);
                    }
                    if (mBitmap == null) {
                        throw new NullPointerException("ContentHandler returned null");
                    }
                    return true;
                } else {
                    mBitmap = null;
                    return false;
                }
            } catch (IOException e) {
                mError = new ImageError(e);
                return true;
            } catch (RuntimeException e) {
                mError = new ImageError(e);
                return true;
            } catch (Error e) {
                mError = new ImageError(e);
                return true;
            }
        }

        public void publishResult() {
            if (mBitmap != null) {
                putBitmap(mUrl, mBitmap);
            } else if (mError != null && !hasError(mUrl)) {
                Log.e(TAG, "Failed to load " + mUrl, mError.getCause());
                putError(mUrl, mError);
            }
            if (mCallback != null) {
                mCallback.imageLoaded(mUrl, mBitmap, mError);
            }
        }
    }

    private interface ImageCallback {
        boolean imageRejected();
        void imageLoaded(String url, Bitmap bitmap, ImageError error);
    }

    private final class BaseAdapterCallback implements ImageCallback {
        private final ImageView mImageView;
        private final WeakReference<BaseAdapter> mAdapter;

        public BaseAdapterCallback(BaseAdapter adapter, ImageView imageView) {
            mAdapter = new WeakReference<BaseAdapter>(adapter);
            mImageView = imageView ;
        }
        
        public boolean imageRejected() {
            return mAdapter.get() == null;
        }

        public void imageLoaded(String url, Bitmap bitmap, ImageError error) {
            BaseAdapter adapter = mAdapter.get();
            if (adapter == null) {
                // The adapter is no longer in use
                return;
            }
            
            final String binding = mImageViewBinding.get(mImageView);
            
            if (!TextUtils.equals(binding, url)) {
                // The ImageView has been unbound or bound to a
                // different URL since the task was started.
                return;
            }
            
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
            
//            if (!adapter.isEmpty()) {
//                adapter.notifyDataSetChanged();
//            } else {
//                // The adapter is empty or no longer in use.
//                // It is important that BaseAdapter#notifyDataSetChanged()
//                // is not called when the adapter is empty because this
//                // may indicate that the data is valid when it is not.
//                // For example: when the adapter cursor is deactivated.
//            }
        }
    }

    private class ImageTask extends AsyncTask<ImageRequest, ImageRequest, Void> {

        public final android.os.AsyncTask<ImageRequest, ImageRequest, Void> executeOnThreadPool(
                ImageRequest... params) {
            if (Build.VERSION.SDK_INT < 4) {
                // Thread pool size is 1
                return execute(params);
            } else if (Build.VERSION.SDK_INT < 11) {
                // The execute() method uses a thread pool
                return execute(params);
            }
            return this; 
        }

        @Override
        protected void onPreExecute() {
            mActiveTaskCount++;
        }

        @Override
        protected Void doInBackground(ImageRequest... requests) {
            for (ImageRequest request : requests) {
                if (request.execute()) {
                    publishProgress(request);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ImageRequest... values) {
            for (ImageRequest request : values) {
                request.publishResult();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            mActiveTaskCount--;
            flushRequests();
        }
    }

    private static class ImageError {
        private static final int TIMEOUT = 2 * 60 * 1000; // Two minutes

        private final Throwable mCause;

        private final long mTimestamp;

        public ImageError(Throwable cause) {
            if (cause == null) {
                throw new NullPointerException();
            }
            mCause = cause;
            mTimestamp = now();
        }

        public boolean isExpired() {
            return (now() - mTimestamp) > TIMEOUT;
        }

        public Throwable getCause() {
            return mCause;
        }

        private static long now() {
            return SystemClock.elapsedRealtime();
        }
    }
}
