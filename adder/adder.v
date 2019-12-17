module adder(
  input a,
  input b,
  output carry,
  output out);
  
  xor2 myxor(a,b,out);
  and2 myand(a,b,carry);
  
endmodule
