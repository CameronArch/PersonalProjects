 	.arch 	armv7
 	.cpu 	cortex-a53
    .equ 	NUL, 0
	.syntax unified	

    .global asm_doRow

	//do_row stack frame 
	.equ 	FP_OFF_D, 32
	.equ 	PAD_D , FP_OFF_D + 4
	.equ	FRMADD_D, PAD_D - FP_OFF_D

	// asm_doRow(belem *dest,    // r0
	//           belem *src,     // r1
	//           size_t curRow,  // r2
	//           size_t nRows,   // r3
	//           size_t nCols,   // fp + 4
	//	     int *newLife)   // fp + 8


asm_doRow:	
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
	bge		.Lexit					//return if i >= cols

	mov 	r6, r0					//r6 = des
	mov 	r7, r1 					//r7 = src
	mov 	r8, r2					//r8 = row
	mov 	r9, r3					//r9 = rows

.LloopD:
	//left
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	mov 	r2, r8					//r2 = row
	sub		r3, r4, 1				//r3 = col

	bl 		nGetIndex				//get index left

	ldrb 	r5, [r7, r0] 			//nAlive = left

	//right
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	mov 	r2, r8					//r2 = row
	add		r3, r4, 1				//r3 = col

	bl 		nGetIndex				//get index right
	
	ldrb 	r0, [r7, r0] 			//r0 = right
	add 	r5, r5, r0				//nAlive += r0	

	//top
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	sub	 	r2, r8, 1				//r2 = row
	mov		r3, r4					//r3 = col

	bl 		nGetIndex				//get index top

	ldrb 	r0, [r7, r0] 			//r0 = top
	add 	r5, r5, r0				//nAlive += r0
		
	//bottom
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	add	 	r2, r8, 1				//r2 = row
	mov		r3, r4					//r3 = col

	bl 		nGetIndex				//get index bottom

	ldrb 	r0, [r7, r0] 			//r0 = bottom
	add 	r5, r5, r0				//nAlive += r0

	//top left
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	sub	 	r2, r8, 1				//r2 = row
	sub		r3, r4, 1				//r3 = col

	bl 		nGetIndex				//get index top left

	ldrb	r0, [r7, r0] 			//r0 = top left
	add 	r5, r5, r0				//nALive += r0

	//top right
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	sub	 	r2, r8, 1				//r2 = row
	add		r3, r4, 1				//r3 = col

	bl 		nGetIndex				//get index top right
	
	ldrb 	r0, [r7, r0] 			//r0 = top right
	add 	r5, r5, r0				//nAlive += r0	

	//bottom left
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	add	 	r2, r8, 1				//r2 = row
	sub		r3, r4, 1				//r3 = col

	bl 		nGetIndex				//get index bottom left

	ldrb 	r0, [r7, r0] 			//r0 = bottom left
	add 	r5, r5, r0				//nALive += r0

	//bottom right
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	add	 	r2, r8, 1				//r2 = row
	add		r3, r4, 1				//r3 = col

	bl 		nGetIndex				//get index bottom right

	ldrb 	r0, [r7, r0] 			//r0 = bottom right
	add 	r5, r5, r0				//nALive += r0

	//setting buffers
	//r0 = idx
	//r1 = src[idx]	
	mov 	r0, r9					//r0 = rows
	mov 	r1, r10					//r1 = cols
	mov 	r2, r8					//r2 = row
	mov		r3, r4					//r3 = col

	bl 		nGetIndex				//get index curr

	ldr 	r2, [fp, 8]				//r2 = newLife

	cmp		r5, 3					//3 universal for alive
	mov		r3, 1					//add one
	strbeq 	r3, [r6, r0]			//store alive if equal
	
	ldreq	r3, [r2]				//r3 = *newLife
	addeq	r3, r3, 1				//r3 += 1
	streq	r3, [r2]				//*newLife++
	
	beq		.Lcheck					//return if equal
	
	ldrb 	r1, [r7, r0]			//r1 = src[idx]
	
	cmp		r1, 1					//check if cell is alive
	bne		.Ldead					//run dead condition for cell

	//if Alive
	cmp		r5, 2					//check if nAlive == 2
	strbeq	r3, [r6, r0]			//make alive if nAlive == 2

	ldreq	r3, [r2]				//r3 = *newLife
	addeq	r3, r3, 1				//r3 += 1
	streq	r3, [r2]				//*newLife++

	movne 	r2, 0					//r2 = 0
	strbne 	r2, [r6, r0]			//dest[idx] = 0

	b		.Lcheck					//return

.Ldead:	
	cmp		r5, 6					//check if nAlive == 6
	strbeq	r3, [r6, r0]			//make alive if 6
	
	ldreq	r3, [r2]				//r3 = *newLife
	addeq	r3, r3, 1				//r3 += 1
	streq	r3, [r2]				//*newLife++
	
	movne 	r2, 0					//r2 = 0
	strbne 	r2, [r6, r0]			//dest[idx] = 0

.Lcheck:
	add 	r4, r4, 1				//i++
	cmp     r4, r10					//i < cols
	blt 	.LloopD					//continue loop

.Lexit:
	sub 	sp, fp, FP_OFF_D		//reset sp
	pop		{r4-r10, fp, lr}		//reset perserved registers
	bx		lr						//void return

	
