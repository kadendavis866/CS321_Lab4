#!/bin/sh

echo "Running SimpleTest and Restore  with list size = 10000"
SimpleTest 10000 list.bin > list.orig
Restore list.bin > list.restored
diff -w  list.orig list.restored

echo "Running TestSuite and Restore  with list size = 10000, num. operations = 10000"
echo "Using random seed = 123"
TestSuite 10000 10000 123 list.bin > list.orig
Restore list.bin > list.restored
diff -w  list.orig list.restored

echo "Using random seed = 33434"
TestSuite 10000 10000 33434 list.bin > list.orig
Restore list.bin > list.restored
diff -w  list.orig list.restored

echo "Using random seed = 7445422"
TestSuite 10000 10000 7445422 list.bin > list.orig
Restore list.bin > list.restored
diff -w  list.orig list.restored

# clean up

rm -f list.orig list.restored
