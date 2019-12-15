module mux16(
  input [15:0] in,
  input [3:0] sel,
  output out);
  
  wire [1:0] tmp;
  
  mux8 mux0(in[7:0],sel[2:0],tmp[0]);
  mux8 mux1(in[15:8],sel[2:0],tmp[1]);  
  mux mux(.in(tmp),.sel(sel[3]),.out(out));
endmodule
