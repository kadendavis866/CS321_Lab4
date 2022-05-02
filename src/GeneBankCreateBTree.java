import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class GeneBankCreateBTree {


    private final int SEQUENCE_LENGTH;
    private final File sourceFile;
    private final BTree bTree;

    public GeneBankCreateBTree(File sourceFile, int sequenceLength, int degree) throws IOException {
        this.sourceFile = sourceFile;
        SEQUENCE_LENGTH = sequenceLength;
        bTree = new BTree(degree, sourceFile.getName() + ".btree.data." + SEQUENCE_LENGTH + "." + degree, BTree.MODE_WRITE);
    }

    public static void main(String[] args) {
        int argsLength = args.length;
        int cache;
        String gbkFile = null;
        int sequenceLength = 0;
        int treeDegree = 0;
        int cacheSize = 0;
        int debugLevel = 0;
        //read in args
        if(argsLength == 4 || argsLength == 5 || argsLength == 6){
            try{
                cache = Integer.parseInt(args[0]);
                if(cache > 1 || cache < 0){ //ensure cache 0 or 1
                    System.out.println("Error: Invalid input for cache selection");
                    throw new IllegalArgumentException();
                }
                treeDegree = Integer.parseInt(args[1]);
                if(treeDegree == 0){ //sets optimal treeDegree if degree is 0
                    treeDegree = (4096 - BTreeNode.METADATA_SIZE + TreeObject.DISK_SIZE) / (2 * (TreeObject.DISK_SIZE + Long.BYTES));
                }
                gbkFile = args[2];
                sequenceLength = Integer.parseInt(args[3]);
                //check for optional arguments
                if(argsLength == 5){
                    if(cache == 1){
                        cache = Integer.parseInt(args[4]);
                    }
                    else{
                        debugLevel = Integer.parseInt(args[4]);
                    }
                }
                if(argsLength == 6){
                    cache = Integer.parseInt(args[4]);
                    debugLevel = Integer.parseInt(args[5]);
                }
                if(debugLevel > 1 || debugLevel < 0){
                    System.out.println("Error: invalid input for debug level");
                    throw new IllegalArgumentException();
                }               
            }
            catch(NumberFormatException e){
                e.printStackTrace();
                System.exit(1);
            }
            catch(IllegalArgumentException e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        else{
            System.out.println("Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
            System.exit(1);
        }

        GeneBankCreateBTree treeCreator;
        try {
            File sourceFile = new File("../BTree/data/" + gbkFile);
            treeCreator = new GeneBankCreateBTree(sourceFile, sequenceLength, treeDegree);
            treeCreator.readFile();
            if(debugLevel == 1){
                treeCreator.createDumpFile();
            }
            System.out.println("Degree = " + treeDegree + "\nNode size: " + BTreeNode.getDiskSize(treeDegree));
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
