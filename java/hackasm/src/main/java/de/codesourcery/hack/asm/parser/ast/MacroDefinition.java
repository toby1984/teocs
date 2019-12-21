package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.Identifier;
import de.codesourcery.hack.asm.parser.TextRegion;
import de.codesourcery.hack.asm.parser.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * First child is IdentifierNode (name), second child is MacroSignature, second child is MacroBody.
 */
public class MacroDefinition extends ASTNode
{
    public Identifier getName() {
        return ((IdentifierNode) firstChild()).name;
    }

    public int getArgumentCount() {
        return child(1).childCount();
    }

    public List<Identifier> getArgumentNames() {
        return child(1).children().stream().map(IdentifierNode.class::cast).map(x->x.name).collect(Collectors.toList());
    }

    public List<Token> getBody() {
        return ((MacroBody) child(2)).body;
    }
}
