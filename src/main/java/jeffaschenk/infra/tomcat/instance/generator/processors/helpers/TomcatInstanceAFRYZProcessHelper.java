package jeffaschenk.infra.tomcat.instance.generator.processors.helpers;

import jeffaschenk.infra.tomcat.instance.generator.model.*;
import jeffaschenk.infra.tomcat.instance.generator.util.Color;
import jeffaschenk.infra.tomcat.instance.generator.util.ConsoleOutput;

import jeffaschenk.infra.tomcat.instance.generator.util.FileUtility;
import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatArchive;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatAvailableArchives;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/***
 *               ______ _______     ________
 *         /\   |  ____|  __ \ \   / /___  /
 *        /  \  | |__  | |__) \ \_/ /   / /
 *       / /\ \ |  __| |  _  / \   /   / /
 *      / ____ \| |    | | \ \  | |   / /__
 *     /_/    \_\_|    |_|  \_\ |_|  /_____|
 *
 *
 */

/**
 * TomcatInstanceAFRYZProcessHelper
 * AFRYZ -- Archive File Resource YAML Zip Process Helper
 *
 *
 *
 * <p>
 * Created by jeffaschenk@gmail.com on 2/16/2017.
 */
public class TomcatInstanceAFRYZProcessHelper {

    /**
     * Private Constructor
     */
    private TomcatInstanceAFRYZProcessHelper() {}

    /**
     * Additional repetitively used Constants
     */
    private static final String UNABLE_TO_DETERMINE_ARCHIVE_FOR_DOWNLOAD =
            "  Unable to determine a Download Archive for Tomcat Version: %s" +
                    ", Notify Engineering to Support new Version of Tomcat!";

    /**
     * Pull specified Version of Tomcat from Internet Download Site ...
     *
     * @param LOGGER Logger
     * @param tomcatInstance    POJO
     */
    public static boolean pullTomcatVersionFromApacheMirror(Logger LOGGER,
                                                               TomcatAvailableArchives tomcatAvailableArchives,
                                                               TomcatInstance tomcatInstance) {
        /**
         * First determine the Latest Release based upon what the configuration Required.
         *
         * The TomcatInstance tomcatVersion property/field will contain the high-level name of the
         * version to pull.  Either v8.5 ot v9.0.
         */
        if (tomcatInstance.getTomcatArchive() == null) {
            TomcatArchive tomcatArchive =
                    tomcatAvailableArchives.getLatestAvailableArchiveByVersionName(tomcatInstance.getTomcatVersion());
            if (tomcatArchive == null || tomcatArchive.getName() == null) {
                ConsoleOutput.out(Color.RED_BOLD, UNABLE_TO_DETERMINE_ARCHIVE_FOR_DOWNLOAD,
                        true, true, true,
                        tomcatInstance.getTomcatVersion());
                return false;
            }
            tomcatInstance.setTomcatArchive(tomcatArchive); // Set a Reference to archive used.
        }
        /**
         * Now ensure we have created our FileDestination Folder, if it does not exist.
         */
        if (tomcatInstance.getDestinationFolder()==null) {
            ConsoleOutput.out(Color.RED_BOLD,
                    "  No Destination Folder Specified, please re-specify Destination Folder or check Permission!",
                    true, true, true);
            return false;
        } else if (!tomcatInstance.getDestinationFolder().exists()) {
            try {
                Files.createDirectories(tomcatInstance.getDestinationFolder().toPath());
            } catch(IOException ioe) {
                ConsoleOutput.out(Color.RED_BOLD,
                        "  Error Creating Destination Folder: %s, please re-specify Destination Folder or check Permission!",
                        true, true, true,
                        tomcatInstance.getDestinationFolder().getAbsolutePath());
                return false;
            }
        }
        /**
         * Now check to see if the Artifact has already been pulled?
         */
        if (validateTomcatDownloadedVersion(LOGGER, tomcatInstance, false)) {
            ConsoleOutput.out(Color.GREEN_BOLD, "  Using previously Downloaded Archive: %s",
                    true, true, true,
                    tomcatInstance.getTomcatArchive().getName());
            return true;
        }
        /**
         * Proceeding to Pull Archive ...
         */
        ConsoleOutput.out(Color.GREEN_BOLD, "  Pulling Tomcat Version from Apache Mirror ...", true, true, true);
        /**
         * Here we will add a recovery iteration, just in case the initial payload is short.
         * This frequently happens when attaching to Mirrored Servers.  If it occurs more often
         * then not, change to a different mirror.  You can set a property for that.
         * For Example: set property name: 'apache.mirror.head.url' to a value mirror prior to executing the Jar.
         *
         * java -Dapache.mirror.head.url=http://apache.cs.utah.edu/tomcat -jar ...
         *
         * List of Mirrors can be found on the apache Download Page:
         * https://www.apache.org/dyn/closer.cgi
         *
         * Pick an Apache mirror and append the .../tomcat and give it a whirl...
         *
         * Also status of all mirrors can be found here:
         * https://www.apache.org/mirrors/
         *
         */
        Integer attempts = 0;
        while(attempts < DefaultDefinitions.TOMCAT_APACHE_MIRROR_MAX_RETRIES) {
            attempts++;
            if (attempts > 1) {
                ConsoleOutput.out(Color.YELLOW_BOLD, "     ... Will Retry Download Attempt ... ",
                        false, true, true,
                        (attempts+1), tomcatInstance.getTomcatArchive().getName());
            }
            if (!pullArchive(LOGGER, tomcatInstance)) {
                // If something else occurs, fail fast...
                return false;
            }
            /**
             * Now immediately check if our file sizes are good, if not make another attempt...
             */
            if (TomcatInstanceAFRYZProcessHelper.validateTomcatDownloadedVersion(LOGGER, tomcatInstance, false)) {
                // If our archive validates, return now...
                return true;
            }
        }
        /**
         * Unable to acquire Archive...
         */
        ConsoleOutput.out(Color.YELLOW_BOLD, "  Attempted %d times to pull selected Archive from Mirror: %s, however, unable to validate!",
                true, true, true,
                attempts, tomcatInstance.getTomcatArchive().getName());
        return false;
    }

    /**
     * pullArchive
     * Helper method used in primary attempt logic, which does the actual download.
     *
     * @param LOGGER - Reference
     * @param tomcatInstance Instance Provide Selected Archive.
     * @return
     */
    private static boolean pullArchive(Logger LOGGER, TomcatInstance tomcatInstance) {
        URL url = null;
        URLConnection con = null;
        int i;
        try {
            /**
             * Now construct the URL to use to Pull over Internet.
             */
            url = new URL(tomcatInstance.getTomcatArchive().getDownloadURL());
            ConsoleOutput.out(Color.WHITE_BOLD, "   Using URL for Downloading Artifact: %s",
                    false, true, true,
                    url.toString());

            con = url.openConnection();
            BufferedInputStream bis = new BufferedInputStream(
                    con.getInputStream());
            try(
                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                                    tomcatInstance.getTomcatArchive().getName()))) {
                while ((i = bis.read()) != -1) {
                    bos.write(i);
                }
                bos.flush();
            }
            bis.close();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
        return false;
    }

    /**
     * Validate the Tomcat Version Archive from our Download
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @return boolean indicating if process was successful or not.
     */
    public static boolean validateTomcatDownloadedVersion(Logger LOGGER,
                                                             TomcatInstance tomcatInstance) {
        return validateTomcatDownloadedVersion(LOGGER, tomcatInstance, true);
    }

    /**
     * Validate the Tomcat Version Archive from our Download
     *
     * @param LOGGER Reference
     * @param tomcatInstance    POJO
     * @param verbose           indicator for logging.
     * @return boolean indicating if process was successful or not.
     */
    protected static boolean validateTomcatDownloadedVersion(Logger LOGGER,
                                                             TomcatInstance tomcatInstance, boolean verbose) {
        if (verbose) {
            ConsoleOutput.out(Color.GREEN_BOLD, "  Validating Tomcat Version Archive ...",
                    true, true, true);
        }
        /**
         * Now determine the Latest Release based upon our Short name.
         */
        TomcatArchive tomcatArchive = tomcatInstance.getTomcatArchive();
        if (tomcatArchive == null || tomcatArchive.getShortName() == null) {
            ConsoleOutput.out(Color.RED_BOLD, UNABLE_TO_DETERMINE_ARCHIVE_FOR_DOWNLOAD, true, true, true,
                    tomcatInstance.getTomcatVersion());
            return false;
        }
        try {
            File archiveFile = new File(tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                    tomcatArchive.getName());
            if (!archiveFile.exists()) {
                if (verbose) {
                    ConsoleOutput.out(Color.YELLOW_BOLD, "  Attempted to Validate previously Downloaded Archive: %s" +
                            ", however, the archive does not exist!",
                            true, true, true, archiveFile.getAbsolutePath());
                }
                return false;
            }
            /**
             * Validate the Size.
             */
            if (archiveFile.length() == tomcatArchive.getSize()) {
                if (verbose) {
                    ConsoleOutput.out(Color.GREEN_BOLD,
                            "  Validated Downloaded Archive: %s, Completed ...",
                            true, true, true,
                            archiveFile.getAbsolutePath(), getFileCheckSum(archiveFile.getAbsolutePath()));
                    ConsoleOutput.out(Color.CYAN_BOLD,
                            "     + File Size: '%d', Correct.   Computed CheckSum:'%s'.",
                            true, true, true,
                            archiveFile.length(), getFileCheckSum(archiveFile.getAbsolutePath()));
                }
                return true;
            } else {
                ConsoleOutput.out(Color.RED_BOLD, "  Downloaded Archive: %s, File Size Not Correct!",
                        true, true, true,
                        archiveFile.getAbsolutePath());
                ConsoleOutput.out(Color.YELLOW_BOLD,
                        "     - File Size: '%d', Invalid, should have been: '%d', removing bad File Image!",
                        false, true, true,
                        archiveFile.length(), tomcatArchive.getSize());
                FileUtility.delete(archiveFile, false);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * removeExplodedArchive
     *
     * @param tomcatInstance - reference
     * @param removeAll - indicator to determine if explode zip and zip itself should be removed...
     * @return boolean indicates if function was successful or not.
     */
    public static boolean removeExplodedArchive(TomcatInstance tomcatInstance, boolean removeAll) {
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
            /**
             * Determine if Archive should be removed as well...
             */
            if (removeAll) {
                FileUtility.delete(
                        new File(tomcatInstance.getCoreFolder().getParent() + File.separator +
                                tomcatInstance.getTomcatArchive()), true);
            }
        }
        return true;
    }

    /**
     * Explode the Tomcat Version Archive for Customization
     *
     * @return boolean indicating if process was successful or not.
     */
    public static boolean explodeTomcatVersionForCustomization(Logger LOGGER,
                                                                  TomcatInstance tomcatInstance) {
        /**
         * Now determine the Latest Release based upon our Short name.
         */
        TomcatArchive tomcatArchive =
                tomcatInstance.getTomcatArchive();
        if (tomcatArchive == null || tomcatArchive.getShortName() == null) {
            ConsoleOutput.out(Color.RED_BOLD,UNABLE_TO_DETERMINE_ARCHIVE_FOR_DOWNLOAD,
                    true, true, true,  tomcatInstance.getTomcatVersion());
            return false;
        }
        /**
         * Check for a Previous Download exploded Head ...
         */
        File headDirectory = new File(tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                tomcatArchive.getHeadName());
        if (headDirectory.exists()) {
            ConsoleOutput.out(Color.RED_BOLD,
                    "  Existing Unzip Directory Head: %s, already exists, unable to continue!",
                    true, true, true,  headDirectory.getAbsolutePath());

            ConsoleOutput.out(Color.RED_BOLD,"  Please Clean up existing Unzip Directory Head before re-running process!",
                    true, true, true);
            return false;
        }
        /**
         * Begin Exploding Archive
         */
        ConsoleOutput.out(Color.GREEN_BOLD, "  Exploding Tomcat Version Archive for Customizations ...", true, true, true);
        try {
            unZipArchive(LOGGER, tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                            tomcatArchive.getName(),
                    tomcatInstance.getDestinationFolder().getAbsolutePath());
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            /**
             * Indicate a Failure has Occurred
             */
            return false;
        }
    }

    /**
     * renameReferencedExplodedArtifact
     * <p>
     * Rename the exploded Artifact to a new Instance Name.
     *
     * @param LOGGER Logger
     * @param tomcatInstance    POJO
     * @return boolean Indicating if this process/method was successful or not.
     */
    public static boolean renameReferencedExplodedArtifact(Logger LOGGER,
                                                              TomcatInstance tomcatInstance) {
        try {
            File explodedArtifactFolder = new File(tomcatInstance.referenceDownloadedArchiveFolder());
            if (explodedArtifactFolder.exists() && explodedArtifactFolder.isDirectory()) {
                String newArtifactFolderName = tomcatInstance.referenceTomcatInstanceFolder();
                File newArtifactFolder = new File(tomcatInstance.getDestinationFolder().getAbsolutePath() + File.separator +
                        newArtifactFolderName);
                if (newArtifactFolder.exists()) {
                    ConsoleOutput.out(Color.RED_BOLD, "  Unable to Rename Original Archive from: %s to %s, as new Folder Already Exists!",
                            true, true, true,
                            explodedArtifactFolder.getName(),
                            newArtifactFolderName);
                    return false;
                }
               ConsoleOutput.out(Color.GREEN_BOLD, "  Renaming Exploded Archive from: %s to %s ",
                       true, true, true,
                       explodedArtifactFolder.getName(),
                            newArtifactFolderName);
                return explodedArtifactFolder.renameTo(newArtifactFolder);
            } else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * unZip Archive
     *
     * @param zipFilePath  input zip file
     * @param outputFolder zip file output folder
     */
    protected static void unZipArchive(Logger LOGGER,
                                       String zipFilePath, String outputFolder) throws IOException {
        /**
         * Create Output Directory, but should already Exist.
         */
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        try(
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = outputFolder + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(LOGGER, zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    protected static void extractFile(Logger LOGGER,
                                      ZipInputStream zipIn, String filePath) throws IOException {
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
        LOGGER.debug("  Extracted zip Entry: {}", filePath);
    }

    /**
     * Validate a Tomcat Archives Check Sum
     *
     * @param filename FileName to check
     * @return boolean Indicates if checkSums are Equal and Valid or not.
     * @throws IOException              Raised if, File IO Issues
     * @throws NoSuchAlgorithmException Raised if, SHA1 Algorithm does not Exist
     */
    public static String getFileCheckSum(String filename)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] dataBytes = new byte[8192];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
        }
        byte[] mdbytes = md.digest();
        //convert the byte to hex format
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        /**
         * Return the Formulated CheckSum
         */
        return sb.toString();
    }

    /**
     * Zip File into Archive
     *
     * @param LOGGER Reference
     * @param zipFilePath       Zip file Destination.
     * @param fileFolder        Folder to be zipped Up ...
     * @param verbose           Indicates verbosity
     */
    public static boolean zipFile(Logger LOGGER,
                                     String zipFilePath, String fileFolder, boolean verbose) {
        /**
         * First get All Nodes with Folder
         */
        List<String> fileList = new ArrayList<>();
        generateFileListForCompression(new File(fileFolder), fileList);
        File sourceFolder = new File(fileFolder);
        byte[] buffer = new byte[8192];
        try {
            try(
            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            ) {
                ConsoleOutput.out(Color.GREEN_BOLD, "  Compressing Instance Generation from: %s to Zip: %s",
                        true, true, true,
                        new File(fileFolder).getAbsolutePath(),
                        zipFilePath);
                /**
                 * Loop Over Files
                 */
                String zfileNameToRemove = sourceFolder.getParent();
                for (String filename : fileList) {
                    int indexToRemove = filename.lastIndexOf(zfileNameToRemove);
                    String zipEntryName = filename.substring(indexToRemove + zfileNameToRemove.length() + 1);
                    ZipEntry ze = new ZipEntry(zipEntryName);
                    zos.putNextEntry(ze);
                    try (
                            FileInputStream in =
                                    new FileInputStream(filename)) {
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    }
                    if (verbose) {
                        ConsoleOutput.out(Color.GREEN_BOLD, "  File Added to Archive: %s",
                                true, true, true, zipEntryName);
                    }
                }
                /**
                 * Close
                 */
                zos.closeEntry();
            }
            return true;
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(),ex);
            return false;
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     *
     * @param node file or directory
     */
    protected static void generateFileListForCompression(File node, List<String> fileList) {
        if (node.isFile()) {
            fileList.add(node.getAbsolutePath());
        }
        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileListForCompression(new File(node, filename), fileList);
            }
        }
    }

    /**
     * Generate YAML Configuration File for Instance Being Generated
     *
     * @param LOGGER Reference
     * @param tomcatInstance    Instance to be Converted to YAML.
     * @return boolean Indicates if Method process was successful or not.
     */
    public static boolean generateYAMLConfigurationForInstance(Logger LOGGER,
                                                               TomcatInstance tomcatInstance) {
        /**
         * Instantiate YAML processor
         */
        Yaml yaml = new Yaml();
        Writer writer = null;
        /**
         * Dump our Tomcat Instance Configuration to a YAML File.
         */
        File destinationYAMLFile = tomcatInstance.referenceSourceYAMLFile();
        ConsoleOutput.out(Color.WHITE_BOLD, "  Creating YAML Configuration File: %s",
                true, true, true,
                destinationYAMLFile.getAbsolutePath());
        try {
            /**
             * We shall persist the YAML in the form of a Map for easy mobility.
             */
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(destinationYAMLFile), "utf-8"));
            yaml.dump(tomcatInstance.map(), writer);
            return true;
        } catch (IOException ex) {
            ConsoleOutput.out(Color.RED_BOLD, "  Unable to Create YAML Configuration File: %s, unable to Continue!",
                    true, true, true,
                    destinationYAMLFile);
            return false;
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (Exception ex) {/*ignore*/}
        }
    }

    /**
     * loadYAMLConfigurationForInstance
     *
     * @param fileName To Read in TomcatInstance YAML Configuration.
     * @return TomcatInstance instantiate from persisted Configuraiton File.
     * @throws IOException Thrown if issues reading YAML File.
     */
    public static TomcatInstance loadYAMLConfigurationForInstance(String fileName) throws IOException {
        Yaml yaml = new Yaml(new Constructor(Map.class));
        InputStream input = new FileInputStream(new File(fileName));
        Map<String, Object> mapFromYaml = (Map) yaml.load(input);
        return new TomcatInstance(mapFromYaml);
    }

    /**
     * Copy over our new YAML Configuration File into the Base of the Tomcat Instance.
     *
     * @param tomcatInstance POJO
     * @throws Exception thrown if issues arise ...
     */
    protected static void copyYAMLConfigurationFileToTomcatInstanceDirectory(TomcatInstance tomcatInstance) throws Exception {
        /**
         * Copy over the Generated YAML File into our Instance Folder for safe keeping.
         */
        File yamlSourceFile = tomcatInstance.referenceSourceYAMLFile();
        File yamlDestinationFile = tomcatInstance.referenceDestinationYAMLFile();
        FileUtils.copyFile(yamlSourceFile, yamlDestinationFile);
    }
    
}
