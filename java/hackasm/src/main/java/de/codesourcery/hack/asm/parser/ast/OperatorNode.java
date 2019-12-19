package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.Operator;
import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

public class OperatorNode extends ASTNode
{
    public final Operator operator;

    public OperatorNode(Operator operator,TextRegion region)
    {
        super( region );
        Validate.notNull( operator, "operator must not be null" );
        this.operator = operator;
    }
}
