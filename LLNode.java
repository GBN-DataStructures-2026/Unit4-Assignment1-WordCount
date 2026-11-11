/**
 * Generic Singly-Linked List Node.
 * 
 * @param <T> The type of data stored inside the node.
 */
public class LLNode<T> {
    private T info;
    private LLNode<T> link;

    public LLNode(T info, LLNode<T> link) {
        this.info = info;
        this.link = link;
    }

    public LLNode(T info) {
        this(info, null);
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public LLNode<T> getLink() {
        return link;
    }

    public void setLink(LLNode<T> link) {
        this.link = link;
    }
}