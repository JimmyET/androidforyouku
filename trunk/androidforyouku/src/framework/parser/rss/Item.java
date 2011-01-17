package framework.parser.rss;

import java.util.ArrayList;

public class Item {
    ArrayList<String> mTags;
    ArrayList<String> mValues = new ArrayList<String>();
	
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

    public void setValues(ArrayList<String> values) {
        this.mValues = values;
    }
}
