package de.codesourcery.hack.asm;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AssemblerTest
{
    private Assembler asm;

    @Before
    public void setup() {
        asm = new Assembler();
    }

    @Test
    public void testGenWord()
    {
        assertInsn( 0x1234, asm.assemble( ".word 0x1234" ) );
    }

    @Test
    public void assembleAInstruction()
    {
        final int expected = 0b0000_0000_0000_0100_0000;

        assertInsn( expected, asm.assemble( "@64" ) );
        assertInsn( expected, asm.assemble( "@0b1000000" ) );
        assertInsn( expected, asm.assemble( "@0x40" ) );
    }

    @Test
    public void testJumpInstructions() {

        // test jump instructions
        //                     iXX_acccccc_ddd_jjj
        assertInsn( 0b111_0101010_000_011, asm.assemble( "0;JGE" ) );
        assertInsn( 0b111_0101010_000_001, asm.assemble( "0;JGT" ) );
        assertInsn( 0b111_0101010_000_000, asm.assemble( "0" ) );
        assertInsn( 0b111_0101010_000_010, asm.assemble( "0;JEQ" ) );
        assertInsn( 0b111_0101010_000_100, asm.assemble( "0;JLT" ) );
        assertInsn( 0b111_0101010_000_101, asm.assemble( "0;JNE" ) );
        assertInsn( 0b111_0101010_000_110, asm.assemble( "0;JLE" ) );
        assertInsn( 0b111_0101010_000_111, asm.assemble( "0;JMP" ) );
    }

    @Test
    public void testDestinations() {

        // test jump instructions
        //                     iXX_acccccc_ddd_jjj
        assertInsn( 0b111_0101010_000_000, asm.assemble( "0" ) );
        assertInsn( 0b111_0101010_001_000, asm.assemble( "M=0" ) );
        assertInsn( 0b111_0101010_010_000, asm.assemble( "D=0" ) );
        assertInsn( 0b111_0101010_011_000, asm.assemble( "MD=0" ) );
        assertInsn( 0b111_0101010_100_000, asm.assemble( "A=0" ) );
        assertInsn( 0b111_0101010_101_000, asm.assemble( "AM=0" ) );
        assertInsn( 0b111_0101010_110_000, asm.assemble( "AD=0" ) );
        assertInsn( 0b111_0101010_111_000, asm.assemble( "AMD=0" ) );
    }

    @Test
    public void testMacroExpansion1() {

        assertInsn( 0b111_0001111_000_000, asm.assemble( ".macro test(x) = -x\n" +
                                                             "test(D)" ) );
    }

    @Test
    public void testMacroExpansion2() {

        assertInsn( 0b111_0101010_100_000, asm.assemble( ".macro test(src,dst) = dst=src\n" +
                                                             "test(0,A)" ) );
    }

    @Test
    public void testComputations() {

        // a == 0
        //                     iXX_acccccc_ddd_jjj
        assertInsn( 0b111_0001111_000_000, asm.assemble( "-D" ) );

        assertInsn( 0b111_0111010_000_000, asm.assemble( "-1" ) );
        assertInsn( 0b111_0111111_000_000, asm.assemble( "1" ) );
        assertInsn( 0b111_0101010_000_000, asm.assemble( "0" ) );
        assertInsn( 0b111_0001100_000_000, asm.assemble( "D" ) );
        assertInsn( 0b111_0110000_000_000, asm.assemble( "A" ) );
        assertInsn( 0b111_0001101_000_000, asm.assemble( "!D" ) );
        assertInsn( 0b111_0110001_000_000, asm.assemble( "!A" ) );
        assertInsn( 0b111_0110011_000_000, asm.assemble( "-A" ) );
        assertInsn( 0b111_0011111_000_000, asm.assemble( "D+1" ) );
        assertInsn( 0b111_0110111_000_000, asm.assemble( "A+1" ) );
        assertInsn( 0b111_0001110_000_000, asm.assemble( "D-1" ) );
        assertInsn( 0b111_0110010_000_000, asm.assemble( "A-1" ) );
        assertInsn( 0b111_0000010_000_000, asm.assemble( "D+A" ) );
        assertInsn( 0b111_0010011_000_000, asm.assemble( "D-A" ) );
        assertInsn( 0b111_0000111_000_000, asm.assemble( "A-D" ) );
        assertInsn( 0b111_0000000_000_000, asm.assemble( "D&A" ) );
        assertInsn( 0b111_0010101_000_000, asm.assemble( "D|A" ) );

        // a ==
        //                     iXX_acccccc_ddd_jjj
        assertInsn( 0b111_1110000_000_000, asm.assemble( "M" ) );
        assertInsn( 0b111_1110001_000_000, asm.assemble( "!M" ) );
        assertInsn( 0b111_1110011_000_000, asm.assemble( "-M" ) );
        assertInsn( 0b111_1110111_000_000, asm.assemble( "M+1" ) );
        assertInsn( 0b111_1110010_000_000, asm.assemble( "M-1" ) );
        assertInsn( 0b111_1000010_000_000, asm.assemble( "D+M" ) );
        assertInsn( 0b111_1010011_000_000, asm.assemble( "D-M" ) );
        assertInsn( 0b111_1000111_000_000, asm.assemble( "M-D" ) );
        assertInsn( 0b111_1000000_000_000, asm.assemble( "D&M" ) );
        assertInsn( 0b111_1010101_000_000, asm.assemble( "D|M" ) );
    }

    @Test
    public void assembleAInstructionWithLabel()
    {
        final int expected = 0b0000_0000_0000_0000_0010;

        final String src = "@0\n" +
                           "@1\n" +
                           "label:\n"+
                           "@2\n" +
                           "@3\n" +
                           "@label";
        assertInsn( expected, asm.assemble( src ) , 8 );
    }

    public static int readWord(byte[] array,int offset) {
        return Assembler.bytesToWord( array, offset );
    }

    private static void assertInsn(int expected, byte[] data) {
        assertInsn( expected, data, 0 );
    }

    private static void assertInsn(int expected, byte[] data, int offset) {

        int actual = readWord(data,offset);
        assertEquals( "Expected \n"+binary(expected)+" but got \n"+binary(actual),expected, actual );
    }

    private static String binary(int value) {

        int v = value & 0xffff;
        String s = Integer.toBinaryString( v );
        s = StringUtils.leftPad(s, 16, '0' );
        return "0b"+s;
    }

    @Test
    public void testRoundTrip() throws IOException
    {
        byte[] data = new byte[2];
        final List<String> lines = new ArrayList<>();
        for (int i = 0; i < 65536; i++)
        {
            Assembler.wordToBytes( i ,data,0  );
            lines.clear();
            new Disassembler().disassemble( data,0, (adr, line) -> lines.add( line )  );

            assertFalse( lines.isEmpty() );

            String src = String.join( "\n", lines );
            byte[] compiled = new Assembler().assemble( src );
            assertNotNull(compiled);
            assertEquals( 2, compiled.length );

            if ( !Arrays.equals(data,compiled) ) {

                fail("Mismatch: Source \n\n"+src+"\n\ncompiled to \n\n"+
                     hexdump(compiled,0,compiled.length)+"\n\nwhile we expected \n\n"+hexdump(data,0,data.length));
            }
        }
    }

    private static String hexdump(byte[] data, int offset, int len) {

        final int bytesPerLine = 8;
        final boolean printAscii = false;

        StringBuilder result = new StringBuilder();
        StringBuilder line = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        for ( int dataPtr = offset, cnt = 0 ; cnt < len && dataPtr < data.length ;  )
        {
            line.setLength( 0 );
            ascii.setLength( 0 );
            final String adrString =
                StringUtils.leftPad( Integer.toHexString( dataPtr-offset ) , 4 , '0' )+": ";
            line.append( adrString );
            for ( int j = 0 ; cnt < len && j < bytesPerLine && dataPtr < data.length ; j++,cnt++ ) {
                final int value = data[dataPtr++] & 0xff;
                final String hexValue = StringUtils.leftPad( Integer.toHexString( value ), 2 , '0' );
                line.append( hexValue );
                if ( (j+1) < bytesPerLine && (dataPtr+1) < data.length ) {
                    line.append(" ");
                }
                if ( printAscii )
                {
                    char c = value < 32 || value > 12 ? '.' : (char) value;
                    ascii.append( c );
                } else {
                    ascii.append( StringUtils.leftPad( Integer.toBinaryString( value ), 8 , '0' ) );
                }
            }
            result.append(line).append(" # ").append(ascii);
            if ( (cnt+1) < len && (dataPtr+1) < data.length ) {
                result.append( "\n" );
            }
        }
        return result.toString();
    }
}