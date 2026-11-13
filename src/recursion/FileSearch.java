package recursion;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Searches a directory and all of its subdirectories for files by name.
 * Usage: java recursion.FileSearch [-i] <directory> <fileName> [<fileName> ...]
 *   -i  ignore case when comparing names
 */
public class FileSearch {

    /**
     * Finds every file called fileName inside dir or any of its subdirectories.
     *
     * @param dir an existing directory
     * @param fileName name to look for, not empty
     * @param caseSensitive false to compare names ignoring case
     * @return matching files in visiting order (entries sorted by name); empty list if none
     * @throws IllegalArgumentException if dir is not an existing directory
     *         or fileName is empty
     */
    public static List<File> search(File dir, String fileName, boolean caseSensitive) {
        if (dir == null || !dir.isDirectory()) {
            throw new IllegalArgumentException("directory not found: " + dir);
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("file name is empty");
        }
        List<File> found = new ArrayList<>();
        searchIn(dir, fileName, caseSensitive, found);
        return found;
    }

    /**
     * Number of times a file called fileName appears in dir and its subdirectories.
     * Same preconditions as search().
     */
    public static int count(File dir, String fileName, boolean caseSensitive) {
        return search(dir, fileName, caseSensitive).size();
    }

    /**
     * Searches for several names in one run.
     *
     * @return map from each name (in the given order) to its matches
     *         same preconditions as search()
     */
    public static Map<String, List<File>> searchAll(File dir, List<String> fileNames,
            boolean caseSensitive) {
        Map<String, List<File>> results = new LinkedHashMap<>();
        for (String name : fileNames) {
            results.put(name, search(dir, name, caseSensitive));
        }
        return results;
    }

    // Adds every match under dir to found.
    private static void searchIn(File dir, String fileName, boolean caseSensitive,
            List<File> found) {
        File[] entries = dir.listFiles();
        if (entries == null) {
            return; // directory could not be read (e.g. no permission)
        }
        Arrays.sort(entries);
        for (File entry : entries) {
            if (entry.isDirectory()) {
                searchIn(entry, fileName, caseSensitive, found); // recursive step
            } else if (sameName(entry.getName(), fileName, caseSensitive)) {
                found.add(entry);
            }
        }
    }

    private static boolean sameName(String a, String b, boolean caseSensitive) {
        return caseSensitive ? a.equals(b) : a.equalsIgnoreCase(b);
    }

    public static void main(String[] args) {
        boolean caseSensitive = !(args.length > 0 && args[0].equals("-i"));
        int first = caseSensitive ? 0 : 1;
        if (args.length - first < 2) {
            System.out.println("Usage: java recursion.FileSearch [-i] <directory> <fileName>"
                    + " [<fileName> ...]");
            System.exit(1);
        }
        File dir = new File(args[first]);
        List<String> names = Arrays.asList(args).subList(first + 1, args.length);
        try {
            System.out.println("Searching " + dir.getAbsolutePath()
                    + (caseSensitive ? " (case-sensitive)" : " (ignoring case)"));
            Map<String, List<File>> results = searchAll(dir, names, caseSensitive);
            for (Map.Entry<String, List<File>> result : results.entrySet()) {
                List<File> files = result.getValue();
                if (files.isEmpty()) {
                    System.out.println(result.getKey() + ": not found");
                    continue;
                }
                System.out.println(result.getKey() + ": found " + files.size() + " time(s)");
                for (File f : files) {
                    System.out.println("    " + f.getAbsolutePath());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (SecurityException e) {
            System.out.println("Error: access denied (" + e.getMessage() + ")");
            System.exit(1);
        }
    }
}
