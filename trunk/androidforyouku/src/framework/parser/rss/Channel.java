package framework.parser.rss;

import java.util.ArrayList;

public class Channel {
	ArrayList<String> mTags;
	ArrayList<String> mValues = new ArrayList<String>();
	ArrayList<Item> mItems = new ArrayList<Item>();

    public void setValues(ArrayList<String> mValues) {
        this.mValues = mValues;
    }

    public void setItems(ArrayList<Item> mItems) {
        this.mItems = mItems;
    }

    public void addItem(Item item) {
		this.mItems.add(item);
	}

	public void addValue(String value) {
		this.mValues.add(value);
	}
	
	public ArrayList<String> getTags() {
		return mTags;
	}

	public void setTags(ArrayList<String> tags) {
		this.mTags = tags;
	}

	public ArrayList<String> getValues() {
		return mValues;
	}

	public ArrayList<Item> getItems() {
		return mItems;
	}
}
