package lius.index;

import java.io.File;
import java.io.InputStream;

import lius.config.LiusConfig;

public interface IndexService extends Indexer{
    void index(String string);

    void setFileName(String name);

    void setDocToIndexPath(String absolutePath);

    void setUp(LiusConfig lc);

    void setMimeType(String mimeType);

    void setStreamToIndex(InputStream is);

    void setMixedContentsObj(Object object);

    void setObjectToIndex(Object object);
}
