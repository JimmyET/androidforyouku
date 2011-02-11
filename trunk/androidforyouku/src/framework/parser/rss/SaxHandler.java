package framework.parser.rss;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SaxHandler extends DefaultHandler {
    private final String defaultChannelTag = "channel";
    private final String defaultItemTag = "item";
    private Rss rss = new Rss();;
    private Channel channel;
    private Item item;
    private StringBuilder text;
    private ArrayList<String> channelTags;
    private ArrayList<String> itemTags;
    private boolean isParsingChannel = false;
    private boolean isParsingItem    = false;

    public SaxHandler(ArrayList<String> channelTags, ArrayList<String> itemTags) {
        this.channelTags = channelTags;
        this.itemTags = itemTags;
    }

    public Rss getRss() {
        Log.d("AAA", "XXXXXXXXXX: " + this.rss.getChannels().size());
        return this.rss;
    }

    @Override
    public void characters(char ch[], int start, int length) {
        this.text.append(ch, start, length);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        Log.d("AAA", localName + " : " );
        Log.d("AAA", "size :" +String.valueOf(channelTags.size()));
        for (int i = 0; i < channelTags.size(); i++) {
            if(isParsingChannel && !isParsingItem) {
                
                Log.d("AAA", "size :" +String.valueOf(channelTags.size()) + " ^^ " + channelTags.get(i));
                if (localName.equalsIgnoreCase(channelTags.get(i))) {
                    Log.d("AAA", localName + " @ " + this.text.toString().trim());
                    channel.addValue(this.text.toString().trim());
                    Log.d("AAA", this.text.toString().trim() + " #### " );
                }
            }
        }
        
        for (int i = 0; i < itemTags.size(); i++) {
            if(isParsingItem && !isParsingChannel) {
                if (localName.equalsIgnoreCase(itemTags.get(i))) {
                    Log.d("AAA", localName + " @ " + this.text.toString().trim());
                    this.item.addValue(this.text.toString().trim());
                }
            }
        }

        this.text.setLength(0);

    }

    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException {

        if (localName.equalsIgnoreCase(defaultChannelTag)) {
            Log.d("AAA", localName + " # " + qName);
            this.channel = new Channel();
            this.channel.setTags(channelTags);
            this.rss.addChannel(this.channel);
            isParsingChannel = true;
            isParsingItem    = false;
        }

        if (localName.equalsIgnoreCase(defaultItemTag)) {
            Log.d("AAA", localName + " : " + qName);
            this.item = new Item();
            this.item.setTags(itemTags);
            this.channel.addItem(this.item);
            isParsingItem    = true;
            isParsingChannel = false;
        }
    }

    @Override
    public void startDocument() throws SAXException {
        this.text = new StringBuilder();
    }

    @Override
    public void endDocument() throws SAXException {

    }
}
