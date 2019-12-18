module top;
    reg [15:0] instruction=0;
    reg [15:0] memIn=0;
    reg reset=0;
    reg clk=0;
    
    wire [15:0] memOut;
    wire writeM;
    wire [15:0] memAddress;
    wire [15:0] pc;
      
  always
    #1 clk = !clk;
    
  initial
    begin
    $dumpfile("testcpu.vcd");
    $dumpvars(0,top);
    reset = 1;
    #5
    reset=0;    
    #10
    $finish;
    end   
      
  cpu mycpu(instruction,memIn,reset,clk,memOut,writeM,memAddress,pc);

endmodule
