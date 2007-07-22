package lius.index;

import org.apache.lucene.document.Document;
import org.springframework.core.io.Resource;

public interface ParsingService {
    Document parse(Resource resource);
    Document parse(Object bean);
}
