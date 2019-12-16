module top;
  reg in=1;
  reg load = 0;  
  reg clk =0;
  
  wire out;
  
  always
    #5 clk = !clk;
       
  initial
    begin
    $dumpfile("testdff.vcd");
    $dumpvars(0,top);
    #15
    load=1;
    #15
    load=0;    
    #15
    in = 0;
    #15
    load=1;
    #15
    load=0;
    #15 $finish;
    end   
      
      // clk,in,load,out
  dff mydf(clk,in,load,out);
endmodule
