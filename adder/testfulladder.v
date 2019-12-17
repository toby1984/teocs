module top;

  reg a=0;
  reg b=0;
  reg carryin=0;
  
  wire out,carryout;
  
  initial
    begin
    $dumpfile("testfulladder.vcd");
    $dumpvars(0,top);
    #5
    carryin=0;
    b=0;
    a=1;
    #5
    carryin=0;
    b=1;
    a=0;    
    #5
    carryin=0;
    b=1;
    a=1;    
    
    //
    #5
    carryin=1;
    b=0;
    a=0;    
    #5
    carryin=1;
    b=0;
    a=1;    
    #5
    carryin=1;
    b=1;
    a=0;    
    #5
    carryin=1;
    b=1;
    a=1;    
    
    #5 $finish;
    end   
      
  fulladder myadder(a,b,carryin,carryout,out);

endmodule
