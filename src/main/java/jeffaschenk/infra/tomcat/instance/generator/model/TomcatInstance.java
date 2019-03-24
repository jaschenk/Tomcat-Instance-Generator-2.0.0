package jeffaschenk.infra.tomcat.instance.generator.model;

import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import lombok.Data;

import java.io.File;
import java.util.*;


/**
 * TomcatInstance
 * <p>
 * Created by jeffaschenk@gmail.com on 2/16/2017.
 */
@Data
public final class TomcatInstance {
    /**
     * Tomcat GUID
     */
    private final String tomcatInstanceUUID;

    /**
     * Tomcat Instance Name
     */
    private final String instanceName;

    /**
     * Tomcat Environment Name
     */
    private final String environmentName;

    /**
     * Tomcat Version
     */
    private final String tomcatVersion;
    
    /**
     * Compress Indicator, if True the resulting Tomcat Instance will be Zipped up...
     */
    private final boolean compressed;

    /**
     * JVM Option Minimum Heap.
     */
    private final String jvmOptionXms;

    /**
     * JVM Option Maximum Heap.
     */
    private final String jvmOptionXmx;

    /**
     * JVM Option Thread Stack Size.
     */
    private final String jvmOptionXss;

    /**
     * JVM Option Java8 Metaspace Size
     */
    private final String jvmOptionXXMaxMetaspaceSize;

    /**
     * Additional JVM Options
     */
    private final List<InstanceJVMOption> jvmOptions;

    /**
     * Indicator that Instance Management should be established
     */
    private final boolean instanceManagement;

    /**
     * Instance Management Properties to be set during Build...
     */
    private final List<TomcatInstanceProperty> instanceManagementProperties;

    /**
     * Instance Properties to be set during Build...
     */
    private final List<TomcatInstanceProperty> instanceProperties;

    /**
     * Instance Ports
     */
    private List<InstancePorts> instancePorts;

    /**
     * Archive Associated to this Tomcat Instance.
     */
    private TomcatArchive tomcatArchive;

    /**
     * Destination Folder
     */
    private File destinationFolder;

    /**
     * Core Folder,
     * where our original uncompressed Archive resides,
     * whose parent is the destinationFolder.
     */
    private File coreFolder;

    /**
     * Tomcat Home Directory,
     * where our tomcat-home resides,
     * whose parent is the destinationFolder.
     */
    private File tcHomeFolder;

    /**
     * Tomcat Base Directory Template,
     * where our tomcat-base-0x resides,
     * whose parent is the destinationFolder.
     */
    private File tcBaseFolder;

    /**
     * Tomcat Mounts Directory,
     * where our tomcat-mounts resides,
     * whose parent is the destinationFolder.
     */
    private File tcMountsFolder;

    /**
     * Tomcat Instance Default Constructor
     *
     * @param instanceName      Name of Tomcat Instance
     * @param environmentName   Environment Name
     * @param tomcatVersion     Tomcat Version
     * @param destinationFolder Destination Folder for Instance
     * @param compressed        Indicator
     */
    public TomcatInstance(String instanceName, String environmentName, String tomcatVersion, File destinationFolder,
                          boolean compressed) {
        this.tomcatInstanceUUID = UUID.randomUUID().toString();
        this.instanceName = instanceName;
        this.environmentName = environmentName;
        this.tomcatVersion = tomcatVersion;
        this.destinationFolder = destinationFolder;
        this.compressed = compressed;
        /**
         * Set Default Ports for Instance
         */
        this.instancePorts = new ArrayList();
        this.instancePorts.add(DefaultDefinitions.DEFAULT_INSTANCE_PORT_MAP.get("tc01"));
        /**
         * Set Default JVM Options.
         */
        this.jvmOptionXms = DefaultDefinitions.DEFAULT_JVM_MEMORY_OPTIONS_SELECTED;
        this.jvmOptionXmx = DefaultDefinitions.DEFAULT_JVM_MEMORY_OPTIONS_SELECTED;
        this.jvmOptionXss = DefaultDefinitions.DEFAULT_JVM_MEMORY_OPTIONS[0];
        this.jvmOptionXXMaxMetaspaceSize = DefaultDefinitions.DEFAULT_JVM_MEMORY_OPTIONS[0];
        this.jvmOptions = new ArrayList<>(0);
        /**
         * Set the Instance Management as not used.
         */
        this.instanceManagement = false;
        this.instanceManagementProperties = new ArrayList<>(0);
        /**
         * Set Additional Instance Properties...
         */
        this.instanceProperties = new ArrayList<>(0);
    }

    /**
     * Default Constructor with all applicable Properties to drive the Generation process.
     *
     * @param instanceName                 Name of Tomcat Instance
     * @param environmentName              Environment Name
     * @param tomcatVersion                Tomcat Version
     * @param destinationFolder            Destination Folder for Instance
     * @param compressed                   Indicator
     * @param instancePorts                Ports defined for Instance
     * @param jvmOptionXms                 JVM Minimum Heap Size.
     * @param jvmOptionXmx                 JVM Maximum Heap Size.
     * @param jvmOptionXss                 JVM Thread Stack Size.
     * @param jvmOptionXXMaxMetaspaceSize  JVM Metaspace Size.
     * @param jvmOptions                   Additional JVM Options.
     * @param instanceManagement           Indicator to include instance Management Properties.
     * @param instanceManagementProperties Instance Management Properties.
     * @param instanceProperties           Instance Properties.
     */
    public TomcatInstance(String instanceName, String environmentName, String tomcatVersion,
                          File destinationFolder, boolean compressed,
                          List<InstancePorts> instancePorts,
                          String jvmOptionXms, String jvmOptionXmx, String jvmOptionXss, String jvmOptionXXMaxMetaspaceSize,
                          List<InstanceJVMOption> jvmOptions,
                          boolean instanceManagement,
                          List<TomcatInstanceProperty> instanceManagementProperties,
                          List<TomcatInstanceProperty> instanceProperties) {
        this.tomcatInstanceUUID = UUID.randomUUID().toString();
        this.instanceName = instanceName;
        this.environmentName = environmentName;
        this.tomcatVersion = tomcatVersion;
        this.destinationFolder = destinationFolder;
        this.compressed = compressed;
        this.instancePorts = instancePorts;
        this.jvmOptionXms = jvmOptionXms;
        this.jvmOptionXmx = jvmOptionXmx;
        this.jvmOptionXss = jvmOptionXss;
        this.jvmOptionXXMaxMetaspaceSize = jvmOptionXXMaxMetaspaceSize;
        this.jvmOptions = jvmOptions;
        this.instanceManagement = instanceManagement;
        this.instanceManagementProperties = instanceManagementProperties;
        this.instanceProperties = instanceProperties;
    }

    /**
     * Constructor to read in a Map from a Parsed YAML Configuration File.
     * @param tomcatInstanceMap
     */
    public TomcatInstance(Map<String,Object> tomcatInstanceMap) {
        if (tomcatInstanceMap.get("instanceUUID") == null) {
            this.tomcatInstanceUUID = UUID.randomUUID().toString();
        } else {
            this.tomcatInstanceUUID = (String) tomcatInstanceMap.get("instanceUUID");
        }
        this.instanceName = (String) tomcatInstanceMap.get("instanceName");
        this.environmentName = (String) tomcatInstanceMap.get("environmentName");
        this.tomcatVersion = (String)  tomcatInstanceMap.get("tomcatVersion");
        this.destinationFolder = hydrateFileValue("destinationFolder", tomcatInstanceMap);
        this.coreFolder = hydrateFileValue("coreFolder", tomcatInstanceMap);
        this.tcHomeFolder = hydrateFileValue("tcHomeFolder", tomcatInstanceMap);
        this.tcBaseFolder = hydrateFileValue("tcBaseFolder", tomcatInstanceMap);
        this.compressed = (boolean) tomcatInstanceMap.get("compressed");
        this.jvmOptionXms = (String) tomcatInstanceMap.get("jvmOptionXms");
        this.jvmOptionXmx = (String) tomcatInstanceMap.get("jvmOptionXmx");
        this.jvmOptionXss = (String) tomcatInstanceMap.get("jvmOptionXss");
        this.jvmOptionXXMaxMetaspaceSize = (String) tomcatInstanceMap.get("jvmOptionXXMaxMetaspaceSize");
        this.instanceManagement = (boolean) tomcatInstanceMap.get("instanceManagement");

        this.jvmOptions = new ArrayList<>();
        List<String> jvmOptionsList = (List<String>) tomcatInstanceMap.get("jvmOptions");
        if (jvmOptionsList != null && !jvmOptionsList.isEmpty()) {
            /**
             * Inject in our Defaults...
             */
             for(String jvmOptionElement : jvmOptionsList) {
                 InstanceJVMOption instanceJVMOption = new InstanceJVMOption(jvmOptionElement);
                if (!this.jvmOptions.contains(instanceJVMOption)) {
                    this.jvmOptions.add(instanceJVMOption);
                }
             }
             for(InstanceJVMOption instanceJVMOption : DefaultDefinitions.DEFAULT_JVM_OPTION_ROWS) {
                 if (!this.jvmOptions.contains(instanceJVMOption)) {
                     this.jvmOptions.add(instanceJVMOption);
                 }
             }
        } else {
            this.jvmOptions.addAll(DefaultDefinitions.DEFAULT_JVM_OPTION_ROWS);
        }

        /**
         * DeSerialize our Instance Management Properties
         */
        this.instanceManagementProperties = new ArrayList<>();
        List<Map<String,Object>> instanceManagementPropertiesMapList =
                (List<Map<String,Object>>) tomcatInstanceMap.get("instanceManagementProperties");
        if (instanceManagementPropertiesMapList != null) {
            for (Map<String, Object> mapElement : instanceManagementPropertiesMapList) {
                TomcatInstanceProperty tomcatInstanceProperty = new TomcatInstanceProperty(mapElement);
                instanceManagementProperties.add(tomcatInstanceProperty);
            }
        }
        /**
         * DeSerialize our Instance Properties
         */
        this.instanceProperties = new ArrayList<>();
        List<Map<String,Object>> instancePropertiesMapList =
                (List<Map<String,Object>>) tomcatInstanceMap.get("instanceProperties");
        if (instanceManagementPropertiesMapList != null) {
            for (Map<String, Object> mapElement : instancePropertiesMapList) {
                TomcatInstanceProperty tomcatInstanceProperty = new TomcatInstanceProperty(mapElement);
                instanceProperties.add(tomcatInstanceProperty);
            }
        }
        /**
         * DeSerialize our Instance Ports
         */
        if (tomcatInstanceMap.get("instancePorts") == null) {
            this.instancePorts = new ArrayList<>();
        } else if (tomcatInstanceMap.get("instancePorts") instanceof List) {
            this.instancePorts = (List<InstancePorts>) tomcatInstanceMap.get("instancePorts");
        } else {
            this.instancePorts = new ArrayList<>();
            List<Map<String, Object>> instancePortsMapList =
                    (List<Map<String, Object>>) tomcatInstanceMap.get("instancePorts");
            for (Map<String, Object> mapElement : instancePortsMapList) {
                InstancePorts instancePorts = new InstancePorts(mapElement);
                this.instancePorts.add(instancePorts);
            }
        }
    }

    /**
     * ***************************************************
     * Helper Methods
     * ***************************************************
     */

    /**
     * hydrateFileValue
     *
     * @param valueName - Map Value Name
     * @param tomcatInstanceMap Reference Map
     * @return File or Null.
     */
    protected File hydrateFileValue(String valueName, Map<String,Object> tomcatInstanceMap) {
        if (tomcatInstanceMap.get(valueName) == null ||
                tomcatInstanceMap.get(valueName).toString().isEmpty() ) {
            return null;
        } else {
            return new File(tomcatInstanceMap.get(valueName).toString());
        }
    }
    
    /**
     * Return the Original referenced Download Archive.
     *
     * @return String containing fully qualified formulated Download Archive.
     */
    public String referenceDownloadedArchiveFolder() {
        if (tomcatArchive == null) {
            throw new IllegalStateException("No Tomcat Archive has been Selected!");
        }
        return getDestinationFolder().getAbsolutePath() + File.separator + this.tomcatArchive.getHeadName();
    }

    /**
     * Return the formulated Tomcat Instance Folder for this Instance Creation.
     *
     * @return String containing the formulated new Tomcat Instance folder, less a Parent.
     */
    public String referenceTomcatInstanceFolder() {
        if (tomcatArchive == null) {
            throw new IllegalStateException("No Tomcat Archive has been Selected!");
        }
        return this.tomcatArchive.getHeadName();
    }

    /**
     * Return the formulated Tomcat Instance YAML configuration File for this Instance Creation.
     *
     * @return String containing the formulated new Tomcat Instance folder, less a Parent.
     */
    public String referenceTomcatInstanceYAML() {
        if (tomcatArchive == null) {
            throw new IllegalStateException("No Tomcat Archive has been Selected!");
        }
        return getInstanceName() + "-" + getEnvironmentName() + "-" +
                this.tomcatArchive.getHeadName() + ".yaml";

    }

    /**
     * Return the File reference of the constructed YAML Configuration File.
     *
     * @return File representing the YAML File.
     */
    public File referenceSourceYAMLFile() {
        return new File(getDestinationFolder().getAbsolutePath() + File.separator + referenceTomcatInstanceYAML());
    }

    /**
     * Return the File reference of the YAML Configuration File in the Instance Directory.
     *
     * @return File representing the YAML File.
     */
    public File referenceDestinationYAMLFile() {
        return new File(getDestinationFolder().getAbsolutePath() + File.separator +
                referenceTomcatInstanceFolder() + File.separator + referenceTomcatInstanceYAML());
    }

    /**
     * Generate a Map representing this Object.
     *
     * @return Map representing this Object.
     */
    public Map<String, Object> map() {
        Map<String, Object> tomcatInstanceMap = new HashMap<>();
        tomcatInstanceMap.put("instanceUUID", tomcatInstanceUUID);
        tomcatInstanceMap.put("instanceName", instanceName);
        tomcatInstanceMap.put("environmentName", environmentName);
        tomcatInstanceMap.put("tomcatVersion", tomcatVersion);
        tomcatInstanceMap.put("compressed", compressed);
        tomcatInstanceMap.put("instancePorts", instancePorts);
        tomcatInstanceMap.put("jvmOptionXms", jvmOptionXms);
        tomcatInstanceMap.put("jvmOptionXmx", jvmOptionXmx);
        tomcatInstanceMap.put("jvmOptionXss", jvmOptionXss);
        tomcatInstanceMap.put("jvmOptionXXMaxMetaspaceSize", jvmOptionXXMaxMetaspaceSize);
        tomcatInstanceMap.put("jvmOptions", jvmOptions);
        tomcatInstanceMap.put("instanceManagement", instanceManagement);
        tomcatInstanceMap.put("tomcatArchive", tomcatArchive);
        tomcatInstanceMap.put("destinationFolder", destinationFolder.getAbsolutePath());
        if (coreFolder != null) {
            tomcatInstanceMap.put("coreFolder", coreFolder.getAbsolutePath());
        }
        if (tcHomeFolder != null) {
            tomcatInstanceMap.put("tcHomeFolder", tcHomeFolder.getAbsolutePath());
        }
        if (tcBaseFolder != null) {
            tomcatInstanceMap.put("tcBaseFolder", tcBaseFolder.getAbsolutePath());
        }
        /**
         * Serialize our Instance Management Properties
         */
        if (instanceManagementProperties != null && !instanceManagementProperties.isEmpty()) {
            List<Map<String, String>> instanceManagementPropertiesMapList =
                    new ArrayList<>(instanceManagementProperties.size());
            for (TomcatInstanceProperty tomcatInstanceProperty : instanceManagementProperties) {
                instanceManagementPropertiesMapList.add(tomcatInstanceProperty.map());
            }
            tomcatInstanceMap.put("instanceManagementProperties", instanceManagementPropertiesMapList);
        }
        /**
         * Serialize our Instance Properties
         */
        if (instanceProperties!= null && !instanceProperties.isEmpty()) {
            List<Map<String, String>> instancePropertiesMapList =
                    new ArrayList<>(instanceProperties.size());
            for (TomcatInstanceProperty tomcatInstanceProperty : instanceProperties) {
                instancePropertiesMapList.add(tomcatInstanceProperty.map());
            }
            tomcatInstanceMap.put("instanceProperties", instancePropertiesMapList);
        }
        /**
         * Return the Map for Persisting as YAML.
         */
        return tomcatInstanceMap;
    }
    
    public TomcatArchive getTomcatArchive() {
        return tomcatArchive;
    }

    public void setTomcatArchive(TomcatArchive tomcatArchive) {
        this.tomcatArchive = tomcatArchive;
    }
}
