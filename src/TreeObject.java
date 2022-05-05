/**
 * Represents a tree object to be stored in a BTree
 */
public class TreeObject implements Comparable<TreeObject> {

    public static final int DISK_SIZE = Long.BYTES + Integer.BYTES;

    protected final long substring;
    protected int frequency;

    /**
     * creates a TreeObject with a frequency of 1
     *
     * @param substring DNA sequence (long)
     */
    public TreeObject(long substring) {//Passed in substring
        frequency = 1;
        this.substring = substring;
    }

    /**
     * Creates a tree object
     *
     * @param substring DNA sequence (long)
     * @param frequency frequency of sequence
     */
    public TreeObject(long substring, int frequency) {
        this.substring = substring;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(TreeObject o) {
        long diff = substring - o.substring;
        if (diff > 0) {
            return 1;
        }
        if (diff < 0) {
            return -1;
        }
        return 0;
    }

    /**
     * increments the frequency of this object by 1
     */
    public void incrementFrequency() {
        frequency++;
    }
}
