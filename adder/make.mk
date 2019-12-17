all: adder.o fulladder.o

adder.o: adder.v testadder.v
	iverilog -o adder.o -I../primitives -Wall adder.v testadder.v ../primitives/and2.v ../primitives/or2.v ../primitives/nand2.v

fulladder.o: adder.v fulladder.v testfulladder.v
	iverilog -o adder.o -Wall -I../primitives adder.v fulladder.v testfulladder.v ../primitives/and2.v ../primitives/or2.v ../primitives/nand2.v
