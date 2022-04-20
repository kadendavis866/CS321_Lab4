import java.io.*;
import java.util.*;

public class GeneBankCreateBTree {

    private static final byte A = 0;
    private static final byte T = 1;
    private static final byte C = 2;
    private static final byte G = 3;

    private static int sequenceLength;

    private static long dnaToLong(String sequence){
        long l = 0;
        for(char c : sequence.toCharArray()){
            switch (c) {
                case 'A':
                    l += A;
                    break;
                case 'T':
                    l += T;
                    break;
                case 'C':
                    l += C;
                    break;
                case 'G':
                    l += G;
            }
            l <<= 2;
        }
        l >>= 2;
        return l;
    }

    private static String longToDna(long l){
        char[] chars = new char[sequenceLength];
        for(int i = sequenceLength - 1; i >=0; i--){
            switch ((byte) (l % 4)){
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

    public static void main(String[] args) {
        String sequence = "AGTCGGCTAGAGTCGAGTCGGCTAG";
        sequenceLength = sequence.length();

        long l = dnaToLong(sequence);
        System.out.println(l);
        System.out.println(longToDna(l));
    }

    public void readFile(File file, BTree bTree) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String line;
        String[] dnaLine;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();

            //find and parse the next DNA sequence in the file
            if (line.trim().equals("ORIGIN")) {
                StringBuilder sb = new StringBuilder("");
                line = scanner.nextLine();
                while (!line.trim().equals("//")) {
                    dnaLine = line.split(" ");
                    for (int i = 1; i < dnaLine.length; i++) { //start at 1 because the first index always contains a number
                        if (!dnaLine[i].contains("n")) {
                            sb.append(dnaLine[i]);
                        }
                    }
                }
                String dnaSequence = sb.toString();

                //insert the sequence into the BTree
                long binarySequence;
                for (int i = 0; i <= dnaSequence.length() - sequenceLength; i++) {
                    binarySequence = dnaToLong(dnaSequence.substring(i, i + sequenceLength));
                    bTree.insert(new TreeObject(binarySequence));
                }
            }
        }
    }
}
