module dff( 
input in, 
input clk,
output reg out);

initial out=0;

always @ (posedge clk)
  out <= in;

endmodule
