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
public class LiusParsingMultithreadingTest extends TestCase {
    private ParsingService parsingService;

    public LiusParsingMultithreadingTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        parsingService = new DefaultParsingService(new ClassPathResource(
                "liusConfig.xml"));
    }

    public void testOnMultipleThreads() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    Document document = parsingService
                            .parseMixedContent(new ClassPathResource(
                                    "testMixedIndexing"));
                    assertNotNull(document);
                    assertEquals(8199, document.toString().length());
                }
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        assertDurationSmallerThan(5000, start, 30);
    }

    private void assertDurationSmallerThan(long expectedDuration, long start, long deltaPercent) {
        long actualDuration = System.currentTimeMillis() - start;
        if (actualDuration*100 > expectedDuration*(100+deltaPercent)) {
            fail("Actual duration [" + actualDuration
                    + "] is bigger than expected duration[" + expectedDuration
                    + " + "+deltaPercent+"%].");
        }
    }
}
