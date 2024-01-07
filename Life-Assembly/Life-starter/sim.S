	.arch 	armv6               	// armv6 architecture
    .arm                      		// arm 32-bit IS
    .fpu 	vfp                 	// floating point co-processor
	.syntax unified           		// modern syntax

	//imports
	#include "sim.h"

	.text 

	.global mod
	.global do_row
	.global sim_loop

	.type 	mod, %function
	.type 	do_row, %function
	.type 	sim_loop, %function

	//mod stack frame
	//helper function
	
	//do_row stack frame 
	.equ 	FP_OFF_D, 32
	.equ 	PAD_D , FP_OFF_D + 4
	.equ	FRMADD_D, PAD_D - FP_OFF_D

	//sim_loop stack frame
	.equ 	FP_OFF_S, 32
	.equ	ARG5, FP_OFF_S + 4
	.equ	FRMADD_S, ARG5 - FP_OFF_S
	
mod: 
	//r0 = dividend
	//r1 = divisor
		
	cmp		r0, r1					//check if dividend < divisor
	bxle	lr						//return dividend

.LloopM:
	sub		r0, r0, r1				//r0 -= r1

	cmp		r1, r0					//check if divisor > dividend
	ble		.LloopM					//continue loop if divisor <= dividend
	
	bx 		lr 						//return r0, remainder

	.size 	mod, (. - mod)

do_row:
	//r4 = i
	//r5 = nAlive
	//r6 = dest
	//r7 = src
	//r8 = row
	//r9 = rows
	//r10 = cols

	push 	{r4-r10, fp, lr} 		//save perserved registers
	add 	fp, sp, FP_OFF_D		//set fp
	sub		sp, sp, FRMADD_D		//set sp

	mov 	r4, 0					//i = 0
	ldr 	r10, [fp, 4]			//r10 = cols

	cmp		r4, r10					//check if i >= cols
	bge		.LexitD					//return if i >= cols

	mov 	r6, r0					//r6 = des
	mov 	r7, r1 					//r7 = src
	mov 	r8, r2					//r8 = row
	mov 	r9, r3					//r9 = rows

.LloopD:
	//left
	add		r0, r4, r10				//r0 = i + cols
	sub		r0, r0, 1				//r0 -= 1
	
	mov 	r1, r10					//r1 = cols
	
	bl 		mod						//get remainder

	mov 	r2, r0					//r2 = remainder
	mov 	r0, r10					//r0 = cols
	mov 	r1, r8					//r1 = row

	bl 		get_index				//get index left

	ldrb 	r5, [r7, r0] 			//nAlive = left

	//right
	add		r0, r4, r10				//r0 = i + cols
	add		r0, r0, 1				//r0 += 1
	
	mov 	r1, r10					//r1 = cols
	
	bl 		mod						//get remainder

	mov 	r2, r0					//r2 = remainder
	mov 	r0, r10					//r0 = cols
	mov 	r1, r8					//r1 = row

	bl 		get_index				//get index right
	
	ldrb 	r0, [r7, r0] 			//r0 = right
	add 	r5, r5, r0				//nAlive += r0	

	//top
	add		r0, r9, r8				//r0 = rows + row
	sub		r0, r0, 1				//r0 -= 1
	
	mov 	r1, r9					//r1 = rows
	
	bl 		mod						//get remainder

	mov 	r1, r0					//r1 = remainder
	mov 	r0, r10					//r0 = cols
	mov 	r2, r4					//r2 = i

	bl 		get_index				//get index top

	ldrb 	r0, [r7, r0] 			//r0 = top
	add 	r5, r5, r0				//nAlive += r0
		
	//bottom
	add		r0, r9, r8				//r0 = rows + row
	add		r0, r0, 1				//r0 += 1
	
	mov 	r1, r9					//r1 = rows
	
	bl 		mod						//get remainder

	mov 	r1, r0					//r1 = remainder
	mov 	r0, r10					//r0 = cols
	mov 	r2, r4					//r2 = i

	bl 		get_index				//get index top

	ldrb 	r0, [r7, r0] 			//r0 = bottom
	add 	r5, r5, r0				//nAlive += r0

	//top left
	add		r0, r4, r10				//r0 = i + cols
	sub		r0, r0, 1				//r0 -= 1
	
	mov 	r1, r10					//r1 = cols
	
	bl 		mod						//get remainder

	mov 	r2, r0					//r2 = remainder

	add		r0, r9, r8				//r0 = rows + row
	sub		r0, r0, 1				//r0 -= 1
	
	mov 	r1, r9					//r1 = rows
	
	bl 		mod						//get remainder

	mov 	r1, r0					//r1 = remainder
	mov 	r0, r10					//r0 = cols

	bl 		get_index				//get index top left

	ldrb	r0, [r7, r0] 			//r0 = top left
	add 	r5, r5, r0				//nALive += r0

	//top right
	add		r0, r4, r10				//r0 = i + cols
	add		r0, r0, 1				//r0 += 1
	
	mov 	r1, r10					//r1 = cols
	
	bl 		mod						//get remainder

	mov 	r2, r0					//r2 = remainder

	add		r0, r9, r8				//r0 = rows + row
	sub		r0, r0, 1				//r0 -= 1
	
	mov 	r1, r9					//r1 = rows
	
	bl 		mod						//get remainder
	
	mov 	r1, r0					//r1 = remainder
	mov 	r0, r10					//r0 = cols

	bl 		get_index				//get index top right
	
	ldrb 	r0, [r7, r0] 			//r0 = top right
	add 	r5, r5, r0				//nAlive += r0	

	//bottom left
	add		r0, r4, r10				//r0 = i + cols
	sub		r0, r0, 1				//r0 -= 1
	
	mov 	r1, r10					//r1 = cols
	
	bl 		mod						//get remainder

	mov 	r2, r0					//r2 = remainder

	add		r0, r9, r8				//r0 = rows + row
	add		r0, r0, 1				//r0 += 1
	
	mov 	r1, r9					//r1 = rows
	
	bl 		mod						//get remainder

	mov 	r1, r0					//r1 = remainder
	mov 	r0, r10					//r0 = cols

	bl 		get_index				//get index bottom left

	ldrb 	r0, [r7, r0] 			//r0 = bottom left
	add 	r5, r5, r0				//nALive += r0

	//bottom right
	add		r0, r4, r10				//r0 = i + cols
	sub		r0, r0, 1				//r0 += 1
	
	mov 	r1, r10					//r1 = cols
	
	bl 		mod						//get remainder

	mov 	r2, r0					//r2 = remainder

	add		r0, r9, r8				//r0 = rows + row
	add		r0, r0, 1				//r0 += 1
	
	mov 	r1, r9					//r1 = rows
	
	bl 		mod						//get remainder

	mov 	r1, r0					//r1 = remainder
	mov 	r0, r10					//r0 = cols

	bl 		get_index				//get index bottom right

	ldrb 	r0, [r7, r0] 			//r0 = bottom right
	add 	r5, r5, r0				//nALive += r0

	//setting buffers
	//r0 = idx
	//r1 = src[idx]
	
	mov 	r0, r10					//r0 = cols
	mov 	r1, r8					//r1 = row
	mov 	r2, r4					//r2 = i	

	bl 		get_index 				//gets idx in r0

	cmp		r5, 3					//3 universal for alive
	mov		r3, 1					//add one
	strbeq 	r3, [r6, r0]			//store alive if equal
	beq		.Lexit					//return if equal
	
	ldrb 	r1, [r7, r0]			//r1 = src[idx]
	
	cmp		r1, 1					//check if cell is alive
	bne		.Ldead					//run dead condition for cell

	//if Alive
	cmp		r5, 2					//check if nAlive == 2
	strbeq	r3, [r6, r0]			//make alive if nAlive == 2
	beq		.Lexit					//return if equal

.Ldead:	
	cmp		r5, 6					//check if nAlive == 6
	strbeq	r3, [r6, r0]			//make alive if 6
	beq 	.Lexit 					//return if equal

	mov 	r2, 0					//r2 = 0
	strb 	r2, [r6, r0]			//dest[idx] = 0

.LexitD:
	sub 	sp, fp, FP_OFF_D		//reset sp
	pop		{r4-r10, fp, lr}		//reset perserved registers
	bx		lr						//void return

	.size 	do_row, (. - do_row)

sim_loop:
	//r4 = board
	//r5 = steps
	//r6 = current_buffer
	//r7 = next_buffer
	//r8 = num_rows
	//r9 = num_cols
	//r10 = i

	push	{r4-r10, fp, lr}		//save perserved registers
	add		fp, sp, FP_OFF_S		//set fp
	sub 	sp, sp, FRMADD_S		//set sp	
	
	mov 	r4, r0					//r4 = board
	mov		r5, r1					//r5 = steps
	
	cmp		r5, 0					//check steps == 0
	bge 	.LexitS					//finish if steps == 0

	ldr		r6, [r4, 16]			//r6 = current_buffer
	ldr 	r7, [r4, 20] 			//r7 = next_buffer
	ldr		r8, [r4, 8] 			//r8 = num_rows
	ldr		r9, [r4, 12] 			//r9 = num_cols
	
	str		r9, [fp, -ARG5]			//store 5th parameter 	
	
.LwhileS:
	mov 	r10, 0					//r10 = i = 0
	
	cmp		r10, r8					//check if i >= num_rows
	bne 	.LwhileSp2				//stop for loop

.LloopS:
	mov 	r0, r7					//r0 = next_buffer
	mov 	r1, r6					//r1 = current_buffer
	mov 	r2, r10					//r2 = i
	mov 	r3,	r8					//r3 = num_rows	

	bl		do_row					//call do_row

	add		r10, r10, 1				//i++
	
	cmp		r10, r8					//check if i < num_rows
	blt		.LloopS					//loop if i < num_rows

.LwhileSp2:
	mov		r0, r4					// r0 = board
	
	bl		swap_buffers			//swap buffers

	ldr 	r0,	[r4, 24] 			//r0 = gen
	add		r0, r0, 1 				//r0 += 1
	str 	r0, [r4, 24]			//gen = r0

	sub	 	r5, r5, 1				//steps--
	
	cmp		r5, 0					//check if steps is 0
	bgt		.LwhileS				//do while loop again if steps > 0

.LexitS:
	sub 	sp, fp, FP_OFF_S		//reset sp
	pop		{r4-r10, fp, lr}		//reset perserved registers
	bx		lr						//void return

	.size 	sim_loop, (. - sim_loop)
	.section .note.GNU-stack,"",%progbits
.end		
