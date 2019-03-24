package jeffaschenk.infra.tomcat.instance.generator.knowledgebase;

import jeffaschenk.infra.tomcat.instance.generator.model.InstanceJVMOption;
import jeffaschenk.infra.tomcat.instance.generator.model.InstancePorts;
import jeffaschenk.infra.tomcat.instance.generator.model.InstanceProperty;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static jeffaschenk.infra.tomcat.instance.generator.knowledgebase.ReplacementDefinitions.TOMCAT_INSTANCE_HOSTNAME_TAG;
import static jeffaschenk.infra.tomcat.instance.generator.knowledgebase.ReplacementDefinitions.TOMCAT_INSTANCE_NAME_TAG;
import static jeffaschenk.infra.tomcat.instance.generator.knowledgebase.ReplacementDefinitions.TOMCAT_PRIMARY_PORT_TAG;
import static jeffaschenk.infra.tomcat.instance.generator.knowledgebase.ReplacementDefinitions.MANAGECAT_LICENSE_KEY_TAG;

/**
 * DefaultDefinitions
 *
 * Created by schenkje on 2/15/2017.
 */
public final class DefaultDefinitions {
    /**
     * Do not Allow Instantiation of this Object.
     */
    private DefaultDefinitions(){}
    
    /**
     * External Property for specifying the Apache Mirror site.
     */
    public static final String TOMCAT_APACHE_MIRROR_PROPERTY_NAME =
            "apache.mirror.head.url";
    /**
     * Number of Default Retries before we give up attempting to get a Mirror download.
     */
    public static final Integer TOMCAT_APACHE_MIRROR_MAX_RETRIES = 4;

    /**
     * External Property for specifying if colorized output is acceptable.
     * Default: true, to disable specify property value of false.
     */
    public static final String COLOR_PROPERTY_NAME =
            "color";
    /**
     * ManageCat Specifics...
     */
    public static final String MANAGECAT_LICENSE_KEY_PROPERTY_NAME =
            "tc.managecat.license.key";

    public static final String DEFAULT_MANAGECAT_LICENSE_KEY_VALUE =
            "00000000000000000000000000000000";
    
    public static final String MANAGECAT_LICENSE_KEY_INTERNAL_PROPERTY_NAME =
            "MANAGECAT_LICENSE_KEY_TAG";

    /**
     * Constants
     */
    protected static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    protected static SimpleDateFormat DATE_TIME_ZONE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a z");

    /**
     * Common Names for Tomcat Home and Base
     */
    public static final String TOMCAT_HOME_FOLDER_NAME = "tomcat-home";
    public static final String TOMCAT_BASE_FOLDER_PREFIX = "tomcat-base-";
    public static final String TOMCAT_BASE_FOLDER_TEMPLATE = TOMCAT_BASE_FOLDER_PREFIX + "0x";
    public static final String TOMCAT_MOUNTS_FOLDER_NAME = "tomcat-mounts";

    /**
     * Constants
     */
    public static final String ZIP_ARCHIVE_SUFFIX = ".zip";

    /**
     * Default Environments
     */
    public static final String[] DEFAULT_ENVIRONMENTS =
            {
                    "DEV",
                    "STG", "STG2", "STG3",
                    "ST", "ST2", "ST3",
                    "MO", "MO2", "MO3",
                    "PRD"
            };
    public static final String DEFAULT_ENVIRONMENT_SELECTED = DEFAULT_ENVIRONMENTS[0];

    /**
     * Default Environments
     */
    public static final String[] DEFAULT_ENVIRONMENT_INSTANCES =
            {
                    "tc01", "tc02", "tc03", "tc04",
                    "tc05", "tc06", "tc07", "tc08"
            };
    public static final String DEFAULT_ENVIRONMENT_INSTANCE_SELECTED = DEFAULT_ENVIRONMENT_INSTANCES[0];

    /**
     * Default Tomcat Versions,
     * these values must match what is available on the Tomcat Available Archives, Archive Short Name.
     */
    public static final String[] DEFAULT_TOMCAT_VERSIONS =
            {
                    "v8.5", "v9.0"
            };
    public static final String DEFAULT_TOMCAT_VERSION_SELECTED = DEFAULT_TOMCAT_VERSIONS[0];

    /**
     * Tomcat Full Version to Short Have Name
     */
    public static final Map<String,String> TOMCAT_VERSIONS_TO_SHORT_NAME = new HashMap<>();
    static {
            TOMCAT_VERSIONS_TO_SHORT_NAME.put(DEFAULT_TOMCAT_VERSIONS[0], "v85");
            TOMCAT_VERSIONS_TO_SHORT_NAME.put(DEFAULT_TOMCAT_VERSIONS[1], "v90");
    }


    /**
     * Default Port Matrix by Environment Instance
     * The following depicts the ports used for each Tomcat JVM Instance running on the same base server Operating System.
     * SERVER	HTTP	HTTPS	AJP	    SHUT	JMX	    SNMP	RMI #1	RMI #2	DEBUG*	MANAGECAT RESTART AGENT PORT**
     * TC01 –	8081	 443  	8091	8051	9001	9011	9021	9031	9041	48081
     * TC02 –   8082	     443 	8092	8052	9002	9012	9022	9032	9042	48082
     * TC03 –   8083	     443 	8093	8053	9003	9013	9023	9033	9043	48083
     * TC04 –	8084	 443 	8094	8054	9004	9014	9024	9034	9044	48084
     * TC05 –	8085	 443 	8095	8055	9005	9015	9025	9035	9045	48085
     * TC06 –	8086	 443	8096	8056	9006	9016	9026	9036	9046	48086
     * TC07 –   8087	     443	8097	8057	9007¥	9017¥	9027	9037	9047	48087
     * TC08 –   8088  	 443	8098	8058	9008	9018	9028	9038	9048	48088
     *
     * * Denotes DEBUG port should only be established in the Staging environments only.
     *
     * ** Denotes ManageCat Restart Agent Port only for all Environments, with exception of Developer Environments at this time.
     *
     * ¥ Denotes on Windows Developer Instances SNMP is disabled, and the port 9007 is a common windows port,
     * so on Windows the JMX port will be 9017 instead of the conflicting 9007 on WIN only.
     *
     * HTTPS port 443, redirects back to Apache-Head-End for the Environment.  SSL demarcation is at the Apache-Head-End.
     * 
     */
    public static final Map<String, InstancePorts> DEFAULT_INSTANCE_PORT_MAP = new TreeMap<>();
    static {
                DEFAULT_INSTANCE_PORT_MAP.put("tc01",
                        new InstancePorts("tc01",8081, 443, 8091, 8051,
                                9001, 9011, 9021, 9031, 9041, 48081));

                DEFAULT_INSTANCE_PORT_MAP.put("tc02",
                        new InstancePorts("tc02",8082, 443, 8092, 8052,
                                9002, 9012, 9022, 9032, 9042, 48082));

                DEFAULT_INSTANCE_PORT_MAP.put("tc03",
                        new InstancePorts("tc03",8083, 443, 8093, 8053,
                                9003, 9013, 9023, 9033, 9043, 48083));

                DEFAULT_INSTANCE_PORT_MAP.put("tc04",
                        new InstancePorts("tc04",8084, 443, 8094, 8054,
                                9004, 9014, 9024, 9034, 9044, 48084));

                DEFAULT_INSTANCE_PORT_MAP.put("tc05",
                        new InstancePorts("tc05",8085, 443, 8095, 8055,
                                9005, 9015, 9025, 9035, 9045, 48085));

                DEFAULT_INSTANCE_PORT_MAP.put("tc06",
                        new InstancePorts("tc06",8086, 443, 8096, 8056,
                                9006, 9016, 9026, 9036, 9046, 48086));

                DEFAULT_INSTANCE_PORT_MAP.put("tc07",
                        new InstancePorts("tc07",8087, 443, 8097, 8057,
                                9007, 9017, 9027, 9037, 9047, 48087));

                DEFAULT_INSTANCE_PORT_MAP.put("tc08",
                        new InstancePorts("tc08",8088, 443, 8098, 8058,
                                9008, 9018, 9028, 9038, 9048, 48088));

    }
    /**
     * Default Ports
     */
    public static final Integer DEFAULT_PRIMARY_PORT;
    public static final Integer DEFAULT_SECURE_PORT;
    public static final Integer DEFAULT_SHUTDOWN_PORT;
    public static final Integer DEFAULT_AJP_PORT;
    public static final Integer DEFAULT_DEBUG_PORT;

    static {
        DEFAULT_PRIMARY_PORT =
                DEFAULT_INSTANCE_PORT_MAP.get(DEFAULT_ENVIRONMENT_INSTANCE_SELECTED).getHttpPort();

        DEFAULT_SECURE_PORT =
                DEFAULT_INSTANCE_PORT_MAP.get(DEFAULT_ENVIRONMENT_INSTANCE_SELECTED).getHttpsPort();

        DEFAULT_SHUTDOWN_PORT =
                DEFAULT_INSTANCE_PORT_MAP.get(DEFAULT_ENVIRONMENT_INSTANCE_SELECTED).getShutdownPort();

        DEFAULT_AJP_PORT =
                DEFAULT_INSTANCE_PORT_MAP.get(DEFAULT_ENVIRONMENT_INSTANCE_SELECTED).getAjpPort();

        DEFAULT_DEBUG_PORT =
                DEFAULT_INSTANCE_PORT_MAP.get(DEFAULT_ENVIRONMENT_INSTANCE_SELECTED).getDebugPort();
    }

    /**
     * Available Protocols
     */
    public static final String[] DEFAULT_CATALINA_PROTOCOLS =
            {
                    "HTTP/1.1", "org.apache.coyote.http11.Http11NioProtocol"
            };
    public static final String DEFAULT_CATALINA_PROTOCOL_SELECTED = DEFAULT_CATALINA_PROTOCOLS[0];
    //public static final String DEFAULT_CATALINA_SECURE_PROTOCOL_SELECTED = DEFAULT_CATALINA_PROTOCOLS[1];

    public static final String AJP_NIO_PROTOCOL = "org.apache.coyote.ajp.AjpNioProtocol";

    /**
     * Java JVM Heap Sizes
     */
    public static final String[] DEFAULT_JVM_MEMORY_OPTIONS =
            {
              "None", "64m", "128m", "192m", "256m", "320m", "384m", "448m", "512m",
                    "640m", "768m", "896m",
                    "1g", "1280m", "1536m",
                    "2g", "2560m",
                    "3g", "3584m",
                    "4g", "4608m",
                    "5g", "5632m",
                    "6g", "6656m",
                    "7g", "7680m",
                    "8g"
            };
    public static final String DEFAULT_JVM_MEMORY_OPTIONS_SELECTED = DEFAULT_JVM_MEMORY_OPTIONS[15];
    public static final String DEFAULT_JVM_MEMORY_SS_OPTION_SELECTED = DEFAULT_JVM_MEMORY_OPTIONS[4];
    public static final String DEFAULT_JVM_MEMORY_METASPACE_OPTION_SELECTED = DEFAULT_JVM_MEMORY_OPTIONS[10];

    /**
     * Default Management Properties
     */
    public static final List<InstanceProperty> DEFAULT_MANAGEMENT_PROPERTY_ROWS = new ArrayList<>();
    static {
        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.controller.url","http://rh-tst-01:8080/controller/");

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.collector.url","http://rh-tst-01:8080/collector/");

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.console.agent.groupname","Development");

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.console.agent.servername", TOMCAT_INSTANCE_NAME_TAG);

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.console.agent.hostname", TOMCAT_INSTANCE_HOSTNAME_TAG);

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.console.agent.port", TOMCAT_PRIMARY_PORT_TAG);

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.console.agent.contextname","console");

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.console.agent.secure","false");

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "com.managecat.console.agent.accountkey", MANAGECAT_LICENSE_KEY_TAG);

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "# com.sun.management.jmxremote","");

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "# com.sun.management.jmxremote.port","65420"); 

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "# com.sun.management.jmxremote.ssl","false");

        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_MANAGEMENT_PROPERTY_ROWS,
                "# com.sun.management.jmxremote.authenticate","false");
    }

    /**
     * Default JVM Options
     */
    public static final List<InstanceJVMOption> DEFAULT_JVM_OPTION_ROWS = new ArrayList<>();
    static {

        ADD_DEFAULT_VALUE_TO_JVM_OPTIONS("-server");

        ADD_DEFAULT_VALUE_TO_JVM_OPTIONS(
                "-agentlib:jdwp=transport=dt_socket,address=${TOMCAT_DEBUG_PORT},server=y,suspend=n");
    }
    
    /**
     * Add Default Value To Management Properties
     *
     * @param jvmOption of Entry to be Added
     */
    private static void ADD_DEFAULT_VALUE_TO_JVM_OPTIONS(String jvmOption) {
        DEFAULT_JVM_OPTION_ROWS.add(new InstanceJVMOption(jvmOption));
    }

    /**
     * Default Management Properties
     */
    private static final List<InstanceProperty> DEFAULT_INSTANCE_PROPERTY_ROWS = new ArrayList<>();
    static {
        ADD_DEFAULT_VALUE_TO_PROPERTIES(DEFAULT_INSTANCE_PROPERTY_ROWS,
                "com.moda.welcome.message", "\"Hello to Tomcat Generation Utility\"");
    }

    /**
     * Add Default Value To Management Properties
     *
     * @param defaultList List of default Entries
     * @param name of Entry to be Added
     * @param value of Entry to be Added
     */
    private static void ADD_DEFAULT_VALUE_TO_PROPERTIES(List<InstanceProperty> defaultList,
                                                          String name, String value) {
        InstanceProperty instancePropertyRow = new InstanceProperty();
        instancePropertyRow.setPropertyName(name);
        instancePropertyRow.setPropertyValue(value);
        defaultList.add(instancePropertyRow);
    }

    /**
     * Additional directories to be Created during Customization Processing ...
     */
    public static final String[] ADDITIONAL_DIRECTORIES_TO_BE_ADDED = {};

    /**
     * Additional directories to be Created during Customization Processing ...
     */
    public static final String[] EMPTY_DIRECTORIES_TO_BE_SEEDED = {"logs", "work"};

    /**
     * Additional directories READMEs to be Created during Customization Processing ...
     */
    public static final String[] EMPTY_DIRECTORIES_README_CONTENTS = {"* Contains Tomcat Instance logs.",
             "* Contains 'work' Directory for Catalina Engine."};

    /**
     * Additional Tomcat Mounts directories to be Seeded with Placeholder files ...
     */
    public static final String[] EMPTY_MOUNTS_DIRECTORIES_TO_BE_SEEDED = {"appData", "properties"};
    /**
     * Additional directories READMEs to be Created during Customization Processing ...
     */
    public static final String[] EMPTY_MOUNTS_DIRECTORIES_README_CONTENTS = {"* Contains AppData, normally an NFS Mount.",
            "* Contains Tomcat  Application Runtime Property Files."};

    /**
     * Additional 'lib' Artifacts to be Written ...
     */
    public static final String[] ADDITIONAL_LIB_ARTIFACTS_TO_BE_ADDED = {"agent.jar", "sqljdbc.jar"};

    /**
     * Additional 'lib' Artifacts to be Written ...
     */
    public static final String[] ADDITIONAL_EXTERNAL_LIB_ARTIFACTS_TO_BE_ADDED = {"mcatlib.zip"};

    /**
     * Additional Resource Directories for Home
     */
    public static final String[] ADDITIONAL_HOME_RESOURCE_ARTIFACTS_TO_BE_ADDED = {"jre/README.txt",
            "sbin/tc01.bat", "sbin/tc01.sh", "sbin/tc01_clean.bat", "sbin/tc01_MRA.sh"};

    /**
     * Additional 'webapps' for Instance Management to be Written ...
     */
    public static final String[] AVAILABLE_MANAGEMENT_WEBAPPS = {"console_v105.war", "console_v201.war"};
    public static final String DEFAULT_MANAGEMENT_WEBAPP = AVAILABLE_MANAGEMENT_WEBAPPS[0];
    public static final String DEFAULT_MANAGEMENT_WEBAPP_NAME = "console.war";

    /**
     * 'conf' Files to be Written and Filtered ...
     */
    public static final String[] CONF_FILES_TO_BE_ADDED_FILTERED = {"catalina.properties", "context.xml",
                    "server.xml", "tomcat-users.xml", "web.xml"};

    /**
     * 'bin' Files to be Written and Filtered ...
     */
    public static final String[] BIN_FILES_TO_BE_ADDED_FILTERED = {"agent.bat", "setenv.bat", "setenv.sh" };

    /**
     * Additional 'webapps' for ROOT Application to be Written ...
     */
    public static final String[] AVAILABLE_ROOT_WEBAPP_FILES = {"error.jsp", "error-404.jsp", "index.jsp", "moda.png"};

    /**
     * 'webapps' which should be purged ...
     */
    public static final String[] WEBAPPS_TO_PURGED = {"docs", "examples", "host-manager"};

    /**
     * Resource Files to Copy for an Upgrade Kit.
     */
    public static final String[] UPGRADE_KIT_SCRIPT_FILES_TO_BE_FILTERED = {"tc_upgrade_to_v8.5.xx.sh"};

    /**
     * Additional File Directory Constants
     */
    public static final String WEBAPPS = "webapps";
    public static final String CONF = "conf";


}
