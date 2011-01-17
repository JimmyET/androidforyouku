package framework.parser.rss;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.util.Log;

public class SaxXmlParser implements IBaseXmlParser {
    public Rss parse(InputStream is, ArrayList<String> channelTags,
            ArrayList<String> itemTags) {
        
        SaxHandler saxHandler = new SaxHandler(channelTags, itemTags);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            try {
                parser.parse(is, saxHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        
        Log.d("AAA", "##########: " + saxHandler.getRss().getChannels().size());
        return saxHandler.getRss();
    }
    
}
