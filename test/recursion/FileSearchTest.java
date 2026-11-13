package recursion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSearchTest {

    /*
     * Testing strategy
     *
     * search(), count():
     *   tree: empty directory (base case), file at top level,
     *         file nested 2+ levels (recursive step)
     *   matches: 0, 1, >1
     *   case: sensitive, insensitive
     *   invalid: directory missing, path is a file, empty name
     * searchAll():
     *   names: one, several with some not found
     */

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File root;

    // notes.txt, docs/notes.txt, docs/old/Notes.txt, src/Main.java, empty/
    @Before
    public void makeTree() throws IOException {
        root = tmp.getRoot();
        tmp.newFolder("docs", "old");
        tmp.newFolder("src");
        tmp.newFolder("empty");
        tmp.newFile("notes.txt");
        tmp.newFile("docs/notes.txt");
        tmp.newFile("docs/old/Notes.txt");
        tmp.newFile("src/Main.java");
    }

    @Test
    public void testEmptyDirectoryFindsNothing() {
        assertTrue(FileSearch.search(new File(root, "empty"), "notes.txt", true).isEmpty());
    }

    @Test
    public void testNoMatch() {
        assertTrue(FileSearch.search(root, "missing.txt", true).isEmpty());
        assertEquals(0, FileSearch.count(root, "missing.txt", false));
    }

    @Test
    public void testOneMatchNestedOneLevel() {
        List<File> found = FileSearch.search(root, "Main.java", true);
        assertEquals(Arrays.asList(new File(root, "src/Main.java")), found);
    }

    @Test
    public void testSeveralMatchesCaseSensitive() {
        List<File> found = FileSearch.search(root, "notes.txt", true);
        File nested = new File(root, "docs/notes.txt");
        assertEquals(Arrays.asList(nested, new File(root, "notes.txt")), found);
        assertEquals(2, FileSearch.count(root, "notes.txt", true));
    }

    @Test
    public void testIgnoreCaseFindsDeepestFile() {
        assertEquals(3, FileSearch.count(root, "NOTES.TXT", false));
        List<File> found = FileSearch.search(root, "notes.txt", false);
        assertTrue(found.contains(new File(root, "docs/old/Notes.txt")));
    }

    @Test
    public void testSearchAllKeepsOrderAndMissingNames() {
        List<String> names = Arrays.asList("Main.java", "x.txt", "notes.txt");
        Map<String, List<File>> results = FileSearch.searchAll(root, names, true);
        assertEquals(names, List.copyOf(results.keySet()));
        assertEquals(1, results.get("Main.java").size());
        assertTrue(results.get("x.txt").isEmpty());
        assertEquals(2, results.get("notes.txt").size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingDirectory() {
        FileSearch.search(new File(root, "nope"), "notes.txt", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileInsteadOfDirectory() {
        FileSearch.search(new File(root, "notes.txt"), "notes.txt", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() {
        FileSearch.search(root, "", true);
    }
}
