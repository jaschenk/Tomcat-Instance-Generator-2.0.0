package jeffaschenk.infra.tomcat.instance.generator.util;

import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;

/**
 * ConsoleOutput
 * Helper to make the Console less ugly ...
 *
 * @author schenkje 
 */
public class ConsoleOutput {
    private static final boolean COLOR;

    /**
     * Do not allow instantiation.
     */
    private ConsoleOutput() {}

    /**
     * Obtain COLOR display Property ...
     */
    static {
        COLOR = Boolean.parseBoolean(System.getProperty(DefaultDefinitions.COLOR_PROPERTY_NAME, "true"));
    }

    /**
     * out - Perform Console Output
     * @param message to be Displayed on STDOUT.
     */
    public static void out(final String message) {
        System.out.println(message);
    }

    /**
     * out - Perform Console Output, resolving Parameters
     * @param message to be Resolved, if applicable and Displayed on STDOUT.
     * @param newline if true, indicates to output a newline to STDOUT after Message.
     * @param reset if true, indicates to output a Color Reset to STDOUT after Message.
     */
    public static void out(final String message, boolean newline, boolean reset) {
        if (newline) {
            System.out.println(message);
        } else {
            System.out.print(message);
        }
        if (reset && COLOR) {
            issueReset();
        }
    }

    /**
     * out - Perform Console Output, resolving Parameters
     * @param message to be Resolved, if applicable and Displayed on STDOUT.
     * @param newline if true, indicates to output a newline to STDOUT after Message.
     * @param reset if true, indicates to output a Color Reset to STDOUT after Message.
     * @param args used to resolve placeholders
     */
    public static void out(final String message, boolean newline, boolean reset, final Object... args) {
        if (newline) {
            System.out.println(resolve(message, args));
        } else {
            System.out.print(resolve(message, args));
        }
        if (reset && COLOR) {
            issueReset();
        }
    }

    /**
     * out - Perform Console Output, resolving Parameters
     * @param color Initial Color to be Set.
     * @param message to be Resolved, if applicable and Displayed on STDOUT.
     * @param newlineBefore if true, indicates to output a newline to STDOUT before Message.
     * @param newlineAfter if true, indicates to output a newline to STDOUT after Message.
     * @param reset if true, indicates to output a Color Reset to STDOUT after Message.
     * @param args used to resolve placeholders
     */
    public static void out(Color color, final String message,
                           boolean newlineBefore, boolean newlineAfter, boolean reset, final Object... args) {
        if (newlineBefore) {
            System.out.println(" ");
        }
        if (COLOR) { issueColor(color); }
        if (newlineAfter) {
            System.out.println(resolve(message, args));
        } else {
            System.out.print(resolve(message, args));
        }
        if (reset && COLOR) {
            issueReset();
        }
    }

    /**
     * IssueColor
     * @param c - Color to be output to STDOUT.
     */
    public static void issueColor(Color c) {
        if (COLOR) { System.out.print(c); }
    }

    /**
     * IssueReset
     * Reset previous Color settings on STDOUT.
     */
    public static void issueReset() {
        if (COLOR) { System.out.print(Color.RESET);}
    }

    /**
     * resolve - Helper Method to resolve Message Placeholders with specified parameters.
     * @param message to be resolved.
     * @param args used to resolve any applicable placeholders.
     * @return String contrived from resolved Message and applicable placeholders.
     */
    protected static String resolve(final String message, final Object... args) {
        if (args == null || args.length <= 0) {
            return message;
        }
        return String.format(message, args);
    }
}
