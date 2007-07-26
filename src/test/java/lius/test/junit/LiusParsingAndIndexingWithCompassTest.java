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
import junit.framework.TestCase;
import lius.index.DefaultParsingService;
import lius.index.ParsingService;

import org.compass.core.Compass;
import org.compass.core.CompassDetachedHits;
import org.compass.core.CompassTemplate;
import org.compass.core.CompassTransaction;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.lucene.LuceneResource;
import org.compass.core.lucene.engine.LuceneSearchEngine;
import org.compass.core.spi.InternalCompassSession;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class LiusParsingAndIndexingWithCompassTest extends TestCase {
    protected Compass compass;
    protected CompassTemplate compassTemplate;
    private ParsingService parsingService;

    public LiusParsingAndIndexingWithCompassTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CompassConfiguration config = new CompassConfiguration()
                .configure("/config/compass.cfg.xml");
        compass = config.buildCompass();
        compass.getSearchEngineIndexManager().deleteIndex();
        compass.getSearchEngineIndexManager().createIndex();
        compassTemplate = new CompassTemplate(compass);
        parsingService = new DefaultParsingService(new ClassPathResource(
                "liusConfig.xml"));
    }

    protected void tearDown() throws Exception {
        compass.close();
        super.tearDown();
    }

    public void testAddLuceneDocument() throws Exception {
        InternalCompassSession session = (InternalCompassSession) compass
                .openSession();
        CompassTransaction tx = session.beginTransaction();
        LuceneResource luceneResource = new LuceneResource("fileAlias",parsingService
                .parse(new ClassPathResource("testFiles/testPDF.pdf")), -1,
                (LuceneSearchEngine) session.getSearchEngine());
        session.save(luceneResource);
        tx.commit();
        session.close();
    }

    public void testRetrieveLuceneDocument() throws Exception {
        populate();
        CompassDetachedHits hits = compassTemplate.findWithDetach("institutionnel");
        assertEquals(1,hits.getLength());
        assertEquals(0.0191,hits.getHits()[0].getScore(),1e-3);
        hits = compassTemplate.findWithDetach("fullPath:institutionnel");
        assertEquals(0,hits.getLength());
        hits = compassTemplate.findWithDetach("content:institutionnel");
        assertEquals(1,hits.getLength());
        assertEquals(0.0203,hits.getHits()[0].getScore(),1e-3);
        hits = compassTemplate.findWithDetach("unknown:institutionnel");
        assertEquals(0,hits.getLength());
    }
    // public void testPdfFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testPDF.pdf"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(14606, document.toString().length());
    // assertEquals(7189, document.get("content").length());
    // }
    //
    // public void testWordFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testWORD.doc"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(281, document.toString().length());
    // assertEquals(26, document.get("content").length());
    // }
    //
    // public void testRtfFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testRTF.rtf"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(276, document.toString().length());
    // assertEquals(24, document.get("content").length());
    // }
    //
    // public void testXmlFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testXML.xml"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(3913, document.toString().length());
    // assertEquals(1878, document.get("content").length());
    // }
    //
    // public void testExcelFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testEXCEL.xls"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(284, document.toString().length());
    // assertEquals(27, document.get("content").length());
    // }
    //
    // public void testPptFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testPPT.ppt"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(554, document.toString().length());
    // assertEquals(163, document.get("content").length());
    // }
    //
    // public void testMp3FactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testMp3.mp3"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(922, document.toString().length());
    // assertEquals(0, document.get("content").length());
    // }
    //
    // public void _testOpenOfficeFactoryIndexingBad() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testOO.sxw"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(100, document.toString().length());
    // assertEquals(100, document.get("content").length());
    // }
    //
    // public void testOpenOfficeFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testOO1.sxw"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(387, document.toString().length());
    // assertEquals(129, document.get("content").length());
    // }
    //
    // public void testOpenOffice2FactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testOO2.odt"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(413, document.toString().length());
    // assertEquals(160, document.get("content").length());
    // }
    //
    // public void testZipFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testZIP.zip"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(15638, document.toString().length());
    // assertEquals(200, document.get("content").length());
    // }
    //
    // public void testTextFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testTXT.txt"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(278, document.toString().length());
    // assertEquals(25, document.get("content").length());
    // }
    //
    // public void testHtmlFactoryIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testHTML.html"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(362, document.toString().length());
    // assertEquals(66, document.get("content").length());
    // }
    //
    // public void testMixedIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testMixedIndexing"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(15778, document.toString().length());
    // assertEquals(7389, document.get("content").length());
    // }
    //
    // public void testUrlIndexing() throws MalformedURLException {
    // Document document = parsingService.parse(new UrlResource(
    // "http://www.doculibre.com/index.html"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(3634, document.toString().length());
    // assertEquals(1722, document.get("content").length());
    // }
    //
    // public void testNodeIndexing() {
    // Document document = parsingService.parse(new ClassPathResource(
    // "testFiles/testXMLNode.xml"));
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNotNull(document.get("fullPath"));
    // assertEquals(618, document.toString().length());
    // assertEquals(183, document.get("content").length());
    // }
    //
    // public void testBeanIndexing() {
    // Personne personne = new Personne();
    // personne.setNom("Benjelloun");
    // personne.setPrenom("Rida");
    // personne.setAdresse("Quebec Canada");
    // Document document = parsingService.parse(personne);
    // assertNotNull(document);
    // assertNotNull(document.get("content"));
    // assertNull(document.get("fullPath"));
    // assertEquals(253, document.toString().length());
    // assertEquals(30, document.get("content").length());
    // assertEquals("Benjelloun Rida Quebec Canada ", document.get("content"));
    // }

    private void populate() {
        InternalCompassSession session = (InternalCompassSession) compass
                .openSession();
        CompassTransaction tx = session.beginTransaction();
        LuceneResource luceneResource = new LuceneResource("fileAlias",parsingService
                .parse(new ClassPathResource("testFiles/testPDF.pdf")), -1,
                (LuceneSearchEngine) session.getSearchEngine());
        session.save(luceneResource);
        tx.commit();
        session.close();
    }
}
