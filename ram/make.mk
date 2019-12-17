testram.o: ram16k.v  ram4k.v  ram512.v  ram64.v  ram8.v 
	iverilog -o testram.o ram4k.v  ram512.v  ram64.v  ram8.v testram.v ../reg/reg16.v ../reg/reg8.v ../reg/reg.v ../reg/dff.v ../mux/mux.v 
