module nand(input A,B,
  output out);
  
  assign out=~(A & B); 
endmodule
