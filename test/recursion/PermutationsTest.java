package recursion;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class PermutationsTest {

    /*
     * Testing strategy (both generatePermutations and generatePermutationsIterative)
     *
     * length: 1 (base case), 2, >2 (recursive step)
     * characters: all distinct, some repeated, all the same
     * unique: true, false
     * invalid: empty string, null
     * recursive and iterative versions return the same permutations
     */

    private static Set<String> setOf(String... s) {
        return new HashSet<>(Arrays.asList(s));
    }

    @Test
    public void testSingleCharacter() {
        assertEquals(Arrays.asList("a"), Permutations.generatePermutations("a", false));
        assertEquals(Arrays.asList("a"), Permutations.generatePermutationsIterative("a", true));
    }

    @Test
    public void testTwoCharacters() {
        assertEquals(setOf("ab", "ba"), new HashSet<>(Permutations.generatePermutations("ab", false)));
    }

    @Test
    public void testThreeDistinct() {
        List<String> perms = Permutations.generatePermutations("abc", false);
        assertEquals(6, perms.size());
        assertEquals(setOf("abc", "acb", "bac", "bca", "cab", "cba"), new HashSet<>(perms));
    }

    @Test
    public void testCountIsFactorial() {
        assertEquals(120, Permutations.generatePermutations("abcde", false).size());
        assertEquals(120, Permutations.generatePermutationsIterative("abcde", false).size());
    }

    @Test
    public void testRepeatedKeepsDuplicates() {
        List<String> perms = Permutations.generatePermutations("aab", false);
        assertEquals(6, perms.size());
        assertEquals(setOf("aab", "aba", "baa"), new HashSet<>(perms));
    }

    @Test
    public void testRepeatedUniqueRemovesDuplicates() {
        List<String> perms = Permutations.generatePermutations("aab", true);
        assertEquals(3, perms.size());
        assertEquals(setOf("aab", "aba", "baa"), new HashSet<>(perms));
        assertEquals(6, Permutations.generatePermutations("aabb", true).size());
    }

    @Test
    public void testAllSameUnique() {
        assertEquals(Arrays.asList("aaa"), Permutations.generatePermutations("aaa", true));
        assertEquals(Arrays.asList("aaa"), Permutations.generatePermutationsIterative("aaa", true));
    }

    @Test
    public void testIterativeMatchesRecursive() {
        for (String s : Arrays.asList("abcd", "aabc", "abab")) {
            for (boolean unique : new boolean[] {false, true}) {
                List<String> rec = Permutations.generatePermutations(s, unique);
                List<String> it = Permutations.generatePermutationsIterative(s, unique);
                assertEquals(rec.size(), it.size());
                assertEquals(new HashSet<>(rec), new HashSet<>(it));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyString() {
        Permutations.generatePermutations("", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyStringIterative() {
        Permutations.generatePermutationsIterative("", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull() {
        Permutations.generatePermutations(null, false);
    }
}
