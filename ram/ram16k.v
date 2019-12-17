module ram16k (
input [13:0] adr, 
input [15:0] in, 
input load,
output reg [15:0] out);

reg [15:0] Mem [0:16383];

initial
begin
    for (int k = 0; k < 16384; k = k + 1)
    begin
        Mem[k] = 0;
    end
end
  
always @ (*)
begin
    if ( load ) begin
      Mem[adr] = in;
    end
    
    out <= Mem[adr];
end

endmodule
