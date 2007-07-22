package lius.index;

import java.io.File;
import java.io.IOException;

import lius.config.LiusConfig;
import lius.config.LiusConfigBuilder;
import lius.index.mixedindexing.MixedIndexer;

import org.apache.lucene.document.Document;
import org.springframework.core.io.Resource;

public class DefaultParsingService implements ParsingService {
    private String indexDir;
    private LiusConfig lc;

    public DefaultParsingService(Resource liusConfigResource) {
        indexDir = new File("target/indexDir2").getAbsolutePath();
        lc = LiusConfigBuilder.getSingletonInstance().getLiusConfig(
                liusConfigResource);
    }

    public Document parse(Resource resource) {
        try {
            IndexService indexer = IndexerFactory.getIndexer(resource, lc);
            return indexer.indexAndGetDocument(indexDir);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse [" + resource
                    + "] caused by [" + e.getMessage() + "].", e);
        }
    }

    public Document parse(Object bean) {
        IndexService indexer = IndexerFactory.createBeanIndexer(bean, lc);
        return indexer.indexAndGetDocument(indexDir);
    }

    public Document parseMixedContent(Resource resource) {
        try {
            IndexService indexer = IndexerFactory.createMixedIndexer(resource,
                    lc);
            return indexer.indexAndGetDocument(indexDir);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse [" + resource
                    + "] caused by [" + e.getMessage() + "].", e);
        }
    }
}
