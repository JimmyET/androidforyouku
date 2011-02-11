package group.jinjie.qq.view;

import framework.parser.rss.Rss;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.HorizontalScrollView;

public class HorizontalScrollLayout extends HorizontalScrollView {

    private GestureDetector mGestureDetector;
    private View.OnTouchListener mGestureListener;
    private static final int SWIPE_MIN_DISTANCE = 5;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int mActiveFeature = 0;
    private int mPageSzie;
    private Rss mRssContent;

    public HorizontalScrollLayout(Context context, Rss rssConent, int size) {
        super(context);

        this.mRssContent = rssConent;
        this.mPageSzie = 10;

        mGestureDetector = new GestureDetector(new MyGestureDetector());
        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    int scrollX = getScrollX();
                    int featureWidth = getMeasuredWidth();
                    mActiveFeature = ((scrollX + (featureWidth / 2)) / featureWidth);
                    int scrollTo = mActiveFeature * featureWidth;
                    smoothScrollTo(scrollTo, 0);
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    public HorizontalScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            try {
                // right to left
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mActiveFeature = (mActiveFeature < (mPageSzie - 1)) ? mActiveFeature + 1
                            : mPageSzie - 1;
                    smoothScrollTo(mActiveFeature * getMeasuredWidth(), 0);
                    return true;
                }
                // left to right
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mActiveFeature = (mActiveFeature > 0) ? mActiveFeature - 1 : 0;
                    smoothScrollTo(mActiveFeature * getMeasuredWidth(), 0);
                    return true;
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }

}
