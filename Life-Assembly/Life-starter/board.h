#ifndef _BOARD_H
#define _BOARD_H
#include "cse30life.h"

// we can change the board type to different sizes to see how this affects the speed.
// DO NOT CHANGE - libcse30life library assumes unsigned char
typedef unsigned char Cell;

typedef struct {
	Cell* buffer_a; // pointer to first life board buffer
	Cell* buffer_b; // pointer to ansecond life board buffer
	size_t num_rows; // number of rows in the life board
	size_t num_cols; // number of cols in the life board
	Cell* current_buffer; // pointer to the current life board's buffer
	Cell* next_buffer; // pointer to the next iteration's board buffer
	unsigned int gen; // generation number
} Board;

extern Board* create_board(const char* filename); // create a board structure
extern void delete_board(Board** board); // delete a board structure
extern void clear_board(Board* board); // clear the boards (current and next)
extern void swap_buffers(Board* board); // swap the board current and next
extern void sim_step(Board* board, unsigned int steps); // simulate one step
extern void set_sim(Board* board, void (*sim)(Board* board, unsigned int steps));
extern size_t get_index(size_t num_cols, size_t row, size_t col); // get index of (row,col) in 1D buffer

#endif // _BOARD_H
