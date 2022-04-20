
public class BTree {

    private BTreeNode root;
    private int t;

    public BTree(){
        BTreeNode rootNode = allocateNode();
        root = rootNode;
    }

    public BTreeNode allocateNode(){//To be changed
        return new BTreeNode(t, true);
    }

    public void insert(TreeObject k){
        BTreeNode r = root;
        if(r.n == (2 * t - 1)){
            BTreeNode s = allocateNode();
            root = s;
            s.leaf = false;
            s.n = 0;
            s.children[0] = r;
            splitChild(s, 1);
            insertNonfull(s, k);
        }
        else{
            insertNonfull(r, k);
        }
    }

    public void splitChild(BTreeNode nonfull, int fullChild){}

    public void insertNonfull(BTreeNode nonFull, TreeObject key){}
}
