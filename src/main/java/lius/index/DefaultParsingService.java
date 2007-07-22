package lius.index;

import java.io.File;
import java.io.IOException;

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
        try {
            IndexService indexer = IndexerFactory.getIndexer(resource, lc);
            return indexer.getDocument();
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse [" + resource
                    + "] caused by [" + e.getMessage() + "].", e);
        }
    }

    public Document parse(Object bean) {
        IndexService indexer = IndexerFactory.createBeanIndexer(bean, lc);
        return indexer.getDocument();
    }

    public Document parseMixedContent(Resource resource) {
        try {
            IndexService indexer = IndexerFactory.createMixedIndexer(resource,
                    lc);
            return indexer.getDocument();
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse [" + resource
                    + "] caused by [" + e.getMessage() + "].", e);
        }
    }
}
