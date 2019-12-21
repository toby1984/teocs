package de.codesourcery.hack.vm;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VMTranslator
{
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
    public String translate(String source)
    {
        final List<String> lines = Arrays.stream(  source.split( "\n") ).map( String::trim ).collect( Collectors.toList());;

        for ( String line : lines )
        {
            final List<String> parts =
                Arrays.stream( line.split("\\s" ) ).map(String::trim).collect( Collectors.toList());

            if ( parts.isEmpty() ) {
                continue;
            }

            switch( parts.get(0).toLowerCase() )
            {
                case "push": // push <segment> <index>
                    break;
                case "pop": //   pop <segment> <index>
                    break;
                // arithmetic
                case "add":
                    break;
                case "sub":
                    break;
                case "neg":
                    break;
                case "and":
                    break;
                case "or":
                    break;
                case "not":
                    break;
                // comparison - yields boolean value (true = x0ffff , false = 0 )
                case "eq":
                    break;
                case "gt":
                    break;
                case "lt":
                    break;
                // misc
                case "label":   // label <symbol>
                    break;
                // control flow
                case "goto":    // goto <smybol>
                    break;
                case "if-goto": // if-goto <symbol>
                    break;
                case "function": // function <name> <number of local variables>
                    break;
                case "call": // call <function name> <number of arguments to pass>
                    break;
                case "return": // return
                    break;
                    // error
                default:
                    throw new RuntimeException("Unknown command: "+line);
            }
        }
        throw new RuntimeException("Not implemented");
    }
}
