module demux4(
   input in, 
   input [1:0] sel,
   output [3:0] out    
   );

assign out[0] = ~sel[1] & ~sel[0] & in;
assign out[1] = ~sel[1] &  sel[0] & in;
assign out[2] =  sel[1] & ~sel[0] & in;
assign out[3] =  sel[1] &  sel[0] & in;

endmodule
