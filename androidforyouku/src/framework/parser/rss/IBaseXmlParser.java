package framework.parser.rss;


import java.io.InputStream;
import java.util.ArrayList;

public interface IBaseXmlParser {
    public Rss parse(InputStream is, ArrayList<String> channelTags,
            ArrayList<String> itemTags);
}
