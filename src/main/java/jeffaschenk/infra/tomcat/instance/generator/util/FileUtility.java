package jeffaschenk.infra.tomcat.instance.generator.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * FileUtility
 *
 * @author schenkje
 */
public class FileUtility {

    /**
     * mkDirectory -- Make a Directory
     *
     * @param baseDir      - Parent Directory Used.
     * @param subdirectory - New Child SubDirectory to be Created.
     * @return boolean indicating Directory created or not.
     * @throws IllegalArgumentException - If bad arguments supplied.
     */
    public static boolean mkDirectory(File baseDir, String subdirectory) {
        if (baseDir == null || !baseDir.exists() || !baseDir.isDirectory()) {
            throw new IllegalArgumentException(String.format("Specified BaseDir: %s",
                    ((baseDir == null) ? "UNKNOWN" : baseDir.getAbsolutePath())));
        }
        if (subdirectory == null || subdirectory.isEmpty()) {
            throw new IllegalArgumentException("Specified SubDirectory is Null or Empty!");
        }
        File subDir = new File(baseDir.getAbsolutePath() + File.separator + subdirectory);
        return subDir.mkdirs();
    }

    /**
     * Remove Directory Contents
     * @param filePath of Directory Folder to be Removed.
     * @return boolean indicating if delete was successful or not...
     */
    public static boolean removeDirectoryAndContents(String filePath, boolean verbose) {
        /**
         * Validate that this File Path Exists, if not ignore the purge quietly...
         */
        File folderToBePurged = new File(filePath);
        if (!folderToBePurged.exists()) {
            return true;
        }
        /**
         * Delete all Files from Directory Structure.
         */
        try (final Stream<Path> pathStream = Files.walk(Paths.get(filePath), FileVisitOption.FOLLOW_LINKS)) {
            pathStream
                    .filter((p) -> !p.toFile().isDirectory())
                    .forEach(p -> purgeFile(p, verbose));
        } catch (final IOException e) {
            ConsoleOutput.out(Color.RED_BOLD, "  - Error Attempting File Deletion operation: %s",
                    true, true, true, e.getMessage());
            ConsoleOutput.out(" ");
            return false;
        }
        /**
         * Delete all Directories from Directory Structure.
         */
        try (final Stream<Path> pathStream = Files.walk(Paths.get(filePath), FileVisitOption.FOLLOW_LINKS)) {
            pathStream
                    .filter((p) -> p.toFile().isDirectory())
                    .forEach(p -> purgeFile(p, verbose));
            return true;
        } catch (final IOException e) {
            ConsoleOutput.out(Color.RED_BOLD, "  - Error Attempting Directory Deletion operation: %s",
                    true, true, true, e.getMessage());
            ConsoleOutput.out(" ");
            System.err.println(e.getMessage());
        }
        return false;
    }

    /**
     * Function Method called from Lambda
     * @param p Path to be Purged.
     */
    private static void purgeFile(Path p, boolean verbose) {
        if (verbose) {
            ConsoleOutput.out(Color.GREEN_BOLD, "   + Purging File: %s",
                    true, true, true, p.toAbsolutePath().toString());
        }
        delete(new File(p.toAbsolutePath().toString()), verbose);
    }
    /**
     * delete helper method to delete file or empty directory.
     * @param file to be deleted.
     */
    public static void delete(File file, boolean verbose) {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                if(file.delete()) {
                    if (verbose) {
                        ConsoleOutput.out(Color.GREEN_BOLD, "    + Directory Deleted: %s",
                                true, true, true, file);
                        ConsoleOutput.out(" ");
                    }
                }
            } else {
                String files[] = file.list();
                for (String fileToDelete : files) {
                    File fileDelete = new File(file, fileToDelete);
                    //recursive delete
                    delete(fileDelete, verbose);
                }
                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    if(file.delete()) {
                        if (verbose) {
                            ConsoleOutput.out(Color.GREEN_BOLD, "    + Directory Deleted: %s",
                                    true, true, true, file);
                            ConsoleOutput.out(" ");
                        }
                    }
                }
            }
        } else {
            //if file, then delete it
            if (file.delete()) {
                if (verbose) {
                    ConsoleOutput.out(Color.GREEN_BOLD, "    + Deleted: %s",
                            true, true, true, file);
                    ConsoleOutput.out(" ");
                }
            }
        }
    }

}
