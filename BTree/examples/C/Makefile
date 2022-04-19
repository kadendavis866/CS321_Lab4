
CC=gcc
CFLAGS=-Wall -g -gstabs+


all: gentxt genbin filesize subdirs


gentxt: generate_text.o
	$(CC) $(CFLAGS)  -o $@ $<

genbin: generate_binary.o
	$(CC) $(CFLAGS)  -o $@ $<

filesize: filesize.o
	$(CC) $(CFLAGS)  -o $@ $<

subdirs:
	cd disk_binary_search; make
	cd checkpoint; make

clean:
	rm --force *.o a.out genbin gentxt  data* filesize
	cd disk_binary_search; make clean
	cd checkpoint; make clean

