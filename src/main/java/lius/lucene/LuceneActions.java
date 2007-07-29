package lius.lucene;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lius.config.LiusConfig;
import lius.config.LiusDocumentProperty;
import lius.config.LiusField;
import lius.index.util.LiusUtils;
import lius.search.LiusHit;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Classe permettant d'effectuer des actions relatives à Lucene. <br/><br/>
 * Class that executes actions related to Lucene. Thread safe since doesn't have
 * any internal state.
 * 
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class LuceneActions {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(LuceneActions.class);
    private static final Logger logger2 = Logger.getLogger(LuceneActions.class
            + ".CONTENT");

    /**
     * Methode permettant de construire un objet de type "Lucene Document" à
     * partir de plusieurs collections contenant des informations sur les
     * documents à indexer. Cette méthode est utilisée pour l'indexation mixte.
     * <br/><br/>Method that constructs a Lucene document object from many
     * collections containing information on the documents to index. This method
     * is used for mixed indexation.
     */
    public Document populateLuceneDocumentFromListOfLiusFields(
            List listCollectionsFieldsContentAndType) {
        Collection coll = new ArrayList();
        for (int i = 0; i < listCollectionsFieldsContentAndType.size(); i++) {
            Collection collList = (Collection) listCollectionsFieldsContentAndType
                    .get(i);
            Iterator it = collList.iterator();
            while (it.hasNext()) {
                LiusField lf = (LiusField) it.next();
                coll.add(lf);
            }
        }
        Document doc = populateLuceneDoc(coll);
        return doc;
    }

    public Document populateLuceneDoc(Collection fieldsContentAndType) {
        if (fieldsContentAndType == null) {
            return null;
        }
        Document doc = new Document();
        LiusDocumentProperty ldp = null;
        if (logger2.isDebugEnabled()) {
            logger2.debug("==== Nouveau lucene document dans l'index ====");
        }
        Iterator it = fieldsContentAndType.iterator();
        while (it.hasNext()) {
            Field field = null;
            Object fieldColl = it.next();
            if (fieldColl instanceof LiusField) {
                LiusField lf = (LiusField) fieldColl;
                if (lf.getType().equalsIgnoreCase("Text")) {
                    field = new Field(lf.getName(), lf.getValue(),
                            Field.Store.YES, Field.Index.TOKENIZED);
                    if (logger2.isDebugEnabled()) {
                        logger2.debug(lf.getName() + " (type = " + lf.getType()
                                + ") " + " : " + lf.getValue());
                    }
                } else if (lf.getType().equalsIgnoreCase("TextReader")) {
                    if (lf.getValueInputStreamReader() != null) {
                        field = new Field(lf.getName(), lf
                                .getValueInputStreamReader());
                    } else if (lf.getValueReader() != null)
                        field = new Field(lf.getName(), lf.getValueReader());
                    if (logger2.isDebugEnabled()) {
                        logger2.debug(lf.getName() + " (type = " + lf.getType()
                                + ") " + " : Texte ajouté");
                    }
                } else if (lf.getType().equalsIgnoreCase("Keyword")) {
                    field = new Field(lf.getName(), lf.getValue(),
                            Field.Store.YES, Field.Index.UN_TOKENIZED);
                    if (logger2.isDebugEnabled()) {
                        logger2.debug(lf.getName() + " (type = " + lf.getType()
                                + ") " + " : " + lf.getValue());
                    }
                } else if (lf.getType().equalsIgnoreCase("concatDate")) {
                    String dateValue = lf.getValue();
                    if (dateValue.indexOf("-") > 0) {
                        dateValue = dateValue.replaceAll("-", "");
                    }
                    if (dateValue.indexOf("/") > 0) {
                        dateValue = dateValue.replaceAll("/", "");
                    }
                    if (dateValue.indexOf(" ") > 0) {
                        dateValue = dateValue.replaceAll(" ", "");
                    }
                    if (dateValue.indexOf("\\") > 0) {
                        dateValue = dateValue.replaceAll("\\", "");
                    }
                    if (dateValue.indexOf(".") > 0) {
                        dateValue = dateValue.replaceAll(".", "");
                    }
                    field = new Field(lf.getName(), dateValue, Field.Store.YES,
                            Field.Index.UN_TOKENIZED);
                    if (logger2.isDebugEnabled()) {
                        logger2.debug(lf.getName() + " (type = " + lf.getType()
                                + ") " + " : " + dateValue);
                    }
                    // } else if (lf.getType().equalsIgnoreCase("DateToString"))
                    // {
                    // field = new Field(lf.getName(), DateTools.dateToString(lf
                    // .getDate()), Field.Store.YES,
                    // Field.Index.UN_TOKENIZED);
                    // logger.debug(lf.getName() + " (type = " + lf.getType()
                    // + ") " + " : " + lf.getDate().toString());
                    // } else if (lf.getType().equalsIgnoreCase("StringToDate"))
                    // {
                    // DateFormat formatter = new SimpleDateFormat(lf
                    // .getDateFormat());
                    // try {
                    // field = new Field(lf.getName(), DateTools
                    // .dateToString(formatter.parse(lf.getValue())),
                    // Field.Store.YES, Field.Index.UN_TOKENIZED);
                    // logger.debug(lf.getName() + " (type = " + lf.getType()
                    // + ") " + " : " + lf.getValue());
                    // } catch (ParseException ex) {
                    // LiusUtils.doOnException(ex.getMessage());
                    // }
                } else if (lf.getType().equalsIgnoreCase("UnIndexed")) {
                    field = new Field(lf.getName(), lf.getValue(),
                            Field.Store.YES, Field.Index.NO);
                    if (logger2.isDebugEnabled()) {
                        logger2.debug(lf.getName() + " (type = " + lf.getType()
                                + ") " + " : " + lf.getValue());
                    }
                } else if (lf.getType().equalsIgnoreCase("UnStored")) {
                    field = new Field(lf.getName(), lf.getValue(),
                            Field.Store.NO, Field.Index.TOKENIZED);
                    if (logger2.isDebugEnabled()) {
                        logger2.debug(lf.getName() + " (type = " + lf.getType()
                                + ") " + " : " + lf.getValue());
                    }
                }
                if (lf.getIsBoosted() && field != null) {
                    field.setBoost(lf.getBoost());
                    if (logger2.isDebugEnabled()) {
                        logger2.debug("--------> Field " + lf.getName()
                                + " setBoost = " + lf.getBoost());
                    }
                }
                if (field != null) {
                    doc.add(field);
                }
            } else if (fieldColl instanceof LiusDocumentProperty) {
                ldp = (LiusDocumentProperty) fieldColl;
            }
        }
        if (ldp != null && ldp.getBoost() != null) {
            doc.setBoost(Float.parseFloat(ldp.getBoost()));
            if (logger2.isDebugEnabled()) {
                logger2.debug("@@@@@@@@@@@@@@@ Document boost = "
                        + ldp.getBoost() + " @@@@@@@@@@@@@@@");
            }
        }
        return doc;
    }

    public List ListAllDocuments(String indexDir, LiusConfig lc) {
        List documentsList = new ArrayList();
        List fieldList = lc.getBrowseFieldsToDisplay();
        Map values = null;
        LiusHit lh = null;
        try {
            Directory directory = FSDirectory.getDirectory(indexDir, false);
            IndexReader ir = IndexReader.open(directory);
            int num = ir.numDocs();
            for (int i = 0; i <= num - 1; i++) {
                lh = new LiusHit();
                values = new HashMap();
                Document luceneDoc = ir.document(i);
                lh.setDocId(i);
                for (int j = 0; j < fieldList.size(); j++) {
                    LiusField lf = (LiusField) fieldList.get(j);
                    Field f = luceneDoc.getField(lf.getName());
                    LiusField nlf = new LiusField();
                    nlf.setName(lf.getName());
                    nlf.setLabel(lf.getLabel());
                    if (f != null) {
                        String content = f.stringValue();
                        nlf.setValue(content);
                        values.put(lf.getName(), nlf);
                    }
                }
                lh.setLiusFieldsMap(values);
                documentsList.add(lh);
            }
            ir.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return documentsList;
    }

    public Directory getDirectory(String directoryPath) {
        try {
            return FSDirectory.getDirectory(directoryPath, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Directory[] getDirectories(String[] directoryPaths) {
        Directory[] directories = new Directory[directoryPaths.length];
        for (int i = 0; i < directoryPaths.length; i++) {
            String directoryPath = directoryPaths[i];
            try {
                directories[i] = FSDirectory.getDirectory(directoryPath, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return directories;
    }
}