module top;
  reg [15:0] in;
  reg [3:0] sel;
  wire out;
       
  initial
    begin
    $dumpfile("testmux.vcd");
    $dumpvars(0,top);
    in=16'b0101010101010101;
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
      
  mux16 mymux(in,sel,out);   
endmodule
