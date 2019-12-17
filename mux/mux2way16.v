module mux2way16(
  input [15:0] in0,
  input [15:0] in1,
  input sel,
  output [15:0] out);
  
  mux mux0( {in1[0],in0[0]},sel,out[0] );
  mux mux1( {in1[1],in0[1]},sel,out[1] );
  mux mux2( {in1[2],in0[2]},sel,out[2] );
  mux mux3( {in1[3],in0[3]},sel,out[3] );
  mux mux4( {in1[4],in0[4]},sel,out[4] );
  mux mux5( {in1[5],in0[5]},sel,out[5] );
  mux mux6( {in1[6],in0[6]},sel,out[6] );
  mux mux7( {in1[7],in0[7]},sel,out[7] );  

  mux mux8( {in1[8],in0[8]},sel,out[8] );
  mux mux9( {in1[9],in0[9]},sel,out[9] );
  mux mux10( {in1[10],in0[10]},sel,out[10] );
  mux mux11( {in1[11],in0[11]},sel,out[11] );
  mux mux12( {in1[12],in0[12]},sel,out[12] );
  mux mux13( {in1[13],in0[13]},sel,out[13] );
  mux mux14( {in1[14],in0[14]},sel,out[14] );
  mux mux15( {in1[15],in0[15]},sel,out[15] );    
  
endmodule
