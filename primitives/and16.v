module and16(
input [15:0] a,
input [15:0] b,
output [15:0] out);  

  and2 myand0(  a[ 0],b[ 0],out[ 0] );
  and2 myand1(  a[ 1],b[ 1],out[ 1] );
  and2 myand2(  a[ 2],b[ 2],out[ 2] );
  and2 myand3(  a[ 3],b[ 3],out[ 3] );
  and2 myand4(  a[ 4],b[ 4],out[ 4] );
  and2 myand5(  a[ 5],b[ 5],out[ 5] );
  and2 myand6(  a[ 6],b[ 6],out[ 6] );
  and2 myand7(  a[ 7],b[ 7],out[ 7] );
  and2 myand8(  a[ 8],b[ 8],out[ 8] );
  and2 myand9(  a[ 9],b[ 9],out[ 9] );
  and2 myand10( a[10],b[10],out[10] );
  and2 myand11( a[11],b[11],out[11] );
  and2 myand12( a[12],b[12],out[12] );
  and2 myand13( a[13],b[13],out[13] );  
  and2 myand14( a[14],b[14],out[14] );
  and2 myand15( a[15],b[15],out[15] );  
endmodule
