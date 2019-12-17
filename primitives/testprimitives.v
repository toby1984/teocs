module top;
  reg a=0;
  reg b=0;
  
  wire notOut;
  wire orOut;
  wire andOut;
  wire nandOut;
  wire xorOut;
  
  initial
    begin
    $dumpfile("testprimitives.vcd");
    $dumpvars(0,top);
    #5
    b=0;
    a=1;
    #5
    b=1;
    a=0;    
    #5
    b=1;
    a=1;      
    #5 $finish;
    end   
      
  not2 mynot(a,notOut);  
  or2 myor(a,b,orOut);
  and2 myand(a,b,andOut);
  nand2 mynand(a,b,nandOut);
  xor2 myxor(a,b,xorOut);

endmodule
