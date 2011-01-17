package group.jinjie.qq;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class MainActivity extends TabActivity implements OnTabChangeListener{
    protected static final int TIMEPICKER_ID = 1;
    private TabHost mTabHost = null;
    private Button mButtonTimePicker = null;
    private ImageButton mButtonLoginout = null;
    private ImageButton mButtonSearch = null;
    private OnClickListener mOnClickListener = null;
    private TextView mTitleText = null;
    private final CharSequence[] items = {"今天", "本周", "本月"}; 
    private LayoutInflater mInflater;
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case TIMEPICKER_ID:
            View viewTitle = mInflater.inflate(R.layout.timepicker_alertdialog_title, null);
            return new AlertDialog.Builder(this)
            .setCustomTitle(viewTitle)
            .setItems(items,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mButtonTimePicker.setText(items[which]);
                        }
                    }).create();
        }
        return null;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);
        mInflater = LayoutInflater.from(this);
        
        mButtonTimePicker = (Button) findViewById(R.id.btn_timepicker);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mButtonLoginout = (ImageButton) findViewById(R.id.btn_loginout);
        mButtonSearch = (ImageButton) findViewById(R.id.btn_search);

        mTitleText.setText("aaa");
        mButtonSearch.setVisibility(View.INVISIBLE);

        mButtonTimePicker.setVisibility(View.VISIBLE);
        mButtonTimePicker.setTextSize(15);
        mButtonTimePicker.setText(items[0]);

        mTabHost = getTabHost();
        Resources res = getResources(); 
        TabHost.TabSpec spec; 
        Intent intent; 

        //initialize tabs and tab intents
        intent = new Intent().setClass(this, HotActivity.class);
        spec = mTabHost.newTabSpec("HOT").setIndicator(res.getString(R.string.hot),
                res.getDrawable(R.drawable.tab_icon_hot)).setContent(
                intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, ChannelActivity.class);
        spec = mTabHost.newTabSpec("CHANNEL").setIndicator(res.getString(R.string.channel),
                res.getDrawable(R.drawable.tab_icon_channels)).setContent(
                intent);
        mTabHost.addTab(spec);
  
        intent = new Intent().setClass(this, HotActivity.class);
        spec = mTabHost.newTabSpec("FAVORITE").setIndicator(res.getString(R.string.favorite),
                res.getDrawable(R.drawable.tab_icon_favorite)).setContent(
                intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, HotActivity.class);
        spec = mTabHost.newTabSpec("UPLOAD").setIndicator(res.getString(R.string.upload),
                res.getDrawable(R.drawable.tab_icon_uploading)).setContent(
                intent);
        mTabHost.addTab(spec);

        
        intent = new Intent().setClass(this, HotActivity.class);
        spec = mTabHost.newTabSpec("ABOUT").setIndicator(res.getString(R.string.about),
                res.getDrawable(R.drawable.tab_icon_about)).setContent(
                intent);
        mTabHost.addTab(spec);

        // set background for each tab
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundDrawable(
                    res.getDrawable(R.drawable.tab_selector_bg));
        }

        //set the default current tab
        mTabHost.setCurrentTab(0);
        mTabHost.setPadding(0, 0, 0, -5);
        //LayoutInflater.from(this).inflate(R.layout.main, mTabHost.getTabContentView(), true);
        this.mTabHost.setOnTabChangedListener(this);

        mOnClickListener = new OnClickListener() {

            public void onClick(View arg0) {
                switch (arg0.getId()) {
                case R.id.btn_loginout:

                    break;
                case R.id.btn_search:
                    Toast.makeText(getApplicationContext(),
                            "button clicked", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.btn_timepicker:
                    showDialog(TIMEPICKER_ID);
                    break;
                }
            }
        };

        mButtonTimePicker.setOnClickListener(mOnClickListener);
        mButtonSearch.setOnClickListener(mOnClickListener);
        mButtonLoginout.setOnClickListener(mOnClickListener);
    }

    public void onTabChanged(String tabId) {
        if (tabId.equals(new String("HOT"))) {
            mTitleText.setVisibility(View.VISIBLE);
            mButtonTimePicker.setVisibility(View.VISIBLE);
            mButtonSearch.setVisibility(View.INVISIBLE);
        }
        if (tabId.equals(new String("CHANNEL"))) {
            mTitleText.setText(getResources().getString(R.string.channel));
            mTitleText.setVisibility(View.VISIBLE);
            mButtonTimePicker.setVisibility(View.INVISIBLE);
            mButtonSearch.setVisibility(View.VISIBLE);
        }

        if (tabId.equals(new String("FAVORITE"))) {
            mTitleText.setText(getResources().getString(R.string.favorite));
            mTitleText.setVisibility(View.VISIBLE);
            mButtonTimePicker.setVisibility(View.INVISIBLE);
            mButtonSearch.setVisibility(View.VISIBLE);
        }

        if (tabId.equals(new String("UPLOAD"))) {
            mTitleText.setText(getResources().getString(R.string.upload));
            mTitleText.setVisibility(View.VISIBLE);
            mButtonTimePicker.setVisibility(View.INVISIBLE);
            mButtonSearch.setVisibility(View.VISIBLE);
        }
        if (tabId.equals(new String("ABOUT"))) {
            mTitleText.setText(getResources().getString(R.string.about));
            mTitleText.setVisibility(View.VISIBLE);
            mButtonTimePicker.setVisibility(View.INVISIBLE);
            mButtonSearch.setVisibility(View.INVISIBLE);
        }
    }
}
