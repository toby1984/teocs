package de.codesourcery.hack.asm.parser.ast;

import java.awt.Taskbar;

public class StatementNode extends ASTNode
{
    @Override
    public ASTNode copyNodeInternal()
    {
        return new StatementNode();
    }
}
