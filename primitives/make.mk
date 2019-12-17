testprimitives.o: or2.v and2.v xor.v nand2.v not.v testprimitives.v and16.v or16.v not16.v
	iverilog -o testprimitives.o or2.v and2.v xor.v nand2.v not.v and16.v or16.v not16.v testprimitives.v
