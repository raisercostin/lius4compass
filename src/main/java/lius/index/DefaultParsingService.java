package lius.index;

import lius.config.LiusConfig;
import lius.config.LiusConfigBuilder;

import org.apache.lucene.document.Document;
import org.springframework.core.io.Resource;

public class DefaultParsingService implements ParsingService {
    private LiusConfig lc;

    public DefaultParsingService(Resource liusConfigResource) {
        lc = LiusConfigBuilder.getSingletonInstance().getLiusConfig(
                liusConfigResource);
    }

    public Document parse(Resource resource) {
        IndexService indexer = IndexerFactory.getIndexer(resource, lc);
        return indexer.getDocument(resource);
    }

    public Document parse(Object bean) {
        IndexService indexer = IndexerFactory.getIndexer(bean,lc);
        return indexer.getDocumentFromObject(bean);
    }
}
