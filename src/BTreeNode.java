/**
 * Represents a BTree node which holds an array of TreeObjects and an array of child pointers
 */
@SuppressWarnings("CanBeFinal")
public class BTreeNode {

    public static final int METADATA_SIZE = Integer.BYTES + 1;

    protected long address;
    protected int n;
    protected boolean leaf;
    protected TreeObject[] keys;
    protected long[] children;


    /**
     * Constructor, creates a new node for the BTree
     *
     * @param t    degree of tree, used to allocate memory for arrays
     * @param leaf true if the new node is a leaf node
     */
    public BTreeNode(int t, boolean leaf) {
        this.leaf = leaf;
        this.keys = new TreeObject[2 * t - 1];
        this.children = new long[2 * t];
        this.n = 0;
    }

    /**
     * @param degree tree degree
     * @return the size of the node in bytes
     */
    public static int getDiskSize(int degree) {
        return METADATA_SIZE + TreeObject.DISK_SIZE * (degree * 2 - 1) + Long.BYTES * degree * 2;
    }
}
