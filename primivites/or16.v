module or16(
input [15:0] a,
input [15:0] b,
output [15:0] out);  

  or2 myor0(  a[ 0],b[ 0],out[ 0] );
  or2 myor1(  a[ 1],b[ 1],out[ 1] );
  or2 myor2(  a[ 2],b[ 2],out[ 2] );
  or2 myor3(  a[ 3],b[ 3],out[ 3] );
  or2 myor4(  a[ 4],b[ 4],out[ 4] );
  or2 myor5(  a[ 5],b[ 5],out[ 5] );
  or2 myor6(  a[ 6],b[ 6],out[ 6] );
  or2 myor7(  a[ 7],b[ 7],out[ 7] );
  or2 myor8(  a[ 8],b[ 8],out[ 8] );
  or2 myor9(  a[ 9],b[ 9],out[ 9] );
  or2 myor10( a[10],b[10],out[10] );
  or2 myor11( a[11],b[11],out[11] );
  or2 myor12( a[12],b[12],out[12] );
  or2 myor13( a[13],b[13],out[13] );  
  or2 myor14( a[14],b[14],out[14] );
  or2 myor15( a[15],b[15],out[15] );  
endmodule
