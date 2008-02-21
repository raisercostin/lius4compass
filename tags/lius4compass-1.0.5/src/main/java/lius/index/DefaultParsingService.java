package lius.index;

import java.util.ArrayList;
import java.util.List;

import lius.config.LiusConfig;
import lius.config.LiusConfigBuilder;

import org.apache.lucene.document.Document;
import org.compass.core.CompassSession;
import org.compass.core.lucene.LuceneResource;
import org.compass.core.lucene.engine.LuceneSearchEngine;
import org.compass.core.spi.InternalCompassSession;
import org.springframework.core.io.Resource;

public class DefaultParsingService implements ParsingService {
    private LiusConfig liusConfig;

    public DefaultParsingService() {
    }

    public DefaultParsingService(Resource liusConfigResource) {
        liusConfig = LiusConfigBuilder.getSingletonInstance().getLiusConfig(
                liusConfigResource);
    }

    public void setLiusConfig(LiusConfig liusConfig) {
        this.liusConfig = liusConfig;
    }

    public Document parse(Resource resource) {
        IndexService indexer = IndexerFactory.getIndexer(resource, liusConfig);
        return indexer.getDocument(resource);
    }

    public List<Document> parse(Resource resource,
            boolean oneDocumentForAllSubResources) {
        IndexService indexer = IndexerFactory.getIndexer(resource, liusConfig);
        return indexer.getDocuments(resource, oneDocumentForAllSubResources);
    }

    public Document parse(Object bean) {
        IndexService indexer = IndexerFactory.getIndexer(liusConfig);
        return indexer.getDocumentFromObject(bean);
    }

    public LuceneResource parseLuceneResource(String alias, Object object,
            CompassSession compassSession) {
        return createLuceneResource(alias, compassSession, parse(object));
    }

    public LuceneResource parseLuceneResource(String alias, Resource resource,
            CompassSession compassSession) {
        return createLuceneResource(alias, compassSession, parse(resource));
    }

    public List<LuceneResource> parseLuceneResources(String alias,
            Resource resource, CompassSession compassSession,
            boolean oneDocumentForAllSubResources) {
        List<LuceneResource> result = new ArrayList<LuceneResource>();
        for (Document document : parse(resource, oneDocumentForAllSubResources)) {
            result.add(createLuceneResource(alias, compassSession, document));
        }
        return result;
    }

    private LuceneResource createLuceneResource(String alias,
            CompassSession compassSession, Document luceneDocument) {
        return new LuceneResource(alias, luceneDocument, -1,
                (LuceneSearchEngine) ((InternalCompassSession) compassSession)
                        .getSearchEngine());
    }
}
