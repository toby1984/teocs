package de.codesourcery.hack.asm.parser;

import org.apache.commons.lang3.Validate;

public class ParseContext
{
    private final SymbolTable root;

    public ParseContext() {
        root = new SymbolTable();
    }

    public ParseContext(SymbolTable root)
    {
        Validate.notNull(root, "root must not be null");
        this.root = root;
    }

    public SymbolTable symbolTable() {
        return root;
    }
}
