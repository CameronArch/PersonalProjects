#include "vm.h"
#include "vmlib.h"


void *vmalloc(size_t size, void **var) {
	if (size == 0) {
		return NULL;
	}
	
	size += sizeof(struct block_header);
	size = ROUND_UP(size,16);

	struct block_header *block = heapstart;
	struct block_header *best_block = NULL;

	while (block->size_status != VM_ENDMARK) {
		if (best_block == NULL && (block->size_status & VM_BUSY) == 0 && BLKSZ(block) >= size) {
			best_block = block;
			if (BLKSZ(block) == size) {
				break;
			}
		}

		else if ((block->size_status & VM_BUSY) == 0 && BLKSZ(block) >= size) {
			if (BLKSZ(block) < BLKSZ(best_block) && BLKSZ(block) >= size) {
				best_block = block;
				if (BLKSZ(block) == size) {
					break;
				}
			}
		}

		block = (struct block_header *) ((char *) block + BLKSZ(block));
	}

	if (best_block == NULL) {
		goto Lswap;
	}
	
	Lswapped:
	
	struct block_header *next_block;

	next_block = (struct block_header *) ((char *) best_block + BLKSZ(best_block));

	if (BLKSZ(best_block) == size) {
		best_block->size_status = best_block->size_status | VM_BUSY;
	
		if (next_block->size_status != VM_ENDMARK) {
			next_block->size_status = next_block->size_status | VM_PREVBUSY;
		}
	}
	
	else {
		size_t extra_space = BLKSZ(best_block) - size;
		struct block_header *split_block = (struct block_header *) ((char *) best_block + size);
		struct block_footer *foot_ptr = NULL;

		best_block->size_status = size + (best_block->size_status & VM_PREVBUSY) + VM_BUSY;
		
		if (next_block->size_status != VM_ENDMARK) {
			if ((next_block->size_status & VM_BUSY) == 0) {
				split_block->size_status = extra_space + BLKSZ(next_block) + VM_PREVBUSY;
				foot_ptr = (struct block_footer *) ((char *) split_block + BLKSZ(split_block) - sizeof(struct block_footer));
				foot_ptr->size = BLKSZ(split_block);
			}

			else {
				split_block->size_status = extra_space + VM_PREVBUSY;
				foot_ptr = (struct block_footer *) ((char *) split_block + BLKSZ(split_block) - sizeof(struct block_footer));
				foot_ptr->size = BLKSZ(split_block);
				
				if ((next_block->size_status & VM_PREVBUSY) == VM_PREVBUSY) {
					next_block->size_status -= VM_PREVBUSY;
				}
			}
		}

		else {
			split_block->size_status = extra_space + VM_PREVBUSY;
			foot_ptr = (struct block_footer *) ((char *) split_block + BLKSZ(split_block) - sizeof(struct block_footer));
			foot_ptr->size = BLKSZ(split_block);
		}	
	}
	
	best_block->ptr_conn = var;
	return (void *) ((char *) best_block + sizeof(struct block_header));

	Lswap:
		block = heapstart;
		while (block->size_status != VM_ENDMARK) {
			if (best_block == NULL && BLKSZ(block) >= size) {
				best_block = block;
				if (BLKSZ(block) == size) {
					break;
				}
			}
			
			else if (best_block != NULL && BLKSZ(block) < BLKSZ(best_block) && BLKSZ(block) >= size) {{
					best_block = block;
					if (BLKSZ(block) == size) {
						break;
					}
				}
			}

			block = (struct block_header *) ((char *) block + BLKSZ(block));
		}

		if (best_block != NULL) {
			swap_to_file(best_block);
			goto Lswapped;
		}
		
		
		//Add swapping when all blocks smaller than size; check free blocks too when checking
		//consequtive blocks
		
		best_block = heapstart;
		size_t free_size = 0;
		struct block_header *following_block = heapstart;
		
		do {
			free_size += BLKSZ(following_block);
			if ((following_block->size_status & VM_BUSY) == 1) {
				swap_to_file(following_block);
			}
			following_block = (struct block_header *)((char *)following_block + BLKSZ(following_block));
		}
	   	while (free_size < size);
		
		best_block->size_status = free_size + VM_PREVBUSY;

		goto Lswapped;
}
