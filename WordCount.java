/**
 * Holds a word string and its frequency count.
 * Implements Comparable for sorting word frequencies.
 */
public class WordCount implements Comparable<WordCount> {
    private String word;
    private int count;

    public WordCount(String word, int count) {
        // TODO Phase 1: Initialize word and count fields
    }

    public WordCount(String word) {
        // TODO Phase 1: Overloaded constructor initializing count to 1
    }

    public String getWord() {
        // TODO Phase 1: Return word
        return null;
    }

    public int getCount() {
        // TODO Phase 1: Return count
        return 0;
    }

    public void increment() {
        // TODO Phase 1: Increment count by 1
    }

    public void setCount(int count) {
        // TODO Phase 1: Set count field
    }

    /**
     * Checks equality based on word text (case-insensitive).
     * 
     * Implement using the 3-step check outlined in README.md:
     * Step 1: Reference check (this == obj)
     * Step 2: Null and exact class match check (obj == null || getClass() != obj.getClass())
     * Step 3: Cast and compare string values (ignoring case)
     */
    @Override
    public boolean equals(Object obj) {
        // TODO Phase 1: Implement the 3-step equals check
        return false;
    }

    /**
     * Compares WordCount objects for ranking.
     * Primary order: Higher counts come first (descending).
     * Secondary order: Alphabetical (ascending) if counts are equal.
     */
    @Override
    public int compareTo(WordCount other) {
        // TODO Phase 1: Implement compareTo with primary (count) and secondary (word) sorting
        return 0;
    }

    @Override
    public String toString() {
        return "(" + word + ": " + count + ")";
    }
}