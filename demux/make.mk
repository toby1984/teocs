testdemux.o: demux16.v  demux4.v  demux8.v  demux.v  make.mk  testdemux.v
	$(CC) -o testdemux.o -Wall demux16.v  demux4.v  demux8.v  demux.v  make.mk  testdemux.v 
