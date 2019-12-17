module mux8way16(
  input [15:0] in0,
  input [15:0] in1,
  input [15:0] in2,
  input [15:0] in3,
  input [15:0] in4,
  input [15:0] in5,
  input [15:0] in6,
  input [15:0] in7,  
  input [2:0] sel,
  output [15:0] out);
  
  wire [15:0] tmp0;
  wire [15:0] tmp1;    
  
  mux4way16 mux0(in0,in1,in2,in3,sel[1:0],tmp0);
  mux4way16 mux1(in4,in5,in6,in7,sel[1:0],tmp1);  
  mux2way16 mux2(tmp0,tmp1,sel[2],out);
  
endmodule
