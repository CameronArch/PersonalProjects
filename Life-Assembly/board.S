 	.arch 	armv6               	// armv6 architecture
    .cpu 	cortex-a72           	// CPU model
    .arm                      		// arm 32-bit IS
    .fpu 	vfp                 	// floating point co-processor
	.syntax unified           		// modern syntax

	//imports 
	#include "cse30life.h"	
	#include "board.h"
	.extern fopen
	.extern fscanf
	
	.section .rodata
.Lrd:	.string "r"
.L1d:	.string "%zu"
.L2d: 	.string "%zu %zu"

	.text 
	
	.global create_board
	.global delete_board
	.global clear_board
	.global swap_buffers
	.global get_index
	
	.type 	create_board, %function
	.type 	delete_board, %function
	.type 	clear_board, %function
	.type 	swap_buffers, %function
	.type 	get_index, %function

	//create_board stack frame
	.equ 	FP_OFF_CR,	28
	.equ 	ROW, 		FP_OFF_CR + 4
	.equ	COL,		ROW + 4
	.equ 	FRMADD_CR, 	COL - FP_OFF_CR

	//delete_board stack frame
	.equ 	FP_OFF_D,	12
	
	//clear_board stack frame
	.equ 	FP_OFF_CL, 	8
	.equ	PAD_CL,		FP_OFF_CL + 4
	.equ 	FRMADD_CL,	PAD_CL - FP_OFF_CL

	//swap_buffers stack frame
	.equ 	FP_OFF_S, 	0
	.equ 	PAD_S, 		4
	
	//get_index stack frame
	.equ 	FP_OFF_G,	0
	.equ 	PAD_G,		4

create_board:
	//r4 = file
	//r5 = bp
	//r6 = buf_len
	//r7 = &row
	//r8 = &col
	//r9 = idx_Alive
	
	push 	{r4-r9, fp, lr} 		//save perserved registers
	add 	fp, sp, FP_OFF_CR		//sets fp 
	sub 	sp, sp, FRMADD_CR		//sets sp

	ldr 	r1, =.Lrd				//r1 = "r"
	
	bl		fopen					//calls fopen of filename
	
	cmp		r0, 0 					//checks for NULL
	beq		.LexitC					//return NULL if NULL
	
	mov 	r4, r0					//r4 = file
	
	mov 	r0, 28					// r0 = sizeof(board)
	
	bl 		malloc					//allocate space for board struct
	
	cmp		r0, 0 					//checks for NULL
	beq		.LexitC					//return NULL if NULL

	mov	 	r5, r0 					//r5 = bp
	
	mov 	r0, 0					//r0 = 0
	str 	r0, [r5, 8]				//bp->gen = 0

	mov 	r0, r4 					//r0 = file
	ldr 	r1, =.L1d				//r1 = "%zu"
	add 	r2, r5, 8				//r2 = &bp->num_rows

	bl 		fscanf 					//calls fscanf to get number of rows
	
	cmp 	r0, 1					//checks if fscanf did not return 1
	bne 	.LNULL					//return NULL if r0 != 1

	mov 	r0, r4 					//r0 = file
	ldr 	r1, =.L1d				//r1 = "%zu"
	add 	r2, r5, 12				//r2 = &bp->num_cols

	bl 		fscanf 					//calls fscanf to get number of rows
	
	cmp 	r0, 1					//checks if fscanf did not return 1
	bne 	.LNULL					//return NULL if r0 != 1

	ldr 	r0, [r5, 8]				//r0 = bp->num_rows
	ldr 	r1, [r5, 12]			//r1 = bp->num_cols
	mov 	r2, 0					//r2 = 0
	
	bl 		get_index				//gets buffer length

	mov 	r6, r0					// r6 = buf_len
	
	bl		malloc 					//allocate memory for buffers

	cmp		r0, 0 					//checks for NULL
	beq		.LexitC					//return NULL if NULL
	
	str 	r0, [r5] 				//bp->buffer_a = malloc ptr1
	str 	r0, [r5, 16] 			//bp->current_buffer = malloc ptr1
	
	mov 	r0, r6					// r0 = buf_len
	
	bl		malloc 					//allocate memory for buffers

	cmp		r0, 0 					//checks for NULL
	beq		.LexitC					//return NULL if NULL
	
	str 	r0, [r5, 4] 			//bp->buffer_b = malloc ptr2
	str 	r0, [r5, 20] 			//bp->next_buffer = malloc ptr2

	mov 	r0, r5					//r0 = bp

	bl		clear_board				//clears buffers

	mov 	r0, r4					//r0 = file
	ldr 	r1, =.L2d				//r1 = "%zu %zu"
	sub 	r2, fp, ROW				//r2 = &row
	sub 	r3, fp, COL 			//r3 = &col 

	bl 		fscanf 					//calls fscanf to obtain coordinates
	
	cmp 	r0, 0 					//checks if fscanf returned 0
	beq 	.Ldone					//finish function

.Lwhile:
	ldr 	r0, [r5, 12] 			//r0 = bp->num_cols
	ldr 	r1, [fp, -ROW] 			//r1 = row
	ldr 	r2, [fp, -COL] 			//r2 = col

	bl 		get_index 				//gets index of current alive cell

	cmp 	r0, r6 					//checks idx_Alive is not greater than or equal to buf_len
	bge		.LNULL 					//return NULL if idx_Alive >= buf_len

	ldr 	r1, [r5, 16]			//r1 = bp->current_buffer
	mov 	r2, 1 					//r2 = 1
	str 	r2, [r1, r0]			//current_buffer[idx_Alive] = 1
	
	mov 	r0, r4					//r0 = file
	ldr 	r1, =.L2d				//r1 = "%zu %zu"
	sub 	r2, fp, ROW				//r2 = &row
	sub 	r3, fp, COL 			//r3 = &col 

	bl 		fscanf 					//calls fsanf to obtain coordinates
	
	cmp 	r0, 0					//check if r0 is greater than 0
	bgt 	.Lwhile 				//continue while loop if coordinate was return
	
.Ldone: 	
	mov 	r0, r4 					//r0 = file
	
	bl 		fclose					//close file
	
	mov 	r0, r5 					//return bp	

.LexitC:
	sub 	sp, fp, FP_OFF_CR		//reset sp
	pop		{r4-r9, fp, lr}			//reset perserved register, fp, lr
	bx 		lr						//return

.LNULL:
	mov		r0, 0 					//r0 = NULL	

	b 		.LexitC					//return NULL

	.size   create_board, (. - create_board)



delete_board:
	//r4 = bpp
	//r5 = *bpp
	
	push 	{r4-r5, fp, lr}  		//saves perserved registers
	add 	fp, sp, FP_OFF_D 		//sets fp
	
	mov 	r4, r0 					//r4 = bpp
	ldr 	r5, [r4] 				//r5 = *bpp

	ldr 	r0, [r5] 				//r0 = (*bpp)->buffer_a

	bl		free					//free buffer_a
	
	ldr 	r0, [r5, 4] 			//r0 = (*bpp)->buffer_b

	bl		free					//free buffer_b

	mov 	r0, r5					//r0 = *bpp

	bl 		free 					//free *bpp

	mov 	r0, 0					//r0 = NULL
	str 	r0, [r4]				//*bpp = NULL

	pop		{r4-r5, fp, lr}			//resets perserved registers
	bx 		lr						//void return

	.size   delete_board, (. - delete_board)

clear_board:
	//r4 = board, 0 (for loop)
	//r0 = buf_len

	push 	{r4, fp, lr}			//saves preserved register
	add 	fp, sp, FP_OFF_CL		//sets fp
	sub 	sp, sp, FRMADD_CL		//sets sp

	mov 	r4, r0 					//r4 = board
	
	ldr 	r0,	[r4, 12]			//r0 = board->num_cols
	ldr 	r1, [r4, 8] 			//r1 = board->num_rows
	mov 	r2, 0					//r2 = 0

	bl 		get_index 				//get buf_len
	
	cmp		r0, 0 					//checks if buf_len is zero
	beq 	.Lexit					//finish function

	ldr 	r2, [r4, 16] 			//r2 = current_buffer
	ldr		r3,	[r4, 20]			//r3 = next_buffer
	mov 	r4, 0					//r4 = 0
	
	mov 	r1, 0					//i = 0
.Lfor:
	str 	r4, [r2, r1] 			//current_buffer[i] = 0
	str 	r4, [r3, r1] 			//next_buffer[i] = 0
	
	add 	r1, r1, 1 				//i++
	
	cmp		r0, r1					//checks if i < buf_len
	blt 	.Lfor					// repeat loop if i < buf_len

.Lexit:
	sub 	sp, fp, FRMADD_CL 		//resets sp
	pop 	{r4, fp, lr}			//resets perserved registers
	bx 		lr 						//void return

	.size   clear_board, (. - clear_board)

swap_buffers:
	push 	{fp} 					//saves perserved registers
	add 	fp, sp, FP_OFF_S		//sets fp
	sub		sp, sp, PAD_S			//sets sp

	ldr 	r1, [r0, 16]			//r1 = current_buffer
	ldr 	r2, [r0, 20]			//r2 = next_buffer
	
	str 	r2, [r0, 16]			//current_buffer = next_buffer
	str		r1, [r0, 20] 			//next_buffer = current_buffer

	sub 	sp, fp, FP_OFF_S		//resets sp
	pop 	{fp}					//resets perserved registers
	bx		lr						//void return

	.size   swap_buffers, (. - swap_buffers)

get_index:
	push 	{fp}					//saves perserved registers
	add 	fp, sp, FP_OFF_G		//sets fp
	sub 	sp, sp, PAD_G			//sets sp
	
	mul 	r0, r1, r0				//r0 = row * num_cols	
	add 	r0, r0, r2				//r0 = r0 + col
	
	sub		sp, fp, FP_OFF_G		//resets sp
	pop 	{fp}					//resets perserved registers
	bx 		lr						//returns index in r0
	
	.size   get_index, (. - get_index)

	.section .note.GNU-stack,"",%progbits
.end
					
	
	


