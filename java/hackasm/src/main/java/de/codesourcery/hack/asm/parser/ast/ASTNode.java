package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ASTNode implements IASTNode
{
    public ASTNode parent;
    public final List<ASTNode> children = new ArrayList<>();
    private TextRegion region;

    protected ASTNode() {
    }

    protected ASTNode(TextRegion region) {
        this.region = region;
    }

    @Override
    public void add(ASTNode child)
    {
        Validate.notNull( child, "child must not be null" );
        this.children.add(child);
        child.setParent(this);
    }

    @Override
    public void setParent(ASTNode parent)
    {
        Validate.notNull( parent, "parent must not be null" );
        this.parent = parent;
    }

    @Override
    public List<ASTNode> children()
    {
        return children;
    }

    @Override
    public ASTNode parent()
    {
        return parent;
    }

    @Override
    public TextRegion region()
    {
        return this.region;
    }

    public <T> T visit(IterationVisitor<T> visitor)
    {
        final IterationContext<T> ctx = new IterationContext<>();
        visitor.visit( this, ctx );
        if ( ! ctx.stop && ! ctx.dontGoDeeper )
        {
            for (ASTNode child : children)
            {
                child.visit( visitor, ctx );
                if ( ctx.stop ) {
                    break;
                }
            }
        }
        ctx.dontGoDeeper = false;
        return ctx.value;
    }
}