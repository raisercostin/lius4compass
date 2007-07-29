package lius.index;

import org.apache.lucene.document.Document;
import org.compass.core.CompassSession;
import org.compass.core.lucene.LuceneResource;
import org.springframework.core.io.Resource;

public interface ParsingService {
    Document parse(Resource resource);

    Document parse(Object bean);

    LuceneResource parseLuceneResource(String alias, Resource resource,
            CompassSession compassSession);
}
