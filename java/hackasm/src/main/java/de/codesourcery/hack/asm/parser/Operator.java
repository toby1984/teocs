package de.codesourcery.hack.asm.parser;

public enum Operator
{
    UNARY_MINUS('-',100, 1, false,false),
    BITWISE_NOT('!',95,1,false),
    PLUS('+',90, 2, true),
    MINUS('-',90, 2, true),
    // --
    BITWISE_AND('&',85, 2,true),
    BITWISE_OR('|',80, 2,true),
    ASSIGNMENT('=',75, 2, false),
    ;

    public final char literal;
    public final int precedence;
    public final int argumentCount;
    public final boolean isLeftAssociative;
    public final boolean parsedByLexer;

    Operator(char literal,int precedence,int argumentCount,boolean isLeftAssociative) {
        this(literal,precedence,argumentCount,isLeftAssociative,true);
    }

    Operator(char literal,int precedence,int argumentCount,boolean isLeftAssociative,boolean parsedByLexer)
    {
        this.literal = literal;
        this.precedence = precedence;
        this.argumentCount = argumentCount;
        this.isLeftAssociative = isLeftAssociative;
        this.parsedByLexer = parsedByLexer;
    }

    public static Operator parseOperator(char c)
    {
        for ( Operator op : values() ) {
            if ( op.parsedByLexer && op.literal == c ) {
                return op;
            }
        }
        return null;
    }
}
