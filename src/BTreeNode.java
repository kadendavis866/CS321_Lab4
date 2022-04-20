public class BTreeNode {
    protected TreeObject[] node;
    protected BTreeNode parent;
    protected BTreeNode[] children;
    protected int n;
    protected boolean leaf;
    protected int t;

    public BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.node = new TreeObject[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.n = 0;
    }
}
