package de.codesourcery.hack.vm;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
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
        CONSTANT( "constant" ),
        STATIC( "static" ),
        // other
        ARGUMENT( "argument" ),
        LOCAL( "local" ),
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
        emit("@"+value);
        emit("D=A");
        emit("@"+address);
        emit("M=D");
    }

    private void memReadIntoD(int address) {
        emit("@"+address);
        emit("D=M");
    }

    private void memReadIntoA(int address) {
        emit("@"+address);
        emit("A=M");
    }

    //  Push the value of segment[index] onto the stack.
    private void push(MemorySegment segment, int idx) {

        /*
constant: This segment is truly virtual, as it does not occupy any physical space on the target architecture.
Instead, the VM implementation handles any VM access to 〈constant i〉 by simply supplying the constant i.
         */
        if ( segment == MemorySegment.CONSTANT ) {
            emit("@"+idx);
            emit("D=A");
            pushD();
            return;
        }
        if ( segment == MemorySegment.STATIC ) {

            emit("@16");
            emit("D=A");
            emit("@"+idx);
            emit("A=D+A"); // A = 16 + idx
            emit("D=M"); // D = Mem[16+idx]
            emit("@0");
            emit("A=M"); // A = Mem[0];
            emit("M=D"); // Mem[A] = D
            emit("@0");
            emit("M=M-1"); // decrement stack pointer
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
         * RAM[16-...] - static variables
         */
        emit("@"+ ptrFor(segment)); // A = "address register"
        emit("D=M"); // D=Mem[A] -> D = ptr to start of segment
        emit("@"+idx);
        emit("A=D+A"); // A = (ptr to start of segment) + idx
        emit("D=M"); // D=*((ptr to start of segment) + idx)
        pushD();
    }

    //   Pop the top stack value and store it in segment[index].
    private void pop(MemorySegment segment, int idx) {

        if ( segment == MemorySegment.CONSTANT ) {
            throw new IllegalArgumentException("Can't pop to CONSTANT segment");
        }
        if ( segment == MemorySegment.STATIC ) {
            emit("@16");
            emit("D=A");
            emit("@"+idx);
            emit("D=D+A"); // D = destination address where to store value
        }
        else
        {
            // calculate destination address
            emit("@" + ptrFor(segment));
            emit("D=M"); // load base address
            emit("@" + idx);
            emit("D=D+A");  // D = destination address where to store value
        }

        emit("@13"); // store destination adr in tmp variable
        emit("M=D");

        emit("@"+0); // A = 0 -> ToS ptr
        emit("AM=M+1"); // bump ptr
        emit("D=M");

        // read destination address from tmp variable
        emit("@13");
        emit("A=M");
        emit("M=D");
    }

    private void popIntoA() {
        emit("@"+0); // A = 0 -> ToS ptr
        emit("AM=M+1"); // bump ptr
        emit("A=M");
    }

    private void popIntoD() {
        emit("@"+0); // A = 0 -> ToS ptr
        emit("AM=M+1"); // bump ptr
        emit("D=M");
    }

    private void add() {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument
        emit("D=D+A");
        pushD();
    }

    private void sub() {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument
        emit("D=A-D");
        pushD();
    }

    private void neg() {
        popIntoD(); // D = 2nd argument
        emit("D=-D");
        pushD();
    }

    private void writeJmp(String destination) {
        emit("@"+destination);
        emit("0;JMP");
    }

    private void eq()
    {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument

        emit("D=A-D");
        final String eqLabel = genLabel("eq");
        final String cont = genLabel("cont");
        emit("@"+eqLabel);
        emit("D;JEQ");
        // this is the "not equals" path
        emit("@0"); // true = -1 , false = 0
        pushA();
        writeJmp(cont);

        // this is the "equals" path
        writeLabel(eqLabel);
        emit("@-1"); // true = -1 , false = 0
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
        emit("D=D-A");
        emit("@"+gtLabel);
        emit("D;JGT "+gtLabel);
        // "not greater" path
        emit("@0"); //  false = 0
        pushA();
        writeJmp(cont);

        // "greater than" path
        writeLabel(gtLabel);
        emit("@-1"); // true = -1
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
        emit("D=A-D");
        emit("@"+ltLabel);
        emit("D;JLT "+ltLabel);
        // -> not less
        emit("@0"); //  false = 0
        pushA();
        writeJmp(cont);

        writeLabel(ltLabel);
        emit("@-1"); // true = -1
        pushA();

        writeLabel(cont);
    }

    private void and()
    {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument
        emit("D=A&D");
        pushD();
    }

    private void ifGoto(String destination) {
        popIntoD();
        emit("@"+destination);
        emit("D;JNE");
    }

    private void or()
    {
        popIntoD(); // D = 2nd argument
        popIntoA(); // A = first argument
        emit("D=A|D");
        pushD();
    }

    private void not()
    {
        popIntoD(); // D = 2nd argument
        emit("D=!D");
        pushD();
    }

    private void writeLabel(String label) {
        emit(label+":");
    }

    private void pushA() {

        emit("D=A");
        pushD();
    }

    private void pushD() {

        emit("@"+0); // A = 0 -> ToS ptr
        emit("A=M");
        emit("M=D");

        emit("@"+0); // A = 0 -> ToS ptr
        emit("M=M-1"); // bump ptr
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

    private int ptrFor(MemorySegment segment)
    {
        /*
pointer, temp: These segments are each mapped directly onto a fixed area in the RAM. The pointer
segment is mapped on RAM locations 3-4 (also called THIS and THAT) and the temp segment on
locations 5-12 (also called R5, R6,..., R12). Thus access to pointer i should be translated to assembly
code that accesses RAM location 3 + i, and access to temp i should be translated to assembly code that
accesses RAM location 5 + i.

         */
        switch(segment)
        {
            case LOCAL:    return 1;
            case ARGUMENT: return 2;
            case THIS:     return 3;
            case POINTER:  return 3;
            case THAT:     return 4;
            case TEMP:     return 5;
            default:
                throw new IllegalStateException("Unexpected value: " + segment);
        }
    }

    private void parse(String source, BiConsumer<String,List<String>> visitor )
    {
        final List<String> lines = Arrays.stream( source.split( "\n") ).map( String::trim ).collect(Collectors.toList());

        for ( String line : lines )
        {
            List<String> args =
                Arrays.stream(line.split("\\s")).map(String::trim).collect(Collectors.toList());

            if ( ! args.isEmpty())
            {
                final String cmd = args.get(0).toLowerCase();
                if ( args.size() > 1 ) {
                    args = args.subList(1,args.size());
                }
                visitor.accept(cmd, args);
            }
        }
    }

    public void translate(String source)
    {
        // now process source
        parse(source, (cmd, args) ->
        {
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
                    sub();
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
                    if ( args.size() != 2 ) {
                        throw new RuntimeException("function <func name>  <local var count> requires two arguments, got " + args.size() );
                    }
                    function(args.get(0), Integer.parseInt(args.get(1)));
                    break;
                case "call": // call <function name> <number of arguments to pass>
                    if ( args.size() != 2 ) {
                        throw new RuntimeException("call <func name>  <arg count> requires two arguments, got " + args.size() );
                    }
                    call(args.get(0), Integer.parseInt(args.get(1)));
                    break;
                case "return": // return
                    ret();
                    break;
                // error
                default:
                    throw new RuntimeException("Unparseable line: "+cmd+" "+ String.join(" ", args));
            }
        });
    }

    private void ret()
    {
        // save LCL in as temporary in RAM[14]
        emit("@"+ptrFor(MemorySegment.LOCAL ) );
        emit("D=M");
        emit("@14");
        emit("M=D");

        // retrieve return address ,
        // *(LOCAL-5)
        emit("@5");
        emit("A=D-A");
        emit("D=M");

        // save return address as temporary in RAM[13]
        emit("@13");
        emit("M=D");

        // reposition return value: *ARG = pop()
        popIntoD();
        emit("@"+ptrFor(MemorySegment.ARGUMENT));
        emit("A=M");
        emit("M=D");

        // SP = ARG + 1
        emit("D=A+1");
        emit("@0");
        emit("M=D");

        // THAT = *(FRAME-1)
        emit("@14");
        emit("D=M-1");
        emit("@"+ptrFor(MemorySegment.THAT));
        emit("M=D");

        // THIS = *(FRAME-2)
        emit("D=D-1");
        emit("@"+ptrFor(MemorySegment.THIS));
        emit("M=D");

        // ARG = *(FRAME-3)
        emit("D=D-1");
        emit("@"+ptrFor(MemorySegment.ARGUMENT));
        emit("M=D");

        // LCL = *(FRAME-4)
        emit("D=D-1");
        emit("@"+ptrFor(MemorySegment.LOCAL));
        emit("M=D");

        // jump to return address
        emit("@13");
        emit("A=M");
        emit("0;JMP");
    }

    private void function(String name, int localArgCount)
    {
        // push zero values for local variables
        if ( localArgCount > 0 )
        {
            emit("@0");
            emit("A=M");
            for (int i = 0; i < localArgCount ; i++)
            {
                emit("M=0");
                emit("A=A-1");
            }
            emit("D=A");
            emit("@0");
            emit("M=D");
        }
    }

    private void call(String funcName,int argCount) {

        // push return address
        final String cont = genLabel("call_"+funcName);
        emit("@cont");
        pushA();

        // push LCL (RAM[1])
        emit("@1");
        emit("D=M");
        pushD();

        // push ARG (RAM[2])
        emit("@2");
        emit("D=M");
        pushD();

        // push THIS (RAM[3])
        emit("@3");
        emit("D=M");
        pushD();
        // push THAT (RAM[4])
        emit("@4");
        emit("D=M");
        pushD();

        // set ARG = SP - argcount - 5
        emit("@0");
        emit("D=M"); // D = SP
        emit("@"+argCount);
        emit("D=D-A"); // D = SP - argcount
        emit("@5");
        emit("D=D-A"); // D = SP - argcount - 5
        emit("@"+ptrFor(MemorySegment.ARGUMENT));

        // set LCL = SP
        emit("@0");
        emit("D=M");
        emit("@"+ptrFor(MemorySegment.LOCAL));
        emit("M=D");

        // jump unconditionally
        emit("@"+funcName);
        emit("0;JMP");
        writeLabel(cont );
    }

    private void emit(String s) {
        writer.println(s);
    }
}
