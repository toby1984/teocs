module xor2(input a,input b,output out);  

  wire tmp0,tmp1,tmp2;
  
  nand2 nand0(a,b,tmp0); 
  
  nand2 nand1(a,tmp0,tmp1); 
  nand2 nand2(tmp0,b,tmp2); 
  nand2 nand3(tmp1,tmp2,out);   
endmodule
