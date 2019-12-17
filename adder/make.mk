all: adder.o fulladder.o adder16.o

adder16.o: adder16.v testadder16.v adder.v fulladder.v 
	iverilog -o testadder16.o -I../primitives -Wall adder.v adder16.v fulladder.v testadder16.v ../primitives/and2.v ../primitives/xor.v ../primitives/nand2.v

adder.o: adder.v testadder.v 
	iverilog -o testadder.o -I../primitives -Wall adder.v testadder.v ../primitives/and2.v ../primitives/xor.v ../primitives/nand2.v 

fulladder.o: adder.v fulladder.v testfulladder.v
	iverilog -o testfulladder.o -Wall -I../primitives adder.v fulladder.v testfulladder.v ../primitives/and2.v ../primitives/xor.v ../primitives/nand2.v
