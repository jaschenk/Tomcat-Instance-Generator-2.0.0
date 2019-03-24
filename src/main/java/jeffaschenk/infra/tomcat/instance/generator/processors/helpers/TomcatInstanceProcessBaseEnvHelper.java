package jeffaschenk.infra.tomcat.instance.generator.processors.helpers;

import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import jeffaschenk.infra.tomcat.instance.generator.model.InstancePorts;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;
import jeffaschenk.infra.tomcat.instance.generator.util.Color;
import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;
import jeffaschenk.infra.tomcat.instance.generator.util.FileUtility;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TomcatInstanceProcessBaseEnvHelper
 *
 * @author schenkje
 */
public class TomcatInstanceProcessBaseEnvHelper {


    /**
     * createBaseEnvironment
     *
     * @param LOGGER - Reference
     * @param tomcatInstance - Reference
     * @param createMounts - indicator if mounts should be generated as well. For Upgrade, this would be false.
     * @param upgrade - Indicates that this is processing an Upgrade process and not a full generation.
     * @return boolean indicates if function was successful or not.
     */
    public static boolean createBaseEnvironment(Logger LOGGER, TomcatInstance tomcatInstance,
                                                boolean createMounts, boolean upgrade) {
        /**
         * We now need to Carve up the Archive into a usage tomcat-home and tomcat-base-0x folders.
         */
        File destinationFolder = tomcatInstance.getDestinationFolder();
        File sourceFolder = new File(destinationFolder.getAbsolutePath()+
                File.separator + tomcatInstance.getTomcatArchive().getHeadName());
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            return false;
        }
        /**
         * Make initial Core Directories:
         * vx.x.xx
         * vx.x.xx/tomcat-base-0x
         * vx.x.xx/tomcat-home
         * vx.x.xx/tomcat-mounts
         */
        File coreFolder = new File(destinationFolder.getAbsolutePath()+
                File.separator + "v" + tomcatInstance.getTomcatArchive().getShortName());
        if (!coreFolder.mkdirs()) {
            return false;
        }
        tomcatInstance.setCoreFolder(coreFolder);
        /**
         * Create Tomcat Home
         */
        File tcHomeFolder = new File(coreFolder.getAbsolutePath()+ File.separator + DefaultDefinitions.TOMCAT_HOME_FOLDER_NAME);
        if (!tcHomeFolder.mkdirs()) {
            return false;
        }
        tomcatInstance.setTcHomeFolder(tcHomeFolder);
        FileUtility.mkDirectory(tcHomeFolder, "bin");
        FileUtility.mkDirectory(tcHomeFolder, "lib");
        if (!upgrade) {
            FileUtility.mkDirectory(tcHomeFolder, "jre");
            FileUtility.mkDirectory(tcHomeFolder, "sbin");
        }

        /**
         * Create Tomcat Base
         */
        File tcBaseFolder = new File(coreFolder.getAbsolutePath()+ File.separator + DefaultDefinitions.TOMCAT_BASE_FOLDER_TEMPLATE);
        if(!tcBaseFolder.mkdirs()) {
            return false;
        }
        tomcatInstance.setTcBaseFolder(tcBaseFolder);
        FileUtility.mkDirectory(tcBaseFolder, "bin");
        FileUtility.mkDirectory(tcBaseFolder, DefaultDefinitions.WEBAPPS);
        if (!upgrade) {
            FileUtility.mkDirectory(tcBaseFolder, DefaultDefinitions.CONF);
            FileUtility.mkDirectory(tcBaseFolder, "lib");
            FileUtility.mkDirectory(tcBaseFolder, "logs");
            FileUtility.mkDirectory(tcBaseFolder, "work");
        }
        /**
         * Create Tomcat Mounts
         */
        if (createMounts && !upgrade) {
            File tcMountsFolder = new File(coreFolder.getAbsolutePath() + File.separator + DefaultDefinitions.TOMCAT_MOUNTS_FOLDER_NAME);
            if (!tcMountsFolder.mkdirs()) {
                return false;
            }
            tomcatInstance.setTcMountsFolder(tcMountsFolder);
            FileUtility.mkDirectory(tcMountsFolder, "appData");
            FileUtility.mkDirectory(tcMountsFolder, "properties");
        }
        /**
         * Now proceed to Copy necessary File Elements in both Base and Home Directories from our source Directory.
         */
        try {
            copyFilesToTCHome(sourceFolder, tcHomeFolder);
            copyFilesToTCBase(sourceFolder, tcBaseFolder, upgrade);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return false;
        }
        /**
         * Conclude Base Creation.
         */
        return true;
    }

    /**
     * Copy Files from Tomcat Distribution to a Tomcat Home
     * @param sourceFolder - Tomcat Distribution
     * @param destDir - Tomcat Home Directory
     */
    protected static void copyFilesToTCHome(File sourceFolder, File destDir) throws IOException {

        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "CONTRIBUTING.md"), new File(destDir.getAbsolutePath()+File.separator+"CONTRIBUTING.md"));
        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "LICENSE"), new File(destDir.getAbsolutePath()+File.separator+"LICENSE"));
        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "NOTICE"), new File(destDir.getAbsolutePath()+File.separator+"NOTICE"));
        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "README.md"), new File(destDir.getAbsolutePath()+File.separator+"README.md"));
        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "RELEASE-NOTES"), new File(destDir.getAbsolutePath()+File.separator+"RELEASE-NOTES"));
        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "RUNNING.txt"), new File(destDir.getAbsolutePath()+File.separator+"RUNNING.txt"));

        /**
         * Now Copy over the Full Directories as applicable...
         */
        FileUtils.copyDirectory(new File(sourceFolder.getAbsolutePath()+File.separator+"bin"),
                new File(destDir.getAbsolutePath()+File.separator+"bin"),true);

        FileUtils.copyDirectory(new File(sourceFolder.getAbsolutePath()+File.separator+"lib"),
                new File(destDir.getAbsolutePath()+File.separator+"lib"),true);

    }

    /**
     * Copy Files from Tomcat Distribution to a Tomcat Base
     * @param sourceFolder - Tomcat Distribution
     * @param destDir - Tomcat Base Directory
     * @param upgrade - Some files will not be copied if an upgrade process
     */
    protected static void copyFilesToTCBase(File sourceFolder, File destDir, boolean upgrade) throws IOException {

        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "LICENSE"), new File(destDir.getAbsolutePath()+File.separator+"LICENSE"));
        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "NOTICE"), new File(destDir.getAbsolutePath()+File.separator+"NOTICE"));
        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "RELEASE-NOTES"), new File(destDir.getAbsolutePath()+File.separator+"RELEASE-NOTES"));
        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+
                "RUNNING.txt"), new File(destDir.getAbsolutePath()+File.separator+"RUNNING.txt"));

        FileUtils.copyFile(new File(sourceFolder.getAbsolutePath()+File.separator+"bin"+File.separator+
                "tomcat-juli.jar"), new File(destDir.getAbsolutePath()+File.separator+"bin"+File.separator+"tomcat-juli.jar"));

        /**
         * Now Copy over the Full Directories as applicable...
         */
        if (!upgrade) {
            FileUtils.copyDirectory(new File(sourceFolder.getAbsolutePath() + File.separator + DefaultDefinitions.CONF),
                    new File(destDir.getAbsolutePath() + File.separator + DefaultDefinitions.CONF), true);
        }

        FileUtils.copyDirectory(new File(sourceFolder.getAbsolutePath()+File.separator+ DefaultDefinitions.WEBAPPS+
                        File.separator+"manager"),
                new File(destDir.getAbsolutePath()+File.separator+ DefaultDefinitions.WEBAPPS+
                        File.separator+"manager"),true);

        FileUtils.copyDirectory(new File(sourceFolder.getAbsolutePath()+File.separator+ DefaultDefinitions.WEBAPPS+
                        File.separator+"ROOT"),
                new File(destDir.getAbsolutePath()+File.separator+ DefaultDefinitions.WEBAPPS+
                        File.separator+"ROOT"),true);

    }


    /**
     * replicateBaseEnvironment
     *
     * @param LOGGER - Reference
     * @param tomcatInstance - Reference
     * @return
     */
    public static boolean replicateBaseEnvironment(Logger LOGGER, TomcatInstance tomcatInstance) throws IOException {

        /**
         * Ensure our Base Folder Exists...
         */
        if (tomcatInstance.getTcBaseFolder() == null || !tomcatInstance.getTcBaseFolder().exists()) {
            ConsoleOutput.out(Color.RED_BOLD, "  No Base Environment Template Found, Unable to Continue!",
                    true, true, true);
            return false;
        }
        /**
         * Replicate the Base Environment, per number of applicable Instances we normally run per
         * machine.
         */
        if (tomcatInstance.getInstancePorts() == null || tomcatInstance.getInstancePorts().isEmpty()) {
            setDefaultInstancePorts(tomcatInstance, 1);
        }
        for(InstancePorts instancePorts : tomcatInstance.getInstancePorts()) {
            /**
             * Replicate from Base to New Instance Base ...
             */
            String instanceFolderName = DefaultDefinitions.TOMCAT_BASE_FOLDER_PREFIX + instancePorts.getInstanceNameForBaseDirectoryName();
            instancePorts.setInstanceFolder(new File(tomcatInstance.getCoreFolder().getAbsolutePath()+File.separator+instanceFolderName));
            FileUtils.copyDirectory(new File(tomcatInstance.getTcBaseFolder().getAbsolutePath()),
                    instancePorts.getInstanceFolder(),true);
            ConsoleOutput.out(Color.CYAN_BOLD, "  Successfully Replicated Tomcat Base to Instance: %s",
                    false, true, true, instancePorts.getInstanceName());
        }
        /**
         * Conclude the Replication ..
         */
        return true;
    }

    /**
     * customizeBaseEnvironmentPreReplication
     * Must occur after Replication Method Function.
     *
     * @param LOGGER - Reference
     * @param tomcatInstance - Reference
     * @return
     */
    public static boolean customizeBaseEnvironmentPreReplication(Logger LOGGER, TomcatInstance tomcatInstance) throws IOException {

            /**
             * Ensure our Base Folder Exists...
             */
            if (tomcatInstance.getTcBaseFolder() == null || !tomcatInstance.getTcBaseFolder().exists()) {
                ConsoleOutput.out(Color.RED_BOLD, "  No Base Environment Template Found, Unable to Continue!",
                        true, true, true);
                return false;
            }


            /**
             * Customize New Instance Base Template for subsequent replication...
             */
            if (!performEnvironmentCustomizations(LOGGER, tomcatInstance)) {
                ConsoleOutput.out(Color.RED_BOLD, " Issue Customizing Tomcat Base Template Instance, stopping process!",
                        true, true, true);
                return false;
            }

        /**
         * Conclude the Customizations ..
         */
        return true;
    }

    /**
     * performEnvironmentCustomizations
     *
     * All of the method functions below, must be using either home or base Template as their
     * respective file folder destination
     *
     * @param LOGGER - reference
     * @param tomcatInstance Reference
     * @return boolean Indicator if Process was successful or not...
     */
    public static boolean performEnvironmentCustomizations(Logger LOGGER, TomcatInstance tomcatInstance) throws IOException {
        /**
         * Create any new Directories in the Installation Directory.
         */
        Integer createdCount = TomcatInstanceProcessCustomizationHelper.additionalDirectories(LOGGER, tomcatInstance);
        if (createdCount < 0) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue creating new Customization Directories, please ensure you have proper permissions!",
                    true, true, true);
            return false;
        } else if (createdCount > 0) {
            ConsoleOutput.out(Color.WHITE_BOLD, "   Successfully Added Additional Directories to: %s", true, true, true,
                    tomcatInstance.referenceTomcatInstanceFolder());
            ConsoleOutput.out(" ");
        }

        /**
         * Seed new Mounts Directories in the Installation Directory.
         */
        if (!TomcatInstanceProcessCustomizationHelper.seedMountsDirectories(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue seeding new Tomcat Mounts Directories, please ensure you have proper permissions!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }
        ConsoleOutput.out(Color.WHITE_BOLD, "   Successfully Seeded Empty Directories in: %s",
                false, true, true,
                tomcatInstance.getTcMountsFolder());
        ConsoleOutput.out(" ");

        /**
         * Seed new Directories in the Installation Directory.
         */
        if (!TomcatInstanceProcessCustomizationHelper.seedDirectories(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue seeding new Customization Directories, please ensure you have proper permissions!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }
        ConsoleOutput.out(Color.WHITE_BOLD, "   Successfully Seeded Empty Directories in: %s",
                true, true, true,
                tomcatInstance.getTcBaseFolder());
        ConsoleOutput.out(" ");

        /**
         * Copy all 'lib' Artifacts from our internal Archive as Resources to the Tomcat Lib Directory.
         */
        if (!TomcatInstanceProcessCustomizationHelper.customizeAdditionalLibArtifacts(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Adding 'lib' Artifacts, please review Generation Logs!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }
        ConsoleOutput.out(Color.WHITE_BOLD, "   Successfully Added Additional Artifacts to: %s%s%s",
                false, true, true,
                tomcatInstance.referenceTomcatInstanceFolder(), File.separator, "lib");
        ConsoleOutput.out(" ");

        /**
         * Copy all 'external-lib' Artifacts from our internal Archive as Resources to the Home Tomcat Directory.
         */
        if (!TomcatInstanceProcessCustomizationHelper.customizeExternalLibArtifacts(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Adding 'external lib' Artifacts, please review Generation Logs!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        if (!TomcatInstanceProcessCustomizationHelper.customizeAdditionalHomeResources(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Adding Home Resource Artifacts, please review Generation Logs!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        /**
         * Copy all 'webapps' Artifacts from our internal Archive as Resources to the Tomcat webapps Directory.
         */
        if (tomcatInstance.isInstanceManagement()) {
            if (!TomcatInstanceProcessCustomizationHelper.addManageCatAgentToWebApps(LOGGER, tomcatInstance)) {
                ConsoleOutput.out(Color.RED_BOLD, "  Issue Adding 'webapps' Artifacts, please review Generation Logs!",
                        true, true, true);
                ConsoleOutput.out(" ");
                return false;
            }
            ConsoleOutput.out(Color.WHITE_BOLD, "   Successfully Added Additional Applications to: %s%s%s",
                    true, true, true,
                    tomcatInstance.referenceTomcatInstanceFolder(), File.separator, DefaultDefinitions.WEBAPPS);
            ConsoleOutput.out(" ");
        }

        /**
         * Perform Specific Customizations for any existing WebApps, such as the Tomcat Manager ...
         */
        if (!TomcatInstanceProcessCustomizationHelper.customizeManagerApp(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Customizing the 'manager' application, please review Generation Logs!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        if (!TomcatInstanceProcessCustomizationHelper.customizeROOTApp(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Customizing the 'ROOT' application, please review Generation Logs!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        if (!TomcatInstanceProcessCustomizationHelper.purgeWEBAPPS(LOGGER, tomcatInstance)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Purging unnecessary 'webapps', please review Generation Logs!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }

        ConsoleOutput.out(Color.WHITE_BOLD, "   Successfully Customized Applications in: %s%s%s",
                true, true, true,
                tomcatInstance.referenceTomcatInstanceFolder(), File.separator, DefaultDefinitions.WEBAPPS);
        ConsoleOutput.out(" ");

        /**
         * Return indicating Successful Generation of Development Instance
         */
        return true;
    }

    /**
     * customizeBaseEnvironmentPostReplication
     * Must occur after Replication Method Function.
     *
     * @param LOGGER - Reference
     * @param tomcatInstance - Reference
     * @return
     */
    public static boolean customizeBaseEnvironmentPostReplication(Logger LOGGER, TomcatInstance tomcatInstance) throws IOException {

        /**
         * Ensure our Base Folder Exists...
         */
        if (tomcatInstance.getTcBaseFolder() == null || !tomcatInstance.getTcBaseFolder().exists()) {
            ConsoleOutput.out(Color.RED_BOLD, "  No Base Environment Template Found, Unable to Continue!",
                    true, true, true);
            return false;
        }
        /**
         * Replicate the Base Environment, per number of applicable Instances we normally run per
         * machine.
         */
        if (tomcatInstance.getInstancePorts() == null || tomcatInstance.getInstancePorts().isEmpty()) {
            setDefaultInstancePorts(tomcatInstance, 1);
        }
        for(InstancePorts instancePorts : tomcatInstance.getInstancePorts()) {
            /**
             * Customize New Instance Base ...
             */
            ConsoleOutput.out(Color.GREEN_BOLD, "  Customizing Tomcat Base Instance: %s",
                    true, true, true, instancePorts.getInstanceName());
            if (performEnvInstanceCustomizations(LOGGER, tomcatInstance, instancePorts)) {
                ConsoleOutput.out(Color.CYAN_BOLD, "  Successfully Customized Tomcat Base Instance: %s",
                        true, true, true, instancePorts.getInstanceName());
            } else {
                ConsoleOutput.out(Color.RED_BOLD, " Issue Customizing Tomcat Base Instance: %s, stopping process!",
                        true, true, true, instancePorts.getInstanceName());
                return false;
            }
        }
        /**
         * Conclude the Customizations ..
         */
        return true;
    }

    /**
     * performEnvInstanceCustomizations
     *
     * @param tomcatInstance Reference
     * @return boolean Indicator if Process was successful or not...
     */
    public static boolean performEnvInstanceCustomizations(Logger LOGGER, TomcatInstance tomcatInstance, InstancePorts instancePorts) throws IOException {
        /**
         * Perform 'conf' Customization Changes ...
         */
        if (!TomcatInstanceProcessCustomizationHelper.customizeConf(LOGGER, tomcatInstance, instancePorts)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Customizing the 'conf' files, please review Generation Logs!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }
        ConsoleOutput.out(Color.GREEN_BOLD, "  Successfully Customized 'conf'  in: %s%s%s",
                true, true, true,
                tomcatInstance.referenceTomcatInstanceFolder(), File.separator, DefaultDefinitions.CONF);
        ConsoleOutput.out(" ");

        /**
         * Perform 'bin' Customization Changes ...
         */
        if (!TomcatInstanceProcessCustomizationHelper.customizeBin(LOGGER, tomcatInstance, instancePorts)) {
            ConsoleOutput.out(Color.RED_BOLD, "  Issue Customizing the 'bin' files, please review Generation Logs!",
                    true, true, true);
            ConsoleOutput.out(" ");
            return false;
        }
        ConsoleOutput.out(Color.GREEN_BOLD, "  Successfully Customized 'bin'  in: %s%s%s",
                true, true, true,
                tomcatInstance.referenceTomcatInstanceFolder(), File.separator, "bin");
        ConsoleOutput.out(" ");

        /**
         * Return indicating Successful Generation of Development Instance
         */
        return true;
    }

    /**
     * Helper method setDefaultInstancePorts
     * @param tomcatInstance Reference
     */
    private static void setDefaultInstancePorts(TomcatInstance tomcatInstance, Integer numberToCreate) {
        if (numberToCreate == null || numberToCreate > DefaultDefinitions.DEFAULT_INSTANCE_PORT_MAP.size()) {
            numberToCreate = 1;
        }
        List<InstancePorts> instancePorts = new ArrayList<>();
            for (String instanceKey : DefaultDefinitions.DEFAULT_INSTANCE_PORT_MAP.keySet()) {
                instancePorts.add(DefaultDefinitions.DEFAULT_INSTANCE_PORT_MAP.get(instanceKey));
                if (instancePorts.size()==numberToCreate) {
                    break;
                }
            }
        tomcatInstance.setInstancePorts(instancePorts);
    }
}
