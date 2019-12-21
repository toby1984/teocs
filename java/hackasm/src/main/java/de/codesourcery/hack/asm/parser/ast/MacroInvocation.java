package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.Identifier;
import de.codesourcery.hack.asm.parser.TextRegion;

import java.util.ArrayList;
import java.util.List;

public class MacroInvocation extends ASTNode
{
    public Identifier getName() {
        return ((IdentifierNode) firstChild()).name;
    }

    public int getArgumentCount() {
        return getArguments().size();
    }

    public List<ASTNode> getArguments()
    {
        if ( childCount() == 1 ) {
            return new ArrayList<>();
        }
        return children.subList(1,children.size());
    }

    @Override
    public ASTNode copyNodeInternal()
    {
        return new MacroInvocation();
    }
}
