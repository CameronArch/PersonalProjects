#include <stdio.h>
#include <stdint.h>
#include "vmlib.h"
#include <assert.h>
void custom_heap1()
{
    vminit(4096);
    char *ptr = vmalloc(4048, (void**)&ptr);
	
	deref_check((void**)&ptr);
	ptr[3759] = 'A';
    char *ptr1 = vmalloc(4048, (void **)&ptr1);
	
	deref_check((void**)&ptr1);
	ptr1[3759] = 'X';
	
	deref_check((void**)&ptr);
	assert(ptr[3759] == 'A');
	assert((uintptr_t)ptr1 % 4 == 1);
	
	deref_check((void**)&ptr1);
	assert(ptr1[3759] == 'X');
	assert((uintptr_t)ptr % 4 == 1);
	
	char *ptr2 = vmalloc(4048, (void**)&ptr2);
	ptr2[3759] = 'G';
	
	deref_check((void**)&ptr);
	assert(ptr[3759] == 'A');
	assert((uintptr_t)ptr1 % 4 == 1);
	assert((uintptr_t)ptr2 % 4 == 1);
	assert((uintptr_t)ptr % 4 == 0);

	
	vmfree((void*)ptr);
	vminfo();
	
	deref_check((void**)&ptr1);
	assert(ptr1[3759] == 'X');
	assert((uintptr_t)ptr1 % 4 == 0);
	assert((uintptr_t)ptr2 % 4 == 1);
	assert((uintptr_t)ptr % 4 == 0);
	
	vminfo();
    vmdestroy();
}

void custom_heap2()
{
    vminit(4096);
    char *ptr = vmalloc(950, (void **)&ptr);
    char *ptr1 = vmalloc(950, (void **)&ptr1);
    char *ptr2 = vmalloc(950, (void **)&ptr2);
    char *ptr3 = vmalloc(950, (void **)&ptr3);

	deref_check((void **)&ptr);
	ptr[20] = 'A';
    char *ptr4 = vmalloc(500, (void **)&ptr4);
	
	deref_check((void**)&ptr4);
	ptr4[20] = 'X';
	assert(ptr4[20] == 'X');
	assert((uintptr_t)ptr1 % 4 == 0);
	assert((uintptr_t)ptr2 % 4 == 0);
	assert((uintptr_t)ptr % 4 == 1);
	ptr1[20] = 'C';
	
	deref_check((void**)&ptr);
	assert(ptr[20] == 'A');
	assert((uintptr_t)ptr1 % 4 == 1);
	assert((uintptr_t)ptr2 % 4 == 0);
	assert((uintptr_t)ptr % 4 == 0);
	assert((uintptr_t)ptr4 % 4 == 0);
	
	deref_check((void**)&ptr1);
	assert(ptr1[20] == 'C');
	assert((uintptr_t)ptr1 % 4 == 0);
	assert((uintptr_t)ptr2 % 4 == 0);
	assert((uintptr_t)ptr % 4 == 1);
	assert((uintptr_t)ptr4 % 4 == 0);
	
	vminfo();
    vmdestroy();

}

void custom_heap3()
{
    vminit(4096);
    char *ptr = vmalloc(950, (void **)&ptr);
    char *ptr1 = vmalloc(950, (void **)&ptr1);
    char *ptr2 = vmalloc(950, (void **)&ptr2);
    char *ptr3 = vmalloc(950, (void **)&ptr3);

	deref_check((void **)&ptr);
	ptr[20] = 'A';
	ptr1[20] = 'Z';
    char *ptr4 = vmalloc(1800, (void **)&ptr4);	
	assert((uintptr_t)ptr % 4 == 1);
	assert((uintptr_t)ptr1 % 4 == 1);
	assert((uintptr_t)ptr2 % 4 == 0);
	assert((uintptr_t)ptr3 % 4 == 0);
	assert((uintptr_t)ptr4 % 4 == 0);
	
	deref_check((void**)&ptr4);
	ptr4[20] = 'X';
	ptr2[20] = 'G';
	assert((uintptr_t)ptr % 4 == 1);
	assert((uintptr_t)ptr1 % 4 == 1);
	assert((uintptr_t)ptr2 % 4 == 0);
	assert((uintptr_t)ptr3 % 4 == 0);
	assert((uintptr_t)ptr4 % 4 == 0);
	
	deref_check((void**)&ptr);
	assert(ptr[20] == 'A');
	assert((uintptr_t)ptr % 4 == 0);
	assert((uintptr_t)ptr1 % 4 == 1);
	assert((uintptr_t)ptr2 % 4 == 1);
	assert((uintptr_t)ptr3 % 4 == 0);
	assert((uintptr_t)ptr4 % 4 == 0);
	
	deref_check((void**)&ptr1);
	assert(ptr1[20] == 'Z');
	assert((uintptr_t)ptr % 4 == 1);
	assert((uintptr_t)ptr1 % 4 == 0);
	assert((uintptr_t)ptr2 % 4 == 1);
	assert((uintptr_t)ptr3 % 4 == 0);
	assert((uintptr_t)ptr4 % 4 == 0);

	char *ptr5 = vmalloc(800, (void **)&ptr5);
	ptr5[20] = 'F';
	assert((uintptr_t)ptr % 4 == 1);
	assert((uintptr_t)ptr1 % 4 == 1);
	assert((uintptr_t)ptr2 % 4 == 1);
	assert((uintptr_t)ptr3 % 4 == 0);
	assert((uintptr_t)ptr4 % 4 == 0);
	assert((uintptr_t)ptr5 % 4 == 0);
	
	deref_check((void **) &ptr);
	assert((uintptr_t)ptr % 4 == 0);
	assert((uintptr_t)ptr1 % 4 == 1);
	assert((uintptr_t)ptr2 % 4 == 1);
	assert((uintptr_t)ptr3 % 4 == 1);
	assert((uintptr_t)ptr4 % 4 == 0);
	assert((uintptr_t)ptr5 % 4 == 0);
	assert(ptr[20] == 'A');

	char *ptr6 = vmalloc(5000, (void **) &ptr6);
	assert(ptr6 == NULL);
	ptr6 = vmalloc(0, (void **) &ptr6);
	assert(ptr6 == NULL);

	vminfo();
    vmdestroy();
}

void gen_images()
{
    // Generate custom heap dumps using your vmalloc
    custom_heap1();
    custom_heap2();
    custom_heap3();
}

void dump_ref(char *ref_dump)
{
    // load in a reference heap dump and print it out with vminfo();
    vmload(ref_dump);
    vminfo();
    vmdestroy();
}

int main()
{
   	
	gen_images();
	
	return 0;
}
