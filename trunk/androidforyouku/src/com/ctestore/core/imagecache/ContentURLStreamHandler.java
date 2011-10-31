package com.ctestore.core.imagecache;

import android.content.ContentResolver;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ContentURLStreamHandler extends URLStreamHandler {

    private final ContentResolver mResolver;

    public ContentURLStreamHandler(ContentResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException();
        }
        mResolver = resolver;
    }

    @Override
    protected URLConnection openConnection(URL url) {
        return new ContentURLConnection(mResolver, url);
    }
}
