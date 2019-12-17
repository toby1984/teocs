module top;
  reg [2:0] adr;
  reg [15:0] data;
  reg load=0;
  reg clk;
  wire [15:0] out;
  
  always
    #1 clk = ~clk;
    
    
    //
// module ram8(
// input [2:0] adr,
// input [15:0] data,
// input clk,
// input load,
// output [15:0] out);    
    //
  initial
    begin
    $dumpfile("testram.vcd");
    $dumpvars(0,top);
    adr = 3'b000;
    data = 16'h1234;
    #2
    load=1;    
    #2
    load=0;
    #5
    $finish;
    end   
      
  ram8 myreg(adr,data,clk,load,out);

endmodule
