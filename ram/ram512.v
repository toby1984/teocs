module ram512(
input [8:0] adr,
input [15:0] data,
input clk,
input load,
output [15:0] out);

// 512 * 2 bytes = 1024 bytes
ram64 r0( adr[5:0], data, clk, ~adr[8] & ~adr[7] & ~adr[6] & load, out);
ram64 r1( adr[5:0], data, clk, ~adr[8] & ~adr[7] &  adr[6] & load, out);
ram64 r2( adr[5:0], data, clk, ~adr[8] &  adr[7] & ~adr[6] & load, out);
ram64 r3( adr[5:0], data, clk, ~adr[8] &  adr[7] &  adr[6] & load, out);
ram64 r4( adr[5:0], data, clk,  adr[8] & ~adr[7] & ~adr[6] & load, out);
ram64 r5( adr[5:0], data, clk,  adr[8] & ~adr[7] &  adr[6] & load, out);
ram64 r6( adr[5:0], data, clk,  adr[8] &  adr[7] & ~adr[6] & load, out);
ram64 r7( adr[5:0], data, clk,  adr[8] &  adr[7] &  adr[6] & load, out);

endmodule
