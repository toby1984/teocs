module reg8( 
input [7:0] in, 
input clk,
input load,
output reg [7:0] out);

wire muxout;
wire dffin;

mux mymux({in,out},load,dffin);
dff mydff(dffin,clk,out);

endmodule
