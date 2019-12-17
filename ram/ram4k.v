module ram4k(
input [11:0] adr,
input [15:0] data,
input clk,
input load,
output [15:0] out);

// 4096 * 2 bytes = 8192 bytes
ram512 r0( adr[8:0], data, clk, ~adr[11] & ~adr[10] & ~adr[9] & load, out);
ram512 r1( adr[8:0], data, clk, ~adr[11] & ~adr[10] &  adr[9] & load, out);
ram512 r2( adr[8:0], data, clk, ~adr[11] &  adr[10] & ~adr[9] & load, out);
ram512 r3( adr[8:0], data, clk, ~adr[11] &  adr[10] &  adr[9] & load, out);
ram512 r4( adr[8:0], data, clk,  adr[11] & ~adr[10] & ~adr[9] & load, out);
ram512 r5( adr[8:0], data, clk,  adr[11] & ~adr[10] &  adr[9] & load, out);
ram512 r6( adr[8:0], data, clk,  adr[11] &  adr[10] & ~adr[9] & load, out);
ram512 r7( adr[8:0], data, clk,  adr[11] &  adr[10] &  adr[9] & load, out);

endmodule
