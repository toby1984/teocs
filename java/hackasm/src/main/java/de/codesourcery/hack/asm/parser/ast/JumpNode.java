package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.Jump;
import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

public class JumpNode extends ASTNode
{
    public final Jump jump;

    public JumpNode(Jump jump, TextRegion region)
    {
        super(region);
        Validate.notNull(jump, "jump must not be null");
        this.jump = jump;
    }

    @Override
    public String toString()
    {
        return "jump: "+jump;
    }
}
