cpu.o: cpu.v testcpu.v
	iverilog -o testcpu.o cpu.v testcpu.v
