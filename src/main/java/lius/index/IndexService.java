package lius.index;

import java.io.InputStream;

import lius.config.LiusConfig;

import org.apache.lucene.document.Document;

public interface IndexService extends Indexer{
    void index(String string);

    void setFileName(String name);

    void setDocToIndexPath(String absolutePath);

    void setUp(LiusConfig lc);

    void setMimeType(String mimeType);

    void setStreamToIndex(InputStream is);

    void setMixedContentsObj(Object object);

    void setObjectToIndex(Object object);

    Document getDocument();
}
