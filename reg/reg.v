module reg2(
input in, 
input clk,
input load,
output reg out);

initial 
  out=0;

  always @(load and posedge clk)
  begin
    out <= in;
  end

endmodule
