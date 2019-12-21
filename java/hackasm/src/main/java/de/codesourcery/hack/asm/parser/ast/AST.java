package de.codesourcery.hack.asm.parser.ast;

public class AST extends ASTNode
{
    @Override
    public ASTNode copyNodeInternal()
    {
        return new AST();
    }
}
