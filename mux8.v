module mux8(
  input [7:0] in,
  input [2:0] sel,
  output out);
  
  wire [1:0] tmp;
  
  mux4 mux0(in[3:0],sel[1:0],tmp[0]);
  mux4 mux1(in[7:4],sel[1:0],tmp[1]);  
  mux mux(.in(tmp),.sel(sel[2]),.out(out));
endmodule
