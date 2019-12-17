module top;

  reg [15:0] a=0;
  reg [15:0] b=0;  
  wire [15:0] out;
  
  initial
    begin
    $dumpfile("testadder16.vcd");
    $dumpvars(0,top);
    a = 16'hffff;
    b = 16'h0001;
    #5 $finish;
    end   
      
  adder16 myadder(a,b,out);

endmodule
