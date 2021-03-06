TESTDIR=cpu
TOTEST=testcpu

MAKE_DIR   = $(PWD)
PRIM_DIR  := $(MAKE_DIR)/primitives
REG_DIR   := $(MAKE_DIR)/reg
MUX_DIR   := $(MAKE_DIR)/mux
DEMUX_DIR := $(MAKE_DIR)/demux
ADDER_DIR := $(MAKE_DIR)/adder
RAM_DIR   := $(MAKE_DIR)/ram
COUNTER_DIR   := $(MAKE_DIR)/counter
ALU_DIR   := $(MAKE_DIR)/alu
CPU_DIR   := $(MAKE_DIR)/cpu

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
	@$(MAKE) -C $(RAM_DIR) -f make.mk
	@$(MAKE) -C $(COUNTER_DIR) -f make.mk
	@$(MAKE) -C $(ALU_DIR) -f make.mk
	@$(MAKE) -C $(CPU_DIR) -f make.mk

.PHONY: clean

clean:
	rm -f $(PRIM_DIR)/*.o $(REG_DIR)/*.o $(MUX_DIR)/*.o $(DEMUX_DIR)/*.o $(ADDER_DIR)/*.o $(RAM_DIR)/*.o $(COUNTER_DIR)/*.o $(ALU_DIR)/*.o $(CPU_DIR)/*.o
	rm -f *.vcd 
