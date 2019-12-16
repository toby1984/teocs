module nand2(input A,B,
  output out);
  
  assign out=~(A & B); 
endmodule
