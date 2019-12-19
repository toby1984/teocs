package de.codesourcery.hack.asm.parser;

public class ParseContext
{
    private SymbolTable root = new SymbolTable();

    public SymbolTable symbolTable() {
        return root;
    }
}
