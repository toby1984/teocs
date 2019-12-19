package de.codesourcery.hack.asm.parser;

public class UnknownSymbolException extends RuntimeException
{
    public final Identifier name;

    public UnknownSymbolException(Identifier name)
    {
        super("Failed to find symbol "+name+" in scope");
        this.name = name;
    }
}