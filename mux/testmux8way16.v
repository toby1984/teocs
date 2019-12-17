module top;
  reg [15:0] in0=1;
  reg [15:0] in1=2;
  reg [15:0] in2=3;
  reg [15:0] in3=4;
  reg [15:0] in4=5;
  reg [15:0] in5=6;
  reg [15:0] in6=7;
  reg [15:0] in7=8;  
  reg [2:0] sel=0;
  
  wire [15:0] out;
  
  initial
    begin
    $dumpfile("testmux8way16.vcd");
    $dumpvars(0,top);
    #15
    sel=3'b001;
    #15
    sel=3'b010;
    #15
    sel=3'b011;
    #15
    sel=3'b100;   
    #15
    sel=3'b101;
    #15
    sel=3'b110;
    #15
    sel=3'b111;        
    #15 $finish;
    end   
      
  mux8way16 mux(in0,in1,in2,in3,in4,in5,in6,in7,sel,out);

endmodule
