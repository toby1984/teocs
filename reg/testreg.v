module top;
  reg [15:0] data;
  reg load=0;
  reg clk=0;
  wire [15:0] out;
  
  always
    #1 clk = ~clk;
    
  initial
    begin
    $dumpfile("testreg.vcd");
    $dumpvars(0,top);
    data = 15'h5555;
    #5
    load=1;    
    #1
    load=0;
    data = 15'h3333;
    #5
    load=1;
    #15
    $finish;
    end   
      
  reg16 myreg(data,clk,load,out);

endmodule
