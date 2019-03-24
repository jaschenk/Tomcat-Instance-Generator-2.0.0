package jeffaschenk.infra.tomcat.instance.generator.util;

import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import jeffaschenk.infra.tomcat.instance.generator.model.InstancePorts;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * ValidationHelper
 *
 * Created by schenkje on 2/17/2017.
 */
public class ValidationHelper {
    /**
     * Do not Allow Instantiation of this Object.
     */
    private ValidationHelper(){}
    
    /**
     * Regular Express to Validate a Property Name.
     */
    protected static final String VALIDATE_PROPERTY_NAME_REGEX =
            "^[a-zA-Z0-9_\\.]*$";
    protected static final Pattern VALIDATE_PROPERTY_NAME_PATTERN = Pattern.compile(VALIDATE_PROPERTY_NAME_REGEX);

    protected static final String VALIDATE_JVM_OPTION_REGEX =
            "^[\\-]+[a-zA-Z0-9_\\.\\-:;=]*$";
    protected static final Pattern VALIDATE_JVM_OPTION_PATTERN = Pattern.compile(VALIDATE_JVM_OPTION_REGEX);

    protected static final String VALIDATE_INSTANCE_NAME_REGEX =
            "^[a-zA-Z0-9_\\-\\.]*$";
    protected static final Pattern VALIDATE_INSTANCE_NAME_PATTERN = Pattern.compile(VALIDATE_INSTANCE_NAME_REGEX);

    /**
     * Validate Numeric Data
     *
     * @param textField Field to Validate as Numeric
     * @return Integer containing transformed value or Null, if invalid Data!
     */
    public static Integer validateNumericData(String textField) {
        if (textField == null || textField.isEmpty()) {
            return null;
        }
        try {
            return  Integer.parseInt(textField);
        } catch(NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * Validate Port
     *
     * @param port Validate Port Range
     * @return boolean indicating valid or not.
     */
    public static boolean validatePort(Integer port) {
        if (port == null || port < 1024 || port > 65535) {
            return false;
        }
        return true;
    }

    /**
     * Validate Numeric Data
     *
     * @param port Validate Port Range
     * @return boolean indicating valid or not.
     */
    public static boolean validateOptionalPort(Integer port) {
        if (port == null) { return true; }
        if (port < 1024 || port > 65535) {
            return false;
        }
        return true;
    }

    /**
     * Validate Property Names
     * @param propertyName to be Validated
     * @return boolean to indicate if the Name is valid or not.
     */
    public static boolean validatePropertyName(String propertyName) {
        if (propertyName == null || propertyName.isEmpty()) {
            return false;
        } else if (propertyName.startsWith("#")) {
            return true;
        }
        return VALIDATE_PROPERTY_NAME_PATTERN.matcher(propertyName).find();
    }

    /**
     * Validate JVM Option
     * @param jvmOption to be Validated
     * @return boolean to indicate if the Name is valid or not.
     */
    public static boolean validateJVMOption(String jvmOption) {
        if (jvmOption == null || jvmOption.isEmpty()) {
            return false;
        } else if (jvmOption.startsWith("#")) {
            return true;
        }
        return VALIDATE_JVM_OPTION_PATTERN.matcher(jvmOption).find();
    }

    /**
     * Validate Instance Name
     * @param instanceName to be Validated
     * @return boolean to indicate if the Name is valid or not.
     */
    public static boolean validateInstanceName(String instanceName) {
        if (instanceName == null || instanceName.isEmpty()) {
            return false;
        }
        return VALIDATE_INSTANCE_NAME_PATTERN.matcher(instanceName).find();
    }

    /**
     * Validate currently Specified Ports ...
     *
     * @return boolean indicates if Ports are Valid or Not ...
     */
    public static boolean validatePrimaryPortsSpecified(TomcatInstance tomcatInstance) {

        /**
         * Validate All Instance Ports across all Instances ...
         */
        for(InstancePorts instancePorts : tomcatInstance.getInstancePorts()) {
            /**
             * Validate HTTP Port
             */
            if (!validatePort(instancePorts.getHttpPort())) {
                return badPortMessage("Primary HTTP", instancePorts.getHttpPort(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
            /**
             * Validate Shutdown Port
             */
            if (!validatePort(instancePorts.getShutdownPort())) {
                return badPortMessage("Shutdown", instancePorts.getShutdownPort(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
            /**
             * Validate AJP Port
             */
            if (!validatePort(instancePorts.getAjpPort())) {
                return badPortMessage("AJP", instancePorts.getAjpPort(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
            /**
             * Now Validate Optional debugging Port
             */
            if (!validatePort(instancePorts.getDebugPort())) {
                return badPortMessage("Debugging", instancePorts.getDebugPort(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
            /**
             * Now Validate Optional SNMP Port
             */
            if (!validatePort(instancePorts.getSnmpPort())) {
                return badPortMessage("SNMP", instancePorts.getSnmpPort(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
            /**
             * Now Validate Optional JMX Port
             */
            if (!validatePort(instancePorts.getJmxPort())) {
                return badPortMessage("JMX", instancePorts.getJmxPort(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
            /**
             * Now Validate Optional RMI#1 Port
             */
            if (!validatePort(instancePorts.getRmi1Port())) {
                return badPortMessage("RMI #1", instancePorts.getRmi1Port(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
            /**
             * Now Validate Optional RMI#2 Port
             */
            if (!validatePort(instancePorts.getRmi2Port())) {
                return badPortMessage("RMI #2", instancePorts.getRmi2Port(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
            /**
             * Now Validate Optional ManageCat Restart Port
             */
            if (!validatePort(instancePorts.getMcatRestartAgentPort())) {
                return badPortMessage("ManageCat Restart", instancePorts.getMcatRestartAgentPort(),
                        instancePorts.getInstanceName(), tomcatInstance.getInstanceName());
            }
        }
        /**
         * Now Check for Duplicates
         */
        if (validatePortsUnique(getListOfPrimaryPorts(tomcatInstance))) {
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, "  Duplicate Ports found, please fix YAML!",
                    true,true,false);
            Set<Integer> duplicates =
                    findDuplicates(getListOfPrimaryPorts(tomcatInstance));
            for(Integer port : duplicates) {
                ConsoleOutput.out(Color.WHITE_BOLD, "   ** Duplicate Port: %d",
                        false, true, true, port);
            }
            ConsoleOutput.issueReset();
            return false;
        }
    }

    /**
     * Obtain a List of Primary Ports.
     * @return List of Primary Ports for Instance.
     */
    private static List<Integer> getListOfPrimaryPorts(TomcatInstance tomcatInstance) {
        List<Integer> ports = new ArrayList<>();
        /**
         * For Each Instance defined accumulate the Ports
         */
        for(InstancePorts instancePorts : tomcatInstance.getInstancePorts()) {
            /**
             * Primary Ports, which are Required.
             */
            addPort(ports, instancePorts.getHttpPort());
            addPort(ports, instancePorts.getShutdownPort());
            addPort(ports, instancePorts.getAjpPort());
            /**
             * Optional Ports.
             */
            addPort(ports, instancePorts.getDebugPort());
            addPort(ports, instancePorts.getJmxPort());
            addPort(ports, instancePorts.getSnmpPort());
            addPort(ports, instancePorts.getRmi1Port());
            addPort(ports, instancePorts.getRmi2Port());
            addPort(ports, instancePorts.getMcatRestartAgentPort());
        }
        return ports;
    }

    /**
     * addPort -- simple Helper to add only a Specified Port.
     * @param ports Array
     * @param port To be checked if not null, added to Ports Array.
     */
    private static void addPort(List<Integer> ports, Integer port) {
        if (ports != null && port != null) {
            ports.add(port);
        }
    }

    /**
     * badPortMessage - Display Issue with specified Port!
     * @param portType - Type of Port
     * @param port - Port Number
     * @param instanceName - Base Instance Name
     * @param tcInstanceName - Tomcat Instance Name
     * @return boolean indicating issue.
     */
    private static boolean badPortMessage(String portType, Integer port, String instanceName, String tcInstanceName) {
        ConsoleOutput.out(Color.RED_BOLD, "  %s Port %d is not Valid for Instance Named: %s %s",
                true,true,true,
                portType, port, tcInstanceName, instanceName);
        return false;
    }


    /**
     * Validate that this list of Ports are Unique.
     *
     * @param instancePortsList List of ports to validate all are unique.
     * @return
     */
    public static boolean validateInstancePortsUnique(List<InstancePorts> instancePortsList) {
        List<Integer> ports = new ArrayList<>();
        for(InstancePorts instancePorts : instancePortsList)
        {
            // We leave out the HTTPS Port, as that will always be the same of '443'.
            ports.add(instancePorts.getHttpPort());
            ports.add(instancePorts.getAjpPort());
            ports.add(instancePorts.getShutdownPort());
            ports.add(instancePorts.getDebugPort());
            ports.add(instancePorts.getJmxPort());
            ports.add(instancePorts.getRmi1Port());
            ports.add(instancePorts.getRmi2Port());
            ports.add(instancePorts.getSnmpPort());
            ports.add(instancePorts.getMcatRestartAgentPort());
        }
        return validatePortsValidAndUnique(ports);
    }

    /**
     * Validate that this list of Ports are Unique.
     *
     * @param ports List of ports to validate all are unique.
     * @return boolean True if valid List of Ports, False if either duplicate found or invalid port number.
     */
    private static boolean validatePortsValidAndUnique(List<Integer> ports) {
        for(Integer port:ports) {
            if (port == null || port < 1024 || port > 65535){
                return false;
            }
        }
        Set<Integer> duplicates = findDuplicates(ports);
        if (duplicates == null || duplicates.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Validate that this list of Ports are Unique.
     *
     * @param ports List of ports to validate all are unique.
     * @return
     */
    protected static boolean validatePortsUnique(List<Integer> ports) {
          Set<Integer> duplicates = findDuplicates(ports);
          if (duplicates == null || duplicates.isEmpty()) {
              return true;
          } else {
              return false;
          }
    }

    /**
     * Helper Method to return Duplicates within a Collection.
     * @param list Collection to find Duplicates
     * @return Set of Duplicates found.
     */
    private static Set<Integer> findDuplicates(List<Integer> list) {
        Set<Integer> dups = new HashSet<>();
        Set<Integer> uniques = new HashSet<>();
        for(Integer value : list) {
            if (!uniques.add(value)) {
                dups.add(value);
            }
        }
        return dups;
    }

    /**
     * validateMemorySettings
     *
     * @param tomcatInstance reference
     * @return boolean indicates if current memory settings are correct/acceptable or not..
     */
    public static boolean validateMemorySettings(TomcatInstance tomcatInstance) {
         Integer check = validateMemorySettings("Xms",
                 tomcatInstance.getJvmOptionXms(), tomcatInstance.getInstanceName());
         check += validateMemorySettings("Xmx",
                 tomcatInstance.getJvmOptionXmx(), tomcatInstance.getInstanceName());
         check += validateMemorySettings("Xss",
                 tomcatInstance.getJvmOptionXss(), tomcatInstance.getInstanceName());
         check += validateMemorySettings("XXMaxMetaspaceSize",
                 tomcatInstance.getJvmOptionXXMaxMetaspaceSize(), tomcatInstance.getInstanceName());
         return check==4;
    }

    /**
     * validateMemorySettings
     *
     * @param memorySettingType string value providing information on type of memory setting.
     * @param memorySettingValue string value to be verified is a valid memory setting.
     * @return boolean indicates if current memory setting is correct/acceptable or not..
     */
    private static Integer validateMemorySettings(String memorySettingType, String memorySettingValue,
                                                  String tcInstanceName) {
         if (memorySettingValue != null && !memorySettingValue.isEmpty()) {
             for(String validMemorySetting : DefaultDefinitions.DEFAULT_JVM_MEMORY_OPTIONS) {
                 if (validMemorySetting.equalsIgnoreCase(memorySettingValue)) {
                     return 1;
                 }
             }
         }
        ConsoleOutput.out(Color.RED_BOLD,"%s Memory Settings of %s is not Valid for Instance Named: %s",
                true,true,true,
                memorySettingType, (memorySettingValue==null||memorySettingValue.isEmpty())?"Unknown":memorySettingValue,
                tcInstanceName);
        ConsoleOutput.issueReset();
        return 0;
    }

}
