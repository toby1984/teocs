module top;

  reg a=0;
  reg b=0;
  
  wire out,carry;
  
  initial
    begin
    $dumpfile("testadder.vcd");
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
      
  adder myadder(a,b,carry,out);

endmodule
