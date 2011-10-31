
package group.jinjie.qq.adapter;

import com.ctestore.core.imagecache.ImageLoader;

import java.util.Date;

import framework.parser.rss.Rss;
import group.jinjie.qq.ChannelEpisodeActivity;
import group.jinjie.qq.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChannelItemAdapter extends BaseAdapter {
    private static final String TAG = "ChannelItemAdapter";

    private LayoutInflater mInflater;

    private Context mContext = null;

    private Rss mRssContent = null;

    private ListView listView;

    private ImageLoader mImageLoader;

    // public int count = 10;

    public ChannelItemAdapter(Context context, ListView listView, Rss rss) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mRssContent = rss;
        this.listView = listView;
        mImageLoader = ImageLoader.get(context);
    }

    public int getCount() {
        if (mRssContent != null && !mRssContent.getChannels().isEmpty()) {
            // Log.d(TAG, "#####" +
            // mRssContent.getChannels().get(0).getItems().size());
            return mRssContent.getChannels().get(0).getItems().size();
        } else {
            // Log.d(TAG, "#####");
            return 0;
        }
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Bitmap itemIcon = null;
        ViewHolder holder = null;
        // String imageLink = null;
        if (convertView == null) {
            // Log.d(TAG, "#### :" + "fsdfsd");
            convertView = mInflater.inflate(R.layout.image_and_text_row, null);
            holder = new ViewHolder();
            holder.mItemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
            // holder.mMoreButton = (Button) convertView
            // .findViewById(R.id.ItemRecording);
            holder.mItemTitle = (TextView) convertView.findViewById(R.id.item_title);
            holder.mItemTime = (TextView) convertView.findViewById(R.id.item_pubdate);
            holder.mItemViewedTimes = (TextView) convertView.findViewById(R.id.item_viewed_times);
            // holder.mIcon = (ImageView) convertView
            convertView.setClickable(true);

            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    String vid = (String) v.getTag(R.id.vid);
                    String imageLink = (String) v.getTag(R.id.imageLink);
                    String mediaLink = (String) v.getTag(R.id.mediaLink);

                    String vtitle = (String) v.getTag(R.id.item_title);
                    String vtime = (String) v.getTag(R.id.item_pubdate);
                    String vtimes = (String) v.getTag(R.id.item_viewed_times);
                    // Intent intent = new Intent(ChannelActivity.this,
                    // VideoPlayer.class);
                    Intent intent = new Intent(mContext, ChannelEpisodeActivity.class);
                    intent.putExtra("imageLink", imageLink);
                    intent.putExtra("mediaLink", mediaLink);
                    intent.putExtra("vid", vid);

                    intent.putExtra("vtitle", vtitle);
                    intent.putExtra("vtime", vtime);
                    intent.putExtra("vtimes", vtimes);

                    mContext.startActivity(intent);

                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Log.d(TAG, "#### :" + position);
        holder.mItemTitle.setText(mRssContent.getChannels().get(0).getItems().get(position)
                .getValues().get(3));

        holder.mItemTime.setText(getRelativeTime(mRssContent.getChannels().get(0).getItems().get(
                position).getValues().get(12), mContext));

        holder.mItemViewedTimes.setText(mRssContent.getChannels().get(0).getItems().get(position)
                .getValues().get(6)
                + " " + mContext.getResources().getString(R.string.channel_item_viewed_times));

        // holder
        // item_time

        // holder.mItemTitle.setTextColor(0xff004980);

        String vid = mRssContent.getChannels().get(0).getItems().get(position).getValues().get(0);
        String mediaLink = mRssContent.getChannels().get(0).getItems().get(position).getValues()
                .get(2);
        String imageLink = mRssContent.getChannels().get(0).getItems().get(position).getValues()
                .get(4);
        String vtitle = holder.mItemTitle.getText().toString();
        String vtime = holder.mItemTime.getText().toString();
        String vtimes = holder.mItemViewedTimes.getText().toString();

        convertView.setTag(R.id.mediaLink, mediaLink);
        convertView.setTag(R.id.imageLink, imageLink);
        convertView.setTag(R.id.vid, vid);

        convertView.setTag(R.id.item_title, vtitle);
        convertView.setTag(R.id.item_pubdate, vtime);
        convertView.setTag(R.id.item_viewed_times, vtimes);
        // final ImageView iv = (ImageView) listView.findViewWithTag(mRssContent
        // .getChannels().get(0).getItems().get(position)
        // .getValues().get(4));
        mImageLoader.bind(this, holder.mItemIcon, imageLink, R.drawable.item_default_icon);

        // itemIcon = ImageDownloader.loadImage(mContext, imageLink, 0,
        // position,
        // new ImageDownloaderCallback() {
        // public void imageLoaded(Bitmap bitmap, final int channelId,
        // final int itemId, String imageUrl) {
        // ImageView iv = (ImageView) listView.findViewWithTag(mRssContent
        // .getChannels().get(0).getItems().get(position)
        // .getValues().get(4));
        // Log.d("BBBB", "#########");
        // if (iv != null) {
        // Log.d("BBBB", "&&&&&&");
        // if (bitmap != null) {
        // Log.d("BBBB", "VVVVVVV");
        // iv.setImageBitmap(bitmap);
        // }
        // }
        // // if (bitmap != null) {
        // // notifyDataSetChanged();
        // // }
        // }
        // });

        // if (itemIcon == null) {
        // Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory
        // .decodeResource(mContext.getResources(),
        // R.drawable.item_default_icon), 110, 80, true);
        // holder.mItemIcon.setImageBitmap(bitmap);
        // } else {
        // //holder.mItemIcon.setImageBitmap(itemIcon);
        // }

        return convertView;
    }

    static class ViewHolder {
        ImageView mItemIcon;

        TextView mItemTitle;

        TextView mItemTime;

        TextView mItemViewedTimes;
    }

    public String getRelativeTime(String t, Context c) {
        String s = t;
        Date date = new Date();
        int y = date.getYear() + 1900;
        int m = date.getMonth() + 1;
        int d = date.getDate();

        s = s.split(" ")[0];
        String arr[] = s.split("-");

        // Log.d(TAG, arr[0]+ " $$$ " + y +" >>>>>>"+ date);
        if (y != Integer.valueOf(arr[0])) {
            return String.valueOf(y - Integer.valueOf(arr[0])) + " "
                    + c.getResources().getString(R.string.channel_item_time_pubyear);
        } else if (m != Integer.valueOf(arr[1])) {
            return String.valueOf(m - Integer.valueOf(arr[1])) + " "
                    + c.getResources().getString(R.string.channel_item_time_pubmonth);
        } else {
            return String.valueOf(d - Integer.valueOf(arr[2])) + " "
                    + c.getResources().getString(R.string.channel_item_time_pubday);
        }

    }
}
