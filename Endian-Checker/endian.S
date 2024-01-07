	.arch 	armv6               	// armv6 architecture
    .cpu 	cortex-a72           	// CPU model
    .arm                      		// arm 32-bit IS
    .fpu 	vfp                 	// floating point co-processor
	.syntax unified           		// modern syntax

	.section .rodata
.Lstr:	.string "Lowest Byte 0: 0x%02x, Byte 1: 0x%02x, Byte 2: 0x%02x, Highest Byte 3: 0x%02x\n"

	.text 
	
	.global main
	.type 	main, %function

	.equ 	FP_OFF,	8
	.equ	HEX, 	FP_OFF + 4
	.equ 	PTR,	HEX + 4
	.equ	PAD, 	PTR + 0
	.equ	ARG5,	PAD + 4
	.equ	FRMADD,	ARG5 - FP_OFF

main:
	push 	{r4, fp, lr}	
	add 	fp, sp, FP_OFF
	sub		sp, sp, FRMADD
	
	ldr 	r0, [r1, 4]
	
	sub 	r1, fp, PTR

	mov 	r2, 0

	bl 		strtoul
 
	str 	r0, [fp, -HEX]

	sub 	r4, fp, HEX

	ldr 	r0, =.Lstr
	
	ldrb	r1, [r4]
	
	ldrb	r2, [r4, 1]

	ldrb	r3, [r4, 2]

	ldrb	r4, [r4, 3]
	
	str		r4, [fp, -ARG5]

	bl		printf		

	mov 	r0, 0
	
	sub		sp, fp, FP_OFF
	pop 	{r4, fp, lr}
	bx 		lr

	.size 	main, (. - main)
	.section .note.GNU-stack,"",%progbits
.end	
