EXE = life
OBJS = main.c board.c sim.c
LIBS = -lcse30life
CFLAGS := -ggdb3 -O0 --std=gnu17 -L./Life-starter

.PHONY: clean

$(EXE): $(OBJS) 
	gcc -Wall -Wextra -o $@ $(CFLAGS) $(OBJS) $(LIBS)

clean :
	rm -f $(EXE)
