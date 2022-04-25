import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class GeneBankCreateBTree {

    private static final byte A = 0;
    private static final byte T = 1;
    private static final byte C = 2;
    private static final byte G = 3;
    private final int SEQUENCE_LENGTH;
    private final String sourceFile;
    private final BTree bTree;

    public GeneBankCreateBTree(String sourceFile, int sequenceLength, int treeDegree) throws IOException {
        this.sourceFile = sourceFile;
        SEQUENCE_LENGTH = sequenceLength;
        bTree = new BTree(treeDegree, sourceFile + ".btree.data." + SEQUENCE_LENGTH + "." + treeDegree);
    }

    public static void main(String[] args) {
        int sequenceLength = 5; // replace with args[3]
        int treeDegree = 8; // replace with args[1]
        GeneBankCreateBTree treeCreator;
        try {
            treeCreator = new GeneBankCreateBTree("BTree/data/test1.gbk", sequenceLength, treeDegree);
            treeCreator.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // just a line for me to put a breakpoint on for debugging
        System.out.println();
    }

    private long dnaToLong(String sequence) {
        long l = 0;
        for (char c : sequence.toCharArray()) {
            switch (Character.toLowerCase(c)) {
                case 'a':
                    l += A;
                    break;
                case 't':
                    l += T;
                    break;
                case 'c':
                    l += C;
                    break;
                case 'g':
                    l += G;
            }
            l <<= 2;
        }
        l >>= 2;
        return l;
    }

    private String longToDna(long l) {
        char[] chars = new char[SEQUENCE_LENGTH];
        for (int i = SEQUENCE_LENGTH - 1; i >= 0; i--) {
            switch ((byte) (l % 4)) {
                case A:
                    chars[i] = 'A';
                    break;
                case T:
                    chars[i] = 'T';
                    break;
                case C:
                    chars[i] = 'C';
                    break;
                case G:
                    chars[i] = 'G';
            }
            l >>= 2;
        }
        return String.valueOf(chars);
    }

    public void readFile() throws IOException {
        // moved try block here to prevent resource leak on scanner
        try (Scanner scanner = new Scanner(new File(sourceFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                //find and parse the next DNA sequence in the file
                if (line.trim().equals("ORIGIN")) {
                    StringBuilder sb = new StringBuilder();
                    line = scanner.nextLine();
                    while (!line.trim().equals("//")) {
                        String[] dnaLine = line.split(" ");
                        for (int i = 1; i < dnaLine.length; i++) { //start at 1 because the first index always contains a number
                            sb.append(dnaLine[i]);
                        }
                        line = scanner.nextLine();
                    }
                    String dnaSequence = sb.toString();

                    //insert the sequence into the BTree
                    for (int i = 0; i <= dnaSequence.length() - SEQUENCE_LENGTH; i++) {
                        String substring = dnaSequence.substring(i, i + SEQUENCE_LENGTH);
                        if (!substring.contains("n")) {
                            long binarySequence = dnaToLong(substring);
                            bTree.insert(new TreeObject(binarySequence));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
