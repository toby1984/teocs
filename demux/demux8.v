module demux8(
   input in, 
   input [2:0] sel,
   output [7:0] out    
   );

assign out[0] = ~sel[2] & ~sel[1] & ~sel[0] & in;
assign out[1] = ~sel[2] & ~sel[1] &  sel[0] & in;
assign out[2] = ~sel[2] &  sel[1] & ~sel[0] & in;
assign out[3] = ~sel[2] &  sel[1] &  sel[0] & in;

assign out[4] =  sel[2] & ~sel[1] & ~sel[0] & in;
assign out[5] =  sel[2] & ~sel[1] &  sel[0] & in;
assign out[6] =  sel[2] &  sel[1] & ~sel[0] & in;
assign out[7] =  sel[2] &  sel[1] &  sel[0] & in;

endmodule
