#include "cse30life.h"
#include "board.h"
#include "sim.h"
#include "assert.h"
#include <stdbool.h>
#include <strings.h>
extern void asm_doRow(Cell *dest, Cell *src, size_t curRow, size_t nRows, size_t nCols, int *newLife);

typedef struct {
	char* filename;
	bool ascii;
} Args;

/**
 * help printing routine
 * print descrption of all the options as well as interactive commands.
 */
void help_and_exit(const char* name) {
	printf("Usage: %s <file> [ascii]\n", name);
	printf("Interactive commands:\n");
	printf("\td filename: dump the current state to filename\n");
	printf("\tn N: run the simulation N steps without displaying intermediate results\n");
	printf("\ts N: run the simulation N steps displaying intermediate results\n");
	printf("\tq: quit\n");
	exit(EXIT_FAILURE);
}

/**
 * parse the input options
 *
 */
void parse_opts(int argc, char** argv, Args* args) {
	if (argc != 2 && argc != 3) {
		help_and_exit(argv[0]);
	}
	args->filename = argv[1];
	args->ascii = false;
	if (argc == 3) {
		if (strcmp(argv[2], "ascii") == 0) {
			args -> ascii = true;
		} else {
			help_and_exit(argv[0]);
		}
	}
}

/**
 * main - parse options, call functions.
 *
 * - parse the command line options
 * - create the board structure
 * - load the board
 * - plot the initial board
 * - while !done
 *     get a command from the user
 *     simulate for the specified number of cycles
 *     display
 */
int main(int argc, char** argv) {
	Board* boards;
	Args args;

	parse_opts(argc, argv, &args);

	if ((boards = create_board(args.filename)) == NULL) {
		fprintf(stderr, "Failed to process file %s\n", args.filename);
		exit(EXIT_FAILURE);
	}

	printf("simulating life board %zu rows %zu cols\n", boards->num_rows, boards->num_cols);
	fflush(stdout);

	bool done = false;
	int step_size = 1;  // number of steps that sim(..) will simulate <= quanta
	int quanta = 1;    // total number of steps to simulate
	const int ibuf_size = 128;

	while (!done) {
		char ibuf[ibuf_size];

		clear_screen();
		print_board(
			boards->current_buffer,
			boards->num_rows,
			boards->num_cols,
			boards->gen,
			args.ascii
		);

		printf("cmd(d filename, s [#], n [#], q) : ");
		fflush(stdout);

		fgets(ibuf, ibuf_size, stdin);
		fflush(stdout);
		
		if (ibuf[0] == 'q') {
			done = true;
			break;
		} else if (ibuf[0] == 's') {
			sscanf(ibuf, "%*s %d", &quanta);
			step_size = 1;
		} else if (ibuf[0] == 'n') {
			sscanf(ibuf, "%*s %d", &quanta);
			step_size = quanta;
		} else if (ibuf[0] == 'd') {
			char dump[ibuf_size];
			sscanf(ibuf, "%*s %s", dump);
			dump_board(boards->current_buffer, boards->num_rows, boards->num_cols, dump);
			continue;
		}

		/**
		 * main simulation loop
		 *   simulate for quanta steps, display every step_size
		 */
		clear_screen();
		for (int i = 0; i < quanta; i += step_size) {
			start_meas();
			sim_loop(boards, step_size);
			stop_meas();
			printf(
				"speed gen/s = %08.2f  gen = %10d\n",
				(double) step_size / get_secs(),
				boards->gen
			);
			fflush(stdout);
			print_board(
				boards->current_buffer,
				boards->num_rows,
				boards->num_cols,
				boards->gen,
				args.ascii
			);
		}
		clear_screen();
		print_board(
			boards->current_buffer,
			boards->num_rows,
			boards->num_cols,
			boards->gen,
			args.ascii
		);
		fflush(stdout);
	}

	delete_board(&boards);
	assert(boards == NULL);

	return EXIT_SUCCESS;
}

void sim_loop(Board* board, unsigned int steps) {
	unsigned int count = 0;
	int newLife = 0;
	while (count < steps) {
		for (size_t i = 0; i < board->num_rows; i++) {
			asm_doRow(board->next_buffer, board->current_buffer, i, board->num_rows, board->num_cols, &newLife);
		}
		
		swap_buffers(board);
		board->gen++;

		count++;
	}
}
