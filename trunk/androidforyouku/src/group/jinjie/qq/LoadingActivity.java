package group.jinjie.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LoadingActivity extends Activity implements OnClickListener {
    public static final String TAG = "LoadingActivity";
    ImageView mLogo = null;
    Button btn_youku = null;
    Button btn_tudou = null;
    Button btn_ku6 = null;

    Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case 0:
                mLogo.setImageResource(R.drawable.tudoulogo);
                mHandler.sendEmptyMessageDelayed(1, 3*1000);
                break;
            case 1:
                mLogo.setImageResource(R.drawable.ku6logo);
                mHandler.sendEmptyMessageDelayed(2, 3*1000);
                break;
            case 2:
                mLogo.setImageResource(R.drawable.youkulogo);
                mHandler.sendEmptyMessageDelayed(0, 3*1000);
                break;
            default:
                break;
            }

        }

    };
    
//    private Thread  mRefreshThread = new Thread() {
//        int i = 0;
//
//        public void run() {
//            while (!Thread.interrupted()) {
//                Log.d(TAG, "no  interrupted");
//                Message message = Message.obtain(mHandler, i++);
//                mHandler.sendMessageDelayed(message, 3*1000);
//                //mHandler.sendMessage(message);
//                if (i == 3)
//                    i = 0;
////                try {
////                    Thread.sleep(3000);
////                } catch (InterruptedException e) {
////                    // TODO Auto-generated catch block
////                    e.printStackTrace();
////                    Log.d(TAG, "interrupted");
////                }
//            }
//        }
//
//    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        mLogo = (ImageView) findViewById(R.id.logo1);
        setButton();
        mHandler.sendEmptyMessage(0);
        //mRefreshThread.start();
    }

    private void setButton() {
        btn_youku = (Button) findViewById(R.id.btn_youku);
        btn_tudou = (Button) findViewById(R.id.btn_tudou);
        btn_ku6 = (Button) findViewById(R.id.btn_ku6);
        btn_youku.setClickable(false);
        btn_youku.setOnClickListener(this);
        btn_tudou.setOnClickListener(this);
        btn_ku6.setOnClickListener(this);
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if(!mRefreshThread.isAlive())   mRefreshThread.start();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
        //mRefreshThread.interrupt();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        //mRefreshThread.interrupt();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(0);
        //if(!mRefreshThread.isAlive())   mRefreshThread.start();
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btn_youku) {
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);  
        } else if (v == btn_tudou) {
            Toast.makeText(LoadingActivity.this, "tudou", Toast.LENGTH_LONG)
                    .show();
        } else if (v == btn_ku6) {
            Toast.makeText(LoadingActivity.this, "ku6", Toast.LENGTH_LONG)
                    .show();
        }
    }
}