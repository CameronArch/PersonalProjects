//! functions to support life

#include <assert.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <inttypes.h>
#include <stdbool.h>

/**
 * time funcs
 */
//! start a time measurement
extern void start_meas();

//! stop a time measurement
extern void stop_meas();

//! get elapsed seconds
extern double get_secs();

/**
 *  predefined shapes
 */
//! create a glider
extern void glider(unsigned char* buf, size_t cols, size_t v, size_t h);

//! create a toad
extern void toad(unsigned char* buf, size_t cols, size_t v, size_t h);

//! create an acorn
extern void acorn(unsigned char* buf, size_t cols, size_t v, size_t h);

//! create an oscillator horiz orient
extern void oscillator(unsigned char* buf, size_t cols, size_t v, size_t h);

//! create an oscillator vertical 
extern void oscillator_vert(unsigned char* buf, size_t cols, size_t v, size_t h);

/**
 * board plotting/printing
 */
//! print the board to the screen as ascii
extern void print_board(unsigned char* buf, size_t rows, size_t cols, size_t gen, bool legacy);
extern void clear_screen();

//! dump board to a file
extern void dump_board(unsigned char* buf, size_t rows, size_t cols, const char* dump);
