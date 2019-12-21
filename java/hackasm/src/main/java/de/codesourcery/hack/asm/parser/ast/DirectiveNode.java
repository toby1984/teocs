package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

public class DirectiveNode extends ASTNode
{
    public enum Directive
    {
        /**
         * Initialize a word of memory.
         */
        WORD("word"),
        MACRO("macro");

        public final String literal;

        Directive(String literal)
        {
            this.literal = literal;
        }

        public static Directive of(String name) {
            for ( Directive d : values() ) {
                if ( d.literal.equalsIgnoreCase( name ) ) {
                    return d;
                }
            }
            return null;
        }
    }

    public final Directive directive;

    private DirectiveNode(Directive directive) {
        this.directive = directive;
    }
    public DirectiveNode(Directive directive, TextRegion region)
    {
        super( region );
        Validate.notNull( directive, "direction must not be null" );
        this.directive = directive;
    }

    @Override
    public ASTNode copyNodeInternal()
    {
        return new DirectiveNode(this.directive);
    }
}
