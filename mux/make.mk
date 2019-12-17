all: testmux4way16.o testmux.o

testmux4way16.o: mux16.v  mux2way16.v  mux4.v  mux4way16.v  mux8.v  mux.v  testmux4way16.v 
	$(CC) -o testmux4way16.o -Wall mux16.v  mux2way16.v  mux4.v  mux4way16.v  mux8.v  mux.v  testmux4way16.v

testmux.o: mux.v mux4.v mux8.v mux16.v testmux.v
	$(CC) -o testmux.o -Wall mux.v mux4.v mux8.v mux16.v testmux.v
