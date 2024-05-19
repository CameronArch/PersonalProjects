#include "cse30life.h"
#include "board.h"

/**
 * create a new board
 *
 * - malloc a Board structure
 * - set the generation to 0
 * - open the file (if it doesn't exist, return a NULL pointer
 * - read the first line which is the number of rows
 * - read the second line which is the number of cols
 * - set the # of rows and # of cols in the boards structure
 * - malloc bufferA and bufferB 
 * - Set currentBuffer and nextBuffer
 * - clear both board buffers
 * - read the file until done.  each row contains a row and a columns separted by
 *   white space
 *     for each line, set the cell in the current buffer
 * - close the file
 * - return the boards pointer if successfull or NULL ptr otherwise
 */
Board* create_board(const char* filename) {
	FILE *file = fopen(filename, "r");
	if (file == NULL) {
		return NULL;
	}
	
	Board *bp = malloc(sizeof(Board));
	if (bp == NULL) {
		return NULL;
	}
	
	bp->gen = 0;

	if ((fscanf(file, "%zu", &bp->num_rows)) != 1) {
		return NULL;
	}
		
       	if ((fscanf(file, "%zu", &bp->num_cols)) != 1) {
		return NULL;
	}
	
	size_t buf_len = get_index(bp->num_cols, bp->num_rows, 0);
		
	bp->buffer_a = calloc(buf_len, sizeof(Cell));
	if (bp->buffer_a == NULL) {
		return NULL;
	}
	
	bp->buffer_b = calloc(buf_len, sizeof(Cell));
	if (bp->buffer_b == NULL) {
		return NULL;
	}
	
	bp->current_buffer = bp->buffer_a; bp->next_buffer = bp->buffer_b;

	size_t row;
	size_t col;
	while (fscanf(file, "%zu %zu", &row, &col) > 0) {

		size_t idxAlive = get_index(bp->num_cols, row, col);

		if (idxAlive >= buf_len) {
			return NULL;
		}

		bp->current_buffer[idxAlive] = 1;
	}
	
	

	fclose(file);	
	return bp;
}

/**
 * delete a board
 */
void delete_board(Board** bpp) {
	free((**bpp).buffer_a);
	free((**bpp).buffer_b);
	free(*bpp);

	*bpp = NULL;
}

/**
 * set all the belems in both buffers to 0
 */
void clear_board(Board* board) {
	size_t buf_len = get_index(board->num_cols, board->num_rows, 0);

	for (size_t i = 0; i < buf_len; i++) {
		(*board).current_buffer[i] = 0;
		(*board).next_buffer[i] = 0;
	}	
}

/**
 * swap the current and next buffers
 */
void swap_buffers(Board* board) {
	Cell *temp = board->current_buffer;
	
	board->current_buffer = board->next_buffer;
	board->next_buffer = temp;	
}

/**
 * get a cell index
 */
size_t get_index(size_t num_cols, size_t row, size_t col) {
	return (row * num_cols + col);
}
