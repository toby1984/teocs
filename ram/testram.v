module top;
  reg [2:0] adr;
  reg [15:0] data;
  reg load=0;
  wire [15:0] out;
      
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
    #2
    adr = 3'b001;
    data = 16'h5678;
    #1
    load=1;
    #1 
    load=0;
    #1
    adr= 3'b000;
    #2    
    $finish;
    end   
      
  ram8 myreg(adr,data,load,out);

endmodule
