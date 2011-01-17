package group.jinjie.qq.net;

import framework.parser.rss.Item;
import framework.parser.rss.Rss;
import group.jinjie.qq.ChannelActivity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class RssDownloaderThread extends Thread {
    private static final String TAG = "RssDownloaderThread";
    private String mUrl;
    private int msg;
    private Context mContext;
    private Handler mHandler;
    private Rss mRssContent;

    public RssDownloaderThread(Context context, Rss preRssContent, String url,
            Handler handler, int message) {
        this.mContext = context;
        this.mRssContent = preRssContent;
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
                    ChannelActivity.mChannelTags, ChannelActivity.mItemTags);
            downloader.downloadRssContent(mUrl);

            Rss tempRssContent = (Rss) downloader.getRssContent();

            // Check the downloading contents, if no data won't send message.
            if (tempRssContent == null
                    || tempRssContent.getChannels().isEmpty()
                    || tempRssContent.getChannels().get(0).getItems().isEmpty()) {
                return;
            }
            
            Log.d(TAG, "46   line!!!");

            /**
             * Check rss contents, if the value is null assign the new value,
             * otherwise add the new items in the previous rss contents.
             */
            if (mRssContent != null && !mRssContent.getChannels().isEmpty()
                    && !mRssContent.getChannels().get(0).getItems().isEmpty()) {
                for (Item tempItem : tempRssContent.getChannels().get(0)
                        .getItems()) {
                    mRssContent.getChannels().get(0).addItem(tempItem);
                }
            } else  mRssContent = tempRssContent;

            Log.d(TAG, "60   line!!!");
            mHandler.sendEmptyMessage(msg);
            Log.d(TAG, "62   line!!!");
            //this.interrupt();
        //}
    }
    
    public Rss getRssContent() {
        return mRssContent;
    }
}
