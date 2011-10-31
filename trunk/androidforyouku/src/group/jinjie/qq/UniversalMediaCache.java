
package group.jinjie.qq;

import com.ctestore.core.filecache.FileResponseCache;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.ResponseCache;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class UniversalMediaCache extends FileResponseCache {

    private static final String TAG = "JamendoCache";

    public static void install(Context context) {
        ResponseCache responseCache = ResponseCache.getDefault();
        if (responseCache instanceof UniversalMediaCache) {
            Log.d(TAG, "Cache has already been installed.");
        } else if (responseCache == null) {
            UniversalMediaCache dropCache = new UniversalMediaCache(context);
            ResponseCache.setDefault(dropCache);
        } else {
            Class<? extends ResponseCache> type = responseCache.getClass();
            Log.e(TAG, "Another ResponseCache has already been installed: " + type);
        }
    }

    private static File getCacheDir(Context context) {
        //File dir = context.getCacheDir();
        //dir = new File(dir, "filecache");
        String path = null;
        
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        
        File outFile = new File(path+"/CTestore/images/");
        if(!outFile.exists()) outFile.mkdirs();
        return outFile;
    }

    private final Context mContext;

    public UniversalMediaCache(Context context) {
        mContext = context;
    }
    
    @Override
    protected boolean isStale(File file, URI uri, String requestMethod,
            Map<String, List<String>> requestHeaders, Object cookie) {
        android.util.Log.d("age", "++++++++++++++++=");
        if (cookie instanceof Long) {
            android.util.Log.d("age", "---------------------");
            Long maxAge = (Long) cookie;
            android.util.Log.d("age", "1: "+maxAge);
            long age = System.currentTimeMillis() - file.lastModified();
            if (age > maxAge.longValue()) {
                return true;
            }
        }
        return super.isStale(file, uri, requestMethod, requestHeaders, cookie);
    }

    @Override
    protected File getFile(URI uri, String requestMethod, Map<String, List<String>> requestHeaders,
            Object cookie) {
        try {
            File parent = getCacheDir(mContext);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(String.valueOf(uri).getBytes("UTF-8"));
            byte[] output = digest.digest();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < output.length; i++) {
                builder.append(Integer.toHexString(0xFF & output[i]));
            }
            String filename = builder.toString();
            return new File(parent, filename);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
