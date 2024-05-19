EXE = life
EXEOPT = life_opt
OBJS = doRow.o nGetIndex.o main.o board.o sim.o
OOBJS = OBJS = doRow.o nGetIndexO3.o main.o

LIBS = -lcse30liferv -lcse30life 
CC = gcc 
CFLAGS := -O0 -g --std=gnu11 -L/home/linux/ieng6/oce/68/carch/life-assembly/Life-starter 
CFLAGSOPT := -O3 -g --std=gnu11 -L/home/linux/ieng6/oce/68/carch/life-assembly/Life-starter 

.PHONY: clean

$(EXE) : $(OBJS) 
	gcc -o $@ $(CFLAGS) $(OBJS) $(LIBS)

$(EXEOPT) : doRow.o nGetIndexO3.o
	gcc -o $@ $(CFLAGS) doRow.o nGetIndexO3.o $(LIBS)

nGetIndex.o : nGetIndex.c nGetIndex.h

nGetIndexO3.o : nGetIndex.c nGetIndex.h
	-ln -s nGetIndex.c nGetIndexO3.c
	gcc $(CFLAGSOPT) -c nGetIndexO3.c

main.o : main.c
	gcc -c $(CFLAGS) main.c

board.o : board.c
	gcc -c $(CFLAGS) board.c

sim.o : sim.c
	gcc -c $(CFLAGS) sim.c

doRow.o : doRow.S
	gcc -c $(CFLAGS) doRow.S

clean :
	rm -f $(OBJS)
	rm $(EXE)

