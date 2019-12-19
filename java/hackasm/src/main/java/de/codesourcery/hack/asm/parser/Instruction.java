package de.codesourcery.hack.asm.parser;

import de.codesourcery.hack.asm.IObjectCodeWriter;

public enum Instruction
{
    // A instruction
    LOADA("loada",1 ) { // LOADA { 123 | <label> }
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    // C instructions
    JMP("jmp",1 ) { // MOVE {#0 | #1 | #-1 | D | A | M} -> ...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    MOVE("move",1 ) { // MOVE {#0 | #1 | #-1 | D | A | M} -> ...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    NOT("not",1 ) { // NOT A|D|M,...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    NEG("neg",1 ) { // NEG A|D|M,...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    INC("inc",1 ) { // INC { D,A, M } -> ...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    DEC("dec",1 ) { // DEC { D,A, M } -> ...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    ADD("add",2 ) { // ADD { D,A | D, M } -> ...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    SUB("sub",2 ) { // SUB { D,A | D,M| A,D | M,D } -> ...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    AND("and",2 ) { // AND { D,A | D,M } -> ...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    },
    OR("or",2 ) { // OR { D,A | D,M } -> ...
        @Override
        public void genCode(IObjectCodeWriter writer, ParseContext parseCtx)
        {

        }
    };
    public final String mnemonic;
    public final int srcOpCount;

    Instruction(String mnemonic, int srcOpCount)
    {
        this.mnemonic = mnemonic;
        this.srcOpCount = srcOpCount;
    }

    public static boolean isValid(String s) {
        return parse(s) != null;
    }

    public static Instruction parse(String s)
    {
        if ( s != null ) {
            for ( Instruction insn : values() ) {
                if ( insn.mnemonic.equalsIgnoreCase( s ) ) {
                    return insn;
                }
            }
        }
        return null;
    }

    public abstract void genCode(IObjectCodeWriter writer, ParseContext parseCtx);
}
