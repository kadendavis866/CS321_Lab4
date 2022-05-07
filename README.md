****************
* Lab 4: Bioinformatics
* CS321 Min Long
* Fri May 6 2022
* Kaden Davis, Drew McMains, Andrew Doering
**************** 

OVERVIEW:

This project uses a BTree to store substrings of a desired length of a DNA sequence. The BTree can then be queried to find the 
frequency of various DNA substrings.

INCLUDED FILES:

 * BTree.java - a BTree that uses disk to read and write files
 * GeneBankCreateBTree.java - driver class to read in and create a BTree from a GeneBank file
 * GeneBankSearch.java - Driver class to query DNA sequences from created BTree file
 * DiskReadWrite.java - logic to read and write nodes onto the disk
 * TreeObject.java - object to be stored in the BTree that contains a DNA substring (represented by a long datatype) and frequency
 * BTreeNode.java - node of the BTree that holds an array of TreeObjects and an array of child pointers
 * DNAConversion.java - logic to convert a DNA sequence to a long and back to a DNA sequence
 * Cache.java - cache to be used for increased efficiency in BTree
 * CacheNode.java - node for the cache
 * README - this file


COMPILING AND RUNNING:

 From the directory containing all source files, compile the
 driver class (and all dependencies) with the command:
 ``` 
 $ javac *.java
  ```

To run GeneBankCreateBTree.java use the following command:
```
java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]
```
This command will create a BTree using the provided GeneBank file, sequence length, and tree degree (an input of 0 will use the 
tree's optimal degree). The program will print its runtime to the console, and, if debug level 1 is selected, it will write all 
DNA substrings and their frequencies to a dump file. The BTree will be stored in a file with the naming convention 
<file name\>.gbk.btree.data.<sequence length\>.<degree\>.

To run GeneBankSearch.java, first use GeneBankCreateBTree.java to build a BTree file, then run the command:
```
java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]
```
This command will search the BTree for the provided queries in the query file with matching sequence length. It prints the 
results of the queries to the console along with the rumtime of the program. If debug level 1 is selected, it will output the 
results to a dump file instead. 

PROGRAM DESIGN AND IMPORTANT CONCEPTS:

This program reads in a DNA sequence, breaks them into substrings of a provided length, and writes the substrings into a BTree 
data structure. 

TESTING:

This program was primarily tested by comparing out output to the example outpus that were provided to us. Once our output matched
the example output exactly, we were confident that our program was working correctly. We also ran the program with every provided 
GeneBank file and a variety of settings, making sure to test edge cases such as k = 1 and k = 31. Without an example output file 
to compare to for these inputs, we just did a quick check of the output to make sure it looks reasonable. There are currently no 
known bugs in the program.

DISCUSSION:

A cache was implemented to help improve efficiency of the BTree. When testing with a cache size of 100 creating the btree took an average of .195 seconds among 10 tests, while creating with a cache size of 500 took an average of .176 seconds with 10 tests. While searching the BTree efficiency is also improved when searching using a larger cache. Searching with cache size 500 provides an average of .376 seconds, while size 100 has an average of .46 seconds.Both tests were run searching a BTree of degree 8 and sequence length 6.
