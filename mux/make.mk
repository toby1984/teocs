all: testmux4way16.o testmux8way16.o testmux.o

testmux8way16.o: mux16.v  mux2way16.v  mux4.v  mux4way16.v  mux8way16.v mux8.v  mux.v  testmux8way16.v 
	iverilog -o testmux8way16.o -Wall mux16.v  mux2way16.v  mux8way16.v mux4.v  mux4way16.v  mux8.v  mux.v  testmux8way16.v

testmux4way16.o: mux16.v  mux2way16.v  mux4.v  mux4way16.v  mux8.v  mux.v  testmux4way16.v 
	iverilog -o testmux4way16.o -Wall mux16.v  mux2way16.v  mux4.v  mux4way16.v  mux8.v  mux.v  testmux4way16.v

testmux.o: mux.v mux4.v mux8.v mux16.v testmux.v
	iverilog -o testmux.o -Wall mux.v mux4.v mux8.v mux16.v testmux.v
