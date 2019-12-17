module ram64(
input [5:0] adr,
input [15:0] data,
input clk,
input load,
output [15:0] out);

// 64*2 bytes = 128 bytes
ram8 r0( adr[2:0], data, clk, ~adr[5] & ~adr[4] & ~adr[3] & load, out);
ram8 r1( adr[2:0], data, clk, ~adr[5] & ~adr[4] &  adr[3] & load, out);
ram8 r2( adr[2:0], data, clk, ~adr[5] &  adr[4] & ~adr[3] & load, out);
ram8 r3( adr[2:0], data, clk, ~adr[5] &  adr[4] &  adr[3] & load, out);
ram8 r4( adr[2:0], data, clk,  adr[5] & ~adr[4] & ~adr[3] & load, out);
ram8 r5( adr[2:0], data, clk,  adr[5] & ~adr[4] &  adr[3] & load, out);
ram8 r6( adr[2:0], data, clk,  adr[5] &  adr[4] & ~adr[3] & load, out);
ram8 r7( adr[2:0], data, clk,  adr[5] &  adr[4] &  adr[3] & load, out);

endmodule
