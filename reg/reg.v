module reg2(
input in, 
input clk,
input load,
output reg out);

initial 
  out=0;

  always @(posedge clk and posedge load)
  begin
    out <= in;
  end

endmodule
