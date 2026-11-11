import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

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
        testBookCorpusAnalysis();

        printSummary();
    }

    // ==========================================
    // 1. EQUALS CHECK
    // ==========================================
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

    // ==========================================
    // 2. COMPARE TO CHECK
    // ==========================================
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

    // ==========================================
    // 3. INSERTION & LOOKUP
    // ==========================================
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

    // ==========================================
    // 4. VISUAL LINKED LIST DIAGRAM
    // ==========================================
    private static void testVisualNodeRepresentation() {
        printHeader("4. Visual Linked List State (Before vs. After Sorting)");

        WordCountList list = new WordCountList();
        list.add("apple");
        list.add("banana");
        list.add("apple");
        list.add("cherry");
        list.add("cherry");
        list.add("cherry");

        System.out.println(CYAN + "Original Linked List (Insertion Order):" + RESET);
        printLinkedListNodes(list);

        list.rank(); // Run MergeSort

        System.out.println(CYAN + "After MergeSort rank():" + RESET);
        printLinkedListNodes(list);

        boolean correctOrder = list.size() == 3 
            && list.get(0) != null && list.get(0).getWord().equalsIgnoreCase("cherry")
            && list.get(1) != null && list.get(1).getWord().equalsIgnoreCase("apple")
            && list.get(2) != null && list.get(2).getWord().equalsIgnoreCase("banana");

        assertTest("Visual State rendered and correctly sorted", correctOrder);
    }

    // ==========================================
    // 5. SORTING CORRECTNESS
    // ==========================================
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
                !nWC.getWord().equalsIgnoreCase(mWC.getWord()) || 
                nWC.getCount() != mWC.getCount()) {
                matches = false;
            }
        }

        assertTest("Naive Rank and MergeSort Rank produce identical results", matches);
        assertTest("Top ranked element is 'the' with count 3", 
                   listMerge.size() > 0 && 
                   listMerge.get(0) != null && 
                   "the".equalsIgnoreCase(listMerge.get(0).getWord()) && 
                   listMerge.get(0).getCount() == 3);
    }

    // ==========================================
    // 6. SYNTHETIC BENCHMARK SUITE
    // ==========================================
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

    // ==========================================
    // 7. BOOK CORPUS ANALYSIS & ELAPSED TIMINGS
    // ==========================================
    private static void testBookCorpusAnalysis() {
        printHeader("7. Real Text File Corpus Analysis & Performance Timings");

        WordCountList listNaive = new WordCountList();
        WordCountList listMerge = new WordCountList();
        
        String[] targetFiles = {"Hamlet.txt", "HuckFinn.txt"};
        String loadedFile = null;

        long buildStart = System.currentTimeMillis();
        for (String filename : targetFiles) {
            if (loadTextFile(filename, listMerge)) {
                loadedFile = filename;
                loadTextFile(filename, listNaive);
                break;
            }
        }
        long buildEnd = System.currentTimeMillis();
        long buildTime = buildEnd - buildStart;

        if (loadedFile != null) {
            System.out.println(GREEN + "Successfully loaded text file: " + loadedFile + RESET);
            System.out.println("Total Unique Words Found: " + listMerge.size());
            System.out.printf("Time to Build List (add/find lookups): " + CYAN + "%d ms" + RESET + "\n\n", buildTime);

            System.out.print("Running Naive Sort O(n^2)... ");
            long startNaive = System.currentTimeMillis();
            listNaive.rankNaive();
            long timeNaive = System.currentTimeMillis() - startNaive;
            System.out.println(CYAN + timeNaive + " ms" + RESET);

            System.out.print("Running MergeSort O(n log n)... ");
            long startMerge = System.currentTimeMillis();
            listMerge.rank();
            long timeMerge = System.currentTimeMillis() - startMerge;
            System.out.println(CYAN + timeMerge + " ms" + RESET);

            System.out.println("\n" + BOLD + "--- Execution Timing Summary ---" + RESET);
            System.out.printf("%-30s | %s\n", "Phase", "Elapsed Time");
            System.out.println("-----------------------------------------------");
            System.out.printf("%-30s | %d ms\n", "1. List Construction (add/find)", buildTime);
            System.out.printf("%-30s | %d ms\n", "2. Naive Rank O(n^2)", timeNaive);
            System.out.printf("%-30s | %d ms\n", "3. MergeSort Rank O(n log n)", timeMerge);
            System.out.println("-----------------------------------------------\n");

            printTopWords(listMerge, 15);
            assertTest("Book text parsed, timed, and ranked successfully", listMerge.size() > 0);
        } else {
            System.out.println(YELLOW + "No external file (Hamlet.txt/HuckFinn.txt) found in directory." + RESET);
            System.out.println("Running analysis on sample Shakespeare text passage instead...\n");

            String samplePassage = "To be or not to be that is the question "
                + "Whether 'tis nobler in the mind to suffer "
                + "The slings and arrows of outrageous fortune "
                + "Or to take arms against a sea of troubles "
                + "And by opposing end them Hamlet's choice is clear";

            for (String rawToken : samplePassage.split("\\s+")) {
                String clean = cleanWord(rawToken);
                if (!clean.isEmpty()) {
                    listMerge.add(clean);
                }
            }

            listMerge.rank();
            printTopWords(listMerge, 10);
            assertTest("Sample text passage parsed and ranked successfully", listMerge.size() > 0);
        }
    }

    // ==========================================
    // HELPER & UTILITY METHODS
    // ==========================================

    public static String cleanWord(String rawToken) {
        if (rawToken == null) return "";
        String clean = rawToken.replaceAll("[^a-zA-Z0-9']", "");
        clean = clean.replaceAll("^'+|'+$", "");
        return clean.toLowerCase();
    }

    public static boolean loadTextFile(String filename, WordCountList list) {
        File file = new File(filename);
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String clean = cleanWord(scanner.next());
                if (!clean.isEmpty()) {
                    list.add(clean);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static void printTopWords(WordCountList list, int topN) {
        int displayCount = Math.min(topN, list.size());
        System.out.println(CYAN + "Top " + displayCount + " Most Frequent Words:" + RESET);
        System.out.printf("%-6s | %-16s | %s\n", "Rank", "Word", "Frequency");
        System.out.println("----------------------------------------");

        for (int i = 0; i < displayCount; i++) {
            WordCount wc = list.get(i);
            if (wc != null) {
                System.out.printf("#%-5d | %-16s | %d\n", (i + 1), wc.getWord(), wc.getCount());
            }
        }
        System.out.println();
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
