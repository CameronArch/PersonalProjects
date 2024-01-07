#include "sim.h"

#define C_IMPL
extern void asm_do_row(Cell*, Cell*, size_t, size_t, size_t);

/**
 * gets x mod N (works for negative numbers as well! Use this instead of %)
 */
size_t mod(int x, size_t N) {
	return (x + x / N * N) % N;
}

/**
 * process one row of the board
 */
static void do_row(Cell* dest, Cell* src, size_t row, size_t rows, size_t cols) {
	for (size_t i = 0; i < cols; i++) {
		size_t idx = get_index(cols, row, i);
		unsigned char nAlive = 0;

		unsigned char left = src[get_index(cols, row, mod(cols + i - 1, cols))];

		unsigned char right = src[get_index(cols, row, mod(cols + i + 1, cols))];
			
		unsigned char top = src[get_index(cols, mod(rows + row - 1 , rows), i)];	
	
		unsigned char bottom = src[get_index(cols, mod(rows + row + 1 , rows), i)];
			
		unsigned char tLeft = src[get_index(cols, mod(rows + row - 1, rows), mod(cols + i - 1, cols))];

		unsigned char tRight = src[get_index(cols, mod(rows + row - 1, rows), mod(cols + i + 1, cols))];
		
		unsigned char bLeft = src[get_index(cols, mod(rows + row + 1, rows), mod(cols + i - 1, cols))];
		
		unsigned char bRight = src[get_index(cols, mod(rows + row + 1, rows), mod(cols + i + 1, cols))];

		nAlive = left + right + top + bottom + tLeft + tRight + bLeft + bRight;

		if (src[idx] == 1) {
			if ((nAlive == 2) || (nAlive == 3)) {
				dest[idx] = 1;
			}
			
			else {
				dest[idx] = 0;
			}
		}

		if (src[idx] == 0) {
			if ((nAlive == 3)) {
				dest[idx] = 1;
			}

			else {
				dest[idx] = 0;
			}
		}	
	}
}


/**
 * perform a simulation for "steps" generations
 *
 * for steps
 *   calculate the next board
 *   swap current and next
 */
void sim_loop(Board* board, unsigned int steps) {
	unsigned int count = 0;
	while (count < steps) {
		for (size_t i = 0; i < board->num_rows; i++) {
			do_row(board->next_buffer, board->current_buffer, i, board->num_rows, board->num_cols);
		}
		
		swap_buffers(board);
		board->gen++;

		count++;
	}
}
