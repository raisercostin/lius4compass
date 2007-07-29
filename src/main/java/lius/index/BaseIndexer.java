/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lius.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lius.config.LiusConfig;
import lius.lucene.LuceneActions;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public abstract class BaseIndexer implements Indexer, IndexService {
    public final static int INDEXER_CONFIG_FIELDS_COL = 1;
    public final static int INDEXER_CONFIG_FIELDS_MAP = 2;
    public static Logger logger = Logger.getLogger(BaseIndexer.class);
    protected LuceneActions luceneActions = new LuceneActions();
    protected LiusConfig liusConfig = null;

    public BaseIndexer() {
    }

    public abstract int getType();

    public abstract Collection getConfigurationFields(LiusConfig liusConfig);

    public abstract boolean isConfigured(LiusConfig liusConfig);

    public void setLuceneActions(LuceneActions luceneActions) {
        this.luceneActions = luceneActions;
    }

    public void setUp(LiusConfig lc) {
        this.liusConfig = lc;
    }

    public List<Document> getDocuments(Resource resource,
            boolean oneDocumentForAllSubResources) {
        if (oneDocumentForAllSubResources) {
            List<Document> result = new ArrayList<Document>();
            Document luceneDoc = getDocument(resource);
            result.add(luceneDoc);
            return result;
        } else {
            try {
                List<Document> result = new ArrayList<Document>();
                for (Iterator iterator = FileUtils.iterateFiles(resource
                        .getFile(), null, true); iterator.hasNext();) {
                    File file = (File) iterator.next();
                    result.add(getDocument(new FileSystemResource(file)));
                }
                return result;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Document getDocument(Resource resource) {
        return luceneActions.populateLuceneDoc(parseResourceInternal(resource));
    }

    public ParsingResult getLiusDocument(Resource resource) {
        return parseResourceInternal2(resource);
    }

    public Document getDocumentFromObject(Object object) {
        Document luceneDoc = luceneActions
                .populateLuceneDoc(parseObjectInternal(object));
        return luceneDoc;
    }

    protected LuceneActions getLuceneActions() {
        return luceneActions;
    }

    public ParsingResult parseResource(LiusConfig liusConfig, Resource resource) {
        throw new IllegalStateException();
    }

    public ParsingResult parseObject(LiusConfig liusConfig, Object mixedContent) {
        throw new IllegalStateException();
    }

    public String getMimeType(Resource resource) {
        return MimeTypeUtils.getMimeType(resource);
    }

    private Collection parseObjectInternal(Object object) {
        ParsingResult parsingResult = parseObject(liusConfig, object);
        parsingResult.reinit();
        return parsingResult.getCollection();
    }

    private Collection parseResourceInternal(Resource resource) {
        return parseResourceInternal2(resource).getCollection();
    }

    private ParsingResult parseResourceInternal2(Resource resource) {
        ParsingResult parsingResult = parseResource(liusConfig, resource);
        parsingResult.setResource(resource);
        parsingResult.reinit();
        return parsingResult;
    }
}