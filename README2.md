# Extension Lab: Data Structure & Algorithm Experiments

Now that you have built and verified your singly-linked `WordCountList` and $O(n \log n)$ MergeSort, you can test how different underlying data structures and insertion heuristics affect real-world runtime on large texts like *Hamlet.txt* or *HuckFinn.txt*.

Rather than overwriting your graded `WordCountList.java`, you will use a **Java Interface** so the benchmark harness can run multiple implementations side-by-side and verify that every experiment produces identical word counts.

---

## Step 1: Create the `WordFrequencyList` Interface

Create a new file named `WordFrequencyList.java` in your project directory:

```java
public interface WordFrequencyList {
    void add(String word);
    WordCount find(String target);
    WordCount get(int index);
    int size();
    void rank();
}
```

Next, open your completed `WordCountList.java` and update the class header so it implements this interface:

```java
public class WordCountList implements WordFrequencyList {
```

Because `WordCountList` already has all five methods with matching signatures, no other changes to your original file are needed.

---

## Step 2: Run the Baseline Benchmark

Compile all files and run `ExperimentBenchmark` (defaults to `Hamlet.txt`, or pass another `.txt` file as a command-line argument):

```bash
javac *.java
java ExperimentBenchmark Hamlet.txt
```

`ExperimentBenchmark` loads the cleaned words into memory **once** before starting any timers, then measures two distinct phases for each registered implementation:
1. **Build Time (`add` / `find`):** How fast the data structure ingests ~30,000 words and accumulates duplicate counts.
2. **Sort Time (`rank`):** How fast it reorders the ~4,800 unique `WordCount` entries by frequency (highest count first).

---

## Step 3: Build & Compare Experimental Implementations

Create any of the classes below (each implementing `WordFrequencyList`), uncomment its line in `ExperimentBenchmark.java`, and re-run the benchmark.

### Experiment A: Tail-Appending Linked List (`TailWordCountList`)
* **Idea:** Maintain both a `head` pointer and a `tail` pointer. Append new unique words to the **end** of the list in $O(1)$ time instead of prepending to `head`.
* **Why test it?** Under Zipf's Law, common words like `"the"` and `"and"` appear early in a book. Appending new words to the tail keeps high-frequency words near `head`, dramatically shortening future `find()` traversals.

### Experiment B: Move-To-Front Self-Organizing List (`MTFWordCountList`)
* **Idea:** Keep standard head-insertion, but whenever `find(target)` locates an existing node, **unlink that node from its current position and move it to `head`**.
* **Why test it?** Frequently used words naturally migrate to the front of the list and stay there without needing a `tail` pointer. (This heuristic is widely used in caches and data compression.)

### Experiment C: Array-Backed List + Built-in Sort (`ArrayWordCountList`)
* **Idea:** Store `WordCount` objects inside a standard `java.util.ArrayList<WordCount>`. Use a linear loop for `find()`, `list.add(new WordCount(word))` for new words, and `Collections.sort(list)` for `rank()`.
* **Why test it?** Even though linear `find()` is still $O(n)$, contiguous array memory gives your CPU **cache locality**—avoiding the memory-pointer chasing of linked nodes—while `Collections.sort()` uses **TimSort**.

### Experiment D: Alphabetically Sorted ArrayList + Binary Search (`BinarySearchWordList`)
* **Idea:** Keep an `ArrayList<WordCount>` permanently sorted in **alphabetical order** during the Build phase.
  * When `add(word)` is called, use **Binary Search** ($O(\log n)$) to look up the word.
  * If found, increment its count.
  * If not found, insert `new WordCount(word)` at the exact alphabetical index (`list.add(insertIndex, new WordCount(word))`) so the array stays sorted.
  * When `rank()` is called, sort the list by frequency (`Collections.sort(list)`).

> **⚠️ Important Implementation Note for `BinarySearchWordList`:**  
> Do **not** use `WordCount.compareTo()` during your binary search! `WordCount.compareTo()` orders by **frequency count first**, whereas this array must stay ordered **alphabetically** during `add()` and `find()`. Compare strings directly using `current.getWord().compareToIgnoreCase(target)`.
>
> **Tip:** If you write a manual binary search loop (`low`, `high`, `mid`), when the loop ends without finding the word, `low` is automatically the exact alphabetical index where `list.add(low, new WordCount(word))` should insert it!

---

## Step 4: Analysis & Discussion Questions

1. **Build vs. Sort Trade-Off:** Which phase dominates total runtime across your experiments—building the frequency table or sorting it at the end?
2. **The Sorted Array Paradox:** In `BinarySearchWordList`, inserting a new unique word into the middle of an `ArrayList` requires shifting half the array ($O(n)$ work). Why does `BinarySearchWordList` still outperform linked lists on *Hamlet.txt*? *(Hint: What percentage of the ~30,000 words in Hamlet are repeats vs. brand-new unique words?)*
3. **Worst-Case Stress Test:** What kind of input file would make `BinarySearchWordList` perform much worse than `ArrayWordCountList`?
4. **Looking Ahead:** If you wanted $O(\log n)$ lookup speed **without** paying the $O(n)$ cost of shifting array elements during insertion, what kind of linked data structure could you build? What if you wanted $O(1)$ average lookup speed?