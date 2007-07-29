package lius.test.junit;

import org.apache.log4j.Logger;

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
public class LiusParsingTest extends TestCase {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(LiusParsingTest.class);
    private ParsingService parsingService;

    public LiusParsingTest(String name) {
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
        assertNotNull(document.get("content"));
        assertEquals(
                "s une suite de projets numériques comme le projet Érudit, la",
                document.get("content").substring(200, 260));
        assertEquals(7189, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testWordFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testWORD.doc"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals("Test d’indexation Word\r\n\r\n", document.get("content"));
        assertEquals(26, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testRtfFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testRTF.rtf"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals("Test d’indexation Word\n\n", document.get("content")
                .substring(0, 24));
        assertEquals(24, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testXmlFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testXML.xml"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals(
                "La dimension géographique de l'articulation vie professionne",
                document.get("content").substring(0, 60));
        assertEquals(1878, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testExcelFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testEXCEL.xls"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals("Test Excel Rida Benjelloun ", document.get("content")
                .substring(0, 27));
        assertEquals(27, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testPptFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testPPT.ppt"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals("Cliquez pour modifier le style du titre", document.get(
                "content").substring(3, 42));
        assertEquals(163, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testMp3FactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testMp3.mp3"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals("", document.get("content").substring(0, 0));
        assertEquals(0, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void _testOpenOfficeFactoryIndexingBad() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testOO.sxw"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals("", document.get("content").substring(0, 60));
        assertEquals(100, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testOpenOfficeFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testOO1.sxw"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals(
                "Test d'indexation d'OpenOffice  OpenOffice.org 1.1.4 (Win32)",
                document.get("content").substring(0, 60));
        assertEquals(129, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testOpenOffice2FactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testOO2.odt"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals(
                "Test OpenOffice version 2  OpenOffice.org/2.0$Win32 OpenOffi",
                document.get("content").substring(0, 60));
        assertEquals(160, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testZipFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testZIP.zip"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals(
                "Archimède et Lius  Rida Benjelloun  Java  XML  XSLT  JDOM  I",
                document.get("content").substring(0, 60));
        assertEquals(200, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testTextFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testTXT.txt"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals("Test d'indexation de Txt ", document.get("content")
                .substring(0, 25));
        assertEquals(25, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testHtmlFactoryIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testHTML.html"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals(
                " Test Indexation Html  Test Indexation Html Test Indexation ",
                document.get("content").substring(0, 60));
        assertEquals(66, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testMixedIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testMixedIndexing"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        String[] values = document.getValues("content");
        assertEquals(1, values.length);
        assertTrue(document.get("content").contains("Archimède et Lius  Rida Benjelloun  Java  XML  XSLT  JDOM  I"));
        assertEquals(7389, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testUrlIndexing() throws MalformedURLException {
        UrlResource urlResource = new UrlResource(
                "http://www.doculibre.com/index.html");
        if (urlResource.exists()) {
            Document document = parsingService.parse(urlResource);
            assertNotNull(document);
            assertNotNull(document.get("content"));
            assertEquals(
                    "  DocuLibre - Solutions libres en gestion de l'information  ",
                    document.get("content").substring(0, 60));
            assertEquals(1722, document.get("content").length());
            assertNotNull(document.get("fullPath"));
        } else {
            logger
                    .warn("The test testUrlIndexing wasn't runned because couldn't be found.");
        }
    }

    public void testNodeIndexing() {
        Document document = parsingService.parse(new ClassPathResource(
                "testFiles/testXMLNode.xml"));
        assertNotNull(document);
        assertNotNull(document.get("content"));
        assertEquals(
                "Livre 1  reda benjelloun    Programmer en Java  claude de la",
                document.get("content").substring(0, 60));
        assertEquals(183, document.get("content").length());
        assertNotNull(document.get("fullPath"));
    }

    public void testBeanIndexing() {
        Personne personne = new Personne();
        personne.setNom("Benjelloun");
        personne.setPrenom("Rida");
        personne.setAdresse("Quebec Canada");
        Document document = parsingService.parse(personne);
        assertNotNull(document);
        assertNull(document.get("fullPath"));
        assertNotNull(document.get("content"));
        assertEquals("Benjelloun Rida Quebec Canada ", document.get("content")
                .substring(0, 30));
        assertEquals(30, document.get("content").length());
    }
}
