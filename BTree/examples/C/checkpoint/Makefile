
CC=gcc
CFLAGS=-Wall -g -gstabs+ -I.
#LFLAGS=-lefence
LFLAGS=


all: SimpleTest Restore TestSuiteChkPt TestSuite


SimpleTest: SimpleTest.o List.o Node.o Job.o
	$(CC) $(CFLAGS) -o $@ SimpleTest.o List.o Node.o Job.o $(LFLAGS)

Restore: Restore.o List.o Node.o Job.o
	$(CC) $(CFLAGS) -o $@ Restore.o List.o Node.o Job.o $(LFLAGS)

TestSuite: TestSuite.o List.o Node.o Job.o
	$(CC) $(CFLAGS) -o $@ TestSuite.o List.o Node.o Job.o $(LFLAGS)

TestSuiteChkPt: TestSuiteChkPt.o List.o Node.o Job.o
	$(CC) $(CFLAGS) -o $@ TestSuiteChkPt.o List.o Node.o Job.o $(LFLAGS)

clean:
	rm --force list.bin *.o TestSuite TestSuiteChkPt SimpleTest Restore list.orig list.restored
