package lius.index.xml;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lius.config.LiusConfig;
import lius.config.LiusField;
import lius.index.BaseIndexer;
import lius.index.ParsingResult;
import lius.index.util.LiusUtils;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.springframework.core.io.Resource;

/**
 * Classe se basant sur JDOM et XPATH pour indexer des noeuds de documents XML.
 * <br/><br/> Class based on JDOM and XPATH for indexing nodes in XML
 * documents.
 * 
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class XmlNodeIndexer extends BaseIndexer {
    public static Logger logger = Logger.getLogger(XmlNodeIndexer.class);
    private XmlFileIndexer xfi;

    public XmlNodeIndexer() {
        xfi = new XmlFileIndexer();
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public boolean isConfigured(LiusConfig liusConfig) {
        return liusConfig.getXmlFileFields() != null;
    }

    @Override
    public Collection getConfigurationFields(LiusConfig liusConfig) {
        List ls = new ArrayList();
        ls.add(liusConfig.getXmlNodesFields());
        return ls;
    }

    public String getContent(InputStream is) {
        return null;
    }

    @Override
    public ParsingResult parseResource(LiusConfig liusConfig,
            Resource resource) {
        logger
                .info("Pour des raisons de performance cette méthode n'a pas été implémentée");
        return null;
    }

    public org.apache.lucene.document.Document storeNodeInLuceneDocument(
            Element xmlDoc, Collection liusFields, Resource resource) {
        ParsingResult resColl = xfi.getPopulatedLiusFields(xmlDoc, liusFields,
                resource);
        org.apache.lucene.document.Document luceneDoc = getLuceneActions()
                .populateLuceneDoc(resColl.getCollection());
        return luceneDoc;
    }

//    private synchronized void index(String indexDir, Resource resource) {
//        InputStream inputStream;
//        try {
//            inputStream = resource.getInputStream();
//        } catch (IOException e1) {
//            throw new IllegalArgumentException(e1);
//        }
//        IndexWriter iw = null;
//        org.jdom.Document xmlDoc = LiusUtils.parse(inputStream);
//        try {
//            Set s = liusConfig.getXmlNodesFields().keySet();
//            Object[] a = s.toArray();
//            iw = getLuceneActions().openIndex(indexDir, liusConfig);
//            for (int i = 0; i < a.length; i++) {
//                String XpathNode = (String) a[i];
//                List ls = XPath.selectNodes(xmlDoc, XpathNode);
//                Iterator it = ls.iterator();
//                while (it.hasNext()) {
//                    Element elem = (Element) it.next();
//                    org.apache.lucene.document.Document luceneDoc = storeNodeInLuceneDocument(
//                            elem, (Collection) liusConfig.getXmlNodesFields()
//                                    .get(XpathNode), resource);
//                    getLuceneActions().save(luceneDoc, iw, liusConfig);
//                }
//            }
//            iw.close();
//        } catch (JDOMException e) {
//            LiusUtils.doOnException(e);
//        } catch (IOException e) {
//            LiusUtils.doOnException(e);
//        } finally {
//            getLuceneActions().unLock(indexDir);
//        }
//    }

//    @Override
//    public synchronized void indexWithCostumLiusFields(String indexDir,
//            List LuceneCostumFields, Resource resource) {
//        InputStream inputStream;
//        try {
//            inputStream = resource.getInputStream();
//        } catch (IOException e1) {
//            throw new IllegalArgumentException(e1);
//        }
//        IndexWriter iw = null;
//        org.jdom.Document xmlDoc = LiusUtils.parse(inputStream);
//        try {
//            Set s = liusConfig.getXmlNodesFields().keySet();
//            Object[] a = s.toArray();
//            iw = getLuceneActions().openIndex(indexDir, liusConfig);
//            for (int i = 0; i < a.length; i++) {
//                String XpathNode = (String) a[i];
//                List ls = XPath.selectNodes(xmlDoc, XpathNode);
//                Iterator it = ls.iterator();
//                while (it.hasNext()) {
//                    Element elem = (Element) it.next();
//                    org.apache.lucene.document.Document luceneDoc = storeNodeInLuceneDocument(
//                            elem, (Collection) liusConfig.getXmlNodesFields()
//                                    .get(XpathNode), resource);
//                    for (int m = 0; m < LuceneCostumFields.size(); m++) {
//                        LiusField lf = (LiusField) LuceneCostumFields.get(m);
//                        Field field = null;
//                        if (lf.getType().equalsIgnoreCase("text")) {
//                            field = new Field(lf.getName(), lf.getValue(),
//                                    Field.Store.YES, Field.Index.TOKENIZED);
//                        } else if (lf.getType().equalsIgnoreCase("keyword")) {
//                            field = new Field(lf.getName(), lf.getValue(),
//                                    Field.Store.YES, Field.Index.UN_TOKENIZED);
//                        } else if (lf.getType().equalsIgnoreCase("unindexed")) {
//                            field = new Field(lf.getName(), lf.getValue(),
//                                    Field.Store.YES, Field.Index.NO);
//                        } else if (lf.getType().equalsIgnoreCase("unstored")) {
//                            field = new Field(lf.getName(), lf.getValue(),
//                                    Field.Store.NO, Field.Index.TOKENIZED);
//                        }
//                        logger.info("New field added to document : "
//                                + lf.getName() + " (type = " + lf.getType()
//                                + ") " + " : " + lf.getValue());
//                        if (field != null) {
//                            luceneDoc.add(field);
//                        }
//                    }
//                    getLuceneActions().save(luceneDoc, iw, liusConfig);
//                }
//            }
//            iw.close();
//        } catch (JDOMException e) {
//            LiusUtils.doOnException(e);
//        } catch (IOException e) {
//            LiusUtils.doOnException(e);
//        } finally {
//            getLuceneActions().unLock(indexDir);
//        }
//    }
}