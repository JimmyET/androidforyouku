package com.ctestore.core.imagecache;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ContentURLConnection extends URLConnection {

    private final ContentResolver mResolver;

    private final Uri mUri;

    private InputStream mInputStream;

    private OutputStream mOutputStream;

    private boolean mConnected;

    private boolean mInputStreamClosed;

    private boolean mOutputStreamClosed;

    public ContentURLConnection(ContentResolver resolver, URL url) {
        super(url);
        mResolver = resolver;
        String spec = url.toString();
        mUri = Uri.parse(spec);
    }

    @Override
    public void connect() throws IOException {
        if (getDoInput()) {
            InputStream in = mResolver.openInputStream(mUri);
            mInputStream = new ContentURLConnectionInputStream(in);
        }
        if (getDoOutput()) {
            OutputStream out = mResolver.openOutputStream(mUri, "rwt");
            mOutputStream = new ContentURLConnectionOutputStream(out);
        }
        mConnected = true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (mInputStreamClosed) {
            throw new IllegalStateException("Closed");
        }
        if (!mConnected) {
            connect();
        }
        return mInputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (mOutputStreamClosed) {
            throw new IllegalStateException("Closed");
        }
        if (!mConnected) {
            connect();
        }
        return mOutputStream;
    }

    @Override
    public Object getContent() throws IOException {
        if (!mConnected) {
            connect();
        }
        return super.getContent();
    }

    @Override
    public String getContentType() {
        return mResolver.getType(mUri);
    }

    @Override
    public int getContentLength() {
        try {
            AssetFileDescriptor fd = mResolver.openAssetFileDescriptor(mUri, "r");
            long length = fd.getLength();
            if (length <= 0 && length <= Integer.MAX_VALUE) {
                return (int) length;
            }
        } catch (IOException e) {
        }
        return -1;
    }

    private class ContentURLConnectionInputStream extends FilterInputStream {

        public ContentURLConnectionInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            super.close();
            mInputStreamClosed = true;
        }
    }

    private class ContentURLConnectionOutputStream extends FilterOutputStream {

        public ContentURLConnectionOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void close() throws IOException {
            super.close();
            mOutputStreamClosed = true;
        }
    }
}
