module reg8( 
input [7:0] in, 
input clk,
input load,
output [7:0] out);

reg2 reg0(in[0],clk,load,out[0]);
reg2 reg1(in[1],clk,load,out[1]);
reg2 reg2(in[2],clk,load,out[2]);
reg2 reg3(in[3],clk,load,out[3]);
reg2 reg4(in[4],clk,load,out[4]);
reg2 reg5(in[5],clk,load,out[5]);
reg2 reg6(in[6],clk,load,out[6]);
reg2 reg7(in[7],clk,load,out[7]);

endmodule
