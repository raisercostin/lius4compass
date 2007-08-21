package lius.test.junit;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;
import lius.config.LiusConfig;
import lius.config.LiusConfigBuilder;
import lius.index.Indexer;
import lius.index.IndexerFactory;
import lius.index.javaobject.BeanIndexer;
import lius.index.mixedindexing.MixedIndexer;
import lius.index.xml.XmlNodeIndexer;
import lius.test.beans.Personne;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class LiusTestIndexation extends TestCase {
    private String indexDir;
    // private File classDir;
    private File toIndex;
    private LiusConfig lc;
    private Indexer indexer = null;

    public LiusTestIndexation(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        indexDir = new File("target/indexDir").getAbsolutePath();
        lc = LiusConfigBuilder.getSingletonInstance().getLiusConfig(new ClassPathResource("liusConfig.xml"));
    }

    public void testPdfFactoryIndexing() {
        String string = "testPDF.pdf";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testWordFactoryIndexing() {
        String string = "testWORD.doc";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    private File getFile(String fileName) {
        try {
            return new ClassPathResource("testFiles/" + fileName).getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void testRtfFactoryIndexing() {
        String string = "testRTF.rtf";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testXmlFactoryIndexing() {
        String string = "testXML.xml";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testExcelFactoryIndexing() {
        String string = "testEXCEL.xls";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testPptFactoryIndexing() {
        String string = "testPPT.ppt";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void _testOpenOfficeFactoryIndexingBad() {
        String string = "testOO.sxw";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testOpenOfficeFactoryIndexing() {
        String string = "testOO1.sxw";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testOpenOffice2FactoryIndexing() {
        String string = "testOO2.odt";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testZipFactoryIndexing() {
        String string = "testZIP.zip";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testTextFactoryIndexing() {
        String string = "testTXT.txt";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    /*
     * public void testMp3FactoryIndexing() { toIndex = new
     * File(classDir.getParent() + File.separator + "ExempleFiles" +
     * File.separator + "testFiles" + File.separator + "testMP3.mp3"); indexer =
     * IndexerFactory.getIndexer(toIndex, lc); indexer.index(indexDir); }
     */
    public void testHtmlFactoryIndexing() {
        String string = "testHTML.html";
        toIndex = getFile(string);
        indexer = IndexerFactory.getIndexer(toIndex, lc);
        indexer.index(indexDir);
    }

    public void testMixedIndexing() throws IOException {
        toIndex = new ClassPathResource("testMixedIndexing").getFile();
        indexer = new MixedIndexer();
        indexer.setUp(lc);
        indexer.setMixedContentsObj(toIndex);
        indexer.index(indexDir);
    }

    public void testUrlIndexing() throws MalformedURLException {
        URL url = new URL("http://www.doculibre.com/index.html");
        indexer = IndexerFactory.getIndexer(url, lc);
        indexer.index(indexDir);
    }

    public void testNodeIndexing() throws FileNotFoundException {
        String string = "testXMLNode.xml";
        toIndex = getFile(string);
        indexer = new XmlNodeIndexer();
        indexer.setUp(lc);
        indexer.setStreamToIndex(new FileInputStream(toIndex));
        indexer.index(indexDir);
    }

    public void testBeanIndexing() {
        Personne personne = new Personne();
        personne.setNom("Benjelloun");
        personne.setPrenom("Rida");
        personne.setAdresse("Quebec Canada");
        indexer = new BeanIndexer();
        indexer.setUp(lc);
        indexer.setObjectToIndex(personne);
        indexer.index(indexDir);
    }
}