/**
 * This header file defines the *private* interface for our memory allocation
 * system. E.g. internal data structures and constants. Helper functions.
 */

#ifndef VM_H
#define VM_H

#include <stddef.h>
#include <stdlib.h>

/* Undefine memory allocation functions in stdlib.h */
#define malloc(x) NULL
#define calloc(x) NULL
#define realloc(x) NULL
#define reallocarray(x) NULL

/* The size_status value of the end mark block */
#define VM_ENDMARK 0x1

#define VM_BLKSZMASK 0xFFFFFFFC
#define VM_BUSY 0x00000001
#define VM_PREVBUSY 0x00000002

#define BLKSZ(b) ((b)->size_status & VM_BLKSZMASK)
#define ROUND_UP(N, S) ((((N) + (S)-1) / (S)) * (S))

/**
 * This structure serves as the block header for each heap block.
 * ptr_conn is the associated ptr's address to start of data.
 * expired for removing use on swap file.
 * 12 bytes.
 */
struct block_header {
    /**
     * The size of a block is always a multiple of 8. This means that
     * the last two bits are always zero. We use the last two bits of
     * this variable to store other information.
     *
     * LSB: Least Significant Bit (Last Bit)
     * SLB: Second to Last Bit
     *
     * LSB = 0 <=> This block is free
     * LSB = 1 <=> This block is allocated
     * SLB = 0 <=> Previous block is free
     * SLB = 1 <=> Previous block is allocated
     *
     * Example:
     *   For a busy block with a total size of 24 (i.e. 20B payload + 4B header),
     *   If the previous block is allocated, size_status should be set to 27;
     *   If the previous block is free, then size_status should be set to 25.
     *
     * When used as End Mark:
     *   size_status should be 1 (i.e. zero sized busy block). see VM_ENDMARK macro.
     *
     * When we want to read the size, we should ignore the last two bits.
     */
    size_t size_status;
	void **ptr_conn;
	int expired;
	//size_t extra_space;

};

struct block_footer {
    size_t size;
};

/**
 * Global pointer to the first block in the heap.
 */
extern struct block_header *heapstart;
extern size_t heapSize;




#endif /* VM_H */
