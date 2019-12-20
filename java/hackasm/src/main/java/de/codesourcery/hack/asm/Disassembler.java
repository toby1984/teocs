package de.codesourcery.hack.asm;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Disassembler
{
    private static final String COMMENT_START = "#";

    private static boolean printAddress = false;
    private static boolean printHexAddress = false;

    public interface ILineReceiver {
        void receive(int address,String line);
    }

    public static void main(String[] args) throws IOException
    {
        final ILineReceiver printer = (adr, line) -> System.out.println( printAddress( adr ) + line );
        final InputStream  in = args.length == 0 ? System.in : new FileInputStream( new File(args[0] ) );
        new Disassembler().disassemble( in, printer );
    }

    private static String printAddress(int adr)
    {
        String result = null;
        if ( printAddress )
        {
            if ( printHexAddress )
            {
                result = StringUtils.leftPad( Integer.toHexString( adr ), 4, '0' );
            }
            else
            {
                result = StringUtils.leftPad( Integer.toString( adr ), 5, '0' );
            }
        }
        return result == null ? "" : result + ": ";
    }
    public void disassembleTextFile(File text, ILineReceiver receiver) throws IOException
    {
        final List<String> lines = Files.readAllLines( text.toPath() );
        int lineNo=1;
        int adr = 0;
        for ( String line : lines ) {
            line = line.trim();
            if ( line.length() != 16 ) {
                throw new IOException("Invalid line length "+line.length()+" on line "+lineNo);
            }
            int value = 0;
            for ( int i = 0 ; i < 16 ; i++ ) {
                value <<= 1;
                switch( line.charAt(i) ) {
                    case '0':
                        break;
                    case '1':
                        value |= 1;
                        break;
                    default:
                        throw new IOException("Invalid character '"+line.charAt(i)+" at column "+i+" on line "+lineNo);
                }
            }
            receiver.receive( adr, disassemble( value ) );
            lineNo++;
            adr+=1;
        }
    }

    public void disassemble(File binary, ILineReceiver receiver) throws IOException
    {
        try ( InputStream in = new FileInputStream( binary ) ) {
            disassemble( in, receiver );
        }
    }

    public void disassemble(byte[] data, int offset, ILineReceiver receiver ) throws IOException {
        final InputStream i = new InputStream()
        {
            int ptr = offset;
            @Override
            public int read() throws IOException
            {
                if ( ptr >= data.length ) {
                    return -1;
                }
                return data[ptr++] & 0xff;
            }
        };
        disassemble( i,receiver );
    }

    public void disassemble(InputStream in, ILineReceiver receiver ) throws IOException
    {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        do {
            int read = in.read( buffer );
            if ( read < 1 ) {
                break;
            }
            bos.write(buffer,0,read);
        } while(true);

        byte[] data = bos.toByteArray();
        int adr=0;
        for ( ; adr < data.length-1 ; adr+=2 )
        {
            int instruction = Assembler.bytesToWord( data,adr );
            String line = disassemble( instruction );
            receiver.receive( adr, line  );
        }

        if ( (data.length & 1) != 0 ) {
            int v = data[data.length-1] & 0xff;
            receiver.receive( adr, COMMENT_START+" trailing extra byte: "+StringUtils.leftPad(Integer.toBinaryString(v),8,'0')+" (0x"+Integer.toHexString( v ) );
        }
    }

    private String disassemble(int instruction) {

        if ( ( instruction & 1<<15) == 0 )
        {
            final int constant = (instruction & ((1<<15)-1));
            return "@"+constant+" "+COMMENT_START+" "+toBinary( constant )+" (0x"+Integer.toHexString( constant )+")";
        }
        if ( (instruction & 0b1110_0000_0000_0000) != 0b1110_0000_0000_0000 ) {
            return illegalInstruction( instruction );
        }
        final int jmpCode        = instruction & 0b111;
        final int dstCode = (instruction & 0b111_000) >>> 3;
        final int funcCode    = (instruction & 0b1111111_000_000) >>> 6;

        final String jmpString;
        switch( jmpCode ) {
            case 0b011: jmpString = ";JGE"; break;
            case 0b001: jmpString = ";JGT"; break;
            case 0b000: jmpString = ""; break;
            case 0b010: jmpString = ";JEQ"; break;
            case 0b100: jmpString = ";JLT"; break;
            case 0b101: jmpString = ";JNE"; break;
            case 0b110: jmpString = ";JLE"; break;
            case 0b111: jmpString = ";JMP"; break;
            default:
                throw new RuntimeException("Unreachable code reached");
        }

        final String dstString;
        switch( dstCode ) {
            case 0b000: dstString = ""; break;
            case 0b001: dstString = "M="; break;
            case 0b010: dstString = "D="; break;
            case 0b011: dstString = "MD="; break;
            case 0b100: dstString = "A="; break;
            case 0b101: dstString = "AM="; break;
            case 0b110: dstString = "AD="; break;
            case 0b111: dstString = "AMD="; break;
            default:
                throw new RuntimeException("Unreachable code reached");
        }
        final String funcString;
        switch( funcCode ) {
            case 0b0001111: funcString = "-D"; break;
            case 0b0111010: funcString = "-1"; break;
            case 0b0111111: funcString = "1"; break;
            case 0b0101010: funcString = "0"; break;
            case 0b0001100: funcString = "D"; break;
            case 0b0110000: funcString = "A"; break;
            case 0b0001101: funcString = "!D"; break;
            case 0b0110001: funcString = "!A"; break;
            case 0b0110011: funcString = "-A"; break;
            case 0b0011111: funcString = "D+1"; break;
            case 0b0110111: funcString = "A+1"; break;
            case 0b0001110: funcString = "D-1"; break;
            case 0b0110010: funcString = "A-1"; break;
            case 0b0000010: funcString = "D+A"; break;
            case 0b0010011: funcString = "D-A"; break;
            case 0b0000111: funcString = "A-D"; break;
            case 0b0000000: funcString = "D&A"; break;
            case 0b0010101: funcString = "D|A"; break;
            case 0b1110000: funcString = "M"; break;
            case 0b1110001: funcString = "!M"; break;
            case 0b1110011: funcString = "-M"; break;
            case 0b1110111: funcString = "M+1"; break;
            case 0b1110010: funcString = "M-1"; break;
            case 0b1000010: funcString = "D+M"; break;
            case 0b1010011: funcString = "D-M"; break;
            case 0b1000111: funcString = "M-D"; break;
            case 0b1000000: funcString = "D&M"; break;
            case 0b1010101: funcString = "D|M"; break;
            default:
                return illegalInstruction(instruction);
        }
        return dstString+funcString+jmpString;
    }

    private String illegalInstruction(int instruction) {
        return ".word 0x"+Integer.toHexString( instruction )+ " " +
               COMMENT_START + " ILLEGAL: 0b"+ toBinary(instruction)+" (0x"+Integer.toHexString( instruction )+")";
    }

    private static String toBinary(int value) {
        return StringUtils.leftPad( Integer.toBinaryString( value ), 16, '0' );
    }
}