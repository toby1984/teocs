CC=iverilog
CFLAGS=-I.

run: clean testmux.o
	vvp testmux.o
	gtkwave testmux.vcd

testmux.o: mux.v mux4.v mux8.v mux16.v testmux.v
	iverilog -o testmux.o -Wall mux.v mux4.v mux8.v mux16.v testmux.v

.PHONY: clean

clean:
	rm -f test testmux.vcd
