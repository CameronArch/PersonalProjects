/**
 * This header file defines the *public* interface for our memory allocation
 * system.
 */

#ifndef VMLIB_H
#define VMLIB_H

#include <stddef.h>
#include <vm.h>
/* Initializes an empty virtual heap */
int vminit(size_t sz);
/* Destroy the mmap'd virtual heap */
void vmdestroy();

void *vmalloc(size_t size, void **var);
void vmfree(void *ptr);

/* Print out the heap structure */
void vminfo();
/* Dump the heap into a file */
void vmdump(const char *filename);
/* Load a heap from a dump file */
int vmload(const char *filename);

/*Custom dereference function*/
void deref_check(void **var);


void swap(void **var);


void swap_to_heap(void **var, struct block_header *free_block);


void swap_to_file(struct block_header *curr);

#endif /* VMLIB_H */
