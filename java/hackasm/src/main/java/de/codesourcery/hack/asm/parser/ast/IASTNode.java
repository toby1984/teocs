package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.TextRegion;

import java.util.List;

public interface IASTNode
{
    List<ASTNode> children();

    ASTNode parent();

    TextRegion region();

    void add(ASTNode child);

    void setParent(ASTNode node);

    default int childCount() {
        return children().size();
    }

    default boolean hasChildren() {
        return ! children().isEmpty();
    }

    default boolean hasNoChildren() {
        return ! hasChildren();
    }

    default boolean hasParent() {
        return parent() != null;
    }

    default boolean hasNoParent() {
        return ! hasParent();
    }

    default ASTNode child(int idx) {
        return children().get(idx);
    }

    default ASTNode firstChild() {
        return child(0);
    }

    default ASTNode lhs() {
        return child(0);
    }

    default ASTNode rhs() {
        return child(1);
    }


    interface IterationVisitor<T> {
        void visit(ASTNode node, IterationContext<T> ctx);
    }

    final class IterationContext<T>
    {
        boolean stop;
        boolean dontGoDeeper;
        T value;

        void stop() {
            this.stop = true;
        }

        void stop(T obj) {
            this.stop = true;
            this.value = obj;
        }

        void dontGoDeeper() {
            dontGoDeeper = true;
        }
    }

    <T> T visit(IterationVisitor<T> visitor);

    // NOT PART OF PUBLIC API
    <T> void visit(IterationVisitor<T> visitor, IterationContext<T> ctx);
}