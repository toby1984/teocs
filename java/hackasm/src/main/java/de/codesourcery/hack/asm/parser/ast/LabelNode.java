package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.Identifier;
import de.codesourcery.hack.asm.parser.ParseContext;
import de.codesourcery.hack.asm.parser.Symbol;
import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

public class LabelNode extends ASTNode implements ILiteralValueNode
{
    public Identifier name;

    public LabelNode(Identifier name) {
        this.name = name;
    }

    public LabelNode(Identifier name,TextRegion region)
    {
        super( region );
        Validate.notNull( name, "name must not be null" );
        this.name = name;
    }

    @Override
    public Integer value(ParseContext ctx)
    {
        final Symbol symbol = ctx.symbolTable().get( name );
        if ( symbol == null || symbol.type() == Symbol.Type.UNKNOWN ) {
            return null;
        }
        if ( symbol.type() != Symbol.Type.LABEL ) {
            throw new RuntimeException("Expected a label symbol but found "+symbol);
        }
        return ((Number) symbol.value()).intValue();
    }

    @Override
    public String toString()
    {
        return "label: '"+name.value+"'";
    }

    @Override
    public ASTNode copyNodeInternal()
    {
        return new LabelNode(name);
    }
}
