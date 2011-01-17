package group.jinjie.qq;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;


import framework.parser.rss.Rss;
import group.jinjie.qq.adapter.ChannelAdapter;
import group.jinjie.qq.adapter.ChannelItemAdapter;
import group.jinjie.qq.model.IUrlFactory;
import group.jinjie.qq.model.YouKuCategoryUrlFactory;
import group.jinjie.qq.net.RssDownloaderThread;
import group.jinjie.qq.util.ResourceUtility;
import group.jinjie.qq.view.CustomGallery;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Gallery;
import android.widget.ListView;

public class ChannelActivity extends Activity implements View.OnTouchListener,
        OnScrollListener {
    private static final String TAG = "ChannelActivity";
    public static int[] mChannelButtonDefaultSize = { 0, 0 };

    private static final int[] CHANNEL_BUTTON_WVGA_SIZE = { 100, 100 };
    private static final int[] CHANNEL_BUTTON_HVGA_SIZE = { 100, 60 };
    private static final int[] CHANNEL_BUTTON_QVGA_SIZE = { 70, 50 };

    private static final int SCREEN_WVGA_WIDTH = 480;
    private static final int SCREEN_QVGA_WIDTH = 240;

    private static final int RSS_DOWNLOAD_BEGIN = 0;
    private static final int RSS_DOWNLOAD_COMPLETED = 1;
    private static final int RSS_DOWNLOAD_REFRESHED = 2;
    private static final long mDelayMillis = 100;

    public static ArrayList<String> mChannelTags = new ArrayList<String>();
    public static ArrayList<String> mItemTags = new ArrayList<String>();
    private ArrayList<String> mCidTags = new ArrayList<String>();

    private Gallery mChannelButtons;
    private ListView mChannelItems;

    private ChannelAdapter mChanneAdapter;
    private ChannelItemAdapter mChannelItemAdapter;
    private LayoutInflater mInflater;
    
    private  View mViewFooter;

    private Rss mRssContent = null;

    private int mPreBtnPos = -1;

    private int mPageNumber = 2;
    private int mLastItem = 0;
    
    private IUrlFactory urlFactory;
    private String mBaseCategoryUrl;

    private boolean isRefreshing = false;

    private RssDownloaderThread rssDownloaderThread = null;

    /** RSS_DOWNLOAD_BEGIN: invoked by switching channel buttons
     *  RSS_DOWNLOAD_COMPLETED : sent by each button first downloading
     *  RSS_DOWNLOAD_REFRESHED: sent by item list scrolling
     */ 
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case RSS_DOWNLOAD_BEGIN:
                //avoid duplicated downloading
                if (mPreBtnPos == CustomGallery.mCurBtnPos) {
                    break;
                }
                
                //clean the previous rss contents
                if (mRssContent != null)    mRssContent = null;
                
                mHandler.removeMessages(RSS_DOWNLOAD_BEGIN);
                
                //IUrlFactory urlFactory = new YouKuCategoryUrlFactory(mCidTags.get(CustomGallery.mCurBtnPos), "1");
                String url = urlFactory.newUrlInstance();
                
//                if(rssDownloaderThread != null && rssDownloaderThread.isAlive()) {
//                    rssDownloaderThread.interrupt();
//                    rssDownloaderThread = null;
//                }
                
                rssDownloaderThread = new RssDownloaderThread(
                        ChannelActivity.this, mRssContent,  
                        updateCategoryUrl(mBaseCategoryUrl, mCidTags.get(CustomGallery.mCurBtnPos), "1"), 
                        mHandler, RSS_DOWNLOAD_COMPLETED);
                rssDownloaderThread.start();

                break;

            case RSS_DOWNLOAD_COMPLETED:
                Log.d("BBB", "111");
                mHandler.removeMessages(RSS_DOWNLOAD_COMPLETED);
                mRssContent = rssDownloaderThread.getRssContent();
                mChannelItemAdapter = new ChannelItemAdapter(
                        ChannelActivity.this, mChannelItems, rssDownloaderThread.getRssContent());
                mChannelItems.setAdapter(mChannelItemAdapter);
                break;

            case RSS_DOWNLOAD_REFRESHED:
                mHandler.removeMessages(RSS_DOWNLOAD_REFRESHED);
                mChannelItems.removeFooterView(mViewFooter);
                mRssContent = rssDownloaderThread.getRssContent();
                mChannelItemAdapter.notifyDataSetChanged();
                isRefreshing = false;
                break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_categories);

        mChannelButtons = (Gallery) findViewById(R.id.channel_category);

        initChannelButtonLayout();

        mChanneAdapter = new ChannelAdapter(this);
        mChannelButtons.setAdapter(mChanneAdapter);

        //Get youku url cid (category id)
        mCidTags.clear();
        ResourceUtility.getConfigTags(this, mCidTags, R.array.cid_tags);

        //Get channel and item tag values for rss parsing
        mChannelTags.clear();
        mItemTags.clear();
        ResourceUtility.getConfigTags(this, mChannelTags, mItemTags,
                R.array.channel_tags, R.array.item_tags);

        mChannelItems = (ListView) findViewById(R.id.item_list);

        mInflater = LayoutInflater.from(this);
        mViewFooter = mInflater.inflate(R.layout.list_footer, null);
        
        mChannelButtons.setOnTouchListener(this);

        mChannelItems.setOnScrollListener(this);

        urlFactory = new YouKuCategoryUrlFactory(mCidTags.get(0), "1");

        mBaseCategoryUrl = urlFactory.newUrlInstance();

        //The default downloading when you go into the channel activity at first.
        rssDownloaderThread = new RssDownloaderThread(this,
                mRssContent, mBaseCategoryUrl, mHandler, RSS_DOWNLOAD_COMPLETED);
        rssDownloaderThread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        //use flurry library to track the user data
        FlurryAgent.onStartSession(this, "ID8BVRFNSATW8QU7D4MX");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
//        if(rssDownloaderThread != null && rssDownloaderThread.isAlive()) {
//            rssDownloaderThread.interrupt();
//            rssDownloaderThread = null;
//        }
//            
        Log.i("ChannelActivity","onPause()!!!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
        mPreBtnPos = CustomGallery.mCurBtnPos;

        switch (paramMotionEvent.getAction()) {
        case MotionEvent.ACTION_UP:
            mHandler.sendEmptyMessageDelayed(RSS_DOWNLOAD_BEGIN, mDelayMillis);
            break;
        }

        Log.d(TAG, "kkk");
        return false;
    }

    public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
        if(mRssContent == null || mRssContent.getChannels().isEmpty())     return;
        if ( mLastItem == mRssContent.getChannels().get(0).getItems().size() &&
                paramInt == OnScrollListener.SCROLL_STATE_IDLE) {

            Log.d(TAG, "bbb");

            //Check the page number to the parameter "pn" in channel.
            if (mPageNumber > Integer.valueOf(mRssContent.getChannels().get(0)
                    .getValues().get(3)))   return;

            //If current thread is working, ignore the scrolling.
            if (!isRefreshing) {
//                IUrlFactory urlFactory = new YouKuCategoryUrlFactory(
//                        mCidTags.get(CustomGallery.mCurBtnPos), String.valueOf(mPageNumber++));
//                String url = urlFactory.newUrlInstance();
                
//                if(rssDownloaderThread != null && rssDownloaderThread.isAlive()) {
//                    rssDownloaderThread.interrupt();
//                    rssDownloaderThread = null;
//                }

                rssDownloaderThread = new RssDownloaderThread(
                        this, mRssContent, 
                        updateCategoryUrl(mBaseCategoryUrl, mCidTags.get(CustomGallery.mCurBtnPos), 
                                String.valueOf(mPageNumber++)), 
                        mHandler, RSS_DOWNLOAD_REFRESHED);
                rssDownloaderThread.start();

                isRefreshing = true;
            }
        }

    }

    //This will be called after the scroll has completed.
    public void onScroll(AbsListView paramAbsListView, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        Log.d(TAG, "CCC" + ": " + firstVisibleItem + ": " + visibleItemCount
                + ": " + totalItemCount);

        if(mChannelItems.getFooterViewsCount() == 0) {
            mChannelItems.addFooterView(mViewFooter);
        }
        
        mLastItem = firstVisibleItem + visibleItemCount - 1;
    }

    public void initChannelButtonLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        Log.d(TAG, "## dm.widthPixels: " + dm.widthPixels);

        /*
         * Set the layout parameters of images in Gallery. Here three groups of
         * data can adapt to sorts of devices screens.
         */
        if (width >= SCREEN_WVGA_WIDTH) {
            mChannelButtonDefaultSize[0] = CHANNEL_BUTTON_WVGA_SIZE[0];
            mChannelButtonDefaultSize[1] = CHANNEL_BUTTON_WVGA_SIZE[1];
        } else if (width <= SCREEN_QVGA_WIDTH) {
            mChannelButtonDefaultSize[0] = CHANNEL_BUTTON_QVGA_SIZE[0];
            mChannelButtonDefaultSize[1] = CHANNEL_BUTTON_QVGA_SIZE[1];
        } else {
            mChannelButtonDefaultSize[0] = CHANNEL_BUTTON_HVGA_SIZE[0];
            mChannelButtonDefaultSize[1] = CHANNEL_BUTTON_HVGA_SIZE[1];
        }
    }
    
    public synchronized String  updateCategoryUrl(String url, String newCid, String newPg) {
        String newUrl = url;
        
        String[] strs = newUrl.substring(newUrl.indexOf("cid=")).split("&");
        newUrl = newUrl.replace(strs[0], "cid="+newCid);
        newUrl = newUrl.replace(strs[strs.length - 1], "pg="+newPg);
        
        return newUrl;
    }
}
