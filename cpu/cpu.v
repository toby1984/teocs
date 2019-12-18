module cpu(
input [15:0] instruction,
input [15:0] inM,
input reset,
input clk,
output reg [15:0] outM,
output reg writeM,
output reg [15:0] addressM,
output reg [15:0] pc);

// registers
reg [15:0] aOut;
reg [15:0] dOut;
reg [15:0] result;
reg [15:0] pc;

// misc
reg not_implemented;

always @ (posedge clk or posedge reset) 
    // check whether A or C instruction
    if ( instruction[15] == 0 )
    begin
        // A instruction
        aOut = instruction & 16'b0111111111111111;   
    end
    else begin
  
        // C instruction    
        
        // perform operation
        case( ( instruction & 16'b0000111111000000) >> 6 ) 
            7'b0101010: result = 0;
            7'b0111111: result = 1;
            7'b0111111: result = -1;
            7'b0001100: result = dOut;
            7'b0110000: result = aOut;
            7'b1110000: result = inM;
            7'b0001101: result = ~dOut;
            7'b0110001: result = ~aOut;
            7'b1110001: result = ~inM;
            7'b0001111: result = 0 - dOut;
            7'b0110011: result = 0 - aOut;
            7'b1110011: result = 0 - inM;
            7'b0011111: result = dOut + 1;
            7'b0110111: result = aOut + 1;
            7'b1110111: result =  inM + 1;
            7'b0001110: result = dOut - 1;
            7'b0110010: result = aOut - 1;
            7'b1110010: result =  inM - 1;
            7'b0000010: result = dOut + aOut;
            7'b1000010: result = dOut + inM;
            7'b0010011: result = dOut - aOut;
            7'b1010011: result = dOut - inM;
            7'b0000111: result = aOut - dOut;
            7'b1000111: result = inM - dOut;
            7'b0000000: result = dOut & aOut;
            7'b1000000: result = dOut & inM;
            7'b0010101: result = dOut | aOut;
            7'b1010101: result = dOut | inM;
            default: not_implemented = 1;                 
        endcase
                        
        // reset
        if ( reset )
        begin
            aOut = 0;
            dOut = 0;
            result = 0;
            pc = 0;
            outM = 16'bzzzzzzzzzzzzzzzz;
            addressM = 16'bzzzzzzzzzzzzzzzz;
            writeM=0;              
        end else begin
            
            // store result
            case ( ( instruction & 16'b111000) >> 3 ) 
                3'b001: begin // mem[a] 
                    outM = result;
                    addressM = aOut;
                    writeM=1;
                end
                3'b010: dOut = result; // D register
                3'b011: begin // mem[a] and D register
                    outM = result;
                    addressM = aOut;
                    writeM=1;
                    dOut = result;
                end
                3'b100: aOut = result; // A register
                3'b101: begin; // A register and mem[a]
                    outM = result;
                    addressM = aOut;
                    writeM=1;
                    aOut = result;
                end;
                3'b110: begin // A register and D register
                    aOut = result;
                    dOut = result;
                end
                3'b111:
                    outM = result;
                    addressM = aOut;
                    writeM=1;
                    aOut = result;
                    dOut = result;
                default: 
                    outM = 16'bzzzzzzzzzzzzzzzz;
                    addressM = 16'bzzzzzzzzzzzzzzzz;
                    writeM=0;            
            endcase
            
            // handle jump instructions
            case( instruction & 3'b111 ) 
                3'b000: begin
                    pc = pc + 1;
                end
                3'b001: 
                    if ( result > 0 )
                        pc = aOut;
                3'b010:
                    if ( result == 0 )
                        pc = aOut;                
                3'b011:
                    if ( result >= 0 )
                        pc = aOut;                  
                3'b100:
                    if ( result < 0 )
                        pc = aOut;  
                3'b101: 
                    if ( result != 0 )
                        pc = aOut;                 
                3'b110:
                    if ( result <= 0 )
                        pc = aOut;                 
                3'b111:
                    pc = aOut;                
            endcase
            
        end
        
    end

endmodule
