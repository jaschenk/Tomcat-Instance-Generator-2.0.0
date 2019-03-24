package jeffaschenk.infra.tomcat.instance.generator.processors;

import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;
import jeffaschenk.infra.tomcat.instance.generator.util.Color;
import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;
import org.slf4j.Logger;

/**
 * TomcatInstanceGeneratorProcess
 *
 * Drives the Tomcat Instance Generation from a Command Line Interface.
 * 
 * Created by schenkje on 2/16/2017.
 */
public class TomcatInstanceValidationProcess extends TomcatInstanceProcessBase {
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
    public TomcatInstanceValidationProcess(Logger LOGGER, String[] yamlConfigurationFiles) {
        this.LOGGER = LOGGER;
        this.yamlConfigurationFiles = yamlConfigurationFiles;
    }

    /**
     * Perform the Generation Process driven by CLI
     */
    @Override
    public boolean performProcess(TomcatInstance tomcatInstance) {
        /**
         * Validate the Tomcat Instance Definition ...
         */
        if (!validate(tomcatInstance)) {
            return false;
        }
        ConsoleOutput.out(Color.GREEN_BOLD, "  Tomcat Instance YAML Validation Successful.",
                true,true,true);
        return true;
    }
}
