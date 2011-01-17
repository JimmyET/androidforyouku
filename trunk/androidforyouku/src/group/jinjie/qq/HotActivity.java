package group.jinjie.qq;

import group.jinjie.qq.view.CustomScrollView;


import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;



public class HotActivity extends Activity  {
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot);
		
		CustomScrollView parentScroll = (CustomScrollView) findViewById(R.id.parent_scroll);
		
		parentScroll.setOnTouchListener(mTopVideoListListener);
	
	}
	
	private OnTouchListener mTopVideoListListener = new OnTouchListener(){

        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            return false;
        }
	};

	

}
