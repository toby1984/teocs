module ram8(
input [2:0] adr,
input [15:0] data,
input clk,
input load,
output [15:0] out);

// 8 * 2 bytes = 16 bytes
reg16 reg0 ( data , clk , ~adr[2] & ~adr[1] & ~adr[0] & load, out);
reg16 reg1 ( data , clk , ~adr[2] & ~adr[1] &  adr[0] & load, out);
reg16 reg2 ( data , clk , ~adr[2] &  adr[1] & ~adr[0] & load, out);
reg16 reg3 ( data , clk , ~adr[2] &  adr[1] &  adr[0] & load, out);
reg16 reg4 ( data , clk ,  adr[2] & ~adr[1] & ~adr[0] & load, out);
reg16 reg5 ( data , clk ,  adr[2] & ~adr[1] &  adr[0] & load, out);
reg16 reg6 ( data , clk ,  adr[2] &  adr[1] & ~adr[0] & load, out);
reg16 reg7 ( data , clk ,  adr[2] &  adr[1] &  adr[0] & load, out);

endmodule
