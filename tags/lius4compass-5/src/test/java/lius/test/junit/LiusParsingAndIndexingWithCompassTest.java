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
import java.util.List;

import junit.framework.TestCase;
import lius.index.DefaultParsingService;
import lius.index.ParsingService;
import lius.test.beans.Personne;

import org.compass.core.Compass;
import org.compass.core.CompassDetachedHits;
import org.compass.core.CompassTemplate;
import org.compass.core.CompassTransaction;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.lucene.LuceneResource;
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

    @Override
    protected void tearDown() throws Exception {
        compass.close();
        super.tearDown();
    }

    public void testAddLuceneDocument() throws Exception {
        InternalCompassSession session = (InternalCompassSession) compass
                .openSession();
        CompassTransaction tx = session.beginTransaction();
        LuceneResource luceneResource = parsingService.parseLuceneResource(
                "fileAlias", new ClassPathResource("testFiles/testPDF.pdf"),
                session);
        session.save(luceneResource);
        tx.commit();
        session.close();
    }

    public void testRetrieveLuceneDocument() throws Exception {
        populate();
        CompassDetachedHits hits = compassTemplate.findWithDetach("test");
        assertEquals(14, hits.getLength());
        assertEquals(0.618, hits.getHits()[0].getScore(), 1e-1);
        hits = compassTemplate.findWithDetach("fullPath:institutionnel");
        assertEquals(0, hits.getLength());
        hits = compassTemplate.findWithDetach("content:institutionnel");
        assertEquals(2, hits.getLength());
        assertEquals(0.172, hits.getHits()[0].getScore(), 1e-2);
        hits = compassTemplate.findWithDetach("unknown:institutionnel");
        assertEquals(0, hits.getLength());
        hits = compassTemplate.findWithDetach("Quebec");
        assertEquals(1, hits.getLength());
        assertEquals(1.0, hits.getHits()[0].getScore(), 1e-2);
    }

    private void populate() {
        InternalCompassSession session = (InternalCompassSession) compass
                .openSession();
        CompassTransaction tx = session.beginTransaction();
        List<LuceneResource> luceneResources = parsingService
                .parseLuceneResources("fileAlias", new ClassPathResource(
                        "testFiles"), session, false);
        for (LuceneResource luceneResource : luceneResources) {
            session.save(luceneResource);
        }
        Personne personne = new Personne();
        personne.setNom("Benjelloun");
        personne.setPrenom("Rida");
        personne.setAdresse("Quebec Canada");
        personne.setId("theId");
        session.save(parsingService.parseLuceneResource("beanAlias", personne,
                session));
        tx.commit();
        session.close();
    }
}
