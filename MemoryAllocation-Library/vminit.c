/**
 * This file contians functions for setting up and tearing down the virutal heap
 * simulator used for our memory allocation system.
 * It also defines the `heapstart` global pointer that points to the very first
 * block header.
 */

#include <assert.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdint.h>
#include "vm.h"
#include "vmlib.h"

#define dbprintf(fmt, args...) fprintf(stderr, "%s: " fmt, __func__, ##args)

/* Global pointer to the start of the heap */
struct block_header *heapstart = NULL;
static void *_vminit_mmap_start = NULL;
static size_t _vminit_mmap_size;

static FILE* file = NULL; 

/**
 * Round up heap size to multiples of page size.
 */
static size_t get_heap_size(size_t requested_size)
{
    size_t pagesize = getpagesize();
    size_t padsize = requested_size % pagesize;
    padsize = (pagesize - padsize) % pagesize;

    return requested_size + padsize;
}

/**
 * Allocate simulated heap memory by mapping zero-bytes from /dev/zero.
 */
static void *create_heap(size_t sz)
{
    void *ptr;

    int fd = open("/dev/zero", O_RDWR);
    if (fd < 0)
        return NULL;

    ptr = mmap(NULL, sz, PROT_READ | PROT_WRITE, MAP_PRIVATE, fd, 0);
    close(fd);
    if (ptr == MAP_FAILED)
        return NULL;

    return ptr;
}

static void *mmap_heap(const char *filename, size_t sz)
{
    void *ptr;

    int fd = open(filename, O_RDWR);
    if (fd < 0)
        return NULL;

    ptr = mmap(NULL, sz, PROT_READ | PROT_WRITE, MAP_PRIVATE, fd, 0);
    close(fd);
    if (ptr == MAP_FAILED)
        return NULL;

    return ptr;
}

/**
 * Set up the heap's internal structure.
 * I.e. initialize the entire heap as one big free block.
 */
static struct block_header *init_heap(void *start, size_t sz)
{
    /**
     * Heap diagram:
     *
     *     |<<<---------------- FREE MEMORY SIZE --------------->>>|
     * +---+---+-----------------------------------------------+---+---+
     * | A | B | ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ | C | D |
     * +---+---+-----------------------------------------------+---+---+
     *     |   |                                               |   |
     *     |   ^                                               |   |
     *     |   ptr: the start of allocatable memory            |   |
     *     |                                                   ^   |
     *     ^                       the footer for the first block  |
     *     the first block header                                  ^
     *                                                   the end mark
     * Metadata blocks (32 bits):
     *   A - skipped for double-word alignment
     *   B - The first and only free block header.
     *   C - The block footer for the only block.
     *   D - The end marker (a busy block header with 0 size)
     *
     * As a result, ptr is the first allocatable address.
     */

    /*
     * The first block header is allocated at offset +4, so that
     * the first actual address returned to user is double-word
     * (8-byte) aligned.
     */
    char *header_addr = (char *)start + sizeof(struct block_header);
    char *end_addr = start + sz;
    char *endmark_addr = end_addr - sizeof(struct block_header);
    char *footer_addr = endmark_addr - sizeof(struct block_footer);

    struct block_header *header = (struct block_header *)header_addr;
    struct block_header *endmark = (struct block_header *)endmark_addr;
    struct block_footer *footer = (struct block_footer *)footer_addr;

    /*
     * Actual free memory is from the first block header to
     * the end mark header. (See diagram above.)
     */
    size_t free_sz = endmark_addr - header_addr;

    header->size_status = free_sz;
    header->size_status |= 1U << 1; /* Set previous block bit as busy */
    header->size_status &= ~1U;     /* Set own status bit as free */

    footer->size = free_sz;

    endmark->size_status = VM_ENDMARK;

    return header;
}

void vmdestroy()
{
    munmap(_vminit_mmap_start, _vminit_mmap_size);
    _vminit_mmap_size = 0;
    _vminit_mmap_start = NULL;

	fclose(file);
	if (remove("swapfile.bin") != 0) {
		perror("Swap file failed to be deleted");
	}
	else {
		file = NULL;
	} 
}

void arch_assert()
{
    /*
     * System architecture sanity check:
     * headers should be one 32-bit word.
     */
    assert(sizeof(struct block_header) == 12);
    assert(sizeof(struct block_footer) == 4);
}

int vminit(size_t sz)
{
    arch_assert();

    if (_vminit_mmap_start) {
        dbprintf("Error: vminit called more than once.\n");
        return -1;
    }
    if (sz <= 0) {
        dbprintf("Error: vminit received invalid size.\n");
        return -1;
    }

    sz = get_heap_size(sz);
    _vminit_mmap_size = sz;
    _vminit_mmap_start = create_heap(sz);
    if (!_vminit_mmap_start) {
        dbprintf("Error: vminit failed to create the heap.\n");
        return -1;
    } else {
        dbprintf("heap created at %p (%u bytes).\n", _vminit_mmap_start, _vminit_mmap_size);
    }
    heapstart = init_heap(_vminit_mmap_start, _vminit_mmap_size);
    dbprintf("heap initialization done.\n");

	file = fopen("swapfile.bin", "wb+");
	if (file == NULL) {
		perror("Swap file failed to initialize");
	}


    return _vminit_mmap_size;
}

int vmload(const char *filename)
{
    arch_assert();

    if (_vminit_mmap_start || _vminit_mmap_size) {
        dbprintf("Error: vminit called more than once.\n");
        return -1;
    }

    struct stat st;
    stat(filename, &st);

    size_t pagesize = getpagesize();
    if (st.st_size % pagesize != 0) {
        dbprintf("Error: dump file size not multiple(s) of page size %d\n", pagesize);
        return -1;
    }

    _vminit_mmap_size = st.st_size;
    _vminit_mmap_start = mmap_heap(filename, _vminit_mmap_size);
    if (!_vminit_mmap_start) {
        dbprintf("Error: failed to map the heap from %s.\n", filename);
        return -1;
    } else {
        dbprintf("heap created at %p (%u bytes).\n", _vminit_mmap_start, _vminit_mmap_size);
    }
    heapstart = (struct block_header *)((char *)_vminit_mmap_start + sizeof(struct block_header));
    dbprintf("heap initialization done.\n");
    return _vminit_mmap_size;
}

void vmdump(const char *filename)
{
    if (!_vminit_mmap_start) {
        dbprintf("Error: no heap mounted.\n");
        return;
    }
    FILE *fp = fopen(filename, "w+");
    fwrite(_vminit_mmap_start, sizeof(char), _vminit_mmap_size, fp);
    fclose(fp);
}


void swap_to_file(struct block_header *curr) {
	*(curr->ptr_conn) = (void*) ((uintptr_t)*(curr->ptr_conn) | 0x1);
	//curr->extra_space = 0;

	fseek(file, 0, SEEK_END);

	if (fwrite(curr, BLKSZ(curr), 1, file) != 1) {
		perror("Failed to swap memory");
		return;
	}


}

void swap_to_heap(void **var, struct block_header *free_block) {
	struct block_header to_swap;
	size_t prev_busy = free_block->size_status & VM_PREVBUSY;

	fseek(file, 0, SEEK_SET);
	while (1) {
		fread(&to_swap, sizeof(struct block_header), 1, file);
		if (feof(file)) {
			perror("Data not found");
			return;	
		}	

		if (to_swap.ptr_conn == var && to_swap.expired == 0) {
			break;
		}

		fseek(file, BLKSZ(&to_swap) - sizeof(struct block_header), SEEK_CUR);

	}
	
	*(to_swap.ptr_conn) = (void*)((char *) free_block + sizeof(struct block_header));

	fseek(file, -sizeof(struct block_header), SEEK_CUR);

	if (fread(free_block, 1, BLKSZ(&to_swap), file) != BLKSZ(&to_swap)) {
		perror("Error moving data to heap");
		return;
	}

	if ((free_block->size_status & VM_PREVBUSY) != prev_busy) {
		if ((free_block->size_status & VM_PREVBUSY) > prev_busy) {
			free_block->size_status -= VM_PREVBUSY;
		  }
		else {
			free_block->size_status += VM_PREVBUSY;
		}
	}

	fseek(file, -BLKSZ(&to_swap), SEEK_CUR);
	to_swap.expired = 1;
	if (fwrite(&to_swap, sizeof(struct block_header), 1, file) != 1) {
		perror("Failed to make block on file expired");
		return;
	}
	to_swap.expired = 0;
}

void swap(void **var) {
	struct block_header to_swap;
	
	fseek(file, 0, SEEK_SET);
	while (1) {
		fread(&to_swap, sizeof(struct block_header), 1, file);
		if (feof(file)) {
			perror("Data not found");
			return;	
		}	

		if (to_swap.ptr_conn == var && to_swap.expired == 0) {
			break;
		}

		fseek(file, BLKSZ(&to_swap) - sizeof(struct block_header), SEEK_CUR);

	}
	
	//vmalloc logic
	size_t size = BLKSZ(&to_swap);
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

	//swapping block(s) to file
	if (best_block == NULL) {
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
		}
		
		else {
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
		}
	}
	
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

	swap_to_heap(var, best_block);
}



void deref_check(void **var) {
	uintptr_t address = (uintptr_t) *var;
	if ((address & 0x1) == 1) {
		swap(var);
	}
}
