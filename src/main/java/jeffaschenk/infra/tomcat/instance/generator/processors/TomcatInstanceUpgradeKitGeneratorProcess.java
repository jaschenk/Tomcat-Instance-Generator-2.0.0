package jeffaschenk.infra.tomcat.instance.generator.processors;

import jeffaschenk.infra.tomcat.instance.generator.model.TomcatArchive;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatAvailableArchives;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;
import jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceProcessCustomizationHelper;
import jeffaschenk.infra.tomcat.instance.generator.util.Color;
import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;
import jeffaschenk.infra.tomcat.instance.generator.util.TomcatArchiveUtility;
import org.slf4j.Logger;

import java.io.File;
import java.util.Calendar;
import java.util.Optional;

import static jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceAFRYZProcessHelper.removeExplodedArchive;
import static jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceProcessBaseEnvHelper.createBaseEnvironment;
import static jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceProcessCustomizationHelper.customizeUpgradeKitScripts;

/**
 * TomcatInstanceUpgradeKitGeneratorProcess
 *
 * Drives the Building of a Upgrade Shell for upgrading an existing Tomcat Instance.
 * 
 * Created by schenkje on 2/13/2018.
 */
public class TomcatInstanceUpgradeKitGeneratorProcess extends TomcatInstanceProcessBase {
    /**
     * Primary Logger for Generation utility ...
     */
    private final Logger LOGGER;
    /**
     * YAML Configuration Files to be Read.
     */
    private final String[] overrides;

    /**
     * Default Constructor
     */
    public TomcatInstanceUpgradeKitGeneratorProcess(Logger LOGGER, String[] overrides) {
        this.LOGGER = LOGGER;
        this.overrides = overrides;
    }

    /**
     * Perform the Generation Process driven by CLI
     * The Tomcat Instance will be Null entering this Method, as the Upgrade Kit is not based a Tomcat Instance,
     * but we use to stow our Information necessary to build out the Template Upgrade Kit.
     */
    @Override
    public boolean performProcess(TomcatInstance tomcatInstance) {
        try {
            TomcatAvailableArchives tomcatAvailableArchives = TomcatArchiveUtility.resolveApacheMirror();
            TomcatArchiveUtility.getLatest(LOGGER, tomcatAvailableArchives);
            /**
             * Now Prompt for which Archive to use to build an Upgrade Shell.
             */
            Optional<TomcatArchive> selectedVersion = promptForVersionToUse(tomcatAvailableArchives);
            if (!selectedVersion.isPresent()) {
                return false;
            }
            TomcatArchive tomcatArchive = selectedVersion.get();
            ConsoleOutput.out(Color.GREEN_BOLD,"  Tomcat Version Selected: %s, Starting Upgrade Kit Shell Creation ...",
                    true,true,true,
                    tomcatArchive.getShortName());
            /**
             * Process the select Archive and create the Upgrade Shell, first create a necessary Tomcat Instance,
             * as entering here we will always have a null tomcat instance object, based upon this utility function.
             */
            if (tomcatInstance == null) {
                String instanceName = "v"+tomcatArchive.getShortName()+"_UPGRADE_KIT";
                File destinationFolder = new File(System.getProperty("java.io.tmpdir")
                        + File.separator + instanceName+"_"+Calendar.getInstance().getTimeInMillis());
                /**
                 * Instantiate the Tomcat Instance for creating our Upgrade Shell...
                 */
                tomcatInstance =
                        new TomcatInstance(instanceName, "Tomcat_Upgrade",
                                tomcatArchive.getShortName(), destinationFolder, true);
            }
            /**
             * Set the Selected Archive Version
             */
            tomcatInstance.setTomcatArchive(tomcatArchive);
            ConsoleOutput.out(Color.GREEN_BOLD,"  Using Work Destination Folder: %s",
                    true,true,true,
                    tomcatInstance.getDestinationFolder());

            /**
             * Acquire and Prepare our Archive ...
             */
            if(!acquireAndPrepareArchive(LOGGER, tomcatAvailableArchives, tomcatInstance, false)) {
                return false;
            }
            /**
             * Continue to Generate a Shell for Upgrade Purposes and conclude Upgrade Shell Kit Creation.
             */
            if (!createBaseEnvironment(LOGGER, tomcatInstance, false, true)) {
                return false;
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
            /**
             * Now perform any necessary Customizations ...
             */
            if (!customizeUpgradeKitScripts(LOGGER, tomcatInstance)) {
                return false;
            }
            /**
             * Now Clean-up and remove the downloaded Archive and Base Exploded Directory.
             */
            if(!removeExplodedArchive(tomcatInstance, true)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return false;
        }
    }
    
}
