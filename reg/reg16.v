module reg16(
input [15:0] in,
input clk,
input load,
output [15:0] out);

reg8 reg0(in[7:0],clk,load,out[7:0]);
reg8 reg1(in[15:8],clk,load,out[15:8]);

endmodule
