module counter16(
input [15:0] in,
input clk,
input inc,
input load,
input reset,
output reg [15:0] out);

initial
  out = 0;
  
always @ (negedge clk)
begin
    if ( reset ) 
      out <= 0;
    else if ( load ) 
      out <= in;
    else if ( inc ) 
      out <= out + 1;          
end
endmodule
