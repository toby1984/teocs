module ram4k (
input [11:0] adr, 
input [15:0] in, 
input load,
output reg [15:0] out);

reg [15:0] Mem [0:4095];

initial
begin
    for (int k = 0; k < 4096; k = k + 1)
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
