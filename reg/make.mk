testreg.o: dff.v reg.v reg8.v reg16.v testreg.v
	iverilog -o testreg.o dff.v reg.v reg8.v ../mux/mux.v reg16.v testreg.v
