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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import lius.config.LiusConfig;
import lius.config.LiusField;
import lius.index.util.LiusUtils;
import lius.lucene.LuceneActions;
import lius.transaction.LiusTransactionManager;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.core.io.Resource;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public abstract class BaseIndexer implements Indexer, IndexService {
    public final static int INDEXER_CONFIG_FIELDS_COL = 1;
    public final static int INDEXER_CONFIG_FIELDS_MAP = 2;
    public static Logger logger = Logger.getLogger(BaseIndexer.class);
    protected LuceneActions luceneActions = new LuceneActions();
    protected LiusConfig liusConfig = null;
    private Directory ramDir = null;
    private LiusTransactionManager transaction = null;

    public BaseIndexer() {
    }

    public void setLuceneActions(LuceneActions luceneActions) {
        this.luceneActions = luceneActions;
    }

    public void setUp(LiusConfig lc) {
        this.liusConfig = lc;
    }

    public synchronized void indexUsingTransaction(String indexDir,
            Resource resource) {
        Document luceneDoc = luceneActions
                .populateLuceneDoc(parseResourceInternal(resource));
        ramDir = new RAMDirectory();
        transaction = new LiusTransactionManager(luceneDoc, ramDir, indexDir,
                liusConfig);
        try {
            transaction.start();
            transaction.commit();
        } catch (IOException e) {
            LiusUtils.doOnException(e);
            transaction.rollBack();
        }
    }

    public void index(String indexDir, Resource resource) {
        indexAndGetDocument(indexDir, resource);
    }

    public Document getDocument(Resource resource) {
        Document luceneDoc = luceneActions
                .populateLuceneDoc(parseResourceInternal(resource));
        return luceneDoc;
    }

    public ParsingResult getLiusDocument(Resource resource) {
        return parseResourceInternal2(resource);
    }

    public Document getDocumentFromObject(Object object) {
        Document luceneDoc = luceneActions
                .populateLuceneDoc(parseObjectInternal(object));
        return luceneDoc;
    }

    public synchronized Document indexAndGetDocument(String indexDir,
            Resource resource) {
        Document luceneDoc = luceneActions
                .populateLuceneDoc(parseResourceInternal(resource));
        try {
            luceneActions.index(luceneDoc, indexDir, liusConfig);
        } catch (IOException e) {
            LiusUtils.doOnException(e);
        }
        return luceneDoc;
    }

    public synchronized void index(String indexDir, List LuceneCostumFields,
            Resource resource) {
        Document luceneDoc = luceneActions
                .populateLuceneDoc(parseResourceInternal(resource));
        for (int i = 0; i < LuceneCostumFields.size(); i++) {
            luceneDoc.add((Field) LuceneCostumFields.get(i));
        }
        try {
            luceneActions.index(luceneDoc, indexDir, liusConfig);
        } catch (IOException e) {
            LiusUtils.doOnException(e);
        }
    }

    public synchronized void indexWithCostumLiusFields(String indexDir,
            List LuceneCostumFields, Resource resource) {
        Document luceneDoc = luceneActions
                .populateLuceneDoc(parseResourceInternal(resource));
        for (int i = 0; i < LuceneCostumFields.size(); i++) {
            LiusField lf = (LiusField) LuceneCostumFields.get(i);
            Field field = null;
            if (lf.getType().equalsIgnoreCase("text")) {
                field = new Field(lf.getName(), lf.getValue(), Field.Store.YES,
                        Field.Index.TOKENIZED);
            } else if (lf.getType().equalsIgnoreCase("keyword")) {
                field = new Field(lf.getName(), lf.getValue(), Field.Store.YES,
                        Field.Index.UN_TOKENIZED);
            } else if (lf.getType().equalsIgnoreCase("unindexed")) {
                field = new Field(lf.getName(), lf.getValue(), Field.Store.YES,
                        Field.Index.NO);
            } else if (lf.getType().equalsIgnoreCase("unstored")) {
                field = new Field(lf.getName(), lf.getValue(), Field.Store.NO,
                        Field.Index.TOKENIZED);
            }
            logger
                    .info("New field added to document : " + lf.getName()
                            + " (type = " + lf.getType() + ") " + " : "
                            + lf.getValue());
            if (field != null) {
                luceneDoc.add(field);
            }
        }
        try {
            luceneActions.index(luceneDoc, indexDir, liusConfig);
        } catch (IOException e) {
            LiusUtils.doOnException(e);
        }
    }

    public synchronized void indexUsingTransaction(String indexDir,
            List LuceneCostumFields, Resource resource) {
        Document luceneDoc = luceneActions
                .populateLuceneDoc(parseResourceInternal(resource));
        for (int i = 0; i < LuceneCostumFields.size(); i++) {
            luceneDoc.add((Field) LuceneCostumFields.get(i));
        }
        ramDir = new RAMDirectory();
        transaction = new LiusTransactionManager(luceneDoc, ramDir, indexDir,
                liusConfig);
        try {
            transaction.start();
            transaction.commit();
        } catch (IOException e) {
            LiusUtils.doOnException(e);
            transaction.rollBack();
        }
    }

    protected LuceneActions getLuceneActions() {
        return luceneActions;
    }

    public abstract int getType();

    public abstract Collection getConfigurationFields(LiusConfig liusConfig);

    public abstract boolean isConfigured(LiusConfig liusConfig);

    public ParsingResult parseResource(LiusConfig liusConfig, Resource resource) {
        throw new NotImplementedException();
    }

    public ParsingResult parseObject(LiusConfig liusConfig, Object mixedContent) {
        throw new NotImplementedException();
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