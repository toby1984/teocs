module demux(
  input in,sel,
  output out0,out1
);
always(@in or sel)
begin
  case(sel)
    2'b0: 
    begin
      out0 = in;
      out1 = 0;
    end
    2'b1:
    begin
      out0 = 0;
      out1 = 1;
    end    
end
endmodule
