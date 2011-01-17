package group.jinjie.qq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView{

    private GestureDetector mGestureDetector;
    private final float XYLIMIT = 5.0f;
    
    public CustomScrollView(Context context) {
        super(context);
        init();
    }
    
    public CustomScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }
    
    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override 
    public boolean onInterceptTouchEvent(MotionEvent ev) { 
        super.onInterceptTouchEvent(ev); 
        
        return mGestureDetector.onTouchEvent(ev);
    } 

    @Override 
    public boolean onTouchEvent(MotionEvent ev) { 
        boolean result = super.onTouchEvent(ev); 
        
        final int action = ev.getAction();
        switch (action) {
        case MotionEvent.ACTION_UP:
            if (mOnMotionUpListener != null)
                mOnMotionUpListener.onMotionUp();
            }
        
        return result;
    }
    
    private void init(){
        if (mGestureDetector == null)
            mGestureDetector = new GestureDetector(new MyGesture());
    }
    
    private class MyGesture extends GestureDetector.SimpleOnGestureListener{
        @Override 
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { 
            if (Math.abs(distanceX) < XYLIMIT && Math.abs(distanceY) > XYLIMIT)
                return true; 
                
            return false; 
        } 
    }
    
    public interface onMotionUpListener{
        void onMotionUp();
    }
    
    private onMotionUpListener mOnMotionUpListener;

    public void setOnMotionUpListener(onMotionUpListener l){
        mOnMotionUpListener = l;
    }
}
