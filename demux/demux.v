module demux(
   input in, 
   input sel,
   output [1:0] out    
   );

assign out[0] = ~sel & in;
assign out[1] =  sel & in;

endmodule
