module top;
  reg [15:0] in=0;
  reg reset=0;  
  reg inc=0;
  reg load=0;
  
  wire [15:0] out;
  reg clk=0;
  
  always
    #1 clk = ~clk;
      
  initial
    begin
    $dumpfile("testcounter.vcd");
    $dumpvars(0,top);
    inc = 1;
    #5
    inc = 0;
    #5
    #2
    reset=1;    
    #1
    reset=0;    
    #4
    inc=1;
    #5
    in = 16'h10;
    load = 1;
    #1
    load = 0;
    #5 
    inc = 0;
    #5
    $finish;
    end   
      
  counter16 counter(in,clk,inc,load,reset,out);

endmodule
