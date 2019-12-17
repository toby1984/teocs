module fulladder(
  input a,
  input b,
  input carryin,
  output carryout,
  output out);
  
  wire tmp0;
  wire tmp1,tmp2,tmp3,tmp4;
  
  xor2 myor(a,b,tmp0);
  xor2 myor2(tmp0,carryin,out);
  
  and2 myand(a,b,tmp1);
  and2 myand2(a,carryin,tmp2);  
  and2 myand3(b,carryin,tmp3);    
  
  xor2 myor3(tmp1,tmp2,tmp4);
  xor2 myor4(tmp3,tmp4,carryout);
  
endmodule
