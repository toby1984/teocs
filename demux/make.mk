testdemux.o: demux16.v  demux4.v  demux8.v  demux.v  make.mk  testdemux.v
	iverilog -o testdemux.o -Wall demux16.v  demux4.v  demux8.v  demux.v  testdemux.v
