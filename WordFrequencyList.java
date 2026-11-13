public interface WordFrequencyList {
    void add(String word);
    WordCount find(String target);
    WordCount get(int index);
    int size();
    void rank();
}