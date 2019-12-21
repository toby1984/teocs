package de.codesourcery.hack.asm.parser.ast;

public class InstructionNode extends ASTNode
{
    public final boolean isLoadA;

    public InstructionNode(boolean isLoadA)
    {
        this.isLoadA = isLoadA;
    }

    @Override
    public ASTNode copyNodeInternal()
    {
        return new InstructionNode(isLoadA);
    }
}
