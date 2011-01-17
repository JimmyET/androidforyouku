package framework.parser.rss;

import java.util.ArrayList;

public class Rss {
	private ArrayList<Channel> mChannels;
	
	public Rss() {
	    mChannels = new ArrayList<Channel>();
	}
	
	public void addChannel(Channel channel) {
		this.mChannels.add(channel);
	}

	public ArrayList<Channel> getChannels() {
		return mChannels;
	}

    public void setmChannes(ArrayList<Channel> channels) {
        this.mChannels = channels;
    }
}
