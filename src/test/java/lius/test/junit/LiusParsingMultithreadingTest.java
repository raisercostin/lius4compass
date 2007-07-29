package lius.test.junit;

import junit.framework.TestCase;
import lius.index.DefaultParsingService;
import lius.index.ParsingService;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class LiusParsingMultithreadingTest extends TestCase {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(LiusParsingMultithreadingTest.class);
    private ParsingService parsingService;

    public LiusParsingMultithreadingTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        parsingService = new DefaultParsingService(new ClassPathResource(
                "liusConfig.xml"));
    }

    public void testOnOneThread() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[1];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    Document document = parsingService
                            .parse(new ClassPathResource("testMixedIndexing"));
                    assertNotNull(document);
                    assertEquals(7389, document.get("content").length());
                }
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].run();
        }
        assertDurationSmallerThan("oneThreadMixed", 2000, start, 100);
    }

    public void testOnMultipleThreads() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    Document document = parsingService
                            .parse(new ClassPathResource("testMixedIndexing"));
                    assertNotNull(document);
                    assertEquals(7389, document.get("content").length());
                }
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        assertDurationSmallerThan("multipleThreadsMixed", 5000, start, 100);
    }

    public void testOnOneThreadsWord() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[1];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    Document document = parsingService
                            .parse(new ClassPathResource(
                                    "testFiles/testWORD.doc"));
                    assertNotNull(document);
                    assertEquals(26, document.get("content").length());
                }
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].run();
        }
        assertDurationSmallerThan("oneThreadWord", 200, start, 100);
    }

    public void testOnMultipleThreadsWord() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    Document document = parsingService
                            .parse(new ClassPathResource(
                                    "testFiles/testWORD.doc"));
                    assertNotNull(document);
                    assertEquals(26, document.get("content").length());
                }
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        assertDurationSmallerThan("multipleThreadsWord", 100, start, 100);
    }

    private void assertDurationSmallerThan(String message,
            long expectedDuration, long start, long deltaPercent) {
        long actualDuration = System.currentTimeMillis() - start;
        if (logger.isInfoEnabled()) {
            logger.info(message + ": expectedTime=" + expectedDuration
                    + ", actualTime=" + actualDuration);
        }
        if (actualDuration * 100 > expectedDuration * (100 + deltaPercent)) {
            fail(message + ": Actual duration [" + actualDuration
                    + "] is bigger than expected duration[" + expectedDuration
                    + " + " + deltaPercent + "%].");
        }
    }
}
