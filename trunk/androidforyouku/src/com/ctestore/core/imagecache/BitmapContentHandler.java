
package com.ctestore.core.imagecache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.URLConnection;

/**
 * A {@link ContentHandler} that decodes a {@link Bitmap} from a
 * {@link URLConnection}.
 * An {@link IOException} is thrown if there is a decoding exception.
 */
public class BitmapContentHandler extends ContentHandler {
    @Override
    public Bitmap getContent(URLConnection connection) throws IOException {
        InputStream input = connection.getInputStream();
        try {
            input = new BlockingFilterInputStream(input);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            if (bitmap == null) {
                throw new IOException("Image could not be decoded");
            }
            return bitmap;
        } finally {
            input.close();
        }
    }
}
