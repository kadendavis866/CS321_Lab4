
#include <stdio.h>
#include <stdlib.h>

#include "common.h"
#include "Job.h"
#include "Node.h"
#include "List.h"


const int NUM_TESTS = 6;
/*const int DEBUG = 2;*/
/*const int DEBUG = 1;*/
/*const int DEBUG = 0;*/
const int DEBUG = 1;
char *program;
char *stateFile = ".TestSuite.save";
const int CHECKPOINT_COUNT = 10000;
const int MAX_ERROR_MESSAGE_LENGTH = 2048;

struct parameters {
	int n;
	int count;
	int done;
	unsigned int seed;
	long int randomNum;
	int saveFileLength;
	char *saveFile;
	Boolean restart;
}; 

struct parameters *state;

static void print_usage_and_exit();
static void process_cmdline_arguments(int, char **);
static ListPtr start_from_checkpoint();
static void checkpointTestSuite (struct parameters *, char *);
void runRandomTests(struct parameters *state, ListPtr list);


void printState()
{
	printf("\n");
	printf("%s: state\n", program);
	printf("\t n=%d count=%d done=%d\n", state->n, state->count, state->done);
	printf("\t seed=%d randomNum=%ld\n", state->seed, state->randomNum);
	printf("\t saveFile=%s restart=%d\n", state->saveFile, state->restart);
	printf("\n");
}

static void restoreState(FILE *fin)
{
	fread(&(state->n), sizeof(int), 1, fin);
	fread(&(state->count), sizeof(int), 1, fin);
	fread(&(state->done), sizeof(int), 1, fin);
	fread(&(state->seed), sizeof(unsigned int), 1, fin);
	fread(&(state->randomNum), sizeof(long int), 1, fin);
	fread(&(state->saveFileLength), sizeof(int), 1, fin);
	state->saveFile = (char *)malloc(sizeof(char)*state->saveFileLength);
	fread(state->saveFile, sizeof(char) * state->saveFileLength, 1, fin);
	fread(&(state->restart), sizeof(int), 1, fin);
	if (DEBUG) printState();
	state->restart = TRUE;
}

ListPtr start_from_checkpoint()
{
	char *errmsg;
	FILE *fin;
	ListPtr list = NULL;

	errmsg = (char *) malloc(sizeof(char)*MAX_ERROR_MESSAGE_LENGTH);
	fin = fopen(stateFile, "r");
	if (!stateFile) {
		fprintf(stderr, "%s: No checkpoint file found! (%s)\n", program, stateFile);
        sprintf(errmsg, "%s: %s", program, stateFile);
        perror(errmsg);
		return NULL;
	}
	restoreState(fin);
	list = restoreList(state->saveFile);
	fclose(fin);
	free(errmsg);
	return list;
}

void checkpointTestSuite (struct parameters *state, char *stateFile)
{
	char *errmsg;
	FILE *fout;

	errmsg = (char *) malloc(sizeof(char)*MAX_ERROR_MESSAGE_LENGTH);
	fout = fopen(stateFile, "w");
	if (!stateFile) {
        sprintf(errmsg, "%s: %s", program, stateFile);
        perror(errmsg);
		return;
	}
	fwrite(&(state->n), sizeof(int), 1, fout);
	fwrite(&(state->count), sizeof(int), 1, fout);
	fwrite(&(state->done), sizeof(int), 1, fout);
	fwrite(&(state->seed), sizeof(unsigned int), 1, fout);
	fwrite(&(state->randomNum), sizeof(long int), 1, fout);
	state->saveFileLength = strlen(state->saveFile)+1;
	fwrite(&(state->saveFileLength), sizeof(int), 1, fout);
	fwrite(state->saveFile, sizeof(char) * state->saveFileLength, 1, fout);
	fwrite(&(state->restart), sizeof(int), 1, fout);
	free(errmsg);
	fclose(fout);
}

void runRandomTests(struct parameters *state, ListPtr list)
{
	int i;
	int start;
	int test;
	NodePtr node;
	JobPtr job;
	long int num;

   	srand(state->seed);
	start=0;
	if (state->restart) {
		start = state->done+1;
		srand(state->randomNum);
	}
    for (i=start; i<state->count; i++) {
		num = rand();
        test = num % NUM_TESTS;
		if ((i > 0) && ((i % CHECKPOINT_COUNT) == 0)) {
			fprintf(stderr, "checkpointing list, count = %d\n", i);
			state->done = i-1;
			state->randomNum = num;
			checkpointList(list, state->saveFile);
			checkpointTestSuite(state, stateFile);
		}
        switch (test) {
            case 0:
				if (DEBUG > 1) fprintf(stderr,"addAtFront\n");
                state->n++;
                job = createJob(state->n, "some info");
                node = createNode(job);
                addAtFront(list, node);
                break;
            case 1:
				if (DEBUG > 1) fprintf(stderr,"addAtRear\n");
                state->n++;
                job = createJob(state->n, "some info");
                node = createNode(job);
                addAtRear(list, node);
                break;
            case 2:
				if (DEBUG > 1) fprintf(stderr,"removeFront\n");
                node = removeFront(list);
                break;
            case 3:
				if (DEBUG > 1) fprintf(stderr,"removeRear\n");
                node = removeRear(list);
                break;
            case 4:
				if (DEBUG > 1) fprintf(stderr,"removeNode\n");
                node = removeNode(list, search(list, i));
                break;
            case 5:
				if (DEBUG > 1) fprintf(stderr,"reverseList\n");
                reverseList(list);

            default:
                break;
        }
    }
}

static void process_cmdline_arguments(int argc, char **argv)
{
	program = argv[0];

	state = (struct parameters *) malloc (sizeof(struct parameters));
	state->restart = FALSE;
	state->saveFile = NULL;

	if (argc == 2) {
		if (strncmp(argv[1],"-r",2) == 0) {
			state->restart = TRUE;
		} else {
			print_usage_and_exit();
		}
	} else {
		if (argc < 2 || argc > 5) {
			print_usage_and_exit();
		}
		state->n = atoi(argv[1]);
		state->count = state->n;
		if (argc >= 3) {
			state->count = atoi(argv[2]);
		}
		if (argc >= 4) {
			state->seed = atoi(argv[3]);
		}
		if (argc == 5) {
			state->saveFile = argv[4];
		}
	}
}

static void print_usage_and_exit()
{
	fprintf(stderr, "Usage: %s <list size> [<test size=list size>] [<seed>] [<checkpoint file>] \n",program);
	exit(1);
}

int main(int argc, char **argv)
{	
	int i;

	NodePtr node;
	JobPtr job;
	ListPtr list;

	process_cmdline_arguments(argc, argv);

	if (state->restart) {
			fprintf(stderr, "%s: Attempting to restart from checkpoint.\n", program);
			list = start_from_checkpoint();
	} else {
		list = createList();
		for (i=0; i<state->n; i++)
		{
			job = createJob(i, "args");
			node = createNode(job);
			addAtFront(list, node);
		}
	}

	runRandomTests(state, list);

	if (DEBUG > 0)
		printList(list);

	remove(stateFile);
	remove(state->saveFile);
	exit(0);
}

/* vim: set tabstop=4: */
