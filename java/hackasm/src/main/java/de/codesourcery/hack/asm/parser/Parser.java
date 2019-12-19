package de.codesourcery.hack.asm.parser;

import de.codesourcery.hack.asm.parser.ast.AST;
import de.codesourcery.hack.asm.parser.ast.ASTNode;
import de.codesourcery.hack.asm.parser.ast.CommentNode;
import de.codesourcery.hack.asm.parser.ast.IdentifierNode;
import de.codesourcery.hack.asm.parser.ast.InstructionNode;
import de.codesourcery.hack.asm.parser.ast.LabelNode;
import de.codesourcery.hack.asm.parser.ast.NumberNode;
import de.codesourcery.hack.asm.parser.ast.RegisterNode;
import de.codesourcery.hack.asm.parser.ast.StatementNode;

public class Parser
{
    private Lexer lexer;
    private ParseContext context;

    private final StringBuilder buffer = new StringBuilder();

    private Token token;

    public AST parse(Lexer lexer)
    {
        this.lexer = lexer;
        this.context = new ParseContext();
        final AST ast = new AST();
        token = lexer.next();
        while ( ! token.isEOF() )
        {
            consumeNewLines();
            ASTNode node = parseStatement();
            if ( node == null ) {
                break;
            }
            ast.add( node );
        }
        if ( ! lexer.eof() ) {
            throw new IllegalStateException("Parser did not consume all input tokens ?");
        }
        return ast;
    }

    private ASTNode parseStatement()
    {
        StatementNode result = new StatementNode();
        /* <comment>
         * <label>
         * <label> <comment>
         * <label> <instruction>
         * <label> <instruction> <comment>
         */
        ASTNode label = parseLabel();
        if ( label != null ) {
            result.add( label );
        }
        ASTNode comment = parseComment();
        if ( comment != null ) {
            result.add( comment );
            return result;
        }

        ASTNode insn = parseInstruction();
        if ( insn != null ) {
            result.add( insn );
        }
        comment = parseComment();
        if ( comment != null ) {
            result.add( comment );
        }
        return result.hasChildren() ? result : null;
    }

    private Token peek() {
        return lexer.peek();
    }

    private void consumeNewLines()
    {
        while ( token.isNewline() ) {
            token = lexer.next();
        }
    }

    private void consumeToken() {
        token = lexer.next();
    }

    private void consumeTokens(int count)
    {
        for ( ; count > 0 ; count--) {
            token = lexer.next();
        }
    }

    private void fail(String message) {
        throw new RuntimeException( message+" @ "+token.location );
    }

    private ASTNode parseInstruction()
    {
        InstructionNode result = null;
        if ( token.is(TokenType.INSTRUCTION) )
        {
            Instruction insn = Instruction.parse( token.value );
            result = new InstructionNode(insn,token.region());
            consumeToken();

            // parse source arguments
            for (int i = 0; i < insn.srcOpCount; i++ )
            {
                ASTNode expr = parseExpression();
                if ( expr == null ) {
                    fail("Instruction "+insn+" requires "+insn.srcOpCount +" arguments, expected an expression");
                }
                result.add( expr );
                if ( (i+1) < insn.srcOpCount ) {
                    if ( ! token.is(TokenType.COMMA ) ) {
                        fail("Instruction "+insn+" requires "+insn.srcOpCount +" arguments, missing ','");
                    }
                    consumeToken();
                }
            }
            // parse destination arguments (if any)
            while ( true )
            {
                if ( ! token.is(TokenType.COMMA ) ) {
                    break;
                }
                consumeToken();
                ASTNode dest = parseExpression();
                if ( dest == null ) {
                    fail("Expected an instruction argument");
                }
                result.add( dest );
            }
        }
        return result;
    }

    private ASTNode parseExpression()
    {
        // TODO: Implement proper expression parsing that also supports operators & operator precedence
        ASTNode result = parseNumber();
        if ( result != null ) {
            return result;
        }
        result = parseRegister();
        if ( result != null ) {
            return result;
        }
        result = parseIdentifier();
        if ( result != null ) {
            return result;
        }
        return null;
    }

    private ASTNode parseRegister()
    {
        RegisterNode result = null;
        if ( token.is(TokenType.IDENTIFIER) && RegisterNode.isValidRegister( token.value) )
        {
            result = new RegisterNode( token.value, token.region() );
            consumeToken();
        }
        return result;
    }

    private ASTNode parseNumber()
    {
        NumberNode result = null;
        if ( token.is(TokenType.NUMBER) )
        {
            result = new NumberNode( token.value, token.region() );
            consumeToken();
        }
        return result;
    }

    private ASTNode parseIdentifier()
    {
        IdentifierNode result = null;
        if ( token.is(TokenType.IDENTIFIER) )
        {
            final Identifier id = new Identifier( token.value );
            context.symbolTable().declare( id );
            result = new IdentifierNode( id, token.region());
            consumeToken();
        }
        return result;
    }

    private ASTNode parseLabel()
    {
        ASTNode result = null;
        if ( token.is(TokenType.IDENTIFIER) && peek().is(TokenType.COLON ) )
        {
            final Identifier id = new Identifier( token.value );
            final Symbol symbol = context.symbolTable().declare( id );
            symbol.setValue( null, Symbol.Type.LABEL );
            result = new LabelNode(id,token.region());
            consumeTokens(2);
        }
        return result;
    }

    private ASTNode parseComment()
    {
        ASTNode result = null;
        if ( token.is(TokenType.SEMICOLON) )
        {
            buffer.setLength( 0 );
            Token start = token;
            lexer.setSkipWhitespace( false );
            try
            {
                do
                {
                    buffer.append( token.value );
                    consumeToken();
                } while ( !( token.isEOF() || token.isNewline() ) );
            } finally {
                lexer.setSkipWhitespace( true );
            }
            result = new CommentNode(buffer.toString(), start.location.toRegion(buffer.length() ) );
        }
        return result;
    }

    public ParseContext getContext()
    {
        return context;
    }
}
