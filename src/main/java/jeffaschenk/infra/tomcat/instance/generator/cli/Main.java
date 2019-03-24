package jeffaschenk.infra.tomcat.instance.generator.cli;

import jeffaschenk.infra.tomcat.instance.generator.processors.TomcatInstanceArchivePullProcess;
import jeffaschenk.infra.tomcat.instance.generator.processors.TomcatInstanceGeneratorProcess;
import jeffaschenk.infra.tomcat.instance.generator.processors.TomcatInstanceUpgradeKitGeneratorProcess;
import jeffaschenk.infra.tomcat.instance.generator.processors.TomcatInstanceValidationProcess;
import jeffaschenk.infra.tomcat.instance.generator.util.Color;
import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main CLI Process Invocation
 * <p>
 * Created by schenkje on 2/17/2017.
 */
public class Main {
    /**
     * Logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * VERSION Specification
     */
    private static final String VERSION = "2.0.0";

    /**
     * Main CLI Bootstrap Processing
     *
     * @param args Arguments which are YAML Filenames to process...
     */
    public static void main(String[] args) {
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out(Color.CYAN_BOLD,"   Tomcat Instance Generation Utility %s",
                true, true, true, VERSION);
        ConsoleOutput.out("  --------------------------------------------- ");
        ConsoleOutput.out(" ");
        ConsoleOutput.issueReset();
        /**
         * Ensure we have at least one Argument to Drive the Generation process.
         */
        if (args == null || args.length == 0) {
            ConsoleOutput.out("  Error: No Tomcat Instance Generation arguments specified, unable to Continue!");
            usage();
            return;
        }

        /**
         * Show Arguments from being stacked up...
         */
        ConsoleOutput.out("  Arguments supplied:");
        for(String arg : args) {
            ConsoleOutput.out(Color.CYAN_BOLD,"    + %s",
                    false, true, true, arg);
        }

        /**
         * Determine what method to be performed ...
         */
        boolean response = false;
        switch (args[0]) {
            case "--generate":
            case "-generate":
            case "generate":
                response = generate(shift(args));
                break;
            case "--validate":
            case "-validate":
            case "validate":
                response = validate(shift(args));
                break;
            case "--pull":
            case "-pull":
            case "pull":
                response = pull(shift(args));
                break;
            case "--upgradeKit":
            case "-upgradeKit":
            case "upgradeKit":
                response = upgradeKit(shift(args));
                break;
            case "--help":
            case "-help":
            case "help":
            case "--?":
            case "-?":
            case "?":
                usage();
                response = true;
                break;
            default:
                ConsoleOutput.out(Color.RED_BOLD,"  Error: Unable to determine function based upon argument specified: %s",
                        true, true, true, args[0]);
                usage();
                System.exit(-1);
        }
        if (response) {
            ConsoleOutput.out(Color.GREEN_BOLD, "  Processing Completed Successfully.", true, true, true);
            System.exit(0);
        } else {
            ConsoleOutput.out(Color.RED_BOLD,
                    "  Warning: Processing Completed, however, Issues were detected, please check generation Log!",
                    true,true,true);
            System.exit(1);
        }
    }

    /**
     * Generate
     * @param args Reference
     * @return boolean indicates if function was successful or not...
     */
    private static boolean generate(String[] args) {
        TomcatInstanceGeneratorProcess processor = new TomcatInstanceGeneratorProcess(LOGGER, args);
        return processor.processYAMLFiles(LOGGER, args, processor);
    }

    /**
     * Pull
     * @param args Reference
     * @return boolean indicates if function was successful or not...
     */
    private static boolean pull(String[] args) {
        TomcatInstanceArchivePullProcess processor = new TomcatInstanceArchivePullProcess(LOGGER, args);
        return processor.performProcess(null);
    }

    /**
     * Validate
     * @param args Reference
     * @return boolean indicates if function was successful or not...
     */
    private static boolean validate(String[] args) {
        TomcatInstanceValidationProcess processor = new TomcatInstanceValidationProcess(LOGGER, args);
        return processor.processYAMLFiles(LOGGER, args, processor);
    }

    /**
     * Upgrade
     * @param args Reference
     * @return boolean indicates if function was successful or not...
     */
    private static boolean upgradeKit(String[] args) {
        TomcatInstanceUpgradeKitGeneratorProcess processor = new TomcatInstanceUpgradeKitGeneratorProcess(LOGGER, args);
        return processor.performProcess(null);
    }

    /**
     * Usage
     */
    private static void usage() {
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out(" Usage: java -Dapache.mirror.head.url=${APACHE_MIRROR_URL} -jar TomcatInstanceGenerator.jar \\");
        ConsoleOutput.out("              <command> <Tomcat Instance Yaml File(s) ...>");
        ConsoleOutput.out(" ");
        ConsoleOutput.issueColor(Color.GREEN_BOLD);
        ConsoleOutput.out("  Where runtime properties are: ");
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out("   -Dcolor=[true|false]");
        ConsoleOutput.issueColor(Color.GREEN_BOLD);
        ConsoleOutput.out("       + Default: true, turn off colorized output with a value of false.");
        ConsoleOutput.out(" ");
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out("   -Dapache.mirror.head.url=[Apache Mirror URL.../tomcat]");
        ConsoleOutput.issueColor(Color.GREEN_BOLD);
        ConsoleOutput.out("       + Specifies the HTTP URL for Base Mirror Distribution to be obtained from an Apache Mirror Site,");
        ConsoleOutput.out("       + Pick an Apache mirror and append '/tomcat' and specify as above property value.");
        ConsoleOutput.out("       + Default: None, property must be specified, see Associated Distribution Scripts.");
        ConsoleOutput.issueColor(Color.GREEN_BOLD);
        ConsoleOutput.out("       + For Example: -Dapache.mirror.head.url=", false, false);
        ConsoleOutput.issueColor(Color.MAGENTA_BOLD_BRIGHT);
        ConsoleOutput.out("http://apache.cs.utah.edu/tomcat/");
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out(" ");
        ConsoleOutput.out("        ++ List of Mirrors can be found on the Apache Download Page:");
        ConsoleOutput.out("             https://www.apache.org/dyn/closer.cgi");
        ConsoleOutput.out(" ");
        ConsoleOutput.out("        ++ Also status of all mirrors can be found here:");
        ConsoleOutput.out("             https://www.apache.org/mirrors/");
        ConsoleOutput.out(" ");
        ConsoleOutput.issueReset();
        ConsoleOutput.out("");
        ConsoleOutput.issueColor(Color.GREEN_BOLD);
        ConsoleOutput.out("  Where valid commands are: ");
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out("   generate   ", false, true);
        ConsoleOutput.out("- Process specified YAML Files and perform applicable generation.");
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out("   validate   ", false, true);
        ConsoleOutput.out("- Validate specified YAML Files, no generation will take place.");
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out("   pull       ", false, true);
        ConsoleOutput.out("- Pull Latest Available Tomcat Archive Information.");
        ConsoleOutput.issueColor(Color.CYAN_BOLD);
        ConsoleOutput.out("   upgradeKit ", false, true);
        ConsoleOutput.out("- Create an Upgrade Kit Shell based upon selected Available Tomcat Archives.");
        ConsoleOutput.out("");
        ConsoleOutput.issueReset();
    }

    /**
     * Shift - Helper method to Shift Arguments over by one ...
     * @param args - Existing String argument Array
     * @return String[] Contrived array based upon args, but shifted left one element.
     */
    private static String[] shift(String[] args) {
        if (args == null || args.length<=1) {
            return new String[0];
        }
        String[] newArgs = new String[args.length-1];
        for(int i=1;i<args.length;i++) {
            newArgs[i-1] = args[i];
        }
        return newArgs;
    }
}
