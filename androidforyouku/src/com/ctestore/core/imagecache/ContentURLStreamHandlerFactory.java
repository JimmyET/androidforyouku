package com.ctestore.core.imagecache;

import android.content.ContentResolver;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class ContentURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private final ContentResolver mResolver;

    public ContentURLStreamHandlerFactory(ContentResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException();
        }
        mResolver = resolver;
    }

    /**
     * {@inheritDoc}
     */
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (ContentResolver.SCHEME_CONTENT.equals(protocol)
                || ContentResolver.SCHEME_FILE.equals(protocol)
                || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(protocol)) {
            return new ContentURLStreamHandler(mResolver);
        } else {
            return null;
        }
    }
}
