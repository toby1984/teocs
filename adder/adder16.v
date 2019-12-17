module adder16(
  input [15:0] a,
  input [15:0] b,  
  output [15:0] out);
  
  wire carry0,carry1,carry2,carry3,carry4,carry5,carry6,carry7,carry8,carry9,carry10,carry11,carry12,carry13,carry14,carry15;

  // a,b,carryin,carryout,out
  fulladder adder0 ( a[ 0], b[ 0], 1'b0    , carry0  , out[ 0] );
  fulladder adder1 ( a[ 1], b[ 1], carry0  , carry1  , out[ 1] );
  fulladder adder2 ( a[ 2], b[ 2], carry1  , carry2  , out[ 2] );  
  fulladder adder3 ( a[ 3], b[ 3], carry2  , carry3  , out[ 3] );    
  fulladder adder4 ( a[ 4], b[ 4], carry3  , carry4  , out[ 4] );    
  fulladder adder5 ( a[ 5], b[ 5], carry4  , carry5  , out[ 5] );    
  fulladder adder6 ( a[ 6], b[ 6], carry5  , carry6  , out[ 6] );    
  fulladder adder7 ( a[ 7], b[ 7], carry6  , carry7  , out[ 7] );    
  fulladder adder8 ( a[ 8], b[ 8], carry7  , carry8  , out[ 8] );    
  fulladder adder9 ( a[ 9], b[ 9], carry8  , carry9  , out[ 9] );    
  fulladder adder10( a[10], b[10], carry9  , carry10 , out[10] );    
  fulladder adder11( a[11], b[11], carry10 , carry11 , out[11] );    
  fulladder adder12( a[12], b[12], carry11 , carry12 , out[12] );    
  fulladder adder13( a[13], b[13], carry12 , carry13 , out[13] );    
  fulladder adder14( a[14], b[14], carry13 , carry14 , out[14] );    
  fulladder adder15( a[15], b[15], carry14 , carry15 , out[15] );      
    
endmodule
