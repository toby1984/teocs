package de.codesourcery.hack.asm.parser.ast;

/**
 * Children are IdentifierNode instances for each parameter.
 */
public class MacroSignature extends ASTNode
{
    @Override
    public ASTNode copyNodeInternal()
    {
        return new MacroSignature();
    }
}
