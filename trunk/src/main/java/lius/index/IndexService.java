package lius.index;

import lius.config.LiusConfig;

import org.apache.lucene.document.Document;
import org.springframework.core.io.Resource;

/**
 * Each indexService should be stateless.
 * 
 * @author raisercostin
 */
public interface IndexService extends Indexer {
    /**
     * Gets the lucene document from resource.
     * 
     * @param resource
     * @return
     */
    Document getDocument(Resource resource);

    ParsingResult getLiusDocument(Resource resource);

    /**
     * Gets the lucene document from bean.
     * 
     * @param resource
     * @return
     */
    Document getDocumentFromObject(Object object);

    // @Deprecated
    // void index(String string, Resource resource);
    // @Deprecated
    // void indexBean(String string, Object object);
    void setUp(LiusConfig lc);
}
