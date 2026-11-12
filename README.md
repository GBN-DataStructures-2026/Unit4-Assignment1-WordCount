# Word Frequency & Linked List Sorting Project

## Project Overview
In this project, you will build a word frequency counter backed by a custom singly-linked list. You will implement node traversal, frequency aggregation, equality checking, naive $O(n^2)$ sorting, and an efficient $O(n \log n)$ recursive linked-list MergeSort.

---

## Detailed Implementation Roadmap

### Phase 1: Data Structuring & Frequency Accumulation
* **`WordCount.java`**:
  * Implement constructors and field accessors/mutators.
  * Implement `equals(Object obj)` using the mandatory 3-step check.
  * Implement `compareTo(WordCount other)` with primary and secondary sorting rules.
* **`WordCountList.java`**:
  * `find(String target)`: Traverses nodes to find a matching word (case-insensitive).
  * `add(String word)`: Increments count if found; otherwise prepends a new node to `head`.
  * `size()` & `get(int index)`: Utility accessors.

### Phase 2: Naive $O(n^2)$ Ranking
* **`WordCountList.java`**:
  * `rankNaive()`: Sorts the linked list in-place using Selection Sort or Bubble Sort by swapping node payloads (`WordCount` objects).

### Phase 3: Recursive Linked-List MergeSort $O(n \log n)$
* **`WordCountList.java`**:
  * `divideList(LLNode<WordCount> p)`: Uses slow/fast pointer strategy to split a linked list into two equal halves.
  * `merge2(LLNode<WordCount> list1, LLNode<WordCount> list2)`: Merges two pre-sorted node lists using a dummy head.
  * `mergeSortRec(LLNode<WordCount> listStart)`: Recursive breakdown and re-assembly.
  * `rank()`: Public entry point initiating recursive MergeSort from `head`.

---

## Deep Dive: `equals(Object obj)` vs. `compareTo(WordCount other)`

### Standard `equals(Object obj)` Implementation
To correctly override Java's `equals` method, your implementation must include **three distinct checks**:

```java
@Override
public boolean equals(Object obj) {
    // Part 1: Reference Identity Check
    if (this == obj) {
        return true;
    }

    // Part 2: Null & Class Type Safety Check
    if (obj == null || getClass() != obj.getClass()) {
        return false;
    }

    // Part 3: State Field Comparison
    WordCount other = (WordCount) obj;
    return this.word.equalsIgnoreCase(other.word);
}
```

#### Why All Three Parts Are Mandatory:
1. **Part 1 — Reference Identity (`this == obj`)**:
   * **Purpose:** Checks if both variables reference the exact same object address in memory. If `true`, returns immediately to avoid redundant calculations.
2. **Part 2 — Null & Exact Class Check (`obj == null || getClass() != obj.getClass()`)**:
   * **Purpose:** Prevents `NullPointerException` when calling methods on `obj`. Using `getClass() != obj.getClass()` ensures that `obj` is strictly a `WordCount` instance (preventing `ClassCastException` and enforcing symmetry).
3. **Part 3 — Safe Cast & Value Comparison (`WordCount other = (WordCount) obj;`)**:
   * **Purpose:** Safely casts `obj` to `WordCount` once type safety is confirmed. We compare only the `word` attribute (ignoring `count`) because lookup operations depend on text identity.

### Why `compareTo` Differs From `equals`
* **`equals()` checks Word Identity:** Used during list insertions to check if a word is already in the list.
* **`compareTo()` checks Sorting Order:** Used when ranking to order items by frequency. 

To ensure consistency, `compareTo()` implements **secondary sorting**:
1. **Primary Sort:** Descending order by `count` (highest count first).
2. **Secondary Sort:** Ascending alphabetical order by `word` when counts are equal.

---

## Running the Test Suite

Compile and run `WordCountTester.java` to verify all phases:

```bash
javac LLNode.java WordCount.java WordCountList.java WordCountTester.java
java WordCountTester
```

---

## 💡 Algorithmic Reflection & Discussion Questions

### 1. The Insertion Strategy Paradox (Head vs. Tail)
* **Observation:** When building your `WordCountList` from *Hamlet*, notice the elapsed time for **List Construction**.
* **Question:** In English literature, a few words (like `"the"`, `"and"`, `"to"`) appear thousands of times (Zipf's Law). If your `add()` method inserts new unique words at the **head** (`head = new LLNode<>(..., head)`), where do those high-frequency common words end up as more unique words are added?
* **Analysis:** How does node placement affect the depth of future `find()` traversals for common words? How would appending to the **tail** or using a "Move-To-Front" heuristic change list construction time?

### 2. Runtime Benchmarks ($O(n^2)$ vs. O(n log n))
* Compare the elapsed execution time of `rankNaive()` vs. `rank()` (MergeSort) on *Hamlet.txt* (~4,789 unique words).
* How does the time difference scale when analyzing larger texts like *HuckFinn.txt* or *WarAndPeace.txt*?
