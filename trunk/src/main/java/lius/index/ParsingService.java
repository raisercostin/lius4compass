package lius.index;

import java.util.List;

import org.apache.lucene.document.Document;
import org.compass.core.CompassSession;
import org.compass.core.lucene.LuceneResource;
import org.springframework.core.io.Resource;

public interface ParsingService {
    List<Document> parse(Resource resource,
            boolean oneDocumentForAllSubResources);

    Document parse(Resource resource);

    Document parse(Object bean);

    List<LuceneResource> parseLuceneResources(String alias, Resource resource,
            CompassSession compassSession, boolean oneDocumentForAllSubResources);

    LuceneResource parseLuceneResource(String alias,
            Resource classPathResource, CompassSession compassSession);
}
