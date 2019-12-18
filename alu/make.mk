alu.o: alu.v testalu.v
	iverilog -o testalu.o alu.v testalu.v
