module ram16k(
input [13:0] adr,
input [15:0] data,
input clk,
input load,
output [15:0] out);

// 4 * 8192 bytes = 32767 bytes
ram4k r0( adr[11:0], data, clk, ~adr[13] & ~adr[12] & load, out);
ram4k r1( adr[11:0], data, clk, ~adr[13] &  adr[12] & load, out);
ram4k r2( adr[11:0], data, clk,  adr[13] & ~adr[12] & load, out);
ram4k r3( adr[11:0], data, clk,  adr[13] &  adr[12] & load, out);

endmodule
