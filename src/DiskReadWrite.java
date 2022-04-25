import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DiskReadWrite {

    private final int METADATA_SIZE;
    private final int NODE_SIZE;
    private final FileChannel file;
    private final ByteBuffer buffer;
    private long endAddress;


    public DiskReadWrite(File file, int metadataSize, int nodeSize) throws IOException {
        METADATA_SIZE = metadataSize;
        NODE_SIZE = nodeSize;
        buffer = ByteBuffer.allocateDirect(nodeSize);
        endAddress = 0;
        file.createNewFile();
        RandomAccessFile dataFile = new RandomAccessFile(file, "rw");
        this.file = dataFile.getChannel();
    }

    public void writeMetadata(long rootAddress, int degree) throws IOException {
        file.position(endAddress);

        ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(METADATA_SIZE);

        tmpBuffer.clear();
        tmpBuffer.putLong(rootAddress);
        tmpBuffer.putInt(degree);

        tmpBuffer.flip();
        file.write(tmpBuffer);
        endAddress += METADATA_SIZE;
    }

    private void writeNode(BTreeNode node, long address) throws IOException {
        file.position(address);

        buffer.clear();
        buffer.putInt(node.n);

        byte leaf = 0;
        if (node.leaf) leaf = 1;
        buffer.put(leaf);

        //keys
        for (int i = 0; i < node.n; i++) {
            buffer.putLong(node.keys[i].substring);
            buffer.putInt(node.keys[i].frequency);
        }

        //children
        for (int i = 0; i <= node.n; i++) {
            buffer.putLong(node.children[i]);
        }

        buffer.flip();
        file.write(buffer);
    }

    public void writeNode(BTreeNode node) throws IOException {
        node.address = endAddress;
        endAddress += NODE_SIZE;
        writeNode(node, endAddress);
    }

    public void updateNode(BTreeNode node) throws IOException {
        writeNode(node, node.address);
    }

    public BTreeNode readNode(long address) throws IOException {
        if (address == 0) return null;

        file.position(address);
        buffer.clear();

        file.read(buffer);
        buffer.flip();

        int n = buffer.getInt();
        byte leaf = buffer.get();

        BTreeNode node = new BTreeNode(8, leaf == 1);
        node.address = address;
        node.n = n;

        for (int i = 0; i < n; i++) {
            node.keys[i] = new TreeObject(buffer.getLong(), buffer.getInt());
        }

        for (int i = 0; i <= n; i++) {
            node.children[i] = buffer.getLong();
        }

        return node;
    }

    public void setRoot(Long rootAddress) throws IOException {
        file.position(0);
        ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(Long.BYTES);

        tmpBuffer.clear();
        tmpBuffer.putLong(rootAddress);

        tmpBuffer.flip();
        file.write(tmpBuffer);
    }

}
