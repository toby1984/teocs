testcounter.o: counter16.v testcounter.v
	iverilog -g2012 -o testcounter.o counter16.v testcounter.v ../adder/adder16.v ../adder/fulladder.v ../primitives/xor.v ../primitives/and2.v ../primitives/nand2.v
