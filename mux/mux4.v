module mux4(
  input [3:0] in,
  input [1:0] sel,
  output out);
  
  wire [1:0] tmp;
  
  mux mux0(in[1:0],sel[0],tmp[0]);
  mux mux1(in[3:2],sel[0],tmp[1]);  
  mux mux2(.in(tmp),.sel(sel[1]),.out(out));
endmodule
