package lius.index;


import lius.config.LiusConfig;

import org.apache.lucene.document.Document;
import org.springframework.core.io.Resource;

public interface IndexService extends Indexer{
    Document getDocument(Resource resource);
    Document getDocumentFromObject(Object object);
    void index(String string, Resource resource);
    void setUp(LiusConfig lc);
}
