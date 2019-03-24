package jeffaschenk.infra.tomcat.instance.generator.processors;

import jeffaschenk.infra.tomcat.instance.generator.model.TomcatArchive;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatAvailableArchives;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;
import jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceAFRYZProcessHelper;
import jeffaschenk.infra.tomcat.instance.generator.util.Color;
import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;
import jeffaschenk.infra.tomcat.instance.generator.util.ValidationHelper;
import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

/**
 * TomcatInstanceProcessBase
 * 
 * Created by schenkje on 2/20/2017.
 */
public abstract class TomcatInstanceProcessBase implements TomcatInstanceProcess {

    /**
     * Perform the Generation Process driven by CLI
     */
    public boolean processYAMLFiles(Logger LOGGER, String[] yamlConfigurationFiles,
                                    TomcatInstanceProcessBase tomcatInstanceBuilder) {
        /**
         * Initialize the counters.
         */
        Integer processingCount = 0;
        Integer successCount = 0;
        Integer failureCount = 0;
        try {
            /**
             * We have Arguments Specified validate the specified Files...
             */
            for(String yamlTomcatInstanceConfigFilename : yamlConfigurationFiles) {
                if (yamlTomcatInstanceConfigFilename == null || yamlTomcatInstanceConfigFilename.isEmpty()){
                    continue;
                }
                processingCount++;
                File yamlFile = new File(yamlTomcatInstanceConfigFilename);
                if (yamlFile.exists() && yamlFile.isFile() && yamlFile.canRead()) {
                    ConsoleOutput.out(Color.GREEN_BOLD, "  Processing YAML Instance Generation File: %s ...",
                           true, true, true,
                           yamlFile.getAbsolutePath());
                    TomcatInstance tomcatInstance =
                            TomcatInstanceAFRYZProcessHelper.loadYAMLConfigurationForInstance(yamlFile.getAbsolutePath());
                    if(!tomcatInstanceBuilder.performProcess(tomcatInstance)) {
                        ConsoleOutput.out(Color.RED_BOLD,"  Issue Processing Instance Generation File: %s, review configuration!",
                                true, true, true,
                                yamlFile.getAbsolutePath());
                        failureCount++;
                    } else {
                        successCount++;
                    }
                } else {
                    ConsoleOutput.out(Color.RED_BOLD, "  Issue Processing Instance Generation File: %s, File Not Found!",
                            true, true, true,
                            yamlFile.getAbsolutePath());
                }
            } // End of For Argument Loop ...
            /**
             * Indicate Primary Processing Concluded.
             */
            ConsoleOutput.out(Color.GREEN_BOLD,"  Final Overall Processing Results: Processed: %d, Successful: %d, Failures: %d",
                    true, true, true,
                    processingCount, successCount, failureCount);
            ConsoleOutput.issueReset();
            return failureCount<=0;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return false;
        }
    }


    /**
     * Common Method to perform Instance Archive Creation.
     *
     * @param LOGGER Reference
     * @param tomcatInstance POJO
     * @throws Exception Thrown if issues exists ...
     */
    protected static void performInstanceArchiveCreation(Logger LOGGER,
                                                         TomcatInstance tomcatInstance) throws Exception {
        /**
         * Determine if we are to Zip up the created Tomcat Instance.
         */
        if (tomcatInstance.isCompressed()) {
            /**
             * Perform
             */
            if (!TomcatInstanceAFRYZProcessHelper.zipFile(LOGGER,
                    tomcatInstance.getDestinationFolder().getAbsolutePath()+ File.separator +
                            tomcatInstance.referenceTomcatInstanceFolder()+ DefaultDefinitions.ZIP_ARCHIVE_SUFFIX,
                    tomcatInstance.getDestinationFolder().getAbsolutePath()+ File.separator +
                    tomcatInstance.referenceTomcatInstanceFolder(), false)) {
                ConsoleOutput.out(Color.RED_BOLD, "  Error: Issue Compressing the Generated Instance, please review Generation Logs!",
                        true, true, true);
            } else {
                /**
                 * Now Validate it exists ...
                 */
                File newArchiveFile = new File(tomcatInstance.getDestinationFolder().getAbsolutePath()+ File.separator +
                        tomcatInstance.referenceTomcatInstanceFolder()+ DefaultDefinitions.ZIP_ARCHIVE_SUFFIX);
                if (newArchiveFile.exists()) {
                    ConsoleOutput.out(Color.GREEN_BOLD, "  Successfully Generated Compressed Archive: %s",
                            false, true, true,
                            newArchiveFile.getAbsolutePath());
                    /**
                     * Now Remove the Exploded Folder ...
                     */
                    FileUtils.deleteDirectory(new File(tomcatInstance.getDestinationFolder().getAbsolutePath()+
                            File.separator + tomcatInstance.referenceTomcatInstanceFolder()));
                } else {
                    ConsoleOutput.out(Color.RED_BOLD, "  Error: Issue Compressing Generated Compressed Archive: %s",
                            true, true, true,
                            newArchiveFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Perform Validation of Tomcat Instance Loaded via YAML File.
     * @param tomcatInstance POJO
     * @return boolean indicating if the Tomcat Instance is Valid or not ...
     */
    protected static boolean validate(TomcatInstance tomcatInstance) {
        /**
         * Check for an Instance Name ...
         */
        if (tomcatInstance.getInstanceName() == null || tomcatInstance.getInstanceName().trim().isEmpty() ||
                !ValidationHelper.validateInstanceName(tomcatInstance.getInstanceName().trim())) {
            ConsoleOutput.out(Color.RED_BOLD, "  Unable to Generate, as Instance Name has not been specified or Invalid!",
                    true, true, true);
            return false;
        }
        /**
         * Check for Destination Folder ...
         */
        if (tomcatInstance.getDestinationFolder() == null ||
                !tomcatInstance.getDestinationFolder().exists() ||
                !tomcatInstance.getDestinationFolder().canWrite()) {
            ConsoleOutput.out(Color.RED_BOLD, "  Unable to Generate, as Destination Folder has not been specified!",
                    true, true, true);
            return false;
        }
        /**
         * Check for Valid Ports.
         */
        Integer validationIssueCount = 0;
        if (!ValidationHelper.validatePrimaryPortsSpecified(tomcatInstance)) {
            validationIssueCount++;
        }
        /**
         * Validate Memory Settings ...
         */
        if (!ValidationHelper.validateMemorySettings(tomcatInstance)) {
            validationIssueCount++;
        }
        /**
         * Check our Validation Issue Count and Conclude Validation ...
         */
        if (validationIssueCount > 0) {
            ConsoleOutput.out(Color.RED_BOLD, "  Tomcat Instance did not Validate, issues present!",
                    true, true, true);
            return false;
        }

        /**
         *  Conclude Validation.
         */
        return true;
    }

    /**
     * showAvailableArchives
     *
     * @param tomcatAvailableArchives Reference
     */
    protected static void showAvailableArchives(TomcatAvailableArchives tomcatAvailableArchives) {
        /**
         * Show Available Archives from Archive Site ...
         */
        ConsoleOutput.out(Color.MAGENTA_BOLD, "  Currently Available Archives from Apache Mirror: %s",
                true, true, true,
                tomcatAvailableArchives.getApacheMirrorHeadUrl());
        ConsoleOutput.out(Color.CYAN_BOLD.toString());
        ConsoleOutput.out("  Version  Name                                  Size in Bytes");
        ConsoleOutput.out("  -------  ------------------------------------- -------------");
        ConsoleOutput.issueReset();
        for(String key : tomcatAvailableArchives.getArchives().keySet()) {
            TomcatArchive tomcatArchive = tomcatAvailableArchives.getArchives().get(key);
            ConsoleOutput.out(Color.WHITE_BOLD, "  %s   %s                   %d",
                    false, true, true,
                    tomcatArchive.getShortName().trim(),
                    tomcatArchive.getName().trim(),
                    tomcatArchive.getSize());
        }
    }

    /**
     * promptForVersionToUse
     *
     * @param tomcatAvailableArchives Reference to Available Archives.
     * @return Optional<TomcatArchive> - Selected Tomcat Archive
     */
    protected static Optional<TomcatArchive> promptForVersionToUse(TomcatAvailableArchives tomcatAvailableArchives) {
        showAvailableArchives(tomcatAvailableArchives);
        Scanner scanner = new Scanner(System.in);
        ConsoleOutput.out(" ");
        ConsoleOutput.out(" Select Version to build Upgrade Shell: ", false, true);
        String selectedVersion = scanner.next();
        /**
         * Now Validate the selected Version ...
         */
        if (selectedVersion == null || selectedVersion.isEmpty() || selectedVersion.equalsIgnoreCase("none")) {
            return Optional.empty();
        }
        if(tomcatAvailableArchives.getArchives().containsKey(selectedVersion)) {
            return Optional.of(tomcatAvailableArchives.getArchives().get(selectedVersion));
        }
        /**
         * What was entered, did not match a Version ...
         */
         ConsoleOutput.out(Color.RED_BOLD, "  Selected Version '%s' is Invalid, please specify a Valid Version from list below  ...",
                 true, true, true,
                 selectedVersion);
         return promptForVersionToUse(tomcatAvailableArchives);
    }

    protected static Optional<TomcatArchive> determineVersionToUse(TomcatInstance tomcatInstance,
                                                                   TomcatAvailableArchives tomcatAvailableArchives) {
        if (tomcatInstance.getTomcatVersion() == null || tomcatInstance.getTomcatVersion().isEmpty() ||
                tomcatInstance.getTomcatVersion().equalsIgnoreCase("none")) {
            return Optional.empty();
        }
        /**
         * Check for short name, like v8.5
         */
        String selectedVersion = tomcatInstance.getTomcatVersion();
        if (tomcatInstance.getTomcatVersion().startsWith("v")) {
            selectedVersion = tomcatInstance.getTomcatVersion().substring(1);
        }
        /**
         * Attempt to Lookup the Name to find applicable Version.
         */
        if(tomcatAvailableArchives.getArchives().containsKey(selectedVersion)) {
            return Optional.of(tomcatAvailableArchives.getArchives().get(selectedVersion));
        }
        /**
         * Falling here, we need to find a like named version.
         * Lets say we have 8.5, so provide the latest 8.5 available.
         */
        for(String key : tomcatAvailableArchives.getArchives().keySet()) {
            if (selectedVersion.length() < key.length()) {
                if (selectedVersion.equalsIgnoreCase(key.substring(0, selectedVersion.length()))) {
                    return Optional.of(tomcatAvailableArchives.getArchives().get(key));
                }
            }
        }
        /**
         * Could not determine what version to use based upon provided information.
         */
        return Optional.empty();
    }

    /**
     * acquireAndPrepareArchive
     *
     * @param LOGGER - reference
     * @param tomcatAvailableArchives Available Archives
     * @param tomcatInstance - reference POJO
     * @param rename - boolean to indicate a rename of archive should occur
     * @return boolean indicates if function method was successful or not.
     */
    protected boolean acquireAndPrepareArchive(Logger LOGGER,
                                           TomcatAvailableArchives tomcatAvailableArchives,
                                           TomcatInstance tomcatInstance, boolean rename) {
        /**
         * Pull Tomcat Binary archive from Internet ...
         */
        if (!TomcatInstanceAFRYZProcessHelper.pullTomcatVersionFromApacheMirror(LOGGER,
                tomcatAvailableArchives, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Occurred during Pulling Tomcat Archive from Internet, please try again!",
                    true, true, true);
            return false;
        }
        /**
         * Validate the Size of the Archive we just Pulled from the Internet.
         */
        if (!TomcatInstanceAFRYZProcessHelper.validateTomcatDownloadedVersion(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Occurred during validating Tomcat Archive pulled from Internet!",
                    true, true, true);
            return false;
        }

        // TODO ~ Also Add Download of Necessary Extras ...

        /**
         * Explode the Binary Archive ...
         */
        if (!TomcatInstanceAFRYZProcessHelper.explodeTomcatVersionForCustomization(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Occurred during exploding Tomcat Archive pulled from Internet!",
                    true, true, true);
            return false;
        }
        /**
         * Rename the Exploded Folder to Final Folder Name for Tomcat Instance ...
         */
        if (rename) {
            if (!TomcatInstanceAFRYZProcessHelper.renameReferencedExplodedArtifact(LOGGER, tomcatInstance)) {
                ConsoleOutput.out(Color.RED_BOLD, "  Issue renaming exploded Archive, please ensure you have proper permissions!",
                        true, true, true);
                return false;
            }
            ConsoleOutput.out(Color.GREEN_BOLD, "  Successfully Renamed Instance Folder to: %s",
                    true, true, true,
                    tomcatInstance.referenceTomcatInstanceFolder());
            return true;
        } else {
            ConsoleOutput.out(Color.GREEN_BOLD, "  Successfully Exploded Instance to Folder: %s",
                    true, true, true,
                    tomcatInstance.getTomcatArchive().getHeadName());
            return true;
        }
    }

}
