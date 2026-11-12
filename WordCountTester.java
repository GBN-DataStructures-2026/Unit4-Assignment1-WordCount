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

        testWordCountBasicsAndEquals();
        testWordCountCompareTo();
        testListInsertionLookupAndBoundaries();
        testVisualNodeRepresentation();
        testSortingEdgeCases();
        testSortingCorrectness();
        runBenchmarkSuite();
        testBookCorpusAnalysis();

        printSummary();
    }

    // ==========================================
    // 1. CONSTRUCTORS & EQUALS CHECK
    // ==========================================
    private static void testWordCountBasicsAndEquals() {
        printHeader("1. Testing WordCount Constructors, Mutators & equals(Object obj)");

        WordCount defaultCountWord = new WordCount("apple");
        assertTest("1-Arg Constructor initializes count to 1",
                   defaultCountWord != null && "apple".equals(defaultCountWord.getWord()) && defaultCountWord.getCount() == 1);

        if (defaultCountWord != null) {
            defaultCountWord.increment();
            assertTest("increment() increases count from 1 to 2", defaultCountWord.getCount() == 2);
            defaultCountWord.setCount(7);
            assertTest("setCount(7) updates count to 7", defaultCountWord.getCount() == 7);
        }

        WordCount w1 = new WordCount("Apple", 5);
        WordCount w2 = new WordCount("apple", 10);
        WordCount w3 = new WordCount("banana", 5);
        WordCount sameCaseSameWordDiffCount = new WordCount("Apple", 10);
        String nonWordCount = "apple";

        assertTest("Identity Check (w1.equals(w1))", w1 != null && w1.equals(w1));
        assertTest("Null Safety Check (w1.equals(null) == false)", w1 != null && !w1.equals(null));
        assertTest("Type Safety Check (w1.equals(String) == false)", w1 != null && !w1.equals(nonWordCount));

        boolean equalsCaseInsensitive = (w1 != null && w1.equals(w2));
        assertTest("Content Match ignoring case & count (Apple:5 vs apple:10)", equalsCaseInsensitive);

        if (!equalsCaseInsensitive && w1 != null && w1.equals(sameCaseSameWordDiffCount)) {
            System.out.println(RED + "    --> DIAGNOSIS: equals() failed due to CASE-SENSITIVITY!" + RESET);
            System.out.println(RED + "        Use word.equalsIgnoreCase(other.word) instead of word.equals(other.word)." + RESET);
        }

        assertTest("Mismatch check (Apple vs banana)", w1 != null && !w1.equals(w3));
    }

    // ==========================================
    // 2. COMPARE TO CHECK (WITH CASE DIAGNOSTICS)
    // ==========================================
    private static void testWordCountCompareTo() {
        printHeader("2. Testing WordCount.compareTo()");

        WordCount highCount = new WordCount("zebra", 10);
        WordCount lowCount = new WordCount("apple", 2);
        WordCount equalCountA = new WordCount("Apple", 5);
        WordCount equalCountB = new WordCount("banana", 5);
        WordCount equalCountSameWordDiffCase = new WordCount("apple", 5);

        try {
            assertTest("Primary Sort: Higher count comes first", highCount.compareTo(lowCount) < 0);
            assertTest("Primary Sort: Lower count comes second", lowCount.compareTo(highCount) > 0);

            boolean sameWordCaseIgnored = (equalCountA.compareTo(equalCountSameWordDiffCase) == 0);
            assertTest("Case-Insensitivity: Same word diff case ('Apple' vs 'apple') returns 0", sameWordCaseIgnored);
            if (!sameWordCaseIgnored) {
                System.out.println(RED + "    --> DIAGNOSIS: compareTo() is CASE-SENSITIVE for identical words!" + RESET);
                System.out.println(RED + "        Use word.compareToIgnoreCase(other.word) instead of word.compareTo(other.word)." + RESET);
            }

            boolean secondarySortPass = (equalCountA.compareTo(equalCountB) < 0);
            assertTest("Secondary Sort: Tied counts sort alphabetically ('Apple' < 'banana')", secondarySortPass);
            if (!secondarySortPass) {
                System.out.println(RED + "    --> DIAGNOSIS: compareTo() secondary sort failed!" + RESET);
                System.out.println(RED + "        Ensure you break count ties using word.compareToIgnoreCase(other.word)." + RESET);
            }
        } catch (Exception e) {
            assertTest("compareTo threw Exception: " + e.getMessage(), false);
        }
    }

    // ==========================================
    // 3. INSERTION, MISSING ITEMS & BOUNDARIES
    // ==========================================
    private static void testListInsertionLookupAndBoundaries() {
        printHeader("3. Testing WordCountList Insertion, Missing Items & Boundaries");

        // Empty list checks
        WordCountList emptyList = new WordCountList();
        assertTest("Empty list starts with size 0", emptyList.size() == 0);

        boolean emptyFindSafe = false;
        try {
            emptyFindSafe = (emptyList.find("ghost") == null);
        } catch (Exception e) {
            System.out.println(RED + "    --> ERROR: find() on empty list threw " + e + RESET);
        }
        assertTest("find() on empty list safely returns null", emptyFindSafe);

        // Insertion & case-insensitive frequency accumulation
        WordCountList list = new WordCountList();
        list.add("dog");
        list.add("cat");
        list.add("Dog");
        list.add("DOG");

        assertTest("List size calculation (2 unique words)", list.size() == 2);

        WordCount dogWC = list.find("dog");
        assertTest("Frequency accumulation ignoring case for 'dog' (count == 3)", dogWC != null && dogWC.getCount() == 3);

        WordCount catWC = list.find("cat");
        assertTest("Frequency accumulation for 'cat' (count == 1)", catWC != null && catWC.getCount() == 1);

        // Missing item lookup on populated list (catches while(head != null) loops)
        boolean missingFindSafe = false;
        try {
            missingFindSafe = (list.find("zebra") == null);
        } catch (Exception e) {
            System.out.println(RED + "    --> ERROR: find() for missing word threw " + e + " (check loop condition: while(currNode != null))" + RESET);
        }
        assertTest("find() for missing word on populated list safely returns null", missingFindSafe);

        // Boundary checks for get(index)
        assertTest("get(-1) throws IndexOutOfBoundsException", throwsIndexOutOfBounds(list, -1));
        assertTest("get(size) throws IndexOutOfBoundsException", throwsIndexOutOfBounds(list, list.size()));
    }

    // ==========================================
    // 4. VISUAL LINKED LIST DIAGRAM
    // ==========================================
    private static void testVisualNodeRepresentation() {
        printHeader("4. Visual Linked List State (Before vs. After Sorting)");

        WordCountList list = new WordCountList();
        list.add("apple");
        list.add("banana");
        list.add("Apple");
        list.add("cherry");
        list.add("Cherry");
        list.add("CHERRY");

        System.out.println(CYAN + "Original Linked List (Insertion Order):" + RESET);
        printLinkedListNodes(list);

        list.rank();

        System.out.println(CYAN + "After MergeSort rank():" + RESET);
        printLinkedListNodes(list);

        boolean correctOrder = list.size() == 3
            && list.get(0) != null && list.get(0).getWord().equalsIgnoreCase("cherry")
            && list.get(1) != null && list.get(1).getWord().equalsIgnoreCase("apple")
            && list.get(2) != null && list.get(2).getWord().equalsIgnoreCase("banana");

        assertTest("Visual State rendered and correctly sorted", correctOrder);
    }

    // ==========================================
    // 5. SORTING EDGE CASES (SIZE 0 & SIZE 1)
    // ==========================================
    private static void testSortingEdgeCases() {
        printHeader("5. Testing Sorting Edge Cases (Empty & Single-Node Lists)");

        // Naive Sort Edge Cases
        boolean naiveEdgeSafe = false;
        try {
            WordCountList emptyNaive = new WordCountList();
            emptyNaive.rankNaive();

            WordCountList singleNaive = new WordCountList();
            singleNaive.add("solo");
            singleNaive.rankNaive();

            naiveEdgeSafe = (emptyNaive.size() == 0 && singleNaive.size() == 1
                && "solo".equalsIgnoreCase(singleNaive.get(0).getWord()));
        } catch (Exception e) {
            System.out.println(RED + "    --> ERROR: rankNaive() crashed on size 0 or 1: " + e + RESET);
        }
        assertTest("Phase 2 (rankNaive): Safely handles empty (0) and single-node (1) lists", naiveEdgeSafe);

        // MergeSort Edge Cases
        boolean mergeEdgeSafe = false;
        try {
            WordCountList emptyMerge = new WordCountList();
            emptyMerge.rank();

            WordCountList singleMerge = new WordCountList();
            singleMerge.add("solo");
            singleMerge.rank();

            mergeEdgeSafe = (emptyMerge.size() == 0 && singleMerge.size() == 1
                && "solo".equalsIgnoreCase(singleMerge.get(0).getWord()));
        } catch (Exception e) {
            System.out.println(RED + "    --> ERROR: rank() crashed on size 0 or 1: " + e + RESET);
        }
        assertTest("Phase 3 (rank / MergeSort): Safely handles empty (0) and single-node (1) lists", mergeEdgeSafe);
    }

    // ==========================================
    // 6. SORTING CORRECTNESS & INTEGRITY CHECKS
    // ==========================================
    private static void testSortingCorrectness() {
        printHeader("6. Testing Sorting Algorithms & Post-Sort Structural Integrity");

        WordCountList listNaive = new WordCountList();
        WordCountList listMerge = new WordCountList();

        String[] sampleText = {"the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", "the", "fox"};
        for (String word : sampleText) {
            listNaive.add(word);
            listMerge.add(word);
        }

        // --- PHASE 2: Independent Naive Sort Validation & Output ---
        boolean naiveSorted = false;
        boolean naiveIntegrity = false;
        try {
            listNaive.rankNaive();
            naiveIntegrity = verifyStructuralIntegrity(listNaive, "rankNaive");
            naiveSorted = naiveIntegrity
                && isListSorted(listNaive)
                && listNaive.get(0) != null
                && "the".equalsIgnoreCase(listNaive.get(0).getWord())
                && listNaive.get(0).getCount() == 3;
        } catch (Exception e) {
            System.out.println(RED + "  [ERROR] rankNaive() threw an exception: " + e + RESET);
        }

        System.out.println(CYAN + "Sorted Output from Phase 2 rankNaive():" + RESET);
        printLinkedListNodes(listNaive);

        assertTest("Phase 2 (rankNaive): Post-sort pointer chain integrity intact", naiveIntegrity);
        assertTest("Phase 2 (rankNaive): Independently sorts list in valid rank order", naiveSorted);

        // --- PHASE 3: MergeSort Validation, Integrity & Equivalence ---
        boolean mergeSorted = false;
        boolean mergeIntegrity = false;
        boolean matches = false;
        int maxLen = Math.max(listNaive.size(), listMerge.size());

        try {
            listMerge.rank();
            mergeIntegrity = verifyStructuralIntegrity(listMerge, "MergeSort rank");
            mergeSorted = mergeIntegrity && isListSorted(listMerge);
            matches = naiveSorted && mergeSorted && (listNaive.size() == listMerge.size());

            for (int i = 0; matches && i < maxLen; i++) {
                WordCount nWC = listNaive.get(i);
                WordCount mWC = listMerge.get(i);
                if (nWC == null || mWC == null ||
                    !nWC.getWord().equalsIgnoreCase(mWC.getWord()) ||
                    nWC.getCount() != mWC.getCount()) {
                    matches = false;
                }
            }
        } catch (NullPointerException e) {
            System.out.println(YELLOW + "    --> Note: rank() returned a null list (Phase 3 likely not implemented yet)." + RESET);
        } catch (Exception e) {
            System.out.println(RED + "  [ERROR] rank() threw an exception: " + e + RESET);
        }

        assertTest("Phase 3 (rank / MergeSort): Post-sort pointer chain integrity intact", mergeIntegrity);

        if (naiveSorted && listMerge.head != null && !matches) {
            System.out.println(RED + "\n  [MISMATCH DETECTED] Naive Rank and MergeSort Rank output differs:" + RESET + "\n");
            System.out.printf("  %-6s | %-20s | %-20s | %s\n", "Index", "Naive Rank O(n^2)", "MergeSort Rank O(n log n)", "Status");
            System.out.println("  -------------------------------------------------------------------------");

            for (int i = 0; i < maxLen; i++) {
                WordCount nWC = null;
                WordCount mWC = null;
                try { nWC = listNaive.get(i); } catch (Exception ignored) {}
                try { mWC = listMerge.get(i); } catch (Exception ignored) {}

                String nStr = (nWC != null) ? nWC.toString() : "null";
                String mStr = (mWC != null) ? mWC.toString() : "null";

                boolean itemMatch = (nWC != null && mWC != null &&
                                     nWC.getWord().equalsIgnoreCase(mWC.getWord()) &&
                                     nWC.getCount() == mWC.getCount());

                String status = itemMatch ? GREEN + "MATCH" + RESET : RED + "MISMATCH" + RESET;
                System.out.printf("  #%-5d | %-20s | %-20s | %s\n", i, nStr, mStr, status);
            }
            System.out.println();
        }

        assertTest("Phase 3 (rank / MergeSort): Produces identical sorted order to Naive Rank", matches);
    }

    // ==========================================
    // 7. SYNTHETIC BENCHMARK SUITE
    // ==========================================
    private static void runBenchmarkSuite() {
        printHeader("7. Runtime Performance Benchmark: Naive O(n^2) vs MergeSort O(n log n)");

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
    // 8. BOOK CORPUS ANALYSIS & ELAPSED TIMINGS
    // ==========================================
    private static void testBookCorpusAnalysis() {
        printHeader("8. Real Text File Corpus Analysis & Performance Timings");

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
            System.out.println("Running analysis on sample corpus passage instead...\n");

            String samplePassage = "Data structures organize data so algorithms can process data fast "
                + "Linked lists link nodes together while sorting algorithms rank data "
                + "MergeSort divides lists in half and merges sorted halves cleanly";

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

    private static boolean throwsIndexOutOfBounds(WordCountList list, int badIndex) {
        try {
            list.get(badIndex);
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        } catch (Exception e) {
            System.out.println(RED + "    --> Expected IndexOutOfBoundsException for get(" + badIndex + "), but got: " + e + RESET);
            return false;
        }
    }

    private static boolean isListSorted(WordCountList list) {
        if (list == null || list.size() == 0) return false;
        for (int i = 0; i < list.size() - 1; i++) {
            WordCount curr = list.get(i);
            WordCount next = list.get(i + 1);
            if (curr == null || next == null || curr.compareTo(next) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Walks the head pointer chain to verify that node count matches size()
     * and that no nodes were dropped or linked into a cycle.
     */
    private static boolean verifyStructuralIntegrity(WordCountList list, String label) {
        if (list == null) return false;
        int expectedSize = list.size();
        int actualCount = 0;
        LLNode<WordCount> curr = list.head;

        while (curr != null && actualCount <= expectedSize) {
            actualCount++;
            curr = curr.getLink();
        }

        if (curr != null) {
            System.out.println(RED + "    --> INTEGRITY ERROR (" + label + "): Cycle detected or chain exceeds size (" + expectedSize + ")!" + RESET);
            return false;
        }
        if (actualCount != expectedSize) {
            System.out.println(RED + "    --> INTEGRITY ERROR (" + label + "): Expected " + expectedSize + " nodes from head, but found " + actualCount + " (nodes dropped)!" + RESET);
            return false;
        }
        return true;
    }

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

        if (list.head == null) {
            nodesVisual.append(YELLOW).append("NULL").append(RESET);
        } else {
            LLNode<WordCount> curr = list.head;
            int safetyLimit = Math.max(list.size() + 2, 15);
            int count = 0;

            while (curr != null && count < safetyLimit) {
                WordCount wc = curr.getInfo();
                if (wc != null) {
                    nodesVisual.append("[").append(wc.getWord()).append(": ").append(wc.getCount()).append("]");
                } else {
                    nodesVisual.append("[null]");
                }

                curr = curr.getLink();
                count++;

                if (curr != null) {
                    nodesVisual.append(" -> ");
                } else {
                    nodesVisual.append(" -> ").append(YELLOW).append("NULL").append(RESET);
                }
            }
            if (curr != null) {
                nodesVisual.append(RED).append(" ... [CYCLE / TRUNCATED]").append(RESET);
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
