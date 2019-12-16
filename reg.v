module reg( 
input in, 
input clk,
input load,
output reg out);

wire muxout;
wire dffin;

mux mymux({in,out},load,dffin);
dff mydff(dffin,clk,out);

endmodule
