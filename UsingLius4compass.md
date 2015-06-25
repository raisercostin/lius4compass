#A sample of using this API
You can see a sample piece of code using this API

```
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
```