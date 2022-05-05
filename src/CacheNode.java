/**
 * This class represents a node to be used in a double-linked-list
 * implementation of a cache
 *
 * @param <K> type of key used to reference the cache abjects
 * @param <T> type of object to be stored
 * @version Spring2022
 */
@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
public class CacheNode<K, T> {

    private K key;
    private T element;
    private CacheNode<K, T> previous;
    private CacheNode<K, T> next;

    /**
     * Initializes a new CacheNode which stores the provided object
     *
     * @param key     key of new CacheNode
     * @param element the object to be stored in this node
     */
    public CacheNode(K key, T element) {
        this.key = key;
        this.element = element;
    }

    public K getKey() {
        return key;
    }

    /**
     * @return the object stored by this node
     */
    public T getElement() {
        return element;
    }

    /**
     * @return the previous node in the list
     */
    public CacheNode<K, T> previous() {
        return previous;
    }

    /**
     * @param previous the previous node in the list
     */
    public void setPrevious(CacheNode<K, T> previous) {
        this.previous = previous;
    }

    /**
     * @return the next node in the list
     */
    public CacheNode<K, T> next() {
        return next;
    }

    /**
     * @param next the next node in the list
     */
    public void setNext(CacheNode<K, T> next) {
        this.next = next;
    }

}
