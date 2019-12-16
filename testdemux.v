module top;
  reg in=1;
  reg [3:0] sel;
  wire [15:0] out;
  reg clk =0;
  
  always
    #5 clk = !clk;
       
  initial
    begin
    $dumpfile("testdemux.vcd");
    $dumpvars(0,top);
    sel=0;
    #5 
    sel=1;
    #5
    sel=2;    
    #5
    sel=3;      
    #5
    sel=4;
    #5 
    sel=5;
    #5
    sel=6;    
    #5
    sel=7; 
    #5
    sel=8;
    #5
    sel=9;    
    #5
    sel=10;      
    #5
    sel=11;
    #5 
    sel=12;
    #5
    sel=13;    
    #5
    sel=14;     
    #5
    sel=15;  
    #5 $finish;
    end   
      
  demux16 mydemux(in,sel,out);   
endmodule
