module top;
  reg [15:0] in0=1;
  reg [15:0] in1=2;
  reg [15:0] in2=3;
  reg [15:0] in3=4;
  reg [1:0] sel=0;
  
  wire [15:0] out;
  
  initial
    begin
    $dumpfile("testmux4way16.vcd");
    $dumpvars(0,top);
    #15
    sel=2'b01;
    #15
    sel=2'b10;
    #15
    sel=2'b11;
    #15 $finish;
    end   
      
  mux4way16 mux(in0,in1,in2,in3,sel,out);

endmodule
