package group.jinjie.qq.view;

import java.util.ArrayList;

import group.jinjie.qq.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout{
    
    private Context mContext;
    private int mImageViewCount = 0;
    private int mImageViewPosition = 0;
    private ArrayList<ImageView> mImageViewList = new ArrayList<ImageView>();

    public CustomLinearLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }
    
    public CustomLinearLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        init();
    }
    
    private void init(){
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.setGravity(Gravity.CENTER);
        
        mImageViewList.clear();
        for (int i = 0; i < mImageViewCount; i++){
            ImageView v = new ImageView(mContext);
            mImageViewList.add(v);
            
            v.setScaleType(ImageView.ScaleType.FIT_XY);
            if (i == 0)
                v.setImageResource(R.drawable.rolling_ball_selected);
            else
                v.setImageResource(R.drawable.rolling_ball);
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            this.addView(v, lp);
        }
    }

    public void initRollingBallCount(int i){
        if (i < 0)
            return;
        
        mImageViewCount = i;
        //init();
    }
    
    public void updateImageViewPosition(int p){
        if (p > mImageViewCount-1)
            return;
        
        mImageViewList.get(mImageViewPosition).setImageResource(R.drawable.rolling_ball);
        mImageViewPosition = p;
        mImageViewList.get(mImageViewPosition).setImageResource(R.drawable.rolling_ball_selected);
    }
    
    public int getImageViewPosition(){
        return mImageViewPosition;
    }
}
