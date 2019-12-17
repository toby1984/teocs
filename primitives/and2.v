module and2(input a,input b,output out);  

  wire tmp;
  
  nand2 nand0(a,b,tmp); 
  nand2 nand1(tmp,tmp,out);  
endmodule
