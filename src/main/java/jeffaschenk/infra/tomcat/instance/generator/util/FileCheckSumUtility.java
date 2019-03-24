package jeffaschenk.infra.tomcat.instance.generator.util;

import jeffaschenk.infra.tomcat.instance.generator.processors.helpers.TomcatInstanceAFRYZProcessHelper;

import java.io.File;

/**
 * FileCheckSumUtility
 */
public class FileCheckSumUtility {
    /**
     * main
     * @param args filename to produce checksums
     * @throws Exception thrown if issues...
     */
    public static void main(String args[]) throws Exception {
        if (args == null || args.length == 0) {
            ConsoleOutput.out(Color.GREEN_BOLD, "  Usage FileCheckSumUtility <File Name to Produce CheckSum>",
                    true, true, true);
            return;
        }
        /**
         * Iterate over all Files and Produce CheckSums.
         */
        for(String filename : args) {
            File file = new File(filename);
            if (file.exists())  {
                ConsoleOutput.out(Color.CYAN_BOLD,"File:'%s', Byte Length = '%d', CheckSum = '%s'.",
                        true, true, true,
                        filename, file.length(), TomcatInstanceAFRYZProcessHelper.getFileCheckSum(filename));
            } else {
                System.out.println("File:'"+filename+"', does not exist!'");
            }
        }
    }
}
