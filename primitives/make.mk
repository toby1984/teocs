testprimitives.o: or.v my_and.v xor.v nand.v not.v testprimitives.v and16.v or16.v not16.v
	iverilog -o testprimitives.o or.v my_and.v xor.v nand.v not.v and16.v or16.v not16.v testprimitives.v
