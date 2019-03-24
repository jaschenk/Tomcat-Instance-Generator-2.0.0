package jeffaschenk.infra.tomcat.instance.generator.processors.helpers;

import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import jeffaschenk.infra.tomcat.instance.generator.model.*;
import jeffaschenk.infra.tomcat.instance.generator.util.Color;
import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;
import jeffaschenk.infra.tomcat.instance.generator.util.FileUtility;
import jeffaschenk.infra.tomcat.instance.generator.util.ResourceHelper;
import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.ReplacementDefinitions;
import jeffaschenk.infra.tomcat.instance.generator.model.*;
import org.slf4j.Logger;

import java.io.*;
import java.net.InetAddress;

import java.util.*;

/**
 * TomcatInstanceProcessCustomizationHelper
 * <p>
 * Created by jeffaschenk@gmail.com on 2/16/2017.
 */
public class TomcatInstanceProcessCustomizationHelper {
    /**
     * Private Constructor
     */
    private TomcatInstanceProcessCustomizationHelper() {}
    /**
     * Constants
     */
    private static final String CATALINA_OPTS = "CATALINA_OPTS";
    private static final String WIN_SET = "SET ";
    private static final String WIN_LS = "\r\n";
    private static final String NIX_LS = "\n";
    private static final String README = "README.txt";

    /**
     * Additional repetitively used Constants
     */
    private static final String COULD_NOT_FIND_DEST_FOLDER =
            "  Could not find Destination Folder: %s, unable to Continue!";

    private static final String COULD_NOT_FIND_DEST_FOLDER_WITH_REASON =
            "  Could not find Destination Folder: %s, %s, unable to Continue!";


    /**
     * Generate any Additional Directories per our Customizations.
     *
     * @param LOGGER Logger Reference
     * @param tomcatInstance    Tomcat Instance POJO
     * @return boolean to indicate that the Additional Directories were or were not created...
     */
    protected static Integer additionalDirectories(Logger LOGGER, TomcatInstance tomcatInstance) {
        File destinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.BaseTemplate);
        if (destinationFolder == null) {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER,  true, true, true,
                    "Foolder is Null");
            return -1;
        }
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            for (String additionalDirectory : DefaultDefinitions.ADDITIONAL_DIRECTORIES_TO_BE_ADDED) {
                File newDirectory = new File(destinationFolder + File.separator + additionalDirectory);
                if (newDirectory.exists()) {
                    ConsoleOutput.out(Color.RED_BOLD,
                            "  Tomcat Sub-Directory: %s, already exists, Ignoring!", true, true, true,
                            additionalDirectory);
                } else {
                    ConsoleOutput.out(Color.GREEN_BOLD, "  Creating new Tomcat Sub-Directory: %s", false, true, true,
                            additionalDirectory);
                    if (!newDirectory.mkdirs()) {
                        ConsoleOutput.out(Color.RED_BOLD, "Unable to create sub-directory: %s in %s", true, true, true,
                                additionalDirectory, destinationFolder);
                        return -1;
                    }
                }
            }
            return DefaultDefinitions.ADDITIONAL_DIRECTORIES_TO_BE_ADDED.length;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER,  true, true, true,
                    destinationFolder);
            return -1;
        }
    }

    /**
     * Seed Any Additional Directories which are empty to begin with, as they will not be
     * Archived if a compressed Archive is requested. So ensure these empty directories have a dummy file
     * in there to ensure the directory is copied during compression.
     *
     * @param LOGGER Logger Reference
     * @param tomcatInstance    Tomcat Instance POJO
     * @return boolean to indicate that the Additional Directories were or were not created...
     */
    protected static boolean seedDirectories(Logger LOGGER, TomcatInstance tomcatInstance) {
        File destinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.BaseTemplate);
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            for (int i = 0; i < DefaultDefinitions.EMPTY_DIRECTORIES_TO_BE_SEEDED.length; i++) {
                String seedDirectory = DefaultDefinitions.EMPTY_DIRECTORIES_TO_BE_SEEDED[i];
                File newDirectory = new File(destinationFolder + File.separator + seedDirectory);
                if (!newDirectory.exists()) {
                    ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER_WITH_REASON,
                            true, true, true,
                            newDirectory, "should have been created");
                    return false;
                }
                /**
                 * Now Add a README.txt File to describe the Directory.
                 */
                ResourceHelper.persistStringDataAsFile(LOGGER, DefaultDefinitions.EMPTY_DIRECTORIES_README_CONTENTS[i],
                        newDirectory.getAbsolutePath() + File.separator + README);
            }
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER,
                    true, true, true, destinationFolder);
            return false;
        }
    }

    /**
     * Seed Any Mounts Directories which are empty to begin with, as they will not be
     * Archived if a compressed Archive is requested. So ensure these empty directories have a dummy file
     * in there to ensure the directory is copied during compression.
     *
     * @param LOGGER Logger Reference
     * @param tomcatInstance    Tomcat Instance POJO
     * @return boolean to indicate that the Additional Directories were or were not created...
     */
    protected static boolean seedMountsDirectories(Logger LOGGER, TomcatInstance tomcatInstance) {
        File destinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.Mounts);
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            for (int i = 0; i < DefaultDefinitions.EMPTY_MOUNTS_DIRECTORIES_TO_BE_SEEDED.length; i++) {
                String seedDirectory = DefaultDefinitions.EMPTY_MOUNTS_DIRECTORIES_TO_BE_SEEDED[i];
                File newDirectory = new File(destinationFolder + File.separator + seedDirectory);
                if (!newDirectory.exists()) {
                    ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER_WITH_REASON,
                            true, true, true,
                            newDirectory, "should have been created");
                    return false;
                }
                /**
                 * Now Add a README.txt File to describe the Directory.
                 */
                ResourceHelper.persistStringDataAsFile(LOGGER, DefaultDefinitions.EMPTY_MOUNTS_DIRECTORIES_README_CONTENTS[i],
                        newDirectory.getAbsolutePath() + File.separator + README);
            }
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER,
                    true, true, true, destinationFolder);
            return false;
        }
    }


    /**
     * Add Additional Libraries to the Tomcat Instance as prescribed by our Default Definitions.
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @return boolean indicates if process was successfully for not.
     * @throws IOException If IO failure Occurs.
     */
    protected static boolean customizeAdditionalLibArtifacts(Logger LOGGER, TomcatInstance tomcatInstance)
            throws IOException {
        File baseDestinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.Home);
        File destinationFolder = new File(baseDestinationFolder.getAbsolutePath() + File.separator + "lib");
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {

            for (String additionalLibArtifact : DefaultDefinitions.ADDITIONAL_LIB_ARTIFACTS_TO_BE_ADDED) {
                ConsoleOutput.out(Color.WHITE_BOLD, "   Adding Additional Tomcat 'lib' Artifact: %s",
                        false, true, true,
                        additionalLibArtifact);
                ResourceHelper.readResourceToBinaryFile("tc/lib_additions/" + additionalLibArtifact,
                        destinationFolder.getAbsolutePath() + File.separator + additionalLibArtifact);
            }
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER, true, true, true,
                    destinationFolder);
            return false;
        }
    }

    /**
     * Add Additional External Libraries to the Tomcat Instance as prescribed by our Default Definitions.
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @return boolean indicates if process was successfully for not.
     * @throws IOException If IO failure Occurs.
     */
    protected static boolean customizeExternalLibArtifacts(Logger LOGGER, TomcatInstance tomcatInstance)
            throws IOException {
        File destinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.Home);
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {

            for (String additionalLibArtifact : DefaultDefinitions.ADDITIONAL_EXTERNAL_LIB_ARTIFACTS_TO_BE_ADDED) {
                ConsoleOutput.out(Color.WHITE_BOLD, "   Adding Additional External Library Artifact: %s",
                        false, true, true,
                        additionalLibArtifact);
                ResourceHelper.readResourceToBinaryFile("tc/lib_external/" + additionalLibArtifact,
                        destinationFolder.getAbsolutePath() + File.separator + additionalLibArtifact);
                /**
                 * If this is an Archive, unzip it...
                 */
                if (additionalLibArtifact.endsWith(DefaultDefinitions.ZIP_ARCHIVE_SUFFIX)) {
                    try {
                        /**
                         * Uncompress Archive.
                         */
                        TomcatInstanceAFRYZProcessHelper.unZipArchive(LOGGER,
                                destinationFolder.getAbsolutePath()+File.separator + additionalLibArtifact,
                                destinationFolder.getAbsolutePath());
                        ConsoleOutput.out(Color.WHITE_BOLD, "   Uncompressing External Library Artifact: %s",
                                false, true, true,
                                additionalLibArtifact);
                        /**
                         * Now Remove the provided Archive.
                         */
                        FileUtility.delete(new File(destinationFolder.getAbsolutePath()+File.separator + additionalLibArtifact), false);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(),e);
                        /**
                         * Indicate a Failure has Occurred
                         */
                        return false;
                    }
                } // End of If check for compressed Archive.
            } // End of For Loop.
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER, true, true, true,
                    destinationFolder);
            return false;
        }
    }

    /**
     * Add Additional Home artifacts to the Tomcat Instance as prescribed by our Default Definitions.
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @return boolean indicates if process was successfully for not.
     * @throws IOException If IO failure Occurs.
     */
    protected static boolean customizeAdditionalHomeResources(Logger LOGGER, TomcatInstance tomcatInstance)
            throws IOException {
        File destinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.Home);
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {

            for (String additionalResourceFolderArtifact : DefaultDefinitions.ADDITIONAL_HOME_RESOURCE_ARTIFACTS_TO_BE_ADDED) {
                ConsoleOutput.out(Color.WHITE_BOLD, "   Adding Additional Home Resource: %s",
                        false, true, true,
                        additionalResourceFolderArtifact);
                ResourceHelper.readResourceToBinaryFile("tc/" + additionalResourceFolderArtifact,
                        destinationFolder.getAbsolutePath() + File.separator + additionalResourceFolderArtifact);
            } 
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER, true, true, true,
                    destinationFolder);
            return false;
        }
    }


    /**
     * Add Management Agent To WebApps
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @return boolean indicates if process was successfully for not.
     * @throws IOException If IO failure Occurs.
     */
    protected static boolean addManageCatAgentToWebApps(Logger LOGGER,
                                                        TomcatInstance tomcatInstance) throws IOException {
        File baseDestinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.BaseTemplate);
        File destinationFolder = new File(baseDestinationFolder.getAbsolutePath() + File.separator + DefaultDefinitions.WEBAPPS);
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            /**
             * Add any Specific Apps that are related to enable Instance Management...
             */
            if (tomcatInstance.isInstanceManagement()) {
                    ConsoleOutput.out(Color.GREEN_BOLD, "   Adding Additional Tomcat Management Application: %s",
                            false, true, true,
                            DefaultDefinitions.DEFAULT_MANAGEMENT_WEBAPP);
                    ResourceHelper.readResourceToBinaryFile("tc/webapps/" + DefaultDefinitions.DEFAULT_MANAGEMENT_WEBAPP,
                            destinationFolder.getAbsolutePath() + File.separator + DefaultDefinitions.DEFAULT_MANAGEMENT_WEBAPP_NAME );
                }
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER,  true, true, true, destinationFolder);
            return false;
        }
    }

    /**
     * Customize the Manager Application for Deployments and such ...
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @return boolean Indicates if Method process was successful or not.
     */
    public static boolean customizeManagerApp(Logger LOGGER,
                                              TomcatInstance tomcatInstance) {
        File baseDestinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.BaseTemplate);
        File destinationFolder = new File(baseDestinationFolder.getAbsolutePath() + File.separator + DefaultDefinitions.WEBAPPS + File.separator +
                "manager" + File.separator + "WEB-INF");
        /**
         * Get Short Version Name.
         */
        String shortVersionName = lookupTomcatVersionShortName(tomcatInstance);
        /**
         * Formulate the Destination Folder
         */
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            /**
             * Update the Manager WebApp with our Customizations ...
             */
            if (!ResourceHelper.persistFilteredResourceAsFile(LOGGER, "tc/webapps/manager/" + shortVersionName + "/web.xml",
                    destinationFolder.getAbsolutePath() + File.separator + "web.xml", null)) {
               ConsoleOutput.out(Color.RED_BOLD, " Issue performing Customization of 'manager'  App, unable to Continue!",
                       true, true, true);
                return false;
            }
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER_WITH_REASON,
                    true, true, true, destinationFolder, "for Customizing 'manager' App");
            return false;
        }
    }

    /**
     * customizeROOTApp
     *
     * @param LOGGER Reference
     * @param tomcatInstance Reference
     * @return boolean indicating function method was successful or not.
     */
    public static boolean customizeROOTApp(Logger LOGGER,
                                           TomcatInstance tomcatInstance) throws IOException {
        File baseDestinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.BaseTemplate);
        File destinationFolder = new File(baseDestinationFolder.getAbsolutePath() + File.separator + DefaultDefinitions.WEBAPPS + File.separator +
                "ROOT");
        /**
         * Formulate the Destination Folder
         */
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            /**
             * Update the Manager WebApp with our Customizations ...
             */
            int result = 0;
            for(String rootWebappFile : DefaultDefinitions.AVAILABLE_ROOT_WEBAPP_FILES) {
                if (rootWebappFile.endsWith(".png")) {
                    if (ResourceHelper.readResourceToBinaryFile("tc/webapps/ROOT/"+rootWebappFile,
                            destinationFolder.getAbsolutePath() + File.separator + rootWebappFile)) {
                        result++;
                    }
                } else {
                    if (ResourceHelper.persistFilteredResourceAsFile(LOGGER, "tc/webapps/ROOT/"+rootWebappFile,
                            destinationFolder.getAbsolutePath() + File.separator + rootWebappFile, null)) {
                        result++;
                    }
                }
            }
            if (result != DefaultDefinitions.AVAILABLE_ROOT_WEBAPP_FILES.length) {
                ConsoleOutput.out(Color.RED_BOLD, " Issue performing Customization of 'ROOT'  App, unable to Continue!",
                        true, true, true);
                return false;
            }
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER_WITH_REASON,
                    true, true, true, destinationFolder, "for Customizing 'ROOT' App");
            return false;
        }
    }

    /**
     * purgeWEBAPPS
     *
     * @param LOGGER Reference
     * @param tomcatInstance Reference
     * @return boolean indicating function method was successful or not.
     */
    public static boolean purgeWEBAPPS(Logger LOGGER,
                                           TomcatInstance tomcatInstance) {
        File baseDestinationFolder = obtainBaseDestinationFolder(tomcatInstance, null, TomcatFolderDesignation.BaseTemplate);
        File destinationFolder = new File(baseDestinationFolder.getAbsolutePath() + File.separator + DefaultDefinitions.WEBAPPS);
        /**
         * Formulate the Destination Folder
         */
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            /**
             * Purge Webapps which are not needed ...
             */
            int result = 0;
            for(String baseWebAppToPurged : DefaultDefinitions.WEBAPPS_TO_PURGED) {
                if (FileUtility.removeDirectoryAndContents(destinationFolder.getAbsolutePath()+File.separator+baseWebAppToPurged, false)) {
                    result++;
                }
            }
            if (result != DefaultDefinitions.WEBAPPS_TO_PURGED.length) {
                ConsoleOutput.out(Color.RED_BOLD, " Issue performing purge of unnecessary 'webapps', unable to Continue!",
                        true, true, true);
                return false;
            }
            return true;
        } else {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER_WITH_REASON,
                    true, true, true, destinationFolder, "for purging unnecessary 'webapps'.");
            return false;
        }
    }

    /**
     * Customize the 'conf' aspects of the Tomcat Instance ...
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @return boolean Indicates if Method process was successful or not.
     */
    public static boolean customizeConf(Logger LOGGER,
                                        TomcatInstance tomcatInstance, InstancePorts instancePorts) {
        File baseDestinationFolder = obtainBaseDestinationFolder(tomcatInstance, instancePorts, TomcatFolderDesignation.Base);
        File destinationFolder = new File(baseDestinationFolder.getAbsolutePath() + File.separator + DefaultDefinitions.CONF);
        /**
         * Get Short Version Name.
         */
        String shortVersionName = lookupTomcatVersionShortName(tomcatInstance);
        /**
         * Formulate the Destination Folder and verify it's existence ...
         */
        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER_WITH_REASON,
                    true, true, true,
                    destinationFolder, "for Customizing 'conf' aspects");
            return false;
        }

        /**
         * Create a Replacement Map for our Various Setting at this Phase.
         */
        Map<String, String> replacementMap = configurationReplacementMapPhase1(tomcatInstance, instancePorts);
        
        /**
         * Iterate over files to be customized for 'conf' ...
         */
        for (String confResourceName : DefaultDefinitions.CONF_FILES_TO_BE_ADDED_FILTERED) {
            if (!ResourceHelper.persistFilteredResourceAsFile(LOGGER,"tc/conf/" + shortVersionName + "/" + confResourceName,
                    destinationFolder.getAbsolutePath() + File.separator + confResourceName,
                    replacementMap)) {
                ConsoleOutput.out(Color.RED_BOLD,
                        "  Issue performing Customization of 'conf' Resource: %s, unable to Continue!",
                        true, true, true, confResourceName);
                return false;
            } else {
                ConsoleOutput.out(Color.WHITE_BOLD,
                        "    Customization of 'conf' Resource: %s, successful, written to: %s",
                        false, true, true,
                        confResourceName, destinationFolder.getAbsolutePath() + File.separator + confResourceName);
            }
        }
        return true;
    }

    /**
     * Customize the 'bin' aspects of the Tomcat Instance ...
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @return boolean Indicates if Method process was successful or not.
     */
    public static boolean customizeBin(Logger LOGGER,
                                       TomcatInstance tomcatInstance, InstancePorts instancePorts) {
        File baseDestinationFolder = obtainBaseDestinationFolder(tomcatInstance, instancePorts, TomcatFolderDesignation.Base);
        File destinationFolder = new File(baseDestinationFolder.getAbsolutePath() + File.separator + "bin");
        /**
         * Formulate the Destination Folder and verify it's existence ...
         */
        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER_WITH_REASON,
                    true, true, true,
                    destinationFolder, "for Customizing 'bin' aspects");
            return false;
        }

        /**
         * Iterate over files to be customized for 'bin' ...
         */
        for (String binResourceName : DefaultDefinitions.BIN_FILES_TO_BE_ADDED_FILTERED) {
            /**
             * Create a Replacement Map for our Various Setting at this Phase.
             */
            Map<String, String> replacementMap = configurationReplacementMapPhase1(tomcatInstance, instancePorts);

            /**
             * Add Additional Elements to the replacement Map for primary JAVA JVM Options,
             * Instance Properties and Management Properties.
             *
             * We recreate for each file, as we need to understand the OS type of File
             * we are dealing with as that will affect that replacement data.
             */
            addReplacementsForJvmOptions(binResourceName, replacementMap, tomcatInstance);
            addReplacementsForInstanceProperties(binResourceName, replacementMap, tomcatInstance);
            addReplacementsForManagementProperties(binResourceName, replacementMap, tomcatInstance);

            /**
             * Perform the replacements for this File and Persist accordingly ...
             */
            if (!ResourceHelper.persistFilteredResourceAsFile(LOGGER,"tc/bin/" + binResourceName,
                    destinationFolder.getAbsolutePath() + File.separator + binResourceName,
                    replacementMap)) {
                ConsoleOutput.out(Color.RED_BOLD, "  Issue performing Customization of 'bin' Resource: %s, unable to Continue!",
                        true, true, true,
                        binResourceName);
                return false;
            } else {
                ConsoleOutput.out(Color.WHITE_BOLD, "    Customization of 'bin' Resource: %s, successful.",
                        false, true, true, 
                        binResourceName);
            }
        }
        return true;
    }

    /**
     * customizeUpgradeKitScripts
     *
     * @param LOGGER reference
     * @param tomcatInstance reference
     * @return boolean indicating if function was successful or not.
     */
    public static boolean customizeUpgradeKitScripts(Logger LOGGER,
                                                     TomcatInstance tomcatInstance) {
        File baseDestinationFolder = tomcatInstance.getDestinationFolder();
        /**
         * Formulate the Destination Folder and verify it's existence ...
         */
        if (!baseDestinationFolder.exists() || !baseDestinationFolder.isDirectory()) {
            ConsoleOutput.out(Color.RED_BOLD, COULD_NOT_FIND_DEST_FOLDER_WITH_REASON,
                    true, true, true,
                    baseDestinationFolder, "for Customizing 'upgrade kit' aspects");
            return false;
        }

        /**
         * Iterate over files to be customized for Upgrade Kit ...
         */
        for (String kitResourceName : DefaultDefinitions.UPGRADE_KIT_SCRIPT_FILES_TO_BE_FILTERED) {
            /**
             * Create a Replacement Map for our Various Setting at this Phase.
             */
            Map<String, String> replacementMap = new HashMap<>();
            replacementMap.put(ReplacementDefinitions.REPLACEMENT_UPGRADE_VERSION_TAG, "v"+tomcatInstance.getTomcatVersion());

            /**
             * Perform the replacements for this File and Persist accordingly ...
             */
            if (!ResourceHelper.persistFilteredResourceAsFile(LOGGER,"tc/upgrade/" + kitResourceName,
                    baseDestinationFolder.getAbsolutePath() + File.separator + kitResourceName,
                    replacementMap)) {
                ConsoleOutput.out(Color.RED_BOLD, "  Issue performing Customization of 'upgrade kit' Resource: %s, unable to Continue!",
                        true, true, true,
                        kitResourceName);
                return false;
            } else {
                ConsoleOutput.out(Color.WHITE_BOLD, "    Customization of 'upgrade kit' Resource: %s, successful.",
                        false, true, true,
                        kitResourceName);
            }
        }
        return true;
    }

    /**
     * obtainDestinationFolder based upon InstancePorts Reference and Folder Designation.
     * @param tomcatInstance - Reference
     * @param instancePorts - Reference
     * @param tomcatFolderDesignation - Designation
     * @return File - Contrived Destination Folder.
     */
    private static File obtainBaseDestinationFolder(TomcatInstance tomcatInstance,
                                                    InstancePorts instancePorts, TomcatFolderDesignation tomcatFolderDesignation) {
            switch(tomcatFolderDesignation) {
                case Home:
                    if (tomcatInstance.getTcHomeFolder() != null)
                        { return tomcatInstance.getTcHomeFolder(); }
                    else {
                        return new File(tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                                tomcatInstance.referenceTomcatInstanceFolder());
                    }
                case Base:
                    if (instancePorts == null) {
                        return new File(tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                                tomcatInstance.referenceTomcatInstanceFolder());
                    }
                    if (instancePorts.getInstanceFolder() != null) {
                        return instancePorts.getInstanceFolder();
                    } else {
                        return new File(tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                                tomcatInstance.referenceTomcatInstanceFolder());
                    }
                case BaseTemplate:
                    if (tomcatInstance.getTcBaseFolder() != null) {
                        return tomcatInstance.getTcBaseFolder();
                    } else {
                        return new File(tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                                tomcatInstance.referenceTomcatInstanceFolder());
                    }
                case Mounts:
                    return tomcatInstance.getTcMountsFolder();
                default:
                    break;
            }
            throw new IllegalStateException("Unable to Obtain requested Directory Type: "+tomcatFolderDesignation.toString());
    }

    /**
     * Lookup our Tomcat Version Short Name for use to obtain embedded Artifacts which are
     * version specific.
     *
     * @param tomcatInstance POJO to read Version
     * @return String containing short or short name of Tomcat Version.
     */
    protected static String lookupTomcatVersionShortName(TomcatInstance tomcatInstance) {
        String versionBeingUsed = tomcatInstance.getTomcatArchive().getShortName();
        versionBeingUsed = versionBeingUsed.substring(0, versionBeingUsed.lastIndexOf('.'));
        String shortName = DefaultDefinitions.TOMCAT_VERSIONS_TO_SHORT_NAME.get("v"+versionBeingUsed);
        if (shortName == null || shortName.isEmpty()) {
            throw new IllegalStateException("  Unable to determine Short Name for Tomcat Version: " +
                    tomcatInstance.getTomcatVersion() + ", Used: "+"v"+versionBeingUsed+" for lookup, Update Code Base!");
        } else {
            return shortName;
        }
    }

    /**
     * Add Replacements for JVM Options
     *
     * @param binResourceName Resource Name we need to check the FileType.
     * @param replacementMap  Replacements Key value Pairs.
     * @param tomcatInstance  POJO
     */
    public static void addReplacementsForJvmOptions(String binResourceName,
                                                    Map<String, String> replacementMap, TomcatInstance tomcatInstance) {
        boolean WIN = binResourceName.endsWith(".bat");
        StringBuilder sb = new StringBuilder();
        /**
         * Construct High Level JVM Options ...
         */
        buildOptionalJVMOption(tomcatInstance.getJvmOptionXms(), "-Xms", sb, WIN);

        buildOptionalJVMOption(tomcatInstance.getJvmOptionXmx(), "-Xmx", sb, WIN);

        buildOptionalJVMOption(tomcatInstance.getJvmOptionXss(), "-Xss", sb, WIN);

        buildOptionalJVMOption(tomcatInstance.getJvmOptionXXMaxMetaspaceSize(), "-XX:MaxMetaspaceSize=", sb, WIN);

        /**
         * Obtain Additional Options
         */
        for (InstanceJVMOption jvmOption : tomcatInstance.getJvmOptions()) {
            if (jvmOption.getJvmOption().startsWith("#")) {
                // Simple Write a Comment as is...
                if (WIN) {
                    sb.append("REM ");
                }
                sb.append(jvmOption);
            } else {
                if (WIN) {
                    sb.append(WIN_SET).append(CATALINA_OPTS).append("=").append("%").append(CATALINA_OPTS).append("%");
                    sb.append(" ").append(jvmOption.getJvmOption());
                } else {
                    sb.append(CATALINA_OPTS).append("=\"").append("${").append(CATALINA_OPTS).append("}");
                    sb.append(" ").append(getNIXValue(jvmOption.getJvmOption())).append("\"");
                }
            }
            if (WIN) {
                sb.append(WIN_LS);
            } else {
                sb.append(NIX_LS);
            }
        }
        /**
         * Save the Options for Replacement ...
         */
        replacementMap.put(ReplacementDefinitions.TOMCAT_JVM_OPTIONS, sb.toString());
    }

    protected static void buildOptionalJVMOption(String jvmOption, String prefix, StringBuilder sb, boolean WIN) {
        if (!jvmOption.equalsIgnoreCase("NONE")) {
            if (WIN) {
                sb.append(WIN_SET).append(CATALINA_OPTS).append("=").append("%").append(CATALINA_OPTS).append("%");
                sb.append(" ").append(prefix).append(jvmOption);
            } else {
                sb.append(CATALINA_OPTS).append("=\"").append("${").append(CATALINA_OPTS).append("}");
                sb.append(" ").append(prefix).append(getNIXValue(jvmOption)).append("\"");
            }
        } else {
            if (WIN) {
                sb.append("rem ").append(WIN_SET).append(CATALINA_OPTS).append("=").append("%").append(CATALINA_OPTS).append("%");
                sb.append(" ").append(prefix).append(" None Specified for this configuration.");
            } else {
                sb.append("# ").append(CATALINA_OPTS).append("=\"").append("${").append(CATALINA_OPTS).append("}");
                sb.append(" ").append(prefix).append("?\"").append(" #  None Specified for this configuration.");
            }
        }
        if (WIN) {
            sb.append(WIN_LS);
        } else {
            sb.append(NIX_LS);
        }
    }

    /**
     * Add Replacements for Instance Properties ...
     *
     * @param binResourceName Resource Name we need to check the FileType.
     * @param replacementMap  Replacements Key value Pairs.
     * @param tomcatInstance  POJO
     */
    public static void addReplacementsForInstanceProperties(String binResourceName,
                                                            Map<String, String> replacementMap, TomcatInstance tomcatInstance) {
        boolean WIN = binResourceName.endsWith(".bat");
        StringBuilder sb = new StringBuilder();
        for (TomcatInstanceProperty tomcatInstanceProperty : tomcatInstance.getInstanceProperties()) {
            if (tomcatInstanceProperty.getPropertyName().startsWith("#")) {
                // Simple Write a Comment as is...
                if (WIN) {
                    sb.append("rem ");
                }
                sb.append(tomcatInstanceProperty.getPropertyName()).append("=").
                        append(tomcatInstanceProperty.getPropertyValue());
            } else {
                if (WIN) {
                    sb.append(WIN_SET).append(CATALINA_OPTS).append("=").append("%").append(CATALINA_OPTS).append("%").append(" ");
                    sb.append("-D").append(tomcatInstanceProperty.getPropertyName()).append("=").
                            append(tomcatInstanceProperty.getPropertyValue());
                } else {
                    sb.append(CATALINA_OPTS).append("=\"").append("${").append(CATALINA_OPTS).append("}").append(" ");
                    sb.append("-D").append(tomcatInstanceProperty.getPropertyName()).append("=").
                            append(getNIXValue(tomcatInstanceProperty.getPropertyValue())).append("\"");
                }
            }
            if (WIN) {
                sb.append(WIN_LS);
            } else {
                sb.append(NIX_LS);
            }
        }

        /**
         * Save the Properties for Replacement ...
         */
        replacementMap.put(ReplacementDefinitions.TOMCAT_INSTANCE_PROPERTIES, sb.toString());
    }

    /**
     * Add Replacements for Management Property Options if applicable ...
     *
     * @param binResourceName Resource Name we need to check the FileType.
     * @param replacementMap  Replacements Key value Pairs.
     * @param tomcatInstance  POJO
     */
    public static void addReplacementsForManagementProperties(String binResourceName,
                                                              Map<String, String> replacementMap, TomcatInstance tomcatInstance) {
        /**
         * Create Additional Internal Filters for Management Properties, which if have not been modified,
         * should be replaced.
         */
        Map<String, String> internalReplacementMap = new HashMap<>();
        internalReplacementMap.put(ReplacementDefinitions.TOMCAT_INSTANCE_NAME_TAG, tomcatInstance.getInstanceName());
        internalReplacementMap.put(ReplacementDefinitions.TOMCAT_INSTANCE_HOSTNAME_TAG, getThisDefaultInetAddress());
        if (tomcatInstance.getInstancePorts() == null || tomcatInstance.getInstancePorts().isEmpty()) {
            internalReplacementMap.put(ReplacementDefinitions.TOMCAT_PRIMARY_PORT_TAG, DefaultDefinitions.DEFAULT_PRIMARY_PORT.toString());
        } else {
            internalReplacementMap.put(ReplacementDefinitions.TOMCAT_PRIMARY_PORT_TAG, tomcatInstance.getInstancePorts().get(0).getHttpPort().toString());
        }

        /**
         * Obtain our ManageCat License Information and other specifics if necessary.
         */
        Map<String, String> manageCatResolvedProperties = resolvedExternalManagementProperties();
        internalReplacementMap.put(ReplacementDefinitions.MANAGECAT_LICENSE_KEY_TAG,
                manageCatResolvedProperties.get(DefaultDefinitions.MANAGECAT_LICENSE_KEY_INTERNAL_PROPERTY_NAME));

        /**
         * If the Management Options was specified, add the applicable Replacements.
         */
        boolean WIN = binResourceName.endsWith(".bat");
        StringBuilder sb = new StringBuilder();
        for (TomcatInstanceProperty tomcatInstanceProperty : tomcatInstance.getInstanceManagementProperties()) {
            if (tomcatInstanceProperty.getPropertyName().startsWith("#")) {
                // Simple Write a Comment as is...
                if (WIN) {
                    sb.append("REM ");
                }
                sb.append(tomcatInstanceProperty.getPropertyName()).append("=").
                        append(tomcatInstanceProperty.getPropertyValue());
            } else {
                String linePrefix = "";
                if (!tomcatInstance.isInstanceManagement()) {
                    linePrefix = (WIN) ? "REM " : "# ";
                }
                if (WIN) {
                    sb.append(linePrefix).append(WIN_SET).append(CATALINA_OPTS).append("=").append("%").append(CATALINA_OPTS).append("%").append(" ");
                    sb.append("-D").append(tomcatInstanceProperty.getPropertyName()).append("=").
                            append(tomcatInstanceProperty.getPropertyValue());
                } else {
                    sb.append(linePrefix).append(CATALINA_OPTS).append("=\"").append("${").append(CATALINA_OPTS).append("}").append(" ");
                    sb.append("-D").append(tomcatInstanceProperty.getPropertyName()).append("=").
                            append(getNIXValue(tomcatInstanceProperty.getPropertyValue())).append("\"");
                }
            }
            if (WIN) {
                sb.append(WIN_LS);
            } else {
                sb.append(NIX_LS);
            }
        }
        /**
         * Now perform Internal Replacement of any Properties Value which need to be replaced.
         */
        ResourceHelper.replace(sb, internalReplacementMap);
        /**
         * Save the Properties for Replacement ...
         */
        replacementMap.put(ReplacementDefinitions.TOMCAT_INSTANCE_MANAGEMENT_PROPERTIES, sb.toString());
    }

    /**
     * get *NIX Value for a Property Setting.
     * If the Value is surrounded in quotes, escape them ...
     *
     * @param inValue Incoming String Value to Check Strings/
     * @return
     */
    public static String getNIXValue(String inValue) {
        if (inValue.startsWith("\"") && inValue.endsWith("\"")) {
            return "\\\"" + inValue.substring(1, inValue.length() - 1) + "\\\"";
        } else {
            return inValue;
        }
    }

    /**
     * Get this Instances Default HostName by executing the 'hostname' command against this
     * operating system.
     *
     * @return String containing hostname or 'localhost' if system command fails.
     */
    public static final String getThisDefaultInstanceHostName() {
        try {
            return execReadToString("hostname");
        } catch (IOException ioe) {
            return "localhost";
        }
    }

    /**
     * Execute a Local System Command and pull in it's response.
     *
     * @param execCommand System command to be executed.
     * @return String containing contents of system command response.
     * @throws IOException thrown if IO Exception occurs.
     */
    protected static String execReadToString(String execCommand) throws IOException {
        Process proc = Runtime.getRuntime().exec(execCommand);
        try (InputStream stream = proc.getInputStream()) {
            try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : "";
            }
        }
    }

    /**
     * Get this Instances Default IP Address.
     *
     * @return String containing IP Address or 'localhost' if system command fails.
     */
    public static final String getThisDefaultInetAddress() {
        try {
            InetAddress ipAddress = InetAddress.getLocalHost();
            return ipAddress.getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }

    /**
     * resolvedExternalManagementProperties
     * Obtain any Externalized Management Properties.
     *
     * @return Map containing resolved Properties.
     */
    public static Map<String,String> resolvedExternalManagementProperties() {
        Map<String,String> resolvedProperties = new HashMap<>();
        /**
         * Get the ManageCat License Key Property
         */
        String value = System.getProperty(DefaultDefinitions.MANAGECAT_LICENSE_KEY_PROPERTY_NAME);
        if (value == null || value.isEmpty()) {
             value = DefaultDefinitions.DEFAULT_MANAGECAT_LICENSE_KEY_VALUE;
        }
        resolvedProperties.put(DefaultDefinitions.MANAGECAT_LICENSE_KEY_INTERNAL_PROPERTY_NAME, value);
        /**
         * return the Resolved Properties
         */
        return resolvedProperties;
    }

    /**
     * Generate a Map representing this Object for Replacement throughout the
     * configuration aspects of the customizations phase.
     *
     * @return Map representing this Object.
     */
    private static Map<String, String> configurationReplacementMapPhase1(TomcatInstance tomcatInstance,
                                                                 InstancePorts instancePorts) {
        Map<String, String> tomcatInstanceReplacementMap = new HashMap<>();
        tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_INSTANCE_UUID_TAG, tomcatInstance.getTomcatInstanceUUID());
        tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_ENVIRONMENT_NAME_TAG, tomcatInstance.getEnvironmentName());
        tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_VERSION_TAG, tomcatInstance.getTomcatVersion());

        if (instancePorts != null) {
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_INSTANCE_NAME_TAG, instancePorts.getInstanceName());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_PRIMARY_PORT_TAG,
                    instancePorts.getHttpPort().toString());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_SHUTDOWN_PORT_TAG,
                    instancePorts.getShutdownPort().toString());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_AJP_PORT_TAG,
                    instancePorts.getAjpPort().toString());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_DEBUG_PORT_TAG,
                    instancePorts.getDebugPort().toString());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_SECURE_PORT_TAG,
                    instancePorts.getHttpsPort().toString());
            // Default Primary Port Protocol...
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_PRIMARY_PORT_PROTOCOL_TAG, DefaultDefinitions.DEFAULT_CATALINA_PROTOCOL_SELECTED);
        } else {
            /**
             * Set the Defaults , if nothing specified ...
             */
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_INSTANCE_NAME_TAG, DefaultDefinitions.DEFAULT_ENVIRONMENT_INSTANCE_SELECTED);
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_PRIMARY_PORT_TAG, DefaultDefinitions.DEFAULT_PRIMARY_PORT.toString());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_SHUTDOWN_PORT_TAG, DefaultDefinitions.DEFAULT_SHUTDOWN_PORT.toString());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_AJP_PORT_TAG, DefaultDefinitions.DEFAULT_AJP_PORT.toString());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_DEBUG_PORT_TAG, DefaultDefinitions.DEFAULT_DEBUG_PORT.toString());
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_SECURE_PORT_TAG, DefaultDefinitions.DEFAULT_SECURE_PORT.toString());
            // Default Primary Port Protocol...
            tomcatInstanceReplacementMap.put(ReplacementDefinitions.TOMCAT_PRIMARY_PORT_PROTOCOL_TAG, DefaultDefinitions.DEFAULT_CATALINA_PROTOCOL_SELECTED);
        }

        /**
         * Return the Map for Replacement Phase.
         */
        return tomcatInstanceReplacementMap;
    }
    
}
