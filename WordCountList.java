/**
 * Singly-linked list implementation dedicated to tracking WordCount elements.
 */
public class WordCountList {
    // TODO instance variables (one or more list pointers, size)

    public WordCountList() {
        // TODO default constructor.  Initialize instance variables.
    }

    public int size() {
        // TODO Phase 1: Return size of list
        return 0;
    }

    /**
     * Finds a WordCount entry matching the given word.
     * 
     * @param target Word string to locate
     * @return WordCount element if found, or null if not found
     */
    public WordCount find(String target) {
        // TODO Phase 1: Traverse nodes starting at head to find matching word
        return null;
    }

    /**
     * Adds a word to the list.
     * If the word already exists, increments its count.
     * If new, creates a new node prepended to head.
     * 
     * @param word Word string to insert or update
     */
    public void add(String word) {
        // TODO Phase 1: Check if word exists using find().
        // If found -> increment count.
        // If not found -> create new node, link to current head, update head and size.
    }

    /**
     * Returns the WordCount item at a zero-based index.
     */
    public WordCount get(int index) {
        // TODO Phase 1: Traverse to index and return payload. Throw IndexOutOfBoundsException if invalid.
        return null;
    }

    // ==========================================
    // PHASE 2: Naive O(n^2) Sorting
    // ==========================================

    /**
     * Sorts the list in-place using a naive selection sort algorithm on list nodes.
     */
    public void rankNaive() {
        // TODO Phase 2: Implement Selection Sort or Bubble Sort by swapping node info fields
    }

    // ==========================================
    // PHASE 3: MergeSort O(n log n)
    // ==========================================

    /**
     * Public interface to run O(n log n) MergeSort on the list.
     */
    public void rank() {
        // set the front variable  = mergeSortRec(front variable);
        // this starts the process of the recursive merge sort
    }

    /**
     * Recursively splits and merges nodes.
     */
    private LLNode<WordCount> mergeSortRec(LLNode<WordCount> listStart) {
        // TODO Phase 3:
        // 1. Base case: if listStart is null or listStart.getLink() is null, return listStart.
        // 2. Divide list using divideList()
        // 3. Recursively sort first and second halves
        // 4. Return result of merge2() on both halves
        return null;
    }

    /**
     * Splits a linked list in half using slow/fast pointers.
     * 
     * @param p Head of list to split
     * @return Head of second half of list
     */
    private LLNode<WordCount> divideList(LLNode<WordCount> p) {
        // TODO Phase 3:
        // Use slow/fast pointers to find the middle of the list.
        // Sever the link between first half and second half (set slow.link = null).
        // Return head of second half.
        return null;
    }

    /**
     * Merges two sorted linked lists into one sorted list.
     */
    private LLNode<WordCount> merge2(LLNode<WordCount> list1, LLNode<WordCount> list2) {
        // TODO Phase 3:
        // Use a dummy head node to reconstruct a sorted combined chain from list1 and list2.
        // Compare elements using compareTo().
        return null;
    }

@Override
    public String toString() {
        if (size() == 0) return "EMPTY";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            WordCount wc = get(i);
            sb.append(wc != null ? wc.toString() : "null");
            if (i < size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }
}
