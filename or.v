module or2(input a,input b,  output out);  

  wire tmp0;
  wire tmp1;
  
  nand2 nand0(a,a,tmp0); 
  nand2 nand1(b,b,tmp1); 
  nand2 nand2(tmp0,tmp1,out); 
endmodule
