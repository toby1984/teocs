module reg2(
input in, 
input clk,
input load,
output reg out);

initial 
  out=0;

  always @(posedge clk or posedge load)
  begin
    if ( load )
      out <= in; 
  end

endmodule
