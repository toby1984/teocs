module dff(
input clk, 
input in, 
input load, 
output reg out);

always @ (posedge load)
begin

if (load)
  out <= in;
end

endmodule
