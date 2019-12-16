module top;
  reg in=1;
  reg clk =0;
  
  wire out;
  
  always
    #5 clk = !clk;
       
  initial
    begin
    $dumpfile("testdff.vcd");
    $dumpvars(0,top);
    #15
    in=0;
    #15
    in=1;    
    #15
    in = 0;
    #15
    in=1;
    #15 $finish;
    end   
      
  dff mydf(in,clk,out);
endmodule
