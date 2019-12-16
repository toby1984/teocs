module mux4way16(
  input [15:0] in0,
  input [15:0] in1,
  input [15:0] in2,
  input [15:0] in3,
  input [1:0] sel,
  output [15:0] out);
  
  wire [15:0] tmp0;
  wire [15:0] tmp1;  
  
  mux2way16 mux0(in0,in1,sel[0],tmp0);
  mux2way16 mux1(in2,in3,sel[0] & sel[1],tmp1);  
  mux2way16 mux2(tmp0,tmp1,sel[1],out);
  
endmodule
