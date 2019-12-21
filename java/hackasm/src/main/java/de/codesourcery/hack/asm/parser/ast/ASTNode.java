package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class ASTNode implements IASTNode
{
    public ASTNode parent;
    public final List<ASTNode> children = new ArrayList<>();
    protected TextRegion region;

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
    public void addAll(List<ASTNode> children)
    {
        Validate.notNull( children, "child must not be null" );
        for ( ASTNode child : children )
        {
            add(child);
        }
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

    public final ASTNode copySubtree() {

        final ASTNode result = copyNode();
        result.region = this.region == null ? null : this.region.copy();
        for ( ASTNode child : children ) {
            result.add( child.copySubtree() );
        }
        return result;
    }

    @Override
    public final ASTNode copyNode()
    {
        final ASTNode result = copyNodeInternal();
        result.region = region == null ? null : region.copy();
        return result;
    }

    protected abstract ASTNode copyNodeInternal();

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

    public void replaceChild(ASTNode child, ASTNode newChild)
    {
        final int idx = indexOf(child);
        if ( idx == -1 ) {
            throw new IllegalStateException("Child not found?");
        }
        children.set(idx,newChild);
        newChild.setParent(this);
    }

    public void replaceWith(ASTNode other)
    {
        if ( hasNoParent() ) {
            throw new IllegalStateException("Cannot replace, node has no parent");
        }
        parent().replaceChild(this, other );
    }

    @Override
    public void findAll(Predicate<ASTNode> predicate, List<ASTNode> result)
    {
        if ( predicate.test(this) ) {
            result.add( this );
        }
        for ( ASTNode child : children )
        {
            child.findAll(predicate,result);
        }
    }

    @Override
    public List<ASTNode> findAll(Predicate<ASTNode> predicate)
    {
        final List<ASTNode> result = new ArrayList<>();
        findAll(predicate, result);
        return result;
    }

    @Override
    public ASTNode findFirst(Predicate<ASTNode> predicate)
    {
        if ( predicate.test(this) ) {
            return this;
        }
        for ( ASTNode child : children ) {
            ASTNode tmp = child.findFirst(predicate );
            if ( tmp != null ) {
                return tmp;
            }
        }
        return null;
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

    public <T> void visit(IterationVisitor<T> visitor, IterationContext<T> ctx)
    {
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
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }

    protected TextRegion copyRegion() {
        return region == null ? null : region.copy();
    }
}