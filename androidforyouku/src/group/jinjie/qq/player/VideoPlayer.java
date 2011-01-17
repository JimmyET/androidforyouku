package group.jinjie.qq.player;

import group.jinjie.qq.R;
import group.jinjie.qq.view.CustomVideoView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VideoPlayer extends Activity {

	private static int position = 0;
	private boolean playover = true;

	CustomVideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.player);
		Intent i = this.getIntent();
		String uri = i.getStringExtra("mediaLink")==null? "":i.getStringExtra("mediaLink");
		Log.i("videoUri",uri);
		play(uri);
	}

	private void play(String uri) {

		videoView = (CustomVideoView) findViewById(R.id.videoPlayer);
		videoView.setVideoURI(Uri.parse(uri));
		MediaController mc = new MediaController(this);
		videoView.setMediaController(mc);
		videoView.setKeepScreenOn(true);
		final Activity activity = this;
		
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			public void onPrepared(MediaPlayer arg0) {
				
				ProgressBar pb = (ProgressBar) activity
						.findViewById(R.id.ProgressBar01);
				pb.setVisibility(ProgressBar.INVISIBLE);
			}

		});

		videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.d("onError", "in onError!");
				playover = false;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage("Do you want to exit?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										
										activity.finish();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();

				return false;
			}
		});
		
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		{
			public void onCompletion (MediaPlayer mp) 
			{
				if (playover) // video end because of error also will call back onCompletion
				{
//					Toast.makeText(getApplicationContext(), "Video is over, back...", TOAST_DELAYED_TIME).show();
					//triggerTimer();
					mp.stop();
					VideoPlayer.this.finish();
				}
				
			}
		});
		
		videoView.start();
		new BufferShow(this).start();
	}
	
	public void onResume() {
		super.onResume();
		Log.d("VideoPlayer", "onResume : " + Integer.toString(position));
		videoView.seekTo(position);
		videoView.start();
	}
	
	public void onPause() {
		super.onPause();
		videoView.pause();
		position = videoView.getCurrentPosition();
		Log.d("VideoPlayer", "onPause : " + Integer.toString(position));
	}
	
	public void onStop() {
		super.onStop();
	}
	
	public void onDestroy() {
		super.onDestroy();
		position = 0;
	}
   
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "FullScreen")
                .setIcon(android.R.drawable.ic_menu_manage);   //use system icon
        
        menu.add(0, 2, 0, "Default")
        .setIcon(android.R.drawable.ic_menu_manage);   //use system icon

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
            	videoView.setFullScreen(true);
            	Log.i("ABCD", "FullScreen");
                return true; 
                
            case 2:
            	videoView.setFullScreen(false);
            	Log.i("ABCD", "Default");
            	return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class BufferShow extends Thread{
	
	private Activity videoPlayer;
	public BufferShow(Activity videoPlayer){
		this.videoPlayer = videoPlayer;
	}
	public final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            
            case 100:
            	handler.removeMessages(1);
            	updatePercentage();
            	Message new_msg = Message.obtain();	
            	new_msg.what = 100;
            	if(!((VideoPlayer)videoPlayer).videoView.isPlaying()){
            		handler.sendMessage(new_msg);
            	}else{
            		TextView percentageTextView = (TextView) videoPlayer.findViewById(R.id.bufferedTextView);
            		percentageTextView.setText("");
            	}
            	break;
            }
        }
    };
	
	public void run(){
		Message msg = new Message();
		msg.what = 100;
		handler.sendMessage(msg);
	}
	
	public void updatePercentage(){
		TextView percentageTextView = (TextView) videoPlayer.findViewById(R.id.bufferedTextView);
		percentageTextView.setText(videoPlayer.getString(R.string.buffered_string,new Object[]{((VideoPlayer)videoPlayer).videoView.getBufferPercentage()}));
	}
}
