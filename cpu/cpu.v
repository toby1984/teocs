module cpu(
input [15:0] instruction,
input [15:0] memIn,
input reset,
input clk,
output reg [15:0] memOut,
output reg writeM,
output reg [15:0] memAddress,
output reg [15:0] pc);

// registers
reg [15:0] regA;
reg [15:0] regD;
reg [15:0] result;

// misc
reg not_implemented;

always @ (posedge clk) 

    // reset
    if ( reset )
    begin
        regA <= 0;
        regD <= 0;
        result = 0;
        pc <= 0;
        memOut <= 16'bzzzzzzzzzzzzzzzz;
        memAddress <= 16'bzzzzzzzzzzzzzzzz;
        writeM <= 0;    
        not_implemented <= 0;
    end else begin

        // check whether A or C instruction
        if ( instruction[15] == 0 )
        begin
            // A instruction
            regA <= instruction & 16'b0111111111111111;   
            pc <= pc + 1;          
            
            memOut <= 16'bzzzzzzzzzzzzzzzz;
            memAddress <= 16'bzzzzzzzzzzzzzzzz;
            writeM <= 0;               
        end
        else begin
    
            // C instruction    
            
            // perform operation
            case( ( instruction & 16'b0000111111000000) >> 6 ) 
                7'b0101010: result = 0;
                7'b0111111: result = 1;
                7'b0111111: result = -1;
                7'b0001100: result = regD;
                7'b0110000: result = regA;
                7'b1110000: result = memIn;
                7'b0001101: result = ~regD;
                7'b0110001: result = ~regA;
                7'b1110001: result = ~memIn;
                7'b0001111: result = 0 - regD;
                7'b0110011: result = 0 - regA;
                7'b1110011: result = 0 - memIn;
                7'b0011111: result = regD + 1;
                7'b0110111: result = regA + 1;
                7'b1110111: result =  memIn + 1;
                7'b0001110: result = regD - 1;
                7'b0110010: result = regA - 1;
                7'b1110010: result =  memIn - 1;
                7'b0000010: result = regD + regA;
                7'b1000010: result = regD + memIn;
                7'b0010011: result = regD - regA;
                7'b1010011: result = regD - memIn;
                7'b0000111: result = regA - regD;
                7'b1000111: result = memIn - regD;
                7'b0000000: result = regD & regA;
                7'b1000000: result = regD & memIn;
                7'b0010101: result = regD | regA;
                7'b1010101: result = regD | memIn;
                default: not_implemented = 1;                 
            endcase
                            
            // store result
            case ( ( instruction & 16'b111000) >> 3 ) 
                3'b001: begin // mem[a] 
                    memOut <= result;
                    memAddress <= regA;
                    writeM <= 1;
                end
                3'b010: regD <= result; // D register
                3'b011: begin // mem[a] and D register
                    memOut <= result;
                    memAddress <= regA;
                    writeM <= 1;
                    regD <= result;
                end
                3'b100: regA <= result; // A register
                3'b101: begin; // A register and mem[a]
                    memOut <= result;
                    memAddress <= regA;
                    writeM <= 1;
                    regA <= result;
                end
                3'b110: begin // A register and D register
                    regA <= result;
                    regD <= result;
                end
                3'b111: begin
                    memOut <= result;
                    memAddress <= regA;
                    writeM <= 1;
                    regA <= result;
                    regD <= result;
                end
                default: begin
                    memOut <= 16'bzzzzzzzzzzzzzzzz;
                    memAddress <= 16'bzzzzzzzzzzzzzzzz;
                    writeM <= 0;            
                end
            endcase
            
            // handle jump instructions
            case( instruction & 3'b111 )
                3'b000: begin
                    pc <= pc + 1;
                end
                3'b001: 
                    if ( result > 0 )
                        pc <= regA;
                    else
                        pc <= pc + 1;
                3'b010:
                    if ( result == 0 )
                        pc <= regA;      
                    else
                        pc <= pc + 1;                        
                3'b011:
                    if ( result >= 0 )
                        pc <= regA;    
                    else
                        pc <= pc + 1;                        
                3'b100:
                    if ( result < 0 )
                        pc <= regA;  
                    else
                        pc <= pc + 1;                        
                3'b101: 
                    if ( result != 0 )
                        pc <= regA;         
                    else
                        pc <= pc + 1;                        
                3'b110:
                    if ( result <= 0 )
                        pc <= regA;                 
                    else
                        pc <= pc + 1;                        
                3'b111:
                    pc <= regA;                
            endcase        
        
        end // if ( instruction[15] == 0 )
        
    end // ELSE: if ( reset )        

endmodule
