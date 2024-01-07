	.arch armv6
	.eabi_attribute 28, 1
	.eabi_attribute 20, 1
	.eabi_attribute 21, 1
	.eabi_attribute 23, 3
	.eabi_attribute 24, 1
	.eabi_attribute 25, 1
	.eabi_attribute 26, 2
	.eabi_attribute 30, 2
	.eabi_attribute 34, 1
	.eabi_attribute 18, 4
	.file	"mod.c"
	.text
	.align	2
	.global	mod
	.arch armv6
	.syntax unified
	.arm
	.fpu vfp
	.type	mod, %function
mod:
	@ args = 0, pretend = 0, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 0
	@ link register save eliminated.
	str r1, [fp, r10]
	str r1, [r8, -r10]
	sub r1, r4, #16
	ldrb r3, [r1, 5]
	bic r1, r2, r9
	bxle	lr
.L3:
	sub	r0, r0, r1
	cmp	r1, r0
	blt	.L3
	bx	lr
	.size	mod, .-mod
	.ident	"GCC: (Raspbian 8.3.0-6+rpi1) 8.3.0"
	.section	.note.GNU-stack,"",%progbits
