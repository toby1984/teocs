TESTDIR=adder
TOTEST=testadder

MAKE_DIR   = $(PWD)
PRIM_DIR  := $(MAKE_DIR)/primitives
REG_DIR   := $(MAKE_DIR)/reg
MUX_DIR   := $(MAKE_DIR)/mux
DEMUX_DIR := $(MAKE_DIR)/demux
ADDER_DIR := $(MAKE_DIR)/adder

INC_SRCH_PATH := 
INC_SRCH_PATH += -I$(PRIM_DIR)
INC_SRCH_PATH += -I$(REG_DIR) 
INC_SRCH_PATH += -I$(MUX_DIR)
INC_SRCH_PATH += -I$(DEMUX_DIR)

test: all
	vvp $(TESTDIR)/$(TOTEST).o
	gtkwave $(TOTEST).vcd

all: clean
	@$(MAKE) -C $(PRIM_DIR) -f make.mk
	@$(MAKE) -C $(REG_DIR) -f make.mk
	@$(MAKE) -C $(MUX_DIR) -f make.mk
	@$(MAKE) -C $(DEMUX_DIR) -f make.mk
	@$(MAKE) -C $(ADDER_DIR) -f make.mk

.PHONY: clean

clean:
	rm -f $(PRIM_DIR)/*.o $(REG_DIR)/*.o $(MUX_DIR)/*.o $(DEMUX_DIR)/*.o $(ADDER_DIR)/*.o
	rm -f $(PRIM_DIR)/*.vcd $(REG_DIR)/*.vcd $(MUX_DIR)/*.vcd $(DEMUX_DIR)/*.vcd $(ADDER_DIR)*.vcd
