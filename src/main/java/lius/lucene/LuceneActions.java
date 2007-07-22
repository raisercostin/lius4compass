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
import java.io.File;
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
import lius.index.IndexService;
import lius.index.Indexer;
import lius.index.IndexerFactory;
import lius.index.util.LiusUtils;
import lius.search.LiusHit;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Classe permettant d'effectuer des actions relatives � Lucene. <br/><br/>
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
     * Methode permettant de construire un objet de type "Lucene Document" �
     * partir de plusieurs collections contenant des informations sur les
     * documents � indexer. Cette m�thode est utilis�e pour l'indexation mixte.
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
                                + ") " + " : Texte ajout�");
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

    /**
     * M�thode permettant d'ins�rer une liste de documents Lucene dans l'index.
     * <br/><br/>Method that inserts a list of Lucene documents in the index.
     */
    public void save(List luceneDocs, IndexWriter writer, LiusConfig lc) {
        for (int i = 0; i < luceneDocs.size(); i++)
            save((Document) luceneDocs.get(i), writer, lc);
    }

    /**
     * M�thode permettant d'ins�rer un document Lucene dans l'index <br/><br/>
     * M�thod that inserts a Lucene document in the index.
     */
    public void save(Document luceneDoc, IndexWriter writer, LiusConfig lc) {
        try {
            writer.addDocument(luceneDoc);
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        }
        if (lc.getOptimizeValue() != null) {
            if (lc.getOptimize()) {
                try {
                    writer.optimize();
                } catch (IOException e) {
                    LiusUtils.doOnException( e);
                }
            }
        }
    }

    public void index(Document doc, String indexDir, LiusConfig lc)
            throws IOException {
        Analyzer analyzer = AnalyzerFactory.getAnalyzer(lc);
        IndexWriter writer = null;
        try {
            boolean createIndex = createIndexValue(lc.getCreateIndex(),
                    indexDir);
            Directory fsDir = FSDirectory.getDirectory(indexDir, createIndex);
            writer = new IndexWriter(fsDir, analyzer, createIndex);
            setIndexWriterProps(writer, lc);
            save(doc, writer, lc);
        } catch (Exception e) {
            LiusUtils.doOnException( e);
        } finally {
            unLock(indexDir);
            if (writer != null) {
                writer.close();
            }
        }
    }

    public Directory index(Document doc, Directory indexDir, LiusConfig lc)
            throws IOException {
        Analyzer analyzer = AnalyzerFactory.getAnalyzer(lc);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDir, analyzer, true);
            setIndexWriterProps(writer, lc);
            save(doc, writer, lc);
        } catch (Exception e) {
            LiusUtils.doOnException( e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return indexDir;
    }

    public void index(String toIndex, String indexDir, LiusConfig lc)
            throws IOException {
        Analyzer analyzer = AnalyzerFactory.getAnalyzer(lc);
        IndexWriter writer = null;
        try {
            boolean createIndex = createIndexValue(lc.getCreateIndex(),
                    indexDir);
            Directory fsDir = FSDirectory.getDirectory(indexDir, createIndex);
            writer = new IndexWriter(fsDir, analyzer, createIndex);
            setIndexWriterProps(writer, lc);
            fileDirectoryIndexing(toIndex, indexDir, lc);
        } catch (Exception e) {
            LiusUtils.doOnException("Error trying to index.", e);
        } finally {
            unLock(indexDir);
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void recursifIndexing(File f, String indexDir, LiusConfig lc) {
        IndexWriter iw = null;
        try {
            iw = openIndex(indexDir, lc);
            fileDirectoryProcessing(f, indexDir, lc, iw);
            iw.optimize();
            iw.close();
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        } finally {
            if (iw != null) {
                try {
                    unLock(indexDir);
                    iw.close();
                } catch (IOException e1) {
                    LiusUtils.doOnException(e1);
                }
            }
        }
    }

    public void indexSubDirectories(File f, String indexDir, LiusConfig lc) {
        recursifIndexing(f, indexDir, lc);
    }

    private void fileDirectoryProcessing(File f, String indexDir,
            LiusConfig lc, IndexWriter iw) throws IOException {
        Indexer indexer = null;
        if (f.isFile()) {
            indexer = IndexerFactory.getIndexer(f, lc);
            if (indexer != null) {
                Document doc = populateLuceneDoc(indexer
                        .getPopulatedLiusFields());
                doc.add(new Field("filePath", f.getAbsolutePath(),
                        Field.Store.YES, Field.Index.UN_TOKENIZED));
                iw.addDocument(doc);
            }
        } else {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                fileDirectoryProcessing(files[i], indexDir, lc, iw);
            }
        }
    }

    public void addIndexes(Directory[] directoriesToIndex, String indexDir,
            LiusConfig lc) {
        Analyzer analyzer = AnalyzerFactory.getAnalyzer(lc);
        IndexWriter writer = null;
        try {
            boolean createIndex = createIndexValue(lc.getCreateIndex(),
                    indexDir);
            Directory fsDir = FSDirectory.getDirectory(indexDir, createIndex);
            writer = new IndexWriter(fsDir, analyzer, createIndex);
            setIndexWriterProps(writer, lc);
            writer.addIndexes(directoriesToIndex);
        } catch (Exception e) {
            LiusUtils.doOnException( e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                LiusUtils.doOnException( e);
            }
        }
    }

    public IndexWriter openIndex(String indexDir, LiusConfig lc)
            throws IOException {
        Analyzer analyzer = AnalyzerFactory.getAnalyzer(lc);
        IndexWriter writer = null;
        try {
            boolean createIndex = createIndexValue(lc.getCreateIndex(),
                    indexDir);
            Directory fsDir = FSDirectory.getDirectory(indexDir, createIndex);
            writer = new IndexWriter(fsDir, analyzer, createIndex);
            setIndexWriterProps(writer, lc);
        } catch (Exception e) {
            LiusUtils.doOnException( e);
            unLock(indexDir);
            if (writer != null) {
                writer.close();
            }
        }
        return writer;
    }

    /**
     * M�thode appel�e par la m�thode index(). Elle permet d'effectuer le
     * processus d'indexation. <br/><br/>Method called by index(). It processes
     * the indexation.
     */
    private void fileDirectoryIndexing(String toIndex, String indexDir,
            LiusConfig lc) throws IOException {
        String sep = System.getProperty("file.separator");
        File typFD = new File(toIndex);
        if (typFD.isFile()) {
            fileProcessing(typFD, indexDir, lc);
        } else if (typFD.isDirectory()) {
            File[] liste = (new File(toIndex)).listFiles();
            for (int i = 0; i < liste.length; i++) {
                String fileToIndexB = toIndex + sep + liste[i].getName();
                File fileToIndexBF = new File(fileToIndexB);
                if (fileToIndexBF.isDirectory()) {
                    fileDirectoryIndexing(fileToIndexB, indexDir, lc);
                } else {
                    fileProcessing(fileToIndexBF, indexDir, lc);
                }
            }
        }
    }

    /**
     * M�thode appel�e par la la m�thode fileDirectoryIndexing(), pour indexer
     * en fonction du type de fichier. <br/><br/>Method called by
     * fileDirectoryIndexing(), for indexing related to the file type.
     */
    private void fileProcessing(File fileToIndex, String indexDir, LiusConfig lc) {
        IndexService indexer = null;
        indexer = IndexerFactory.getIndexer(fileToIndex, lc);
        if (indexer != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Index file [" + fileToIndex.getAbsolutePath()
                        + "] ...");
            }
            indexer.index(indexDir);
        } else {
            logger.warn("Couldn't find indexer for file ["
                    + fileToIndex.getAbsolutePath() + "]. No indexing.");
        }
    }

    /**
     * M�thode permettant de forcer l'ouverture de l'index de Lucene quand il
     * est ferm�. <br/><br/>Method that force the opening of Lucene index when
     * it is closed.
     */
    public void unLock(String indexDir) {
        try {
            Directory directory = FSDirectory.getDirectory(indexDir, false);
            IndexReader.open(directory);
            if (IndexReader.isLocked(directory)) {
                IndexReader.unlock(directory);
            }
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        }
    }

    public void deleteAllDocuments(String indexDir) {
        try {
            Directory directory = FSDirectory.getDirectory(indexDir, false);
            IndexReader ir = IndexReader.open(directory);
            int num = ir.numDocs();
            for (int i = 0; i <= num - 1; i++) {
                ir.deleteDocument(i);
            }
            ir.close();
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        }
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
            LiusUtils.doOnException( e);
        }
        return documentsList;
    }

    public void unDeleteAllDocuments(String indexDir) {
        try {
            Directory directory = FSDirectory.getDirectory(indexDir, false);
            IndexReader ir = IndexReader.open(directory);
            ir.undeleteAll();
            ir.close();
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        }
    }

    public void newIndex(String indexDir) {
        try {
            Directory directory = FSDirectory.getDirectory(indexDir, true);
        } catch (IOException ex) {
            LiusUtils.doOnException(ex);
        }
    }

    /**
     * M�thode permettant d'initialiser les propri�t�s de l'index si ces
     * derni�res ont �t� plac�es dans le fichier de configuration. <br/><br/>
     * Method that initializes the properties of the index if those were placed
     * in the configuration file.
     */
    private void setIndexWriterProps(IndexWriter writer, LiusConfig lc) {
        if (lc.getMergeFactor() != null)
            writer
                    .setMergeFactor((new Integer(lc.getMergeFactor()))
                            .intValue());
        if (lc.getMaxMergeDocs() != null)
            writer.setMaxMergeDocs((new Integer(lc.getMaxMergeDocs()))
                    .intValue());
    }

    /**
     * M�thode permettant d'effacer un document dans l'index. Elle prend comme
     * arguments le r�pertoire de l'index, le nom du champs et le contenu
     * recherch�. <br/><br/>Method that erases a document from the index. Its
     * parameters are the directory of the index, the name of the field and the
     * content searched.
     */
    public int deleteDoc(String indexDir, String field, String content) {
        int nbDelete = 0;
        try {
            Directory fsDir = FSDirectory.getDirectory(indexDir, false);
            IndexReader indexReader = IndexReader.open(fsDir);
            Term t = new Term(field, content);
            nbDelete = indexReader.deleteDocuments(t);
            indexReader.close();
            logger.debug("Document supprim�");
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        }
        return nbDelete;
    }

    /**
     * M�thode permettant d'effacer un document dans l'index. Elle prend comme
     * arguments le r�pertoire de l'index et un objet de type Lucene Term. <br/>
     * <br/>Method that erases a document from the index. Its parameters are the
     * directory of the index and a Lucene term object.
     */
    public int deleteDoc(String indexDir, Term t) {
        int nbDelete = 0;
        try {
            Directory fsDir = FSDirectory.getDirectory(indexDir, false);
            IndexReader indexReader = IndexReader.open(fsDir);
            nbDelete = indexReader.deleteDocuments(t);
            indexReader.close();
            logger.info("Document supprim�");
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        }
        return nbDelete;
    }

    public void deleteDoc(String indexDir, int docNum) {
        try {
            Directory fsDir = FSDirectory.getDirectory(indexDir, false);
            IndexReader indexReader = IndexReader.open(fsDir);
            indexReader.deleteDocument(docNum);
            indexReader.close();
            logger.info("Document supprim�");
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        }
    }

    /**
     * M�thode permettant de mettre � jour un document dans l'index. Elle prend
     * comme arguments le r�pertoire de l'index, un objet de type lucene Term,
     * le fichier � indexer � la place de celui trouv� et le fichier XML de
     * configuration qui servira � l'indexation. <br/><br/>Method that updated
     * a document in the index. Its parameters are the directory of the index,
     * an Lucene Term object, the file to index in place of the one found and
     * the XML configuration file which will serve for indexing.
     */
    public void updateDoc(String rep, Term t, String fileToReindex,
            LiusConfig liusConfig) throws IOException {
        deleteDoc(rep, t);
        index(fileToReindex, rep, liusConfig);
        logger.info("Document mis � jour");
    }

    /**
     * M�thode permettant de mettre � jour un document dans l'index. Elle prend
     * comme arguments le repertoire de l'index, le nom du champs qui doit
     * contenir la valeur recherch�e, le fichier � indexer � la place de celui
     * trouv� et le fichier XML de configuration qui servira � la r�indexation.
     * <br/><br/>Method that updates a document in the index. Its parameters
     * are the directory of the index, the name of the field which will contain
     * the searched value, the searched value, the file to index in place of the
     * one found and the XML configuration which will serve for indexing.
     */
    public void updateDoc(String rep, String field, String content,
            String fileToReindex, LiusConfig liusConfig) throws IOException {
        deleteDoc(rep, field, content);
        index(fileToReindex, rep, liusConfig);
        logger.info("Document mis � jour");
    }

    public boolean createIndexValue(String valueCreateIndex, String indexDir) {
        boolean createIndex = false;
        if (valueCreateIndex.equals("true"))
            createIndex = true;
        else if (valueCreateIndex.equals("false"))
            createIndex = false;
        else if (valueCreateIndex.equals("auto")) {
            createIndex = !indexExists(indexDir);
        }
        return createIndex;
    }

    /**
     * M�thode permettant de v�rifier le r�pertoire de sortie de l'index. S'il
     * n'existe pas il sera cr�e. <br/><br/>Method for verifying the output
     * directory of index. If it does not exist it will be created.
     */
    public boolean indexExists(String indexDir) {
        return IndexReader.indexExists(indexDir);
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