module reg2(
input in, 
input clk,
input load,
output out);

  wire muxout;
  
  mux mymux({in,out},load,muxout);
  dff mydff(muxout,clk,out);

endmodule
