package recursion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * All permutations of a string, recursive and iterative.
 * Usage: java recursion.Permutations [-u] <string>    -u  leave out duplicate permutations
 *        java recursion.Permutations -t <maxLength>   time both versions for lengths 1..max
 */
public class Permutations {

    /**
     * Generates permutations by recursively swapping characters.
     *
     * @param input non-empty string
     * @param unique true to leave out repeated permutations caused by repeated characters
     * @return every ordering of the characters of input: n! entries for length n if
     *         unique is false, each distinct ordering exactly once if unique is true
     * @throws IllegalArgumentException if input is null or empty
     */
    public static List<String> generatePermutations(String input, boolean unique) {
        checkInput(input);
        List<String> result = new ArrayList<>();
        permute(input.toCharArray(), 0, unique, result);
        return result;
    }

    // Fixes each remaining character at position k in turn, then permutes the rest.
    private static void permute(char[] chars, int k, boolean unique, List<String> result) {
        if (k == chars.length - 1) {
            result.add(new String(chars)); // base case: one character left
            return;
        }
        Set<Character> tried = new HashSet<>();
        for (int i = k; i < chars.length; i++) {
            if (unique && !tried.add(chars[i])) {
                continue; // same character already placed at k
            }
            swap(chars, k, i);
            permute(chars, k + 1, unique, result); // recursive step
            swap(chars, k, i);
        }
    }

    /**
     * Same result set as generatePermutations, built without recursion: each next character
     * is inserted at every position of every permutation built so far.
     */
    public static List<String> generatePermutationsIterative(String input, boolean unique) {
        checkInput(input);
        List<String> result = List.of(input.substring(0, 1));
        for (int k = 1; k < input.length(); k++) {
            char c = input.charAt(k);
            List<String> next = new ArrayList<>(result.size() * (k + 1));
            for (String p : result) {
                for (int i = 0; i <= p.length(); i++) {
                    next.add(p.substring(0, i) + c + p.substring(i));
                }
            }
            result = unique ? new ArrayList<>(new LinkedHashSet<>(next)) : next;
        }
        return new ArrayList<>(result);
    }

    private static void checkInput(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input string is empty");
        }
    }

    private static void swap(char[] chars, int i, int j) {
        char t = chars[i];
        chars[i] = chars[j];
        chars[j] = t;
    }

    // Prints run times of both versions for lengths 1..maxLength.
    private static void timeBoth(int maxLength) {
        String letters = "abcdefghijklmnopqrstuvwxyz";
        generatePermutations("abcdefg", false); // warm up the JIT so small n are not skewed
        generatePermutationsIterative("abcdefg", false);
        System.out.printf("%-3s %10s %15s %15s%n", "n", "n!", "recursive (ms)",
                "iterative (ms)");
        for (int n = 1; n <= maxLength; n++) {
            String s = letters.substring(0, n);
            long t0 = System.nanoTime();
            int count = generatePermutations(s, false).size();
            long t1 = System.nanoTime();
            generatePermutationsIterative(s, false);
            long t2 = System.nanoTime();
            System.out.printf("%-3d %10d %15.2f %15.2f%n", n, count, (t1 - t0) / 1e6,
                    (t2 - t1) / 1e6);
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length == 2 && args[0].equals("-t")) {
                timeBoth(Integer.parseInt(args[1]));
                return;
            }
            boolean unique = args.length == 2 && args[0].equals("-u");
            if (args.length != (unique ? 2 : 1)) {
                System.out.println("Usage: java recursion.Permutations [-u] <string>"
                        + " | -t <maxLength>");
                System.exit(1);
            }
            List<String> perms = generatePermutations(args[args.length - 1], unique);
            System.out.println(perms.size() + " permutations"
                    + (unique ? " (duplicates removed)" : "") + ":");
            System.out.println(String.join(" ", perms));
        } catch (NumberFormatException e) {
            System.out.println("Error: max length must be a number");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
