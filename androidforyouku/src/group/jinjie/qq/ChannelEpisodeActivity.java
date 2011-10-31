package group.jinjie.qq;

import framework.parser.rss.Rss;
import group.jinjie.qq.adapter.ChannelItemAdapter;
import group.jinjie.qq.model.IUrlFactory;
import group.jinjie.qq.model.YouKuRelatedUrlFactory;
import group.jinjie.qq.net.ImageDownloader;
import group.jinjie.qq.net.RssDownloaderThread;
import group.jinjie.qq.net.ImageDownloader.ImageDownloaderCallback;
import group.jinjie.qq.player.VideoPlayer;
import group.jinjie.qq.view.EpisodeFlipAnimation;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChannelEpisodeActivity extends Activity {

    private static final int RSS_DOWNLOAD_COMPLETED = 0;
    protected static final int RSS_DOWNLOAD_REFRESHED = 1;
    private Rss mRssContent = null;
    private IUrlFactory urlFactory;
    private ChannelItemAdapter mChannelItemAdapter;
    private ListView relatedVideoList;
    private RssDownloaderThread rssDownloaderThread;
    
    private ImageView animImageView;
    private ViewGroup mContainer;
    private ViewGroup mListHeader;
    
    private boolean mIsCurrentImageview = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            // case RSS_DOWNLOAD_BEGIN:
            // //avoid duplicated downloading
            // if (mPreBtnPos == CustomGallery.mCurBtnPos) {
            // break;
            // }
            //
            // //clean the previous rss contents
            // if (mRssContent != null) mRssContent = null;
            //
            // mHandler.removeMessages(RSS_DOWNLOAD_BEGIN);
            // String url = urlFactory.newCategoryUrlInstance().getCategoryUrl(
            // mCidTags.get(CustomGallery.mCurBtnPos),
            // "1");
            // RssDownloaderThread rssDownloaderThread = new
            // RssDownloaderThread(
            // ChannelActivity.this, mRssContent, url, mHandler,
            // RSS_DOWNLOAD_COMPLETED);
            // rssDownloaderThread.start();
            //
            // break;

            case RSS_DOWNLOAD_COMPLETED:
                Log.d("BBB", "111");
//                mHandler.removeMessages(RSS_DOWNLOAD_COMPLETED);
//                mChannelItemAdapter = new ChannelItemAdapter(
//                        ChannelEpisodeActivity.this, relatedVideoList,
//                        rssDownloaderThread.getRssContent());
//                relatedVideoList.setAdapter(mChannelItemAdapter);
                break;
            }

        }
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		
		//String desc = intent.getStringExtra("desc");
		//String info = intent.getStringExtra("info");
		//String title = intent.getStringExtra("title");
		
		String vtime = intent.getStringExtra("vtime");
		String vtimes = intent.getStringExtra("vtimes");
		String vtitle = intent.getStringExtra("vtitle");	
		
		String imageLink = intent.getStringExtra("imageLink");
		final String mediaLink = intent.getStringExtra("mediaLink");
		String vid = intent.getStringExtra("vid");
		Log.i("youku",vtime);
		this.setContentView(R.layout.channel_episode);
		
//		TextView descTextView = (TextView)this.findViewById(R.id.description);
//		descTextView.setText(desc);
//		
//		TextView titleTextView = (TextView)this.findViewById(R.id.episode_title_text);
//		titleTextView.setText(title);
//		
//		TextView infoTextView = (TextView)this.findViewById(R.id.episode_info_text);
//		infoTextView.setText(info);
		
		TextView vtimeTextView = (TextView)this.findViewById(R.id.vtime);
		vtimeTextView.setText(vtime);
		
		TextView vtimesTextView = (TextView)this.findViewById(R.id.vtimes);
		vtimesTextView.setText(vtimes);
		
		TextView vtitleTextView = (TextView)this.findViewById(R.id.vtitle);
		vtitleTextView.setText(vtitle);		
		
//		relatedVideoList = (ListView) this.findViewById(R.id.episode_item_list);
//		
//		urlFactory = new YouKuUrlFactory();
//		String rssUrl = urlFactory.newRelatedUrlInstance().getCategoryUrl(vid, "");
//		
//		rssDownloaderThread = new RssDownloaderThread(
//		        this, mRssContent,  rssUrl, mHandler, RSS_DOWNLOAD_COMPLETED);
//		rssDownloaderThread.start();
//		
//		final ImageView imageView = (ImageView) this.findViewById(R.id.episode_image);
		ImageView pImageView = (ImageView) this.findViewById(R.id.play_image);
//		
//		Bitmap mBitmap = ImageDownloader.loadImage(this, imageLink, 0, 0,
//				new ImageDownloaderCallback() {
//>>>>>>> .r48

        // String desc = intent.getStringExtra("desc");
        // String info = intent.getStringExtra("info");
        // String title = intent.getStringExtra("title");
       // String imageLink = intent.getStringExtra("imageLink");
        //final String mediaLink = intent.getStringExtra("mediaLink");
        //String vid = intent.getStringExtra("vid");

        //this.setContentView(R.layout.channel_episode);

        // TextView descTextView =
        // (TextView)this.findViewById(R.id.description);
        // descTextView.setText(desc);
        //
        // TextView titleTextView =
        // (TextView)this.findViewById(R.id.episode_title_text);
        // titleTextView.setText(title);
        //
        // TextView infoTextView =
        // (TextView)this.findViewById(R.id.episode_info_text);
        // infoTextView.setText(info);
        relatedVideoList = (ListView) this.findViewById(R.id.episode_item_list);
        
        mContainer = (ViewGroup) findViewById(R.id.episode_animation_container);
        
        animImageView = (ImageView) this.findViewById(R.id.anim_iamgeview);
        
        mListHeader = (ViewGroup) findViewById(R.id.list_header);

        urlFactory = new YouKuRelatedUrlFactory(vid);
        String rssUrl = urlFactory.newUrlInstance();

        rssDownloaderThread = new RssDownloaderThread(this,
                rssUrl, mHandler, RSS_DOWNLOAD_COMPLETED);
        rssDownloaderThread.start();

        final ImageView imageView = (ImageView) this
                .findViewById(R.id.episode_image);
        //Button pImageView = (Button) this.findViewById(R.id.play_image);
        //pImageView.setText("����");

        Bitmap mBitmap = ImageDownloader.loadImage(this, imageLink, 0, 0,
                new ImageDownloaderCallback() {

                    public void imageLoaded(Bitmap bitmap, int channelId,
                            int itemId, String imageUrl) {
                        imageView.setImageBitmap(bitmap);
                        animImageView.setImageBitmap(bitmap);
                    }
                });
        if (mBitmap != null) {
            imageView.setImageBitmap(mBitmap);
            animImageView.setImageBitmap(mBitmap);
        } else {
            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory
                    .decodeResource(getResources(),
                            R.drawable.item_default_icon), 110, 80, true);
            imageView.setImageBitmap(bitmap);
            animImageView.setImageBitmap(bitmap);
        }

        pImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // String imageLink = (String) v.getTag(R.id.imageLink);
                Intent intent = new Intent(ChannelEpisodeActivity.this,
                        VideoPlayer.class);
                intent.putExtra("mediaLink", mediaLink);
                startActivity(intent);
            }
        });

        // relatedVideoList.setAdapter(mChannelItemAdapter);
        // imageView.setTag(R.id.mediaLink, mediaLink);
        // imageView.setOnClickListener(new View.OnClickListener() {
        // public void onClick(View v) {
        // Intent intent = new Intent(ChannelEpisodeActivity.this,
        // VideoPlayer.class);
        // intent.putExtra("uri", v.getTag(R.id.mediaLink).toString());
        // ChannelEpisodeActivity.this.startActivity(intent);
        // }
        // });

//        
//        animImageView.
        
        imageView.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                if(mIsCurrentImageview) {
                    applyRotation(0, 90);
                    mIsCurrentImageview = !mIsCurrentImageview;
                } else {
                    applyRotation(0, -90);
                    mIsCurrentImageview = !mIsCurrentImageview;
                }
            }
        });
        
        animImageView.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                applyRotation(0, -90);
                mIsCurrentImageview = !mIsCurrentImageview;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "EpisodeDetails");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if(rssDownloaderThread != null && rssDownloaderThread.isAlive()) {
        // rssDownloaderThread.interrupt();
        // rssDownloaderThread = null;
        // }

        Log.i("onPause", "EpisodeDetails");
    }

    private void applyRotation(float start, float end) {
        // Find the center of image
        final float centerX = mContainer.getWidth() / 2.0f;
        final float centerY = mContainer.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final EpisodeFlipAnimation rotation = new EpisodeFlipAnimation(start,
                end, centerX, centerY);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(mIsCurrentImageview));

        mContainer.startAnimation(rotation);
    }
    
    private final class DisplayNextView implements Animation.AnimationListener {
        private boolean mCurrentView;

        private DisplayNextView(boolean currentView) {
            mCurrentView = currentView;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            animImageView.post(new SwapViews(mCurrentView));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * This class is responsible for swapping the views and start the second
     * half of the animation.
     */
    private final class SwapViews implements Runnable {
        private boolean mIsCurView;
        public SwapViews(boolean isCurView) {
            mIsCurView = isCurView;
        }

        public void run() {
            final float centerX = mContainer.getWidth() / 2.0f;
            final float centerY = mContainer.getHeight() / 2.0f;
            EpisodeFlipAnimation rotation;
            
            if (mIsCurView) {
                relatedVideoList.setVisibility(View.GONE);
                mListHeader.setVisibility(View.GONE);
                animImageView.setVisibility(View.VISIBLE);
                animImageView.requestFocus();

                rotation = new EpisodeFlipAnimation(-90, 0, centerX, centerY);
            } else {
                animImageView.setVisibility(View.GONE);
                relatedVideoList.setVisibility(View.VISIBLE);
                mListHeader.setVisibility(View.VISIBLE);
                relatedVideoList.requestFocus();

                rotation = new EpisodeFlipAnimation(90, 0, centerX, centerY);
            }

            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());

            mContainer.startAnimation(rotation);
        }
    }

}
