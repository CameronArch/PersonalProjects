#include "vm.h"
#include "vmlib.h"

#include <assert.h>

/**
 * The vmfree() function frees the memory space pointed to by ptr,
 * which must have been returned by a previous call to vmmalloc().
 * Otherwise, or if free(ptr) has already been called before,
 * undefined behavior occurs.
 * If ptr is NULL, no operation is performed.
 * vmfree() asserts that ptr is 8-byte aligned.
 */
void vmfree(void *ptr) {
	if (ptr == NULL) {
		return;
	}

	assert((u_int32_t) ptr % 8 == 0);

	struct block_header *freed = (struct block_header *) ((char *) ptr - sizeof(struct block_header));
	freed->size_status -= VM_BUSY;

	struct block_header *next_block = (struct block_header *) ((char *) freed + BLKSZ(freed));
	if (next_block->size_status != VM_ENDMARK) {
		next_block->size_status -= VM_PREVBUSY;
	}
	struct block_footer *foot_ptr = NULL;
	struct block_footer *prev_footer = NULL;
	struct block_header *prev_block = NULL;

	if ((next_block->size_status & VM_BUSY) == 0 && (freed->size_status & VM_PREVBUSY) == 0) {
		prev_footer = (struct block_footer *) ((char *) freed - sizeof(struct block_footer));
		prev_block = (struct block_header *) ((char *) freed - prev_footer->size);
		foot_ptr = (struct block_footer *) ((char *) next_block + BLKSZ(next_block) - sizeof(struct block_footer));
		
		prev_block->size_status = BLKSZ(next_block) + BLKSZ(freed) + BLKSZ(prev_block) + (prev_block->size_status & VM_PREVBUSY);
		foot_ptr->size = BLKSZ(prev_block);
	}	

	else if ((freed->size_status & VM_PREVBUSY) == 0) {
		prev_footer = (struct block_footer *) ((char *) freed - sizeof(struct block_footer));
		prev_block = (struct block_header *) ((char *) freed - prev_footer->size);
		foot_ptr = (struct block_footer *) ((char *) freed + BLKSZ(freed) - sizeof(struct block_footer));
		
		prev_block->size_status = BLKSZ(freed) + BLKSZ(prev_block) + (prev_block->size_status & VM_PREVBUSY);
		foot_ptr->size = BLKSZ(prev_block);
	}

	else if ((next_block->size_status & VM_BUSY) == 0) {
		foot_ptr = (struct block_footer *) ((char *) next_block + BLKSZ(next_block) - sizeof(struct block_footer));

		freed->size_status = BLKSZ(next_block) + BLKSZ(freed) + (freed->size_status & VM_PREVBUSY);
		foot_ptr->size = BLKSZ(freed);
	}

	else {
		foot_ptr = (struct block_footer *) ((char *) freed + BLKSZ(freed) - sizeof(struct block_footer));

		foot_ptr->size = BLKSZ(freed);
	}
}
