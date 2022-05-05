import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Contains methods for reading and writing BTree data to/from a file on the disk.
 * This class is also responsible for creating a dump file if requested.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class DiskReadWrite {

    private final int METADATA_SIZE;
    private final FileChannel file;
    private int NODE_SIZE;
    private ByteBuffer buffer;
    private long endAddress;
    private int degree;
    private int sequenceLength;


    /**
     * Initializes a new DiskReadWrite object
     * Creates a new file if necessary
     *
     * @param file         file to read from, or create if it doesn't exist
     * @param metadataSize size of metadata, used when loading from a file
     */
    public DiskReadWrite(File file, int metadataSize) throws IOException {
        METADATA_SIZE = metadataSize;
        endAddress = 0;
        degree = 0;
        file.createNewFile();
        RandomAccessFile dataFile = new RandomAccessFile(file, "rw");
        this.file = dataFile.getChannel();
    }

    /**
     * Writes the metadata to the disk.
     * Bytes|     8     |    4     |
     * |rootAddress|treeDegree|
     *
     * @param rootAddress address of root node
     * @param degree      tree degree
     */
    public void writeMetadata(long rootAddress, int degree) throws IOException {
        NODE_SIZE = BTreeNode.getDiskSize(degree);
        buffer = ByteBuffer.allocateDirect(NODE_SIZE);
        file.position(endAddress);

        ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(METADATA_SIZE);

        tmpBuffer.clear();
        tmpBuffer.putLong(rootAddress);
        tmpBuffer.putInt(degree);

        tmpBuffer.flip();
        file.write(tmpBuffer);
        endAddress += METADATA_SIZE;
        this.degree = degree;
    }

    /**
     * Reads the root address from the metadata on the disk
     *
     * @return address of the root object
     */
    public long getRootAddress() throws IOException {
        file.position(0);
        ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(Long.BYTES);
        tmpBuffer.clear();
        file.read(tmpBuffer);
        tmpBuffer.flip();
        return tmpBuffer.getLong();
    }

    /**
     * Reads the degree of the BTree from the metadata on the disk
     *
     * @return the degree of the BTree
     */
    public int getDegree() throws IOException {
        file.position(Long.BYTES);
        ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(Integer.BYTES);
        tmpBuffer.clear();
        file.read(tmpBuffer);
        tmpBuffer.flip();
        degree = tmpBuffer.getInt();
        NODE_SIZE = BTreeNode.getDiskSize(degree);
        buffer = ByteBuffer.allocateDirect(NODE_SIZE);
        return degree;
    }

    /**
     * Writes a node to the disk at the given address
     *
     * @param node    the node to be written on the disk
     * @param address address to write node to
     */
    private void writeNode(BTreeNode node, long address) throws IOException {
        file.position(address);

        buffer.clear();
        buffer.putInt(node.n);

        // node metadata
        byte leaf = 0;
        if (node.leaf) leaf = 1;
        buffer.put(leaf);

        //keys
        for (int i = 0; i < degree - 1; i++) {
            if (i < node.n) {
                buffer.putLong(node.keys[i].substring);
                buffer.putInt(node.keys[i].frequency);
            } else {
                buffer.putLong(0);
                buffer.putInt(0);
            }
        }

        //children
        for (int i = 0; i < degree; i++) {
            if (i < node.n + 1) {
                buffer.putLong(node.children[i]);
            } else {
                buffer.putLong(0);
            }
        }

        buffer.flip();
        file.write(buffer);
    }

    /**
     * Writes a node to the end of the file
     *
     * @param node node to be written to the disk
     */
    public void writeNode(BTreeNode node) throws IOException {
        node.address = endAddress;
        writeNode(node, endAddress);
        endAddress += NODE_SIZE;
    }

    /**
     * Updates a node on the disk. (Writes the node to its previous location)
     *
     * @param node node to be updated on disk
     */
    public void updateNode(BTreeNode node) throws IOException {
        writeNode(node, node.address);
    }

    /**
     * @param address address of node to be retrieved
     * @return the node at the specified address
     */
    public BTreeNode readNode(long address) throws IOException {
        if (address == 0) return null;

        file.position(address);
        buffer.clear();

        file.read(buffer);
        buffer.flip();

        int n = buffer.getInt();
        byte leaf = buffer.get();

        BTreeNode node = new BTreeNode(degree / 2, leaf == 1);
        node.address = address;
        node.n = n;

        int i = 0;
        for (; i < n; i++) {
            node.keys[i] = new TreeObject(buffer.getLong(), buffer.getInt());
        }
        buffer.position(buffer.position() + (degree - 1 - i) * TreeObject.DISK_SIZE);

        for (int j = 0; j <= n; j++) {
            node.children[j] = buffer.getLong();
        }

        return node;
    }

    /**
     * Updates the location of the root node in the file
     *
     * @param rootAddress address of new root
     */
    public void setRoot(Long rootAddress) throws IOException {
        file.position(0);
        ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(Long.BYTES);

        tmpBuffer.clear();
        tmpBuffer.putLong(rootAddress);

        tmpBuffer.flip();
        file.write(tmpBuffer);
    }

    /**
     * Recursively writes each BTreeNode to the dump file using in-order traversal
     *
     * @param bw   BufferedWriter used to write to the file
     * @param node root node
     */
    private void inOrderDump(BufferedWriter bw, BTreeNode node) throws IOException {
        if (node.leaf) {
            for (int i = 0; i < node.n; i++) {
                bw.write(DNAConversion.longToDna(node.keys[i].substring, sequenceLength) + ": " + node.keys[i].frequency + "\n");
            }
        } else {
            for (int i = 0; i < node.n; i++) {
                inOrderDump(bw, readNode(node.children[i]));
                bw.write(DNAConversion.longToDna(node.keys[i].substring, sequenceLength) + ": " + node.keys[i].frequency + "\n");
            }
            inOrderDump(bw, readNode(node.children[node.n]));
        }
    }

    /**
     * Creates a dump file and writes node data using in-order traversal
     *
     * @param filename       name of dump file
     * @param sequenceLength length of DNA sequence
     */
    public void dump(String filename, int sequenceLength) {
        this.sequenceLength = sequenceLength;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            inOrderDump(bw, readNode(getRootAddress()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
