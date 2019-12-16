module top;
  reg in=0;
  reg clk =0;
  
  wire out;
  
  always
    #1 clk = !clk;
       
  initial
    begin
    $dumpfile("testdff.vcd");
    $dumpvars(0,top);
    #2    
    in=~in;
    #2    
    in=~in;
    #2    
    in=~in;
    #2    
    in=~in;    
    #2 $finish;
    end   
      
  dff mydf(in,clk,out);
endmodule
