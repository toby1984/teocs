module alu(
input [15:0] x,
input [15:0] y,
input zerox,
input negx,
input zeroy,
input negy,
input functioncode,
input neg_out,
output reg [15:0] out,
output reg out_zero,
output reg out_neg
);

wire [15:0] in0;
wire [15:0] in1;

    assign in0 = zerox ? 0 : negx ? ~x : x; 
    assign in1 = zeroy ? 0 : negy ? ~y : y;
    
always @ (*)
begin

    if (functioncode) // 1 = ADD, 0 = AND
      out = in0 + in1;
    else
      out = in0 & in1;
      
    if ( neg_out ) 
      out = ~out;
      
    out_zero = out ? 0 : 1;
    out_neg = out[15];      
end

endmodule
