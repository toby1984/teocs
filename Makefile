CC=iverilog
CFLAGS=-I.
TOTEST=testdff

run: clean testmux.o testdemux.o testdff.o testmux4way16.o testprimitives.o
	vvp $(TOTEST).o
	gtkwave $(TOTEST).vcd

testprimitives.o: or.v and.v xor.v nand.v not.v testprimitives.v and16.v or16.v not16.v
	iverilog -o testprimitives.o or.v and.v xor.v nand.v not.v and16.v or16.v not16.v testprimitives.v

testmux4way16.o: mux16.v  mux2way16.v  mux4.v  mux4way16.v  mux8.v  mux.v  testmux4way16.v 
	iverilog -o testmux4way16.o -Wall mux16.v  mux2way16.v  mux4.v  mux4way16.v  mux8.v  mux.v  testmux4way16.v

testdff.o: dff.v testdff.v
	iverilog -o testdff.o -Wall dff.v testdff.v

testmux.o: mux.v mux4.v mux8.v mux16.v testmux.v
	iverilog -o testmux.o -Wall mux.v mux4.v mux8.v mux16.v testmux.v

testdemux.o: demux.v demux4.v demux8.v demux16.v testdemux.v
	iverilog -o testdemux.o -Wall demux.v demux4.v demux8.v demux16.v testdemux.v

.PHONY: clean

clean:
	rm -f *.o *.vcd
