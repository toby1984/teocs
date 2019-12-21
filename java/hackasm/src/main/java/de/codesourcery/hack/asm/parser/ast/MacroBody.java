package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.TextRegion;
import de.codesourcery.hack.asm.parser.Token;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class MacroBody extends ASTNode
{
    public final List<Token> body;
    public MacroBody(List<Token>tokens)
    {
        super(toRegion(tokens));
        Validate.notNull(tokens, "tokens must not be null");
        this.body = tokens;
    }

    private static TextRegion toRegion(List<Token> toks) {
        if ( toks.isEmpty() ) {
            return null;
        }
        final TextRegion result = toks.get(0).region();
        for ( int i = 1 ; i < toks.size() ; i++ ) {
            result.merge(toks.get(i).region() );
        }
        return result;
    }
}
