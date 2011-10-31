package group.jinjie.qq.net;

import framework.parser.rss.Item;
import framework.parser.rss.Rss;
import group.jinjie.qq.ChannelActivity;
import group.jinjie.qq.HotActivity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class RssDownloaderThread extends Thread {
    private static final String TAG = "RssDownloaderThread";
    private String mUrl;
    private int msg;
    private Context mContext;
    private Handler mHandler;

    public RssDownloaderThread(Context context, String url,
            Handler handler, int message) {
        this.mContext = context;
        this.mUrl = url;
        this.mHandler = handler;
        this.msg = message;
    }

    public void run() {
        /**
         * This thread downloads the RSS contents from the given URL. At last it
         * will return Rss data.
         */
        //while (!Thread.interrupted()) {
            IRssDownloader downloader = new RssDownloader(mContext,
                    HotActivity.mChannelTags, HotActivity.mItemTags);
            downloader.downloadRssContent(mUrl);

            Rss tempRssContent = (Rss) downloader.getRssContent();

            // Check the downloading contents, if no data won't send message.
            if (tempRssContent == null
                    || tempRssContent.getChannels().isEmpty()
                    || tempRssContent.getChannels().get(0).getItems().isEmpty()) {
                return;
            }
            
            Log.d(TAG, "46   line!!!");

            Log.d(TAG, "60   line!!!");
           // mHandler.sendEmptyMessage(msg);
            mHandler.obtainMessage(msg, tempRssContent).sendToTarget();
            Log.d(TAG, "62   line!!!");
            //this.interrupt();
        //}
    }
}
