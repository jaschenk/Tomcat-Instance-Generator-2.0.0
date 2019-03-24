package jeffaschenk.infra.tomcat.instance.generator.model;

import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * InstancePorts
 *
 * Defines an Instances Port designations, based upon the following Table:
 *
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
 *
 * @author schenkje
 */
@Data
public class InstancePorts {

    private String instanceName;
    private File instanceFolder;

    private Integer httpPort;
    private Integer httpsPort = 443;
    private Integer ajpPort;
    private Integer shutdownPort;
    private Integer jmxPort;
    private Integer snmpPort;
    private Integer rmi1Port;

    /**
     * Default no Parameter constructor for deserialization
     */
    public InstancePorts() {
    }

    private Integer rmi2Port;
    private Integer debugPort;
    private Integer mcatRestartAgentPort;



    /**
     * Default Constructor with all applicable parameters
     *
     * @param instanceName         - Instance Name
     * @param httpPort             - Http Port
     * @param httpsPort            - Https Port
     * @param ajpPort              - AJP Port
     * @param shutdownPort         - Shutdown Port
     * @param jmxPort              - JMX Port
     * @param snmpPort             - SNMP Port
     * @param rmi1Port             - RMI #1 Port
     * @param rmi2Port             - RMI #2 Port
     * @param debugPort            - Debugging Port
     * @param mcatRestartAgentPort - ManageCat Restart Agent Port
     */
    public InstancePorts(String instanceName,
                         Integer httpPort,
                         Integer httpsPort,
                         Integer ajpPort,
                         Integer shutdownPort,
                         Integer jmxPort,
                         Integer snmpPort,
                         Integer rmi1Port,
                         Integer rmi2Port,
                         Integer debugPort,
                         Integer mcatRestartAgentPort) {
        this.instanceName = instanceName;
        this.httpPort = httpPort;
        this.httpsPort = httpsPort;
        this.ajpPort = ajpPort;
        this.shutdownPort = shutdownPort;
        this.jmxPort = jmxPort;
        this.snmpPort = snmpPort;
        this.rmi1Port = rmi1Port;
        this.rmi2Port = rmi2Port;
        this.debugPort = debugPort;
        this.mcatRestartAgentPort = mcatRestartAgentPort;
    }

    /**
     * Constructor to read in a Map from a Parsed YAML Configuration File.
     *
     * @param instancePortsMap - Reference to Map
     */
    public InstancePorts(Map<String, Object> instancePortsMap) {
        this.instanceName = (String) instancePortsMap.get("instanceName");
        this.httpPort = (Integer) instancePortsMap.get("httpPort");
        this.httpsPort = (Integer) instancePortsMap.get("httpsPort");
        this.ajpPort = (Integer) instancePortsMap.get("ajpPort");
        this.shutdownPort = (Integer) instancePortsMap.get("shutdownPort");
        this.jmxPort = (Integer) instancePortsMap.get("jmxPort");
        this.snmpPort = (Integer) instancePortsMap.get("snmpPort");
        this.rmi1Port = (Integer) instancePortsMap.get("rmi1Port");
        this.rmi2Port = (Integer) instancePortsMap.get("rmi2Port");
        this.debugPort = (Integer) instancePortsMap.get("debugPort");
        this.mcatRestartAgentPort = (Integer) instancePortsMap.get("mcatRestartAgentPort");
    }
    
    public String getInstanceNameForBaseDirectoryName() {
        if (this.instanceName.startsWith("tc") && this.instanceName.length() > 2) {
            return this.instanceName.substring(2);
        }
        return this.instanceName;
    }

}
