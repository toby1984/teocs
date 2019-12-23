package de.codesourcery.hack.vm;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VMTranslator
{
    private PrintWriter writer;

    private int labelCount;

    public VMTranslator(PrintWriter writer) {
        this.writer = writer;
    }

    private String genLabel(String name) {
        String result = "label_"+name+"_"+labelCount+":";
        labelCount++;
        return result;
    }

    /*
     * Memory layout (32k words = 64 KB)
     *
     * Word
     * 0-15     | Sixteen virtual registers
     * 16-255   | Static variables of all the VM functions in the VM program
     * 256-2047 | Stack
     * 2048-16483 | Heap (used to store objects and arrays)
     * 16384-24575 | Memory mapped I/O
     *
     * "register" usage:
     *
     * RAM[0] - SP - Top of stack ptr
     * RAM[1] - LCL - Points to the base of the current VMs function's 'local' segment
     * RAM[2] - ARG -  Points to the base of the current VMs function's 'argument' segment
     * RAM[3] - THIS -  Points to the base of the current VMs function's 'this' segment (within the heap)
     * RAM[4] - THAT -  Points to the base of the current VMs function's 'this' segment (within the heap)
     * RAM[5-12] - Holds the contents of the 'temp' segment
     * RAM[13-15] - Free to use by VM implementation for general purposes
     *
     */
    public enum MemorySegment {
        ARGUMENT( "argument" ),
        LOCAL( "local" ),
        STATIC( "static" ),
        CONSTANT( "constant" ),
        THIS( "this" ),
        THAT( "that" ),
        POINTER( "pointer" ),
        TEMP( "temp" );

        public final String name;

        MemorySegment(String name)
        {
            this.name = name;
        }
    }

    private void memWrite(int address,int value) {
        writer.println("@"+value);
        writer.println("D=A");
        writer.println("@"+address);
        writer.println("M=D");
    }

    private void memReadIntoD(int address) {
        writer.println("@"+address);
        writer.println("D=M");
    }

    private void memReadIntoA(int address) {
        writer.println("@"+address);
        writer.println("A=M");
    }

    //  Push the value of segment[index] onto the stack.
    private void push(MemorySegment segment, int idx) {

        if ( segment == MemorySegment.CONSTANT ) {
            writer.println("@"+idx);
            writer.println("D=A");
            pushD();
            return;
        }
        /*
         * RAM[0] - SP - Top of stack ptr
         * RAM[1] - LCL - Points to the base of the current VMs function's 'local' segment
         * RAM[2] - ARG -  Points to the base of the current VMs function's 'argument' segment
         * RAM[3] - THIS -  Points to the base of the current VMs function's 'this' segment (within the heap)
         * RAM[4] - THAT -  Points to the base of the current VMs function's 'this' segment (within the heap)
         * RAM[5-12] - Holds the contents of the 'temp' segment
         * RAM[13-15] - Free to use by VM implementation for general purposes
         */
        writer.println("@"+ ptrFor(segment)); // A = "address register"
        writer.println("D=M"); // D=Mem[A] -> D = ptr to start of segment
        writer.println("@"+idx);
        writer.println("A=D+A"); // A = (ptr to start of segment) + idx
        writer.println("D=M"); // D=*((ptr to start of segment) + idx)
        pushD();
    }

    //   Pop the top stack value and store it in segment[index].
    private void pop(MemorySegment segment, int idx) {

        // calculate destination address
        writer.println("@"+ptrFor(segment));
        writer.println("D=M"); // load base address
        writer.println("@"+idx);
        writer.println("D=D+A"); // A = base address + idx

        writer.println("@13"); // store destination adr in tmp variable
        writer.println("M=D");

        writer.println("@"+0); // A = 0 -> ToS ptr
        writer.println("M=M+1"); // bump ptr
        writer.println("D=M");

        writer.println("@13");
        writer.println("M=D");
    }

    private void popIntoA() {
        writer.println("@"+0); // A = 0 -> ToS ptr
        writer.println("AM=M+1"); // bump ptr
        writer.println("A=M");
    }

    private void popIntoD() {
        writer.println("@"+0); // A = 0 -> ToS ptr
        writer.println("AM=M+1"); // bump ptr
        writer.println("D=M");
    }

    private void add() {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument
        writer.println("D=D+A");
        pushD();
    }

    private void sub() {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument
        writer.println("D=A-D");
        pushD();
    }

    private void neg() {
        popIntoD(); // D = 2nd argument
        writer.println("D=-D");
        pushD();
    }

    private void writeJmp(String destination) {
        writer.println("@"+destination);
        writer.println("0;JMP");
    }

    private void eq()
    {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument

        writer.println("D=A-D");
        final String eqLabel = genLabel("eq");
        final String cont = genLabel("cont");
        writer.println("@"+eqLabel);
        writer.println("D;JEQ");
        // -> not equals
        writer.println("@0"); // true = -1 , false = 0
        pushA();
        writeJmp(cont);

        writeLabel(eqLabel);
        writer.println("@-1"); // true = -1 , false = 0
        pushA();

        writeLabel(cont);
    }

    private void gt()
    {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument

        // A > D
        final String gtLabel = genLabel("gt");
        final String cont = genLabel("cont");
        writer.println("D=D-A");
        writer.println("@"+gtLabel);
        writer.println("D;JGT "+gtLabel);
        // -> not greater
        writer.println("@0"); //  false = 0
        pushA();
        writeJmp(cont);

        writeLabel(gtLabel);
        writer.println("@-1"); // true = -1
        pushA();

        writeLabel(cont);
    }

    private void lt()
    {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument

        // A < D
        final String ltLabel = genLabel("gt");
        final String cont = genLabel("cont");
        writer.println("D=A-D");
        writer.println("@"+ltLabel);
        writer.println("D;JLT "+ltLabel);
        // -> not less
        writer.println("@0"); //  false = 0
        pushA();
        writeJmp(cont);

        writeLabel(ltLabel);
        writer.println("@-1"); // true = -1
        pushA();

        writeLabel(cont);
    }

    private void and()
    {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument
        writer.println("D=A&D");
        pushD();
    }
    private void ifGoto(String destination) {
        popIntoD();
        writer.println("@"+destination);
        writer.println("D;JNE");
    }

    private void or()
    {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument
        writer.println("D=A|D");
        pushD();
    }

    private void not()
    {
        popIntoD(); // D = 2nd argument
        writer.println("D=!D");
        pushD();
    }

    private void writeLabel(String label) {
        writer.println(label+":");
    }


    private void pushA() {

        writer.println("D=A");
        pushD();
    }

    private void pushD() {

        writer.println("@"+0); // A = 0 -> ToS ptr
        writer.println("A=M");
        writer.println("M=D");

        writer.println("@"+0); // A = 0 -> ToS ptr
        writer.println("M=M-1"); // bump ptr
    }

    private MemorySegment segmentFor(String name) {
        switch(name.toLowerCase() ) {
            case "argument": return MemorySegment.ARGUMENT;
            case "local":    return MemorySegment.LOCAL;
            case "static":   return MemorySegment.STATIC;
            case "constant": return MemorySegment.CONSTANT;
            case "this":     return MemorySegment.THIS;
            case "that":     return MemorySegment.THAT;
            case "pointer":  return MemorySegment.POINTER;
            case "temp":     return MemorySegment.TEMP;
            default:
                throw new IllegalStateException("Unexpected value: " + name.toLowerCase());
        }
    }

    private int ptrFor(MemorySegment segment) {
        switch(segment)
        {
            case ARGUMENT: return 2;
            case LOCAL:    return 1;
            case THIS:     return 3;
            case THAT:     return 4;
            default:
                throw new IllegalStateException("Unexpected value: " + segment);
        }
    }

    public String translate(String source)
    {
        final List<String> lines = Arrays.stream( source.split( "\n") ).map( String::trim ).collect(Collectors.toList());

        for ( String line : lines )
        {
            List<String> args =
                Arrays.stream( line.split("\\s" ) ).map(String::trim).collect(Collectors.toList());

            if ( args.isEmpty() ) {
                continue;
            }

            final String cmd = args.get(0).toLowerCase();
            if ( args.size() > 1 ) {
                args = args.subList(1,args.size());
            }

            switch( cmd )
            {
                case "push": // push <segment> <index>
                    if ( args.size() != 2 ) {
                        throw new RuntimeException("push <segment> <index> requires two arguments, got " + args.size());
                    }
                    push( segmentFor(args.get(0)),Integer.parseInt(args.get(1)) );
                    break;
                case "pop": //   pop <segment> <index>
                    if ( args.size() != 2 ) {
                        throw new RuntimeException("pop <segment> <index> requires two arguments, got " + args.size());
                    }
                    pop( segmentFor(args.get(0)),Integer.parseInt(args.get(1) ) );
                    break;
                // arithmetic
                case "add":
                    add();
                    break;
                case "sub":
                    break;
                case "neg":
                    neg();
                    break;
                case "and":
                    and();
                    break;
                case "or":
                    or();
                    break;
                case "not":
                    not();
                    break;
                // comparison - yields boolean value (true = x0ffff , false = 0 )
                case "eq":
                    eq();
                    break;
                case "gt":
                    gt();
                    break;
                case "lt":
                    lt();
                    break;
                // misc
                case "label":   // label <symbol>
                    if ( args.size() != 1 ) {
                        throw new RuntimeException("label <symbol> requires one argument, got " + args.size() );
                    }
                    writeLabel(args.get(0));
                    break;
                // control flow
                case "goto":    // goto <smybol>
                    if ( args.size() != 1 ) {
                        throw new RuntimeException("goto <symbol> requires one argument, got " + args.size() );
                    }
                    writeJmp(args.get(0));
                    break;
                case "if-goto": // if-goto <symbol>
                    if ( args.size() != 1 ) {
                        throw new RuntimeException("if-goto <symbol> requires one argument, got " + args.size() );
                    }
                    ifGoto(args.get(0));
                    break;
                case "function": // function <name> <number of local variables>
                    break;
                case "call": // call <function name> <number of arguments to pass>
                    break;
                case "return": // return
                    break;
                    // error
                default:
                    throw new RuntimeException("Unparseable line: "+line);
            }
        }
        throw new RuntimeException("Not implemented");
    }
}
