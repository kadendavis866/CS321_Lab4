****************
* BTree Gene-Bank/Lab 04
* CS321 MinLong
* Fri May 6 16:47:12 MDT 2022
* Kaden Davis, Andrew McMains, Andrew Doering
**************** 

OVERVIEW:

INCLUDED FILES:

 * BTree.java - Represents a BTree, uses Disk to read and write files
 * GeneBankCreateBTree.java - Driver class to read in and create a BTree from a GBK file
 * GeneBankSearch.java - Driver class to query DNA sequences from created BTree file
 * DiskReadWrite.java - logic to read and write nodes onto a disk
 * TreeObject.java - Object to represent one DNA substring, includes frequence and substring (represented by a long datatype)
 * BTreeNode.java - Node of a BTree which holds an array of tree objects and an array of child pointers
 * DNAConversion.java - Logic to convert a DNA sequence of a long and from a long back to a DNA sequence
 * Cache.java - Cache to be used for increase efficiency in BTree 
 * CacheNode.java - Node to be used in the Cache
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
Will create a BTree using provided GBK file sequence length, and tree degree (0 will calcualte tree's 
optimal degree). 
The running time of the program will be printed to the console, if debug level 1 is selected, will write all sequences and their
frequencys to dump file. The BTree will be stored in file with the naming convention <file name\>.gbk.btree.data.<sequence length\>.<degree\>.

To run GeneBankSearch.java first run GeneBankCreateBTree to build BTree file,
then run the command:
```
java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]
```
This will search the BTree for the provided queries in query file with matching sequence 
length. This will return the frequency of each of substring to standard out along with the 
running time of the program. If debug level 1 is selected this will output the results to a 
dump file.

PROGRAM DESIGN AND IMPORTANT CONCEPTS:


TESTING:


DISCUSSION:

For the layout of the BTree on disk, the BTree metadata is written at the beginning followed by the BTreeNode data.
The first 8 bits are a 

EXTRA CREDIT:

 If the project had opportunities for extra credit that you attempted,
 be sure to call it out so the grader does not overlook it.


----------------------------------------------------------------------------

All content in a README file is expected to be written in clear English with
proper grammar, spelling, and punctuation. If you are not a strong writer,
be sure to get someone else to help you with proofreading. Consider all project
documentation to be professional writing for your boss and/or potential
customers.
