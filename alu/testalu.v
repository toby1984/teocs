module top;
    reg [15:0] x=0;
    reg [15:0] y=0;
    reg zerox=0;
    reg negx=0;
    reg zeroy=0;
    reg negy=0;
    reg functioncode=0;
    reg neg_out=0;
    
    wire [15:0] out;
    wire out_zero;
    wire out_neg;

  initial
    begin
    $dumpfile("testalu.vcd");
    $dumpvars(0,top);
    #2
    x=16'h8fff;
    y=16'h0001;
    neg_out=1;
    functioncode=1;
    #5   
    $finish;
    end   
      
  alu myalu(x,y,zerox,negx,zeroy,negy,functioncode,neg_out,out,out_zero,out_neg);

endmodule
