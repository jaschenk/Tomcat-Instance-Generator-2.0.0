package jeffaschenk.infra.tomcat.instance.generator.processors;

import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;
import jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceAFRYZProcessHelper;
import jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceProcessCustomizationHelper;
import jeffaschenk.infra.tomcat.instance.generator.util.ValidationHelper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TomcatInstanceAFRYZProcessHelperTest
 *
 * Created by schenkje on 2/21/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TomcatInstanceAFRYZProcessHelperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatInstanceAFRYZProcessHelperTest.class);

    /**
     * Test YAML File.
     */
    private static final String TEST_YAML_FILE =
            "src/test/resources/yaml/Test-TomcatInstance.yaml";

    /**
     * Test YAML File.
     */
    private static final String TEST_YAML_FILE_WITH_VALIDATION_FAILURE =
            "src/test/resources/yaml/Test-TomcatInstance_WithValidationFailure.yaml";

    private static final String TEST_YAML_FILE_FROM_PREVIOUS_SERIALIZATION =
            "src/test/resources/yaml/Test-TomcatInstanceFullGeneration.yaml";

    /**
     * Test the Validation of Reading in a YAML File.
     */
    @Test
    public void test01() throws Exception {

        Yaml yaml = new Yaml(new Constructor(Map.class));
        InputStream input = new FileInputStream(new File(TEST_YAML_FILE));
        Map<String,Object> mapFromYaml = (Map) yaml.load(input);
        assertNotNull(mapFromYaml);

        TomcatInstance tomcatInstance = new TomcatInstance(mapFromYaml);
        assertNotNull(tomcatInstance);
        assertEquals("0c4cf885-11ca-4b5e-8df0-66769626f841", tomcatInstance.getTomcatInstanceUUID());
        System.out.println(tomcatInstance.toString());
    }

    /**
     * Test the Validation of Reading in a YAML File using Helper Methods.
     */
    @Test
    public void test02() throws Exception {
        TomcatInstance tomcatInstance =
                TomcatInstanceAFRYZProcessHelper.loadYAMLConfigurationForInstance(TEST_YAML_FILE);
        assertNotNull(tomcatInstance);
        assertEquals(2, tomcatInstance.getInstancePorts().size());
        assertEquals("0c4cf885-11ca-4b5e-8df0-66769626f841", tomcatInstance.getTomcatInstanceUUID());
        assertEquals("Test-FooBar-999", tomcatInstance.getInstanceName());
        //assertEquals("tc01", tomcatInstance.getInstancePorts().get(0).getInstanceName());
        assertTrue(tomcatInstance.getInstancePorts().get(0).getHttpPort() == 8081);
        assertTrue(tomcatInstance.getInstancePorts().get(0).getAjpPort() == 8091);
        assertTrue(tomcatInstance.isInstanceManagement());
        assertFalse(tomcatInstance.isCompressed());
        System.out.println(tomcatInstance.toString());
    }

    @Test
    public void test03() throws Exception {
        String hostname = TomcatInstanceProcessCustomizationHelper.getThisDefaultInstanceHostName();
        assertNotNull(hostname);
        assertFalse(hostname.equalsIgnoreCase("localhost"));
        System.out.println("Hostname obtained: "+hostname);
    }

    @Test
    public void test04() throws Exception {
        String ipAddress = TomcatInstanceProcessCustomizationHelper.getThisDefaultInetAddress();
        assertNotNull(ipAddress);
        assertFalse(ipAddress.equalsIgnoreCase("localhost"));
        System.out.println("IP Address obtained: "+ipAddress);
    }

    @Test
    public void test05() throws Exception {
        assertTrue(TomcatInstanceAFRYZProcessHelper.zipFile(LOGGER,
                "target\\test_instance_archive.zip", "src\\main\\resources",true));
        File zipFile = new File("target\\test_instance_archive.zip");
        assertTrue(zipFile.exists());
    }

    @Test
    public void test06() throws Exception {
        String value = TomcatInstanceProcessCustomizationHelper.getNIXValue("Hello");
        assertNotNull(value);
        assertEquals("Hello", value);

        value = TomcatInstanceProcessCustomizationHelper.getNIXValue("\"Hello there!\"");
        assertNotNull(value);
        assertEquals("\\\"Hello there!\\\"", value);

    }

    /**
     * Test the Validation of Reading in a YAML File.
     */
    @Test
    public void test07() throws Exception {

        Yaml yaml = new Yaml(new Constructor(Map.class));
        InputStream input = new FileInputStream(new File(TEST_YAML_FILE_WITH_VALIDATION_FAILURE));
        Map<String,Object> mapFromYaml = (Map) yaml.load(input);
        assertNotNull(mapFromYaml);

        TomcatInstance tomcatInstance = new TomcatInstance(mapFromYaml);

        assertNotNull(tomcatInstance);
        assertFalse(ValidationHelper.validateInstancePortsUnique(tomcatInstance.getInstancePorts()));
        assertEquals(1, tomcatInstance.getInstancePorts().size());
        System.out.println(tomcatInstance.toString());
    }

    /**
     * Test the Validation of Reading in a YAML File.
     */
    @Test
    public void test08() throws Exception {

        Yaml yaml = new Yaml(new Constructor(Map.class));
        InputStream input = new FileInputStream(new File(TEST_YAML_FILE_FROM_PREVIOUS_SERIALIZATION));
        Map<String,Object> mapFromYaml = (Map) yaml.load(input);
        assertNotNull(mapFromYaml);

        TomcatInstance tomcatInstance = new TomcatInstance(mapFromYaml);

        assertNotNull(tomcatInstance);
        assertTrue(ValidationHelper.validateInstancePortsUnique(tomcatInstance.getInstancePorts()));
        assertEquals(8, tomcatInstance.getInstancePorts().size());
        System.out.println(tomcatInstance.toString());
    }


}
