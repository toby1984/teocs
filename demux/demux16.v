module demux16(
   input in, 
   input [3:0] sel,
   output [15:0] out    
   );

assign out[0] = ~sel[3] & ~sel[2] & ~sel[1] & ~sel[0] & in;
assign out[1] = ~sel[3] & ~sel[2] & ~sel[1] &  sel[0] & in;
assign out[2] = ~sel[3] & ~sel[2] &  sel[1] & ~sel[0] & in;
assign out[3] = ~sel[3] & ~sel[2] &  sel[1] &  sel[0] & in;
assign out[4] = ~sel[3] &  sel[2] & ~sel[1] & ~sel[0] & in;
assign out[5] = ~sel[3] &  sel[2] & ~sel[1] &  sel[0] & in;
assign out[6] = ~sel[3] &  sel[2] &  sel[1] & ~sel[0] & in;
assign out[7] = ~sel[3] &  sel[2] &  sel[1] &  sel[0] & in;

assign out[8]  = sel[3] & ~sel[2] & ~sel[1] & ~sel[0] & in;
assign out[9]  = sel[3] & ~sel[2] & ~sel[1] &  sel[0] & in;
assign out[10] = sel[3] & ~sel[2] &  sel[1] & ~sel[0] & in;
assign out[11] = sel[3] & ~sel[2] &  sel[1] &  sel[0] & in;
assign out[12] = sel[3] &  sel[2] & ~sel[1] & ~sel[0] & in;
assign out[13] = sel[3] &  sel[2] & ~sel[1] &  sel[0] & in;
assign out[14] = sel[3] &  sel[2] &  sel[1] & ~sel[0] & in;
assign out[15] = sel[3] &  sel[2] &  sel[1] &  sel[0] & in;

endmodule
