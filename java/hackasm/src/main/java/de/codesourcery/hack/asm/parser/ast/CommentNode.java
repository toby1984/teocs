package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

public class CommentNode extends ASTNode
{
    public final String value;

    public CommentNode(String value,TextRegion region)
    {
        super( region );
        Validate.notNull( value, "value must not be null" );
        this.value = value;
    }

    private CommentNode(String value) {
        this.value = value;
    }

    @Override
    public ASTNode copyNodeInternal()
    {
        return new CommentNode(value);
    }
}
