module dff( 
input in, 
input clk,
output reg out);

always @ (posedge clk)
  out <= in;

endmodule
