package jeffaschenk.infra.tomcat.instance.generator.util;

import jeffaschenk.infra.tomcat.instance.generator.model.TomcatArchive;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatAvailableArchives;
import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * TomcatAvailableArchiveHelperTest
 *
 * Created by jeffaschenk@gmail.com on 7/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TomcatAvailableArchiveHelperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatAvailableArchiveHelperTest.class);

    @Before
    public void initialize() {
        System.setProperty(DefaultDefinitions.TOMCAT_APACHE_MIRROR_PROPERTY_NAME,
                "http://apache.cs.utah.edu/tomcat");
    }

    /**
     * Test the String Builder Data Replacement.
     */
    @Test
    public void test01() {
        TomcatAvailableArchives tomcatAvailableArchives = TomcatArchiveUtility.resolveApacheMirror();
        assertNotNull(tomcatAvailableArchives);

        assertNotNull(tomcatAvailableArchives.getApacheMirrorHeadUrl());
        assertEquals("http://apache.cs.utah.edu/tomcat", tomcatAvailableArchives.getApacheMirrorHeadUrl());

        assertNotNull(tomcatAvailableArchives.getArchives());
        assertEquals(0, tomcatAvailableArchives.getArchives().size());

    }

    /**
     * Test the String Builder Data Replacement.
     */
    @Test
    public void test02() {
        TomcatAvailableArchives tomcatAvailableArchives = TomcatArchiveUtility.resolveApacheMirror();
        assertNotNull(tomcatAvailableArchives);

        TomcatArchiveUtility.getLatest(LOGGER, tomcatAvailableArchives);
        assertNotNull(tomcatAvailableArchives);
        /**
         * We should only have the latest Archives available, 8.5.xx and 9.0.xx.
         *
         * We may need to change once 9.x.00 appears.
         */
        assertEquals(2, tomcatAvailableArchives.getArchives().size());

        for(String key : tomcatAvailableArchives.getArchives().keySet()) {
            LOGGER.info("{}", tomcatAvailableArchives.getArchives().get(key).toString());
        }

        TomcatArchive tomcatArchive =
                tomcatAvailableArchives.getLatestAvailableArchiveByVersionName("v8.5");
        if (tomcatArchive == null || tomcatArchive.getName() == null) {
            fail("Unable to determine a Download Archive for Tomcat Version, Very Bad!");
        }
        LOGGER.info("For {},  Found Tomcat Archive: {}", "v8.5", tomcatArchive);
    }

}
