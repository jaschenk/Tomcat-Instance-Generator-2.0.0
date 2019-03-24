package jeffaschenk.infra.tomcat.instance.generator.processors;

import jeffaschenk.infra.tomcat.instance.generator.model.TomcatArchive;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatAvailableArchives;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;

import jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceAFRYZProcessHelper;
import jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceProcessBaseEnvHelper;
import jeffaschenk.infra.tomcat.instance.generator.util.Color;
import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;
import jeffaschenk.infra.tomcat.instance.generator.util.FileUtility;
import jeffaschenk.infra.tomcat.instance.generator.util.TomcatArchiveUtility;
import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

import static jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceProcessBaseEnvHelper.*;

/**
 * TomcatInstanceGeneratorProcess
 * <p>
 * Drives the Tomcat Instance Generation from a Command Line Interface.
 * <p>
 * Created by schenkje on 9/10/2018.
 */
public class TomcatInstanceGeneratorProcess extends TomcatInstanceProcessBase implements TomcatInstanceProcess {
    /**
     * Primary Logger for Generation utility ...
     */
    private final Logger LOGGER;
    /**
     * YAML Configuration Files to be Read.
     */
    private final String[] yamlConfigurationFiles;

    /**
     * Default Constructor
     */
    public TomcatInstanceGeneratorProcess(Logger LOGGER, String[] yamlConfigurationFiles) {
        this.LOGGER = LOGGER;
        this.yamlConfigurationFiles = yamlConfigurationFiles;
    }

    /**
     * Perform the Generation Process driven by CLI
     */
    @Override
    public boolean performProcess(TomcatInstance tomcatInstance) {
        try {
            /**
             * Validate the Tomcat Instance Definition ...
             */
            if (!validate(tomcatInstance)) {
                return false;
            }
            ConsoleOutput.out(Color.GREEN_BOLD, "  Tomcat Instance YAML Validation Successful.",
                    true, true, true);

            /**
             * Obtain our Tomcat Available Archives and our default Archive Location...
             */
            TomcatAvailableArchives tomcatAvailableArchives = TomcatArchiveUtility.resolveApacheMirror();
            TomcatArchiveUtility.getLatest(LOGGER, tomcatAvailableArchives);
            /**
             * Now determine which Archive to use to build, based upon Tomcat Instance definition.
             */
            Optional<TomcatArchive> selectedVersion = determineVersionToUse(tomcatInstance, tomcatAvailableArchives);
            if (!selectedVersion.isPresent()) {
                ConsoleOutput.out(Color.RED_BOLD, "  Unable to determine what Tomcat version to use, as specified version: %s is unknown!",
                        true, true, true, tomcatInstance.getTomcatVersion());
                return false;
            }
            TomcatArchive tomcatArchive = selectedVersion.get();
            ConsoleOutput.out(Color.GREEN_BOLD,"  Tomcat Version Selected: %s, Starting generation process ...",
                    true,true,true,
                    tomcatArchive.getShortName());
            /**
             * Set the Selected Archive Version, as well as our Destination for our Work.
             */
            tomcatInstance.setTomcatArchive(tomcatArchive);
            if (tomcatInstance.getDestinationFolder() == null) {
                tomcatInstance.setDestinationFolder(new File(System.getProperty("java.io.tmpdir")));
            }
            tomcatInstance.setDestinationFolder(new File(tomcatInstance.getDestinationFolder().getAbsoluteFile()
                    + File.separator + tomcatInstance.getInstanceName() + "_" + Calendar.getInstance().getTimeInMillis()));
            /**
             * Begin Process ..
             */
            ConsoleOutput.out(Color.GREEN_BOLD, "  Generating Tomcat Instance %s based upon Version: %s",
                    true, true, true,
                    tomcatInstance.getInstanceName(), tomcatInstance.getTomcatArchive().getShortName());
            ConsoleOutput.out(Color.GREEN_BOLD,"  Using Work Destination Folder: %s",
                    true,true,true,
                    tomcatInstance.getDestinationFolder());
            /**
             * Acquire and Prepare our Archive ...
             */
            if (!acquireAndPrepareArchive(LOGGER, tomcatAvailableArchives, tomcatInstance, false)) {
                return false;
            }
            /**
             * First determine what type of Generation we are performing, we base it upon
             * the Environment type:
             *  + DEV : Produce a single Development Instance.
             *  +  Environment : Produce a Multiple Base Instance,
             *                       suitable for establishing a runtime environment with
             *                       minimal modifications.
             */
            boolean response = false;
            switch (tomcatInstance.getEnvironmentName().toUpperCase()) {
                case "DEV":
                    response = generateDevelopmentInstance(tomcatInstance);
                    break;
                case "STG":
                case "STG2":
                case "STG3":
                case "STG9":
                case "ST":
                case "ST2":
                case "ST3":
                case "ST9":
                case "MO":
                case "MO2":
                case "MO3":
                case "MO9":
                case "PRD":
                    response = generateRuntimeEnvironmentInstance(tomcatInstance, true);
                    break;
                default:
                    ConsoleOutput.out(Color.RED_BOLD, "  Unable to determine process for Environment: %s",
                            true, true, true,
                            tomcatInstance.getEnvironmentName());
                    return false;
            }
            if (!response) {
                 ConsoleOutput.out(Color.RED_BOLD, "  Customization Phase of Instance %s, was not successful!",
                        true, true, true,
                        tomcatInstance.referenceTomcatInstanceFolder());
                 ConsoleOutput.out(" ");
                 return false;
            }
            /**
             * Generate the Associated YAML Configuration for this Customization.
             */
            if (!TomcatInstanceAFRYZProcessHelper.generateYAMLConfigurationForInstance(LOGGER, tomcatInstance)) {
                ConsoleOutput.out(Color.RED_BOLD, "  Issue creating YAML configuration File, please ensure you have proper permissions!",
                        true, true, true);
                return false;
            }
            /**
             * If Applicable,  Zip up the created Tomcat Instance.
             */
            performInstanceArchiveCreation(LOGGER, tomcatInstance);

            /**
             * Show final Completion Message...
             */
            ConsoleOutput.out(Color.CYAN_BOLD, "  Customization Phase of Instance %s, Completed. ",
                    true, true, true,
                    tomcatInstance.referenceTomcatInstanceFolder());
            ConsoleOutput.out(" ");
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * generateDevelopmentInstance
     *
     * @param tomcatInstance Reference
     * @return boolean Indicator if Process was successful or not...
     */
    protected boolean generateDevelopmentInstance(TomcatInstance tomcatInstance) throws Exception {
        if (tomcatInstance.getInstancePorts() != null && tomcatInstance.getInstancePorts().size() > 1) {
            return generateRuntimeEnvironmentInstance(tomcatInstance, true);
        }
        /**
         * Set up Initial Externalized Directory for Mounts Location ...
         */
        File tcMountsFolder = new File(tomcatInstance.getDestinationFolder().getAbsolutePath() +
                File.separator + DefaultDefinitions.TOMCAT_MOUNTS_FOLDER_NAME);
        if(!tcMountsFolder.mkdirs()) {
            return false;
        }
        tomcatInstance.setTcMountsFolder(tcMountsFolder);
        FileUtility.mkDirectory(tcMountsFolder, "appData");
        FileUtility.mkDirectory(tcMountsFolder, "properties");
        /**
         * Perform Development Environment Customizations
         */
        if(!TomcatInstanceProcessBaseEnvHelper.performEnvironmentCustomizations(LOGGER, tomcatInstance)) {
            return false;
        }
        return performEnvInstanceCustomizations(LOGGER, tomcatInstance, null);
    }

    /**
     * generateRuntimeEnvironmentInstance
     *
     * @param tomcatInstance Reference
     * @param deleteBaseTemplateFolder Indicator to delee base Template folder when completed.
     * @return boolean Indicator if Process was successful or not...
     */
    protected boolean generateRuntimeEnvironmentInstance(TomcatInstance tomcatInstance,
                                                         boolean deleteBaseTemplateFolder) throws IOException {

        /**
         * Continue to Generate a Shell for Upgrade Purposes and conclude Upgrade Shell Kit Creation.
         */
        if (!createBaseEnvironment(LOGGER, tomcatInstance, true, false)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue creating a Base Environment!!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        /**
         * Now Perform Customizations over all Generated built instances...
         */
        if (!customizeBaseEnvironmentPreReplication(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Customizing the Base Environment!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        /**
         * Now Perform Replication of Base Instance Template to generate necessary number of Base Instances
         * which will run per machine instance.
         */
        if (!replicateBaseEnvironment(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Replicating the Base Environment!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        /**
         * Now Customize each instance which has been replicated...
         */
        if (!customizeBaseEnvironmentPostReplication(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Customizing the Replicated Base Environment!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        /**
         * Now remove the Base template Directory as this is no longer needed...
         */
        if (deleteBaseTemplateFolder) {
            if (!FileUtility.removeDirectoryAndContents(tomcatInstance.getTcBaseFolder().getAbsolutePath(), false)) {
                ConsoleOutput.out(Color.YELLOW_BOLD, "  Unable to Delete Base Template: %s",
                        true, true, true, tomcatInstance.getTcBaseFolder());
            }
        }

        /**
         * Remove Originally Exploded File we obtained from Internet.
         */
        if (tomcatInstance.getTomcatArchive().getName().endsWith(DefaultDefinitions.ZIP_ARCHIVE_SUFFIX)) {
            File dirToBePurged = new File(tomcatInstance.getCoreFolder().getParent() + File.separator +
                    tomcatInstance.getTomcatArchive().getHeadName());
            ConsoleOutput.out(Color.GREEN_BOLD, "  Removing the original exploded folder: %s",
                    true, true, true, dirToBePurged);
            if (!FileUtility.removeDirectoryAndContents(dirToBePurged.getAbsolutePath(), false)) {
                ConsoleOutput.out(Color.YELLOW_BOLD, "  Unable to Delete Original Exploded Archive: %s",
                        true, true, true, dirToBePurged);
            }
        }

        /**
         * Return indicating Successful Generation of Runtime Development Instance
         */
        return true;
    }
    
}
