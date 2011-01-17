package group.jinjie.qq.net;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import framework.parser.rss.IBaseXmlParser;
import framework.parser.rss.Rss;
import framework.parser.rss.SaxXmlParser;

public class RssDownloader implements IRssDownloader {
    private ArrayList<String> mChannelTags;
    private ArrayList<String> mItemTags;
    private Context mContext;
    private Rss mRss = null;

    public RssDownloader(Context context, ArrayList<String> channelTags,
            ArrayList<String> itemTags) {
        this.mContext = context;
        this.mChannelTags = channelTags;
        this.mItemTags = itemTags;
    }

    public void downloadRssContent(String url) {
        IBaseXmlParser saxXmlParser = new SaxXmlParser();
        try {
            Log.d("AAA", "size £º " + String.valueOf(mChannelTags.size()));
            mRss = saxXmlParser.parse(new URL(url).openConnection().getInputStream(),
                    mChannelTags, mItemTags);
            Log.d("AAA", "BBBBBBBB: " + mRss.getChannels().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getRssContent() {
        return  this.mRss;
    }
}
