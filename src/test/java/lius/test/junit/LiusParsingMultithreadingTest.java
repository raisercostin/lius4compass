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

    public void testOnMultipleThreads() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    Document document = parsingService
                            .parse(new ClassPathResource("testMixedIndexing"));
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
                    assertEquals(186, document.toString().length());
                }
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        assertDurationSmallerThan(297, start, 30);
    }

    private void assertDurationSmallerThan(long expectedDuration, long start,
            long deltaPercent) {
        long actualDuration = System.currentTimeMillis() - start;
        if (logger.isInfoEnabled()) {
            logger.info("expectedTime=" + expectedDuration + ", actualTime="
                    + actualDuration);
        }
        if (actualDuration * 100 > expectedDuration * (100 + deltaPercent)) {
            fail("Actual duration [" + actualDuration
                    + "] is bigger than expected duration[" + expectedDuration
                    + " + " + deltaPercent + "%].");
        }
    }
}
