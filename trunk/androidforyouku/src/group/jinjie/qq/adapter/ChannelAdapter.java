package group.jinjie.qq.adapter;

import group.jinjie.qq.ChannelActivity;
import group.jinjie.qq.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ChannelAdapter extends BaseAdapter {
    private static int[] mImageIds = {
            R.drawable.channel_information,
            R.drawable.channel_original,
            R.drawable.channel_entertainment,
            R.drawable.channel_music, 
            R.drawable.channel_sport };

    public Context mContext;
    
    public ChannelAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mImageIds.length;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new Gallery.LayoutParams(
                    ChannelActivity.mChannelButtonDefaultSize[0],
                    ChannelActivity.mChannelButtonDefaultSize[1]));
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(18, 18, 18, 18);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mImageIds[position]);

        return imageView;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
