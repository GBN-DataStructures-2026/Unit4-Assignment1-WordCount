import java.util.Random;

public class WordCountTester {

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BOLD = "\u001B[1m";

    private static int testsPassed = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        printBanner("WORD COUNT & LINKED LIST TESTER SUITE");

        testWordCountEquals();
        testWordCountCompareTo();
        testListInsertionAndLookup();
        testVisualNodeRepresentation();
        testSortingCorrectness();
        runBenchmarkSuite();

        printSummary();
    }

    private static void testWordCountEquals() {
        printHeader("1. Testing WordCount.equals(Object obj)");
        
        WordCount w1 = new WordCount("apple", 5);
        WordCount w2 = new WordCount("apple", 10);
        WordCount w3 = new WordCount("banana", 5);
        String nonWordCount = "apple";

        assertTest("Identity Check (w1.equals(w1))", w1 != null && w1.equals(w1));
        assertTest("Null Safety Check (w1.equals(null) == false)", w1 != null && !w1.equals(null));
        assertTest("Type Safety Check (w1.equals(String) == false)", w1 != null && !w1.equals(nonWordCount));
        assertTest("Content Match across counts (apple:5 vs apple:10)", w1 != null && w1.equals(w2));
        assertTest("Mismatch check (apple vs banana)", w1 != null && !w1.equals(w3));
    }

    private static void testWordCountCompareTo() {
        printHeader("2. Testing WordCount.compareTo()");

        WordCount highCount = new WordCount("zebra", 10);
        WordCount lowCount = new WordCount("apple", 2);
        WordCount equalCountA = new WordCount("apple", 5);
        WordCount equalCountB = new WordCount("banana", 5);

        try {
            assertTest("Primary Sort: Higher count comes first", highCount.compareTo(lowCount) < 0);
            assertTest("Primary Sort: Lower count comes second", lowCount.compareTo(highCount) > 0);
            assertTest("Secondary Sort: Tied counts sort alphabetically ('apple' < 'banana')", 
                       equalCountA.compareTo(equalCountB) < 0);
        } catch (Exception e) {
            assertTest("compareTo threw Exception: " + e.getMessage(), false);
        }
    }

    private static void testListInsertionAndLookup() {
        printHeader("3. Testing WordCountList Insertion & Accumulation");

        WordCountList list = new WordCountList();
        list.add("dog");
        list.add("cat");
        list.add("dog");
        list.add("dog");

        assertTest("List size calculation (2 unique words)", list.size() == 2);
        
        WordCount dogWC = list.find("dog");
        assertTest("Frequency accumulation for 'dog' (count == 3)", dogWC != null && dogWC.getCount() == 3);
        
        WordCount catWC = list.find("cat");
        assertTest("Frequency accumulation for 'cat' (count == 1)", catWC != null && catWC.getCount() == 1);
    }

    private static void testVisualNodeRepresentation() {
        printHeader("4. Visual Linked List Node Diagram Rendering");

        WordCountList list = new WordCountList();
        list.add("apple");
        list.add("banana");
        list.add("apple");
        list.add("cherry");
        list.add("cherry");
        list.add("cherry");

        System.out.println(CYAN + "Current State of Linked List Nodes:" + RESET);
        printLinkedListNodes(list);
        assertTest("Visual State Rendered (size == 3)", list.size() == 3);
    }

    private static void testSortingCorrectness() {
        printHeader("5. Testing Sorting Algorithms (Naive vs MergeSort)");

        WordCountList listNaive = new WordCountList();
        WordCountList listMerge = new WordCountList();

        String[] sampleText = {"the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", "the", "fox"};
        for (String word : sampleText) {
            listNaive.add(word);
            listMerge.add(word);
        }

        listNaive.rankNaive();
        listMerge.rank();

        boolean matches = (listNaive.size() == listMerge.size() && listNaive.size() > 0);
        for (int i = 0; matches && i < listNaive.size(); i++) {
            WordCount nWC = listNaive.get(i);
            WordCount mWC = listMerge.get(i);
            if (nWC == null || mWC == null || 
                !nWC.getWord().equals(mWC.getWord()) || 
                nWC.getCount() != mWC.getCount()) {
                matches = false;
            }
        }

        assertTest("Naive Rank and MergeSort Rank produce identical results", matches);
        assertTest("Top ranked element is 'the' with count 3", 
                   listMerge.size() > 0 && 
                   listMerge.get(0) != null && 
                   "the".equals(listMerge.get(0).getWord()) && 
                   listMerge.get(0).getCount() == 3);
    }

    private static void runBenchmarkSuite() {
        printHeader("6. Runtime Performance Benchmark: Naive O(n^2) vs MergeSort O(n log n)");

        int itemCount = 3000;
        System.out.println("Generating random dataset with " + itemCount + " entries...");

        WordCountList naiveList = new WordCountList();
        WordCountList mergeList = new WordCountList();
        Random rand = new Random(42);

        for (int i = 0; i < itemCount; i++) {
            String word = "word_" + rand.nextInt(800);
            naiveList.add(word);
            mergeList.add(word);
        }

        long startNaive = System.currentTimeMillis();
        naiveList.rankNaive();
        long endNaive = System.currentTimeMillis();
        long timeNaive = Math.max(1, endNaive - startNaive);

        long startMerge = System.currentTimeMillis();
        mergeList.rank();
        long endMerge = System.currentTimeMillis();
        long timeMerge = Math.max(1, endMerge - startMerge);

        System.out.println("\nExecution Timings:");
        printBarChart("Naive Rank   O(n^2)", timeNaive, timeNaive);
        printBarChart("MergeSort    O(n log n)", timeMerge, timeNaive);

        assertTest("MergeSort outperforms Naive Sort on large dataset", timeMerge <= timeNaive);
    }

    private static void printLinkedListNodes(WordCountList list) {
        StringBuilder nodesVisual = new StringBuilder();
        nodesVisual.append("  HEAD -> ");

        for (int i = 0; i < list.size(); i++) {
            WordCount wc = list.get(i);
            if (wc != null) {
                nodesVisual.append("[").append(wc.getWord()).append(": ").append(wc.getCount()).append("]");
            } else {
                nodesVisual.append("[null]");
            }

            if (i < list.size() - 1) {
                nodesVisual.append(" -> ");
            } else {
                nodesVisual.append(" -> ").append(YELLOW).append("NULL").append(RESET);
            }
        }
        System.out.println(nodesVisual.toString());
        System.out.println();
    }

    private static void printBarChart(String label, long valueMs, long maxMs) {
        int barLength = (int) (((double) valueMs / maxMs) * 40);
        barLength = Math.max(1, barLength);

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            bar.append("█");
        }

        System.out.printf("%-22s | %-40s | %d ms\n", label, CYAN + bar.toString() + RESET, valueMs);
    }

    private static void assertTest(String description, boolean condition) {
        totalTests++;
        if (condition) {
            testsPassed++;
            System.out.println("  " + GREEN + "[PASS]" + RESET + " " + description);
        } else {
            System.out.println("  " + RED + "[FAIL]" + RESET + " " + description);
        }
    }

    private static void printBanner(String title) {
        System.out.println(BOLD + CYAN + "==========================================================================" + RESET);
        System.out.println(BOLD + CYAN + "   " + title + RESET);
        System.out.println(BOLD + CYAN + "==========================================================================" + RESET);
    }

    private static void printHeader(String title) {
        System.out.println("\n" + BOLD + title + RESET);
        System.out.println("--------------------------------------------------------------------------");
    }

    private static void printSummary() {
        printBanner("FINAL TEST SUMMARY RESULTS");
        double percentage = (totalTests > 0) ? ((double) testsPassed / totalTests) * 100 : 0;
        String color = (testsPassed == totalTests) ? GREEN : RED;

        System.out.printf("  Total Tests Executed: %d\n", totalTests);
        System.out.printf("  Tests Passed:         %d\n", testsPassed);
        System.out.printf("  Tests Failed:         %d\n", totalTests - testsPassed);
        System.out.println("  Pass Score:           " + color + String.format("%.2f%%", percentage) + RESET + "\n");
    }
}