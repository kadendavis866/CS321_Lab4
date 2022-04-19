
CC=gcc
CFLAGS=-Wall -g -gstabs+
#CFLAGS=-Wall -O 

all: TestBsearch gendata


gendata: gendata.o
	$(CC) $(CFLAGS)  -o $@ $<

TestBsearch: TestBsearch.o ExternalBinarySearch.o Record.o common.h timing.o
	$(CC) $(CFLAGS)  -o $@ TestBsearch.o ExternalBinarySearch.o Record.o timing.o

clean:
	rm --force *.o a.out TestBsearch data.bin gendata
