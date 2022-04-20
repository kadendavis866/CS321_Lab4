

public class BTreeNode {
    private TreeObject[] node;
    private BTreeNode parent;
    private BTreeNode[] children;
    private int n;
    private boolean leaf;
    private int t;

    public BTreeNode(int t, boolean leaf){
        this.t = t;
        this.leaf = leaf;
        this.node = new TreeObject[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.n = 0;
    }

    
}
