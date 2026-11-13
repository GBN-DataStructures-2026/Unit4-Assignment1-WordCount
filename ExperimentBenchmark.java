import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExperimentBenchmark {

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        String filename = (args.length > 0) ? args[0] : "Hamlet.txt";
        List<String> tokens = loadTokensIntoMemory(filename);

        if (tokens.isEmpty()) {
            System.out.println(RED + "Could not load '" + filename + "'. Ensure the file is in your project folder or pass a filename as an argument." + RESET);
            return;
        }

        // =====================================================================
        // REGISTER STUDENT EXPERIMENTS HERE
        // Uncomment each line as you implement the corresponding class!
        // =====================================================================
        Map<String, WordFrequencyList> experiments = new LinkedHashMap<>();
        experiments.put("1. Baseline LinkedList (Head + MergeSort)", new WordCountList());
        // experiments.put("2. LinkedList (Tail Append + MergeSort)", new TailWordCountList());
        // experiments.put("3. LinkedList (Move-To-Front Heuristic)", new MTFWordCountList());
        // experiments.put("4. ArrayList (Linear Search + TimSort)", new ArrayWordCountList());
        // experiments.put("5. Sorted ArrayList (Binary Search + Shift)", new BinarySearchWordList());

        System.out.println(BOLD + CYAN + "==========================================================================================" + RESET);
        System.out.println(BOLD + CYAN + "   DATA STRUCTURE & ALGORITHM EXPERIMENT BENCHMARK (" + filename + ")" + RESET);
        System.out.println(BOLD + CYAN + "==========================================================================================" + RESET);
        System.out.printf("Total cleaned words processed: %,d\n\n", tokens.size());

        System.out.printf("%-44s | %-11s | %-11s | %-11s | %s\n",
                "Implementation", "Build (ms)", "Sort (ms)", "Total (ms)", "Verified");
        System.out.println("--------------------------------------------------------------------------------------------------");

        WordFrequencyList baseline = null;

        for (Map.Entry<String, WordFrequencyList> entry : experiments.entrySet()) {
            String label = entry.getKey();
            WordFrequencyList list = entry.getValue();

            // 1. Time Build Phase (add / find)
            long startBuild = System.nanoTime();
            for (String word : tokens) {
                list.add(word);
            }
            double buildMs = (System.nanoTime() - startBuild) / 1_000_000.0;

            // 2. Time Rank Phase (sorting by frequency)
            long startSort = System.nanoTime();
            list.rank();
            double sortMs = (System.nanoTime() - startSort) / 1_000_000.0;

            double totalMs = buildMs + sortMs;

            // 3. Verify output matches baseline top results
            if (baseline == null) {
                baseline = list;
            }
            boolean valid = matchesBaseline(baseline, list);
            String status = valid ? GREEN + "PASS" + RESET : RED + "MISMATCH" + RESET;

            System.out.printf("%-44s | %9.2f   | %9.2f   | %9.2f   | %s\n",
                    label, buildMs, sortMs, totalMs, status);
        }
        System.out.println("--------------------------------------------------------------------------------------------------\n");
    }

    private static List<String> loadTokensIntoMemory(String filename) {
        List<String> tokens = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return tokens;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String clean = WordCountTester.cleanWord(scanner.next());
                if (!clean.isEmpty()) {
                    tokens.add(clean);
                }
            }
        } catch (FileNotFoundException ignored) {}
        return tokens;
    }

    private static boolean matchesBaseline(WordFrequencyList expected, WordFrequencyList actual) {
        if (expected.size() != actual.size()) return false;
        int checkCount = Math.min(25, expected.size());
        for (int i = 0; i < checkCount; i++) {
            WordCount e = expected.get(i);
            WordCount a = actual.get(i);
            if (e == null || a == null ||
                !e.getWord().equalsIgnoreCase(a.getWord()) ||
                e.getCount() != a.getCount()) {
                return false;
            }
        }
        return true;
    }
}