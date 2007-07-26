package lius.index;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.io.Resource;

import lius.config.LiusDocumentProperty;
import lius.config.LiusField;
import lius.index.util.LiusUtils;

public class ParsingResult {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ParsingResult.class);
    public static final String CONTENT_FIELD = "content";
    private Collection<Object> collection;
    private List<ParsingResult> parsingResults;
    private String content;
    private Resource resource;

    public ParsingResult(Collection<Object> collection, String content) {
        this.collection = collection;
        this.parsingResults = new ArrayList();
        this.content = content;
    }

    public ParsingResult(String content) {
        this.collection = new ArrayList<Object>();
        this.parsingResults = new ArrayList();
        this.content = content;
    }

    public Collection getCollection() {
        return collection;
    }

    /**
     * <pre>
     * Iterator it = populCollFile.iterator();
     * LiusField f = null;
     * LiusField newLF = null;
     * while (it.hasNext()) {
     *     Object o = it.next();
     *     try {
     *         if (o instanceof LiusField) {
     *             f = (LiusField) o;
     *             newLF = new LiusField();
     *             BeanUtils.copyProperties(newLF, f);
     *             populatedList.add(newLF);
     *         }
     *     } catch (InvocationTargetException ex) {
     *         LiusUtils.doOnException(ex);
     *     } catch (IllegalAccessException ex) {
     *         LiusUtils.doOnException(ex);
     *     }
     * }
     * </pre>
     */
    public void addAll(ParsingResult parsingResult) {
        collection.addAll(parsingResult.getCollection());
        parsingResults.add(parsingResult);
    }

    public void add(LiusField liusField) {
        collection.add(liusField);
    }

    public void add(Object liusField) {
        collection.add(liusField);
    }

    public String getContent() {
        return content;
    }

    public void reinit() {
        setContent(computeContent());
    }

    private String computeContent() {
        if (parsingResults.size() > 0) {
            StringBuilder result = new StringBuilder();
            for (ParsingResult parsingResult : parsingResults) {
                result.append(parsingResult.getContent());
            }
            return result.toString();
        } else {
            return content;
        }
    }

    public void setResource(Resource resource) {
        this.resource = resource;
        try {
            addField("fullPath", resource.getURL().toString(), "keyword");
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void setContent(String content) {
        this.content = content;
        if (this.content != null) {
            addField("content", content, "Text");
        }
    }

    private void addField(String name, String value, String type) {
        Object field = find(name);
        if (field == null) {
            LiusField lf = new LiusField();
            lf.setName(name);
            lf.setValue(value);
            lf.setType(type);
            collection.add(lf);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Ignore new value for field [" + name + "].");
            }
        }
    }

    private LiusField find(String name) {
        for (Object fieldColl : collection) {
            if (fieldColl instanceof LiusField) {
                LiusField field = (LiusField) fieldColl;
                if(field.getName().equalsIgnoreCase(name))
                {
                    return field;
                }
            }
        }
        return null;
    }
}
