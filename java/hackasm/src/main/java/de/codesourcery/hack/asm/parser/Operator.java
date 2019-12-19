package de.codesourcery.hack.asm.parser;

public enum Operator
{
    PLUS("+"),
    MINUS("-");

    public final String literal;
    public final boolean isInfix;

    Operator(String literal) {
        this( literal, true );
    }

    Operator(String literal,boolean isInfix)
    {
        this.literal = literal;
        this.isInfix = isInfix;
    }
}
