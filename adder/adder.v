module adder(
  input a,
  input b,
  output carry,
  output out);
  
  or2 myor(a,b,out);
  and2 myand(a,b,carry);
  
endmodule
