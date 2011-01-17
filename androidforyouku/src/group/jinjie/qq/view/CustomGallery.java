package group.jinjie.qq.view;

import group.jinjie.qq.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CustomGallery extends Gallery implements OnItemSelectedListener {
	public static final String TAG = "CustomGallery";
	public static int mCurBtnPos = -1;
	private static final int MSG_ZOOM_IN = 1;
	private  static final long DELAY = 100;

	private View mPrev;
	private float mAnimationScale;
	private float mAnimationOffsetY;
	private int mAnimationDuration;
	
	public CustomGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomGallery);
		mAnimationScale = a.getFloat(R.styleable.CustomGallery_animationScale, 1.5f);
		mAnimationOffsetY = a.getFloat(R.styleable.CustomGallery_animationOffsetY, 0);
		mAnimationDuration = a.getInteger(R.styleable.CustomGallery_animationDuration, 500);
		a.recycle();
		setOnItemSelectedListener(this);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	    Log.d(TAG, "enter onItemSelected !!!");
	    mCurBtnPos = position;
	    if(view == null)  {
	        Log.d(TAG, "enter onItemSelected : " + position);
	        return;
	    }
            
		if (mPrev != null) {
			zoomOut();
		}
		mPrev = view;
		mGalleryHandler.removeCallbacksAndMessages(view);
		Message msg = Message.obtain(mGalleryHandler, MSG_ZOOM_IN, view);
		mGalleryHandler.sendMessageDelayed(msg, DELAY);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "onNothingSelected !!!");
		if (mPrev != null) {
			zoomOut();
			mPrev = null;
		}
	}
	
	private void zoomOut() {
		if (mGalleryHandler.hasMessages(MSG_ZOOM_IN, mPrev)) {
			mGalleryHandler.removeCallbacksAndMessages(mPrev);
		} else {
			ZoomAnimation a = (ZoomAnimation) mPrev.getAnimation();
			a.resetForZoomOut();
			mPrev.startAnimation(a);
		}
	}

	Handler mGalleryHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (msg.what == MSG_ZOOM_IN) {
				ImageView view = (ImageView) msg.obj;
				if(view == null)
				    Log.d(TAG, "NULL pointer!!!");
				Animation a = new ZoomAnimation(view, 1, mAnimationScale, mAnimationOffsetY, mAnimationDuration);
				view.startAnimation(a);
			}
		}
	};
	
	class ZoomAnimation extends Animation {
	    private float mFrom;
	    private float mTo;
	    private float mOffsetY;
	    private int mPivotX;
	    private int mPivotY;
	    private float mInterpolatedTime;

	    public ZoomAnimation(View v, float from, float to, float offsetY,
	            int duration) {
	        super();
	        mFrom = from;
	        mTo = to;
	        mOffsetY = offsetY * v.getHeight();
	        
	        // How long this animation should last.
	        setDuration(duration);
	        
	        // If true, the transformation will persist to the animation ends.
	        setFillAfter(true);
	        mPivotX = v.getWidth() / 2;
	        mPivotY = v.getHeight() / 2;
	    }

	    public void resetForZoomOut() {
	        
	        //Reset the initialization state of this animation.
	        reset(); 
	        mOffsetY = 0;
	        mFrom = mFrom + (mTo - mFrom) * mInterpolatedTime;
	        mTo = 1;
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        mInterpolatedTime = interpolatedTime;
	        float s = mFrom + (mTo - mFrom) * interpolatedTime;
	        Matrix matrix = t.getMatrix();
	        if (mOffsetY != 0) {
	            matrix.preTranslate(0, mOffsetY * interpolatedTime);
	        }
	        if (mTo == 1) {
	            matrix.preRotate(360 * interpolatedTime, mPivotX, mPivotY);
	        }
	        matrix.preScale(s, s, mPivotX, mPivotY);
	    }
	}
}
