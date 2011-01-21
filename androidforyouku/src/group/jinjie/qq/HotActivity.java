package group.jinjie.qq;

import java.util.Date;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import framework.parser.rss.Rss;
import group.jinjie.qq.model.IUrlFactory;
import group.jinjie.qq.model.YouKuRelatedUrlFactory;
import group.jinjie.qq.model.YouKuTopUrlFactory;
import group.jinjie.qq.net.RssDownloaderThread;
import group.jinjie.qq.view.CustomLinearLayout;
import group.jinjie.qq.view.CustomScrollView;
import group.jinjie.qq.view.CustomScrollView.onActionUpListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HotActivity extends Activity {
    private int mWindowWidth;

    private int mMaxVelocity;

    private static final int SNAP_VELOCITY = 500;

    private static final int MAX_PAGE_NUM = 10;

    private static final int TOP_RSS_DOWNLOAD_COMPLETED = 0;

    private int mTopPageCount = 0;
    private int mTopCurrentPage = 0;

    private CustomLinearLayout mTopScrollingBalls;
    private HorizontalScrollView mTopScrollLayout;
    private boolean mTopBallIsScroll = false;

    private VelocityTracker mVelocityTracker;

    private Rss mTopRss;

    private RssDownloaderThread mTopThread;

    private IUrlFactory urlFactory;

    private LayoutInflater mInflater;

    private Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case TOP_RSS_DOWNLOAD_COMPLETED:
                mUiHandler.removeMessages(TOP_RSS_DOWNLOAD_COMPLETED);
                mTopRss = mTopThread.getRssContent();

                initTopScrollLayout();
            }
        }

    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hot);

        // Here we ignore the landscape mode.
        mWindowWidth = this.getWindow().getWindowManager().getDefaultDisplay()
                .getWidth();

        CustomScrollView parentScroll = (CustomScrollView) findViewById(R.id.parent_scroll);

        parentScroll.setOnMotionUpListener(mParentScrollListener);

        mTopScrollLayout = (HorizontalScrollView) findViewById(R.id.top_scrolling_layout);
        mTopScrollLayout.setOnTouchListener(mTopScrollLayoutListener);

        // mTopScrollingBalls.initRollingBallCount(10);

        final ViewConfiguration cfg = ViewConfiguration.get(this);
        mMaxVelocity = cfg.getScaledMaximumFlingVelocity();

        urlFactory = new YouKuTopUrlFactory("1");
        String rssUrl = urlFactory.newUrlInstance();

        mTopThread = new RssDownloaderThread(this, mTopRss, rssUrl, mUiHandler,
                TOP_RSS_DOWNLOAD_COMPLETED);
        mTopThread.start();
    }

    private void initTopScrollLayout() {

        mInflater = LayoutInflater.from(HotActivity.this);
        // init top scroll layout
        int topScrollLayoutThreshold = MAX_PAGE_NUM;
        int recommandRssInitSize = mTopRss.getChannels().get(0).getItems()
                .size();
        if (recommandRssInitSize < topScrollLayoutThreshold) {
            topScrollLayoutThreshold = recommandRssInitSize;
        }

        mTopPageCount = topScrollLayoutThreshold;
        // init the scrolling balls
        mTopScrollingBalls = (CustomLinearLayout) findViewById(R.id.top_scrolling_balls);
        mTopScrollingBalls.initRollingBallCount(topScrollLayoutThreshold);

        LinearLayout topContainer = (LinearLayout) findViewById(R.id.top_container);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                mWindowWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int p = 0; p < topScrollLayoutThreshold; p++) {
            View v = mInflater.inflate(R.layout.top_hotitem, null);

            topContainer.addView(v, params);
        }

    }

    private CustomScrollView.onActionUpListener mParentScrollListener = new onActionUpListener() {

        @Override
        public void onMotionUp() {
            if (mTopScrollLayout != null) {
                if (mTopScrollLayout.getScrollX() % mWindowWidth != 0)
                    mTopScrollLayout.smoothScrollTo(mTopCurrentPage
                            * mWindowWidth, 0);
            }
        }
    };

    private View.OnTouchListener mTopScrollLayoutListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();

            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }

            mVelocityTracker.addMovement(event);

            switch (action) {
            case MotionEvent.ACTION_UP: {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                int velocityX = (int) velocityTracker.getXVelocity();
                int scrollX = mTopScrollLayout.getScrollX();
                final int deltaX = (int) (scrollX - mTopCurrentPage
                        * mWindowWidth);

                if (((velocityX > SNAP_VELOCITY) || (deltaX < -mWindowWidth / 3))
                        && mTopCurrentPage > 0) {
                    mTopCurrentPage -= 1;
                    mTopBallIsScroll = true;
                } else if (((velocityX < -SNAP_VELOCITY) || (deltaX > mWindowWidth / 3))
                        && mTopCurrentPage < (mTopPageCount - 1)) {
                    mTopCurrentPage += 1;
                    mTopBallIsScroll = true;
                }
                mTopScrollLayout.smoothScrollTo(mTopCurrentPage * mWindowWidth,
                        0);
                if (mTopBallIsScroll) {
                    mTopScrollingBalls.updateImageViewPosition(mTopCurrentPage);
                    mTopBallIsScroll = false;
                }

                mTopScrollingBalls.updateImageViewPosition(mTopCurrentPage);
            }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                return true;
            }
            return false;
        }
    };

}
