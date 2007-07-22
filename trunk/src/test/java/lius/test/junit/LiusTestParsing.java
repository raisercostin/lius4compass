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
import java.net.MalformedURLException;

import junit.framework.TestCase;
import lius.index.DefaultParsingService;
import lius.index.ParsingService;
import lius.test.beans.Personne;

import org.apache.lucene.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class LiusTestParsing extends TestCase {
    // private String indexDir;
    // private File classDir;
    // private File toIndex;
    // private LiusConfig lc;
    // private IndexService indexer = null;
    private ParsingService parsingService;

    public LiusTestParsing(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        parsingService = new DefaultParsingService(new ClassPathResource(
                "liusConfig.xml"));
    }

    public void testPdfFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testPDF.pdf"));
        assertNotNull(document);
        assertEquals(7348, document.toString().length());
    }

    public void testWordFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testWORD.doc"));
        assertNotNull(document);
        assertEquals(186, document.toString().length());
    }

    public void testRtfFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testRTF.rtf"));
        assertNotNull(document);
        assertEquals(183, document.toString().length());
    }

    public void testXmlFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testXML.xml"));
        assertNotNull(document);
        assertEquals(1966, document.toString().length());
    }

    public void testExcelFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testEXCEL.xls"));
        assertNotNull(document);
        assertEquals(188, document.toString().length());
    }

    public void testPptFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testPPT.ppt"));
        assertNotNull(document);
        assertEquals(322, document.toString().length());
    }

    public void _testOpenOfficeFactoryIndexingBad() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testOO.sxw"));
        assertNotNull(document);
        assertEquals(100, document.toString().length());
    }

    public void testOpenOfficeFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testOO1.sxw"));
        assertNotNull(document);
        assertEquals(189, document.toString().length());
    }

    public void testOpenOffice2FactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testOO2.odt"));
        assertNotNull(document);
        assertEquals(184, document.toString().length());
    }

    public void testZipFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testZIP.zip"));
        assertNotNull(document);
        assertEquals(8090, document.toString().length());
    }

    public void testTextFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testTXT.txt"));
        assertNotNull(document);
        assertEquals(184, document.toString().length());
    }

    /*
     * public void testMp3FactoryIndexing() { toIndex = new
     * File(classDir.getParent() + File.separator + "ExempleFiles" +
     * File.separator + "testFiles" + File.separator + "testMP3.mp3"); indexer =
     * IndexerFactory.getIndexer(toIndex, lc); indexer.index(indexDir); }
     */
    public void testHtmlFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testHTML.html"));
        assertNotNull(document);
        assertEquals(227, document.toString().length());
    }

    public void testMixedIndexing() {
        Document document = parsingService.parseMixedContent(new ClassPathResource(
                "testMixedIndexing"));
        assertNotNull(document);
        assertEquals(8199, document.toString().length());
    }

    public void testUrlIndexing() throws MalformedURLException {
        Document document = parsingService.parse(new UrlResource(
                "http://www.doculibre.com/index.html"));
        assertNotNull(document);
        assertEquals(1780, document.toString().length());
    }

    public void testNodeIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testXMLNode.xml"));
        assertNotNull(document);
        assertEquals(366, document.toString().length());
    }

    public void testBeanIndexing() {
        Personne personne = new Personne();
        personne.setNom("Benjelloun");
        personne.setPrenom("Rida");
        personne.setAdresse("Quebec Canada");
        Document document = parsingService.parse(personne);
        assertNotNull(document);
        assertEquals(175, document.toString().length());
    }
}
