CC=iverilog
CFLAGS=-I.

run: clean testmux.o testdemux.o testdff.o
	vvp testdff.o
	gtkwave testdff.vcd

testdff.o: dff.v testdff.v
	iverilog -o testdff.o -Wall dff.v testdff.v

testmux.o: mux.v mux4.v mux8.v mux16.v testmux.v
	iverilog -o testmux.o -Wall mux.v mux4.v mux8.v mux16.v testmux.v

testdemux.o: demux.v demux4.v demux8.v demux16.v testdemux.v
	iverilog -o testdemux.o -Wall demux.v demux4.v demux8.v demux16.v testdemux.v

.PHONY: clean

clean:
	rm -f test testmux.o testdemux.o testmux.vcd testdemux.vcd testdff.vcd
