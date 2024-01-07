#ifndef _SIM_H
#define _SIM_H
#include "board.h"
#include <stddef.h>
extern void sim_loop(Board* board, unsigned int steps);
extern void do_row(Cell* dest, Cell* src, size_t row, size_t rows, size_t cols);
#endif
