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

	mov 	r6, r0					//r6 = des
	mov 	r7, r1 					//r7 = src
	mov 	r8, r2					//r8 = row
	mov 	r9, r3					//r9 = rows

	add 	r8, r8, 2				//add 2 to row to check with # of rows
	cmp 	r8, r9					//check if last row or second last row
	sub 	r8, r8, 2				//reset row
	
	bgt		.LlastR					//do last row

	beq 	.LsecondLastR			//do second last row	

	cmp		r8, 0					//check for first row
	bne		.LmiddleR				//not equal do middle row
	

//first row

//first cell

	//left, bottom right, bottom
	sub		r1, r10, 1				//r1 = last col first row
	ldr 	r0, [r7, r1] 			//r0 = left, bottom, bottom right, X
	
	ubfx 	r5, r0, 0, 8			//nAlive = left

	ubfx 	r3, r0, 8, 8			//r1 = bottom
	add 	r5, r5, r3				//nALive += bottom

	ubfx	r3, r0, 16, 8			//r2 = bottom right
	add  	r5, r5, r3				//nAlive += bottom right
	
	//bottom left
	add		r2, r1, r10				//r2 = last col bottom

	ldrb	r0, [r7, r2]			//r0 = bottom left

	add 	r5, r5, r0				//nAlive += bottom left

	//top, top right
	sub		r2, r9, 1				//r2 = last row #
	mul		r2, r2, r10				//r2 = last row first col

	ldrh 	r0, [r7, r2]			//r0 = top, top right

	ubfx 	r3, r0, 0, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 8, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//top left
	add 	r1, r2, r1				//r1 = last row last col

	ldrb	r0, [r7, r1]			//r0 = top left
	
	add 	r5, r5, r0				//nAlive += top left

	//current, right
	ldrh	r0,	[r7]				//r0 = current, right
	
	ubfx	r3,	r0, 8, 8 			//r3 = right
	add		r5, r5, r3				//nAlive += right

	
	//setting des buffer
	and		r0, r0, 0xff			//r0 = current

	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6]				//des[curr] = alive
	beq		.LfrLoop				//finish row
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LffrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6]				//make des[curr] = alive
	
.LffrDead:
	strbne	r8, [r6]				//des[curr] = dead

.LfrLoop:
	//top left, top, top right
	ldr 	r0, [r7, r2]			//r0 = top left, top, top right, X
	
	ubfx 	r5, r0, 0, 8			//nAlive = left top

	ubfx 	r3, r0, 8, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 16, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//bottom left, bottom right, bottom
	add		r1, r10, r4				//r1 = bottom row ith col
	
	ldr 	r0, [r7, r1] 			//r0 = bottom left, bottom, bottom right, X
	
	ubfx 	r3, r0, 0, 8			//r3 = left bottom
	add 	r5, r5, r3				//nAlive += left bottom

	ubfx 	r3, r0, 8, 8			//r3 = bottom
	add 	r5, r5, r3				//nAlive += bottom
	
	ubfx 	r3, r0, 16, 8			//r3 = bottom right
	add 	r5, r5, r3   			//nAlive += bottom right

	//left, current, right
	ldr		r0,	[r7, r4]			//r0 = left, current, right
	
	ubfx 	r3, r0, 0, 8			//r3 = left 
	add 	r5, r5, r3				//nAlive += left
	
	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3   			//nAlive += right
	
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r4, r4, 1				//i = curr col
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r4]			//des[curr] = alive
	beq		.LcheckColfr			//finish loop
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LfrlDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r4]			//make des[curr] = alive
	
.LfrlDead:
	strbne 	r8, [r6, r4]			//des[curr] = dead

.LcheckColfr:
	add		r4, r4, 3				//r4 = i + 3
	cmp		r4, r10					//check if third to last col
	sub		r4, r4, 3				//r4 = nextCol - 1
	add 	r2, r2, 1				//r2 = last row ith col - 1
	bne		.LfrLoop				//repeat loop

//second last cell

	//top left, top, top right
	ldrh 	r0, [r7, r2]			//r0 = top left, top
	
	ubfx 	r5, r0, 0, 8			//nAlive = left top

	ubfx 	r3, r0, 8, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top

	add 	r3, r2, 2				//r3 = top right idx
	
	ldrb	r0, [r7, r3]			//r0 = top right	
	
	add 	r5, r5, r0   			//nAlive += top right

	//bottom left, bottom right, bottom
	add		r1, r10, r4				//r1 = bottom row ith col
	
	ldr 	r0, [r7, r1] 			//r0 = bottom left, bottom, bottom right, X
	
	ubfx 	r3, r0, 0, 8			//r3 = left bottom
	add 	r5, r5, r3				//nAlive += left bottom

	ubfx 	r3, r0, 8, 8			//r3 = bottom
	add 	r5, r5, r3				//nAlive += bottom
	
	ubfx 	r3, r0, 16, 8			//r3 = bottom right
	add 	r5, r5, r3   			//nAlive += bottom right

	//left, current, right
	ldr		r0,	[r7, r4]			//r0 = left, current, right
	
	ubfx 	r3, r0, 0, 8			//r3 = left 
	add 	r5, r5, r3				//nAlive += left
	
	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3   			//nAlive += right
	
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r4, r4, 1				//i = curr col
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r4]			//des[curr] = alive
	beq		.LlastCellfr			//finish loop
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LsfrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r4]			//make des[curr] = alive
	
.LsfrDead:
	strbne 	r8, [r6, r4]			//des[curr] = dead
	
//last cell
.LlastCellfr:	
	add 	r2, r2, 1				//r2 = last row ith col - 1
	
	//top left, top
	ldrh	r0, [r7, r2]			//r0 = top left, top
	
	ubfx 	r5, r0, 0, 8			//nAlive += top left

	ubfx 	r3, r0, 8, 8			//r1 = top
	add 	r5, r5, r3 				//nAlive += top

	//bottom left, bottom
	add		r1, r10, r4				//r2 = last col - 1 bottom

	ldrh	r0, [r7, r1]			//r0 = bottom left, bottom

	ubfx 	r3, r0, 0, 8			//r3 = bottom left
	add 	r5, r5, r3				//nAlive += bottom left

	ubfx 	r3, r0, 8, 8			//r1 = bottom
	add 	r5, r5, r3 				//nAlive += bottom

	//right
	ldrb 	r0, [r7] 				//r0 = right

	add  	r5, r5, r0				//nAlive += right
	
	//top right
	sub		r1, r10, 2				//r2 = second last col
	sub		r2, r2, r1				//r2 = last row first col

	ldrb 	r0, [r7, r2]			//r0 = top right

	add 	r5, r5, r0   			//nAlive += top right

	//left, current, bottom right
	ldr 	r0,	[r7, r4]			//r0 = left, current, bottom right, X
	
	ubfx	r3,	r0, 0, 8 			//r3 = left
	add		r5, r5, r3				//nAlive += left

	ubfx	r3,	r0, 16, 8 			//r3 = bottom right
	add		r5, r5, r3				//nAlive += bottom right
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r4, r4, 1				//i = curr col
	
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r4]			//des[curr] = alive
	beq		.Lfrexit				//return
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LlfrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r4]			//make des[curr] = alive
	mov 	r0, 0					//r0 = 0	
	
.LlfrDead:
	strbne	r0, [r6, r4]			//des[curr] = dead

.Lfrexit:	
	sub 	sp, fp, FP_OFF_D		//reset sp
	pop		{r4-r10, fp, lr}		//reset perserved registers
	bx		lr						//void return
//first row complete

//Last row
.LlastR:
//first cell
			
	//left
	sub		r2, r10, 1				//r2 = last col
	mul 	r1, r8, r10				//r1 = first col last row
	add		r1, r1, r2				//r1 = last row last col
	
	ldrb 	r5, [r7, r1] 			//nAlive = left
	
	//bottom left
	ldrb	r0, [r7, r2]			//r0 = bottom left
	add 	r5, r5, r0				//nAlive += bottom left

	//top, top right
	sub		r2, r9, 2				//r2 = second last row #
	mul		r2, r2, r10				//r2 = second last row first col

	ldrh 	r0, [r7, r2]			//r0 = top, top right

	ubfx 	r3, r0, 0, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 8, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//bottom, bottom right
	ldrh	r0, [r7]				//r0 = bottom, bottom right
	
	ubfx 	r3, r0, 0, 8			//r3 = bottom
	add 	r5, r5, r3				//nAlive += bottom
	
	ubfx 	r3, r0, 8, 8			//r3 = bottom right
	add 	r5, r5, r3   			//nAlive += bottom right

	//top left, current, right
	add 	r2, r2, r10				//r2 = first col last row
	sub 	r2, r2, 1				//r2 = last col second last row	

	ldr		r0,	[r7, r2]			//r0 = top left, current, right, X
	
	ubfx 	r3, r0, 0, 8			//r3 = top left
	add 	r5, r5, r3				//nAlive += top left
	
	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3   			//nAlive += right

	
	//setting des buffer
	ubfx	r0, r0, 8, 8			//r0 = current	
	add 	r1, r2, 1				//r1 = first col last row
	sub 	r2, r1, r10 			//r2 = first col second last row

	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r1]			//des[curr] = alive
	beq		.LlrLoop				//finish row
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LflrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r1]			//make des[curr] = alive	
	mov 	r0, 0					//r0 = 0	

.LflrDead:
	strbne	r0, [r6, r1]			//des[curr] = dead
	
.LlrLoop:
	//bottom left, bottom, bottom right	
	ldr 	r0, [r7, r4]			//r0 = bottom left, bottom, bottom right, X
	
	ubfx 	r5, r0, 0, 8			//nAlive += left bottom

	ubfx 	r3, r0, 8, 8			//r3 = bottom
	add 	r5, r5, r3				//nAlive += bottom
	
	ubfx 	r3, r0, 16, 8			//r3 = bottom right
	add 	r5, r5, r3   			//nAlive += bottom right

	//top left, top, top right
	ldr 	r0, [r7, r2] 			//r0 = top left, top, topright, X
	
	ubfx 	r3, r0, 0, 8			//r3 = left top
	add 	r5, r5, r3				//nAlive += left top

	ubfx 	r3, r0, 8, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 16, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//left, current, right
	ldr		r0,	[r7, r1]			//r0 = left, current, right, X
	
	ubfx 	r3, r0, 0, 8			//r3 = left 
	add 	r5, r5, r3				//nAlive += left
	
	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3   			//nAlive += right
	
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r1, r1, 1				//r1 = cur idx
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r1]			//des[curr] = alive
	beq		.LcheckCollr			//finish loop
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LlrlDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r1]			//make des[curr] = alive
	mov 	r0, 0					//r0 = 0
	
.LlrlDead:
	strbne 	r0, [r6, r1]			//des[curr] = dead

.LcheckCollr:
	add		r4, r4, 4				//r4 = i + 4
	cmp		r4, r10					//check if third to last col
	sub		r4, r4, 3				//r4 = nextCol - 1
	add		r2, r2, 1				//r2 = top left idx for current
	bne		.LlrLoop				//repeat loop

//second last cell
	
	//bottom left, bottom, bottom right	
	ldr 	r0, [r7, r4]			//r0 = bottom left, bottom, bottom right, X
	
	ubfx 	r5, r0, 0, 8			//nAlive += left bottom

	ubfx 	r3, r0, 8, 8			//r3 = bottom
	add 	r5, r5, r3				//nAlive += bottom
	
	ubfx 	r3, r0, 16, 8			//r3 = bottom right
	add 	r5, r5, r3   			//nAlive += bottom right

	//top left, top, top right
	ldr 	r0, [r7, r2] 			//r0 = top left, top, topright, X
	
	ubfx 	r3, r0, 0, 8			//r3 = left top
	add 	r5, r5, r3				//nAlive += left top

	ubfx 	r3, r0, 8, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 16, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//left, current, right
	ldrb	r0,	[r7, r1]			//r0 = left
	
	add 	r5, r5, r0				//nAlive += left
	
	add 	r1, r1, 1				//r1 = curr idx
	
	ldrh	r0, [r7, r1]			//r0 = curr, right
	
	ubfx 	r3, r0, 8, 8			//r3 = right
	add 	r5, r5, r3   			//nAlive += right
	
	
	//setting des buffer
	ubfx 	r0, r0, 0, 8			//r0 = curr
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r1]			//des[curr] = alive
	beq		.LlastCelllr			//finish loop
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LslrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r1]			//make des[curr] = alive
	mov 	r0, 0					//r0 = 0
	
.LslrDead:
	strbne 	r0, [r6, r1]			//des[curr] = dead

//last cell
.LlastCelllr:
	add		r2, r2, 1				//r2 = top left idx for current
	add 	r4, r4, 1 				//r4 = nextCol - 1

	//top left, top, right
	ldr 	r0, [r7, r2]			//r0 = top left, top, right, X
	
	ubfx 	r5, r0, 0, 8			//nAlive += top left

	ubfx 	r3, r0, 8, 8			//r1 = top
	add 	r5, r5, r3 				//nAlive += top

	ubfx 	r3, r0, 16, 8			//r1 = right
	add 	r5, r5, r3 				//nAlive += right

	//bottom left, bottom
	ldrh	r0, [r7, r4]			//r0 = bottom left, bottom

	ubfx 	r3, r0, 0, 8			//r3 = bottom left
	add 	r5, r5, r3				//nAlive += bottom left

	ubfx 	r3, r0, 8, 8			//r1 = bottom
	add 	r5, r5, r3 				//nAlive += bottom

	//bottom right
	ldrb 	r0, [r7] 				//r0 = bottom right

	add  	r5, r5, r0				//nAlive += bottom right
	
	//top right
	add 	r2, r2, 2 				//r2 = first col last row
	sub		r2, r2, r10				//r2 = first col second last row

	ldrb 	r0, [r7, r2]			//r0 = top right

	add 	r5, r5, r0   			//nAlive += top right

	//left, current
	ldrh	r0,	[r7, r1]			//r0 = left, current
	
	ubfx	r3,	r0, 0, 8 			//r3 = left
	
	add		r5, r5, r3				//nAlive += left

	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r1, r1, 1				//r1 = curr idx
	
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r1]			//des[curr] = alive
	beq		.Llrexit				//return
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LllrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r1]			//make des[curr] = alive
	mov		r0, 0					//r0 = 0
	
.LllrDead:
	strbne	r0, [r6, r1]			//des[curr] = dead

.Llrexit:	
	sub 	sp, fp, FP_OFF_D		//reset sp
	pop		{r4-r10, fp, lr}		//reset perserved registers
	bx		lr						//void return
//last row complete

//middle row
.LmiddleR:
//first cell

	//left, bottom right, bottom
	sub		r1, r10, 1				//r1 = last col first row
	mul 	r2, r10, r8				//r2 = first col curr row
	add 	r2, r2, r1				//r2 = last col curr row
	
	
	ldr 	r0, [r7, r2] 			//r0 = left, bottom, bottom right, X
	
	ubfx 	r5, r0, 0, 8			//nAlive = left

	ubfx 	r3, r0, 8, 8			//r1 = bottom
	add 	r5, r5, r3				//nALive += bottom

	ubfx	r3, r0, 16, 8			//r2 = bottom right
	add  	r5, r5, r3				//nAlive += bottom right
	
	//bottom left
	add		r1, r2, r10				//r2 = last col curr + 1

	ldrb	r0, [r7, r1]			//r0 = bottom left

	add 	r5, r5, r0				//nAlive += bottom left

	//top, top right
	sub 	r1, r10, 1				//r1 = cols - 1
	sub 	r2, r2, r1				//r1 = first col curr row
	sub 	r1, r2, r10				//r1 = first col curr - 1 row

	ldrh 	r0, [r7, r1]			//r0 = top, top right

	ubfx 	r3, r0, 0, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 8, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//top left, current, right
	sub		r9, r2, 1				//curr row - 1 last col

	ldr		r0,	[r7, r9]			//r0 = top left, current, right, X
	
	ubfx	r3,	r0, 0, 8 			//r3 = top left
	add		r5, r5, r3				//nAlive += top left
	
	ubfx	r3,	r0, 16, 8 			//r3 = right
	add		r5, r5, r3				//nAlive += right

	
	//setting des buffer
	ubfx	r0, r0, 8, 8			//r0 = current	
	add 	r9, r2, r10				//r9 = curr row + 1 first col
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r2]			//des[curr] = alive
	beq		.LmrLoop				//finish row
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LfmrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r2]			//make des[curr] = alive
	mov		r0, 0					//r0 = 0
	
.LfmrDead:
	strbne	r0, [r6, r2]			//des[curr] = dead

.LmrLoop:
	//top left, top, top right	
	ldr 	r0, [r7, r1]			//r0 = top left, top, top right
	
	ubfx 	r5, r0, 0, 8			//nAlive += left top

	ubfx 	r3, r0, 8, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 16, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//bottom left, bottom right, bottom
	ldr 	r0, [r7, r9] 			//r0 = bottom left, bottom, bottom right, X
	
	ubfx 	r3, r0, 0, 8			//r3 = left bottom
	add 	r5, r5, r3				//nAlive += left bottom

	ubfx 	r3, r0, 8, 8			//r3 = bottom
	add 	r5, r5, r3				//nAlive += bottom
	
	ubfx 	r3, r0, 16, 8			//r3 = bottom right
	add 	r5, r5, r3   			//nAlive += bottom right

	//left, current, right
	ldr		r0,	[r7, r2]			//r0 = left, current, right
	
	ubfx 	r3, r0, 0, 8			//r3 = left 
	add 	r5, r5, r3				//nAlive += left
	
	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3   			//nAlive += right
	
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r2, r2, 1				//r2 = curr idx
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r2]			//des[curr] = alive
	beq		.LcheckColmr			//finish loop
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LmrlDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r2]			//make des[curr] = alive
	mov 	r0, 0					//r0 = 0
	
.LmrlDead:
	strbne 	r0, [r6, r2]			//des[curr] = dead

.LcheckColmr:
	add		r4, r4, 3				//r4 = i + 3
	cmp		r4, r10					//check if second to last col
	sub		r4, r4, 2				//r4 = nextCol - 1
	add 	r9, r9, 1				//r9 = nextCol - 1
	add 	r1, r1, 1 				//r1 = nextCol - 1
	bne		.LmrLoop				//repeat loop
	
//last cell
	
	//top left, top, right	
	ldr		r0, [r7, r1]			//r0 = top left, top, right
	
	ubfx 	r5, r0, 0, 8			//nAlive += top left

	ubfx 	r3, r0, 8, 8			//r1 = top
	add 	r5, r5, r3 				//nAlive += top

	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3				//nAlive += right

	//bottom left, bottom
	ldrh	r0, [r7, r9]			//r0 = bottom left, bottom

	ubfx 	r3, r0, 0, 8			//r3 = bottom left
	add 	r5, r5, r3				//nAlive += bottom left

	ubfx 	r3, r0, 8, 8			//r1 = bottom
	add 	r5, r5, r3 				//nAlive += bottom

	//top right
	sub 	r1, r1, r10				//r1 = curr row - 2 second last col
	add 	r1, r1, 2				//r1 = curr row - 1 first col
	
	ldrb 	r0, [r7, r1] 			//r0 = top right

	add  	r5, r5, r0				//nAlive += top right
	
	//left, current, bottom right
	ldr		r0,	[r7, r2]			//r0 = left, current, bottom right, X
	
	ubfx	r3,	r0, 0, 8 			//r3 = left	
	add		r5, r5, r3				//nAlive += left

	ubfx	r3,	r0, 16, 8 			//r3 = bottom right	
	add		r5, r5, r3				//nAlive += bottom right
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r2, r2, 1				//r2 = curr idx
	
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r2]			//des[curr] = alive
	beq		.Lmrexit				//return
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LlmrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r2]			//make des[curr] = alive
	mov 	r0, 0					//r0 = 0
	
.LlmrDead:
	strbne	r0, [r6, r2]			//des[curr] = dead
	
.Lmrexit:
	sub 	sp, fp, FP_OFF_D		//reset sp
	pop		{r4-r10, fp, lr}		//reset perserved registers
	bx		lr						//void return
//middle row complete

//second last row
.LsecondLastR:
//first cell

	//left, bottom right, bottom
	sub		r1, r10, 1				//r1 = last col first row
	mul 	r2, r10, r8				//r2 = first col curr row
	add 	r2, r2, r1				//r2 = last col curr row
	
	
	ldr 	r0, [r7, r2] 			//r0 = left, bottom, bottom right, X
	
	ubfx 	r5, r0, 0, 8			//nAlive = left

	ubfx 	r3, r0, 8, 8			//r1 = bottom
	add 	r5, r5, r3				//nALive += bottom

	ubfx	r3, r0, 16, 8			//r2 = bottom right
	add  	r5, r5, r3				//nAlive += bottom right
	
	//bottom left
	add		r1, r2, r10				//r2 = last col curr + 1

	ldrb	r0, [r7, r1]			//r0 = bottom left

	add 	r5, r5, r0				//nAlive += bottom left

	//top, top right
	sub 	r1, r10, 1				//r1 = cols - 1
	sub 	r2, r2, r1				//r1 = first col curr row
	sub 	r1, r2, r10				//r1 = first col curr - 1 row

	ldrh 	r0, [r7, r1]			//r0 = top, top right

	ubfx 	r3, r0, 0, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 8, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//top left, current, right
	sub		r9, r2, 1				//curr row - 1 last col

	ldr		r0,	[r7, r9]			//r0 = top left, current, right, X
	
	ubfx	r3,	r0, 0, 8 			//r3 = top left
	add		r5, r5, r3				//nAlive += top left
	
	ubfx	r3,	r0, 16, 8 			//r3 = right
	add		r5, r5, r3				//nAlive += right

	
	//setting des buffer
	ubfx	r0, r0, 8, 8			//r0 = current	
	add 	r9, r2, r10				//r9 = curr row + 1 first col
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r2]			//des[curr] = alive
	beq		.LslrLoop				//finish row
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LfslrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r2]			//make des[curr] = alive
	mov		r0, 0					//r0 = 0
	
.LfslrDead:
	strbne	r0, [r6, r2]			//des[curr] = dead

.LslrLoop:
	//top left, top, top right	
	ldr 	r0, [r7, r1]			//r0 = top left, top, top right
	
	ubfx 	r5, r0, 0, 8			//nAlive += left top

	ubfx 	r3, r0, 8, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 16, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//bottom left, bottom right, bottom
	ldr 	r0, [r7, r9] 			//r0 = bottom left, bottom, bottom right, X
	
	ubfx 	r3, r0, 0, 8			//r3 = left bottom
	add 	r5, r5, r3				//nAlive += left bottom

	ubfx 	r3, r0, 8, 8			//r3 = bottom
	add 	r5, r5, r3				//nAlive += bottom
	
	ubfx 	r3, r0, 16, 8			//r3 = bottom right
	add 	r5, r5, r3   			//nAlive += bottom right

	//left, current, right
	ldr		r0,	[r7, r2]			//r0 = left, current, right
	
	ubfx 	r3, r0, 0, 8			//r3 = left 
	add 	r5, r5, r3				//nAlive += left
	
	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3   			//nAlive += right
	
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r2, r2, 1				//r2 = curr idx
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r2]			//des[curr] = alive
	beq		.LcheckColslr			//finish loop
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LslrlDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r2]			//make des[curr] = alive
	mov 	r0, 0					//r0 = 0
	
.LslrlDead:
	strbne 	r0, [r6, r2]			//des[curr] = dead

.LcheckColslr:
	add		r4, r4, 4				//r4 = i + 4
	cmp		r4, r10					//check if third to last col
	sub		r4, r4, 3				//r4 = nextCol - 1
	add 	r9, r9, 1				//r9 = nextCol - 1
	add 	r1, r1, 1 				//r1 = nextCol - 1
	bne		.LslrLoop				//repeat loop

//second last cell 

	//top left, top, top right	
	ldr 	r0, [r7, r1]			//r0 = top left, top, top right
	
	ubfx 	r5, r0, 0, 8			//nAlive += left top

	ubfx 	r3, r0, 8, 8			//r3 = top
	add 	r5, r5, r3				//nAlive += top
	
	ubfx 	r3, r0, 16, 8			//r3 = top right
	add 	r5, r5, r3   			//nAlive += top right

	//bottom left, bottom right, bottom
	ldrh 	r0, [r7, r9] 			//r0 = bottom left, bottom
	
	ubfx 	r3, r0, 0, 8			//r3 = left bottom
	add 	r5, r5, r3				//nAlive += left bottom

	ubfx 	r3, r0, 8, 8			//r3 = bottom
	add 	r5, r5, r3				//nAlive += bottom

	add		r3, r9, 2				//r3 = bottom right idx
	
	ldrb	r0, [r7, r3]			//r0 = bottom right
	
	add 	r5, r5, r0   			//nAlive += bottom right

	//left, current, right
	ldr		r0,	[r7, r2]			//r0 = left, current, right
	
	ubfx 	r3, r0, 0, 8			//r3 = left 
	add 	r5, r5, r3				//nAlive += left
	
	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3   			//nAlive += right
	
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r2, r2, 1				//r2 = curr idx
		
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r2]			//des[curr] = alive
	beq		.LlastCellslr			//finish loop
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LsslrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r2]			//make des[curr] = alive
	mov 	r0, 0					//r0 = 0
	
.LsslrDead:
	strbne 	r0, [r6, r2]			//des[curr] = dead

//last cell
.LlastCellslr:
	add 	r4, r4, 1				//r4 = nextCol - 1
	add 	r9, r9, 1				//r9 = nextCol - 1
	add		r1, r1, 1				//r1 = nextCol - 1
	
	//top left, top, right	
	ldr		r0, [r7, r1]			//r0 = top left, top, right
	
	ubfx 	r5, r0, 0, 8			//nAlive += top left

	ubfx 	r3, r0, 8, 8			//r1 = top
	add 	r5, r5, r3 				//nAlive += top

	ubfx 	r3, r0, 16, 8			//r3 = right
	add 	r5, r5, r3				//nAlive += right

	//bottom left, bottom
	ldrh	r0, [r7, r9]			//r0 = bottom left, bottom

	ubfx 	r3, r0, 0, 8			//r3 = bottom left
	add 	r5, r5, r3				//nAlive += bottom left

	ubfx 	r3, r0, 8, 8			//r1 = bottom
	add 	r5, r5, r3 				//nAlive += bottom

	//top right
	sub 	r1, r1, r10				//r1 = curr row - 2 second last col
	add 	r1, r1, 2				//r1 = curr row - 1 first col
	
	ldrb 	r0, [r7, r1] 			//r0 = top right

	add  	r5, r5, r0				//nAlive += top right
	
	//left, current, bottom right
	ldr		r0,	[r7, r2]			//r0 = left, current, bottom right, X
	
	ubfx	r3,	r0, 0, 8 			//r3 = left	
	add		r5, r5, r3				//nAlive += left

	ubfx	r3,	r0, 16, 8 			//r3 = bottom right	
	add		r5, r5, r3				//nAlive += bottom right
	
	//setting des buffer
	ubfx 	r0, r0, 8, 8			//r0 = curr
	add 	r2, r2, 1				//r2 = curr idx
	
	cmp		r5, 3					//3 universal alive
	mov		r3, 1					//r3 = 1 	
	strbeq	r3, [r6, r2]			//des[curr] = alive
	beq		.Lslrexit				//return
	
	cmp 	r0, 1					//check if curr alive
	bne 	.LlslrDead				//if dead
	
	cmp 	r5, 2					//make alive if nAlive = 2
	strbeq	r3, [r6, r2]			//make des[curr] = alive
	mov 	r0, 0					//r0 = 0
	
.LlslrDead:
	strbne	r0, [r6, r2]			//des[curr] = dead
	
.Lslrexit:
	sub 	sp, fp, FP_OFF_D		//reset sp
	pop		{r4-r10, fp, lr}		//reset perserved registers
	bx		lr						//void return
//second last row complete
