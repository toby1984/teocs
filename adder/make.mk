all: adder.o fulladder.o

adder.o: adder.v testadder.v
	iverilog -o adder.o -Wall adder.v testadder.v 

fulladder.o: adder.v fulladder.v testfulladder.v
	iverilog -o adder.o -Wall adder.v fulladder.v testfulladder.v 
