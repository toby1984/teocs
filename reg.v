module reg2(
input in, 
input clk,
input load,
output out);

wire dffin;

mux mymux({in,out},load,dffin);
dff mydff(dffin,clk,out);

endmodule
