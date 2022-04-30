import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class GeneBankCreateBTree {


    private final int SEQUENCE_LENGTH;
    private final File sourceFile;
    private final BTree bTree;

    public GeneBankCreateBTree(File sourceFile, int sequenceLength, int treeDegree) throws IOException {
        this.sourceFile = sourceFile;
        SEQUENCE_LENGTH = sequenceLength;
        bTree = new BTree(treeDegree, sourceFile.getName() + ".btree.data." + SEQUENCE_LENGTH + "." + treeDegree);
    }

    public static void main(String[] args) {
        int sequenceLength = 6; // replace with args[3]
        int treeDegree = 26; // replace with args[1], with current structure 26 should be a good size
        GeneBankCreateBTree treeCreator;
        try {
            File sourceFile = new File("../BTree/data/test3.gbk");
            treeCreator = new GeneBankCreateBTree(sourceFile, sequenceLength, treeDegree);
            treeCreator.readFile();
            treeCreator.createDumpFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readFile() throws IOException {
        try (Scanner scanner = new Scanner(sourceFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                //find and parse the next DNA sequence in the file
                if (line.trim().equals("ORIGIN")) {
                    StringBuilder sb = new StringBuilder();
                    line = scanner.nextLine().trim();
                    while (!line.equals("//")) {
                        String[] dnaLine = line.split(" ");
                        for (int i = 1; i < dnaLine.length; i++) { //start at 1 because the first index always contains a number
                            sb.append(dnaLine[i]);
                        }
                        line = scanner.nextLine().trim();
                    }
                    String dnaSequence = sb.toString();

                    //insert the sequence into the BTree
                    int index = 0; //ending index of the window
                    int count = 0;
                    while (count != SEQUENCE_LENGTH && index < dnaSequence.length()) {
                        //find a window to start with that doesn't contain an 'n'
                        if (dnaSequence.charAt(index) == 'n') {
                            count = 0;
                        } else {
                            count++;
                        }
                        index++;
                    }
                    String substring = dnaSequence.substring(index - SEQUENCE_LENGTH, index);
                    long binarySequence = DNAConversion.dnaToLong(substring);
                    bTree.insert(new TreeObject(binarySequence)); //insert the starting window
                    while (index < dnaSequence.length()) {
                        if (dnaSequence.charAt(index) == 'n') { //an 'n' is found
                            while (dnaSequence.charAt(index) == 'n' && index < dnaSequence.length()) {
                                //find next index that is not an 'n'
                                index++;
                            }
                            if (index == dnaSequence.length()) { //dnaSequence ends in an 'n'
                                break;
                            }
                            count = 0;
                            while (count != SEQUENCE_LENGTH && index < dnaSequence.length()) {
                                //again find a valid window to continue with
                                if (dnaSequence.charAt(index) == 'n') {
                                    count = 0;
                                } else {
                                    count++;
                                }
                                index++;
                            }
                            if (index == dnaSequence.length()) { //no valid windows after the last 'n'
                                break;
                            }
                        } else { //not an 'n'; continue as normal
                            index++;
                        }
                        //index will always make a valid window at this point
                        substring = dnaSequence.substring(index - SEQUENCE_LENGTH, index);
                        binarySequence = DNAConversion.dnaToLong(substring);
                        bTree.insert(new TreeObject(binarySequence));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //end
    }

    public void createDumpFile() {
        bTree.dump(sourceFile.getName() + ".btree.dump." + SEQUENCE_LENGTH, SEQUENCE_LENGTH);
    }

}
