package group.jinjie.qq.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.Context;

public class ResourceUtility {
    public static void getConfigTags(Context context, ArrayList<String> channelTags,
            ArrayList<String> itemTags, int channelArrayId, int itemArrayId) {
        String [] channel_tags =  context.getResources().getStringArray(channelArrayId);
        String [] item_tags =  context.getResources().getStringArray(itemArrayId);
        Collections.addAll(channelTags, channel_tags) ;
        Collections.addAll(itemTags, item_tags) ;
    }
    
    public static void getConfigTags(Context context, ArrayList<String> channelTags,
             int channelArrayId) {
        String [] channel_tags =  context.getResources().getStringArray(channelArrayId);
        Collections.addAll(channelTags, channel_tags) ;
    }
    
    public static void flurryEvent(String name, String param, String value) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(param, value);
        com.flurry.android.FlurryAgent.onEvent(name, params);
    }
}
