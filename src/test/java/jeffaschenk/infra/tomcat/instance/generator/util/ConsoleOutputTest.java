package jeffaschenk.infra.tomcat.instance.generator.util;

import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

/**
 * ConsoleOutputTest
 *
 * Created by schenkje on 2/17/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsoleOutputTest {

    private final static String TEST_MESSAGE_01 = "%s";
    private final static String TEST_REPLACEMENT_01 = "C:\\Users\\schenkje\\MyTomcatWorkSpace\\TomcatInstanceGenerator\\src\\test\\resources\\yaml\\Generate-MyContainers.yaml";

    private final static String TEST_MESSAGE_02 = "%d %s %d %d";

    @Test
    public void testMessageRsolver_01() {
        String message = ConsoleOutput.resolve(TEST_MESSAGE_01, TEST_REPLACEMENT_01);
        assertEquals(TEST_REPLACEMENT_01, message);
    }

    @Test
    public void testMessageRsolver_02() {
        String message = ConsoleOutput.resolve(TEST_MESSAGE_02, 10, "FooBar", 20, 30);
        assertEquals("10 FooBar 20 30", message);
    }

}
