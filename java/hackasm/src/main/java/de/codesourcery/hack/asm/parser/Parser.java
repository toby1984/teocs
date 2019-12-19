package de.codesourcery.hack.asm.parser;

import de.codesourcery.hack.asm.Jump;
import de.codesourcery.hack.asm.parser.ast.*;

import java.util.Collections;
import java.util.Stack;

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
        ASTNode expr;
        if ( token.is(TokenType.AT ) ) {
            consumeToken();
            result = new InstructionNode(true);
            expr = parseExpression();
            if ( expr == null ) {
                fail("Expected value to assign to register A");
            }
            result.add( expr );
            return result;
        }
        expr = parseExpression();
        if ( expr != null ) {
            result = new InstructionNode(false);
            result.add( expr );
            if ( token.is(TokenType.SEMICOLON ) ) {
                // parse destination
                consumeToken();
                if ( ! token.is(TokenType.IDENTIFIER ) ) {
                    fail("Expected a JUMP specification");
                }
                Jump jump = Jump.of(token.value);
                if ( jump == null ) {
                    fail("Expected a JUMP specification");
                }
                result.add( new JumpNode(jump, token.region() ) );
                consumeToken();
            }
        }
        return result;
    }

    private ASTNode parseExpression()
    {
        final Stack<ASTNode> valueStack = new Stack<>();
        final Stack<OperatorNode> opStack = new Stack<>();

        boolean sawOperator = true;

        while ( true )
        {
            if (token.is(TokenType.OPERATOR))
            {
                Operator op = Operator.parseOperator(token.value.charAt(0));
                if ( sawOperator && op == Operator.MINUS ) { // this is actually an unary minus
                    op = Operator.UNARY_MINUS;
                }
                final OperatorNode node = new OperatorNode(op, token.region() );

                consumeToken();

                while ( ! opStack.isEmpty() && mustPop(node,opStack ) ) {
                    popOperator(opStack,valueStack);
                }
                opStack.push(node);
                sawOperator = true;
            }
            else
            {
                final ASTNode atom = parseAtom();
                if ( atom == null ) {
                    break;
                }
                sawOperator = false;
                valueStack.push(atom);
            }
        }
        while ( ! opStack.isEmpty() )
        {
            popOperator(opStack, valueStack );
        }
        if ( valueStack.isEmpty() ) {
            return null;
        }
        if ( valueStack.size() > 1 ) {
            fail("Extra values?");
        }
        return valueStack.peek();
    }

    private void popOperator(Stack<OperatorNode> opStack, Stack<ASTNode> valueStack)
    {
        OperatorNode op = opStack.pop();
        if ( valueStack.size() < op.operator.argumentCount ) {
            fail("Too few arguments for '"+op.operator.literal+"', expected "+op.operator.argumentCount+" but got only "+valueStack.size());
        }
        for ( int i = 0 ; i < op.operator.argumentCount ; i++ ) {
            op.add( valueStack.pop() );
        }
        Collections.reverse(op.children() );
        valueStack.push( op );
    }

    private boolean mustPop(OperatorNode current, Stack<OperatorNode> opStack) {
        /*
        while (  (there is an operator at the top of the operator stack with greater precedence)
               or (the operator at the top of the operator stack has equal precedence and is left associative))
            pop operators from the operator stack onto the output queue.
         */
        final Operator top = opStack.peek().operator;
        return top.precedence > current.operator.precedence || ( top.precedence == current.operator.precedence && top.isLeftAssociative);
    }

    private ASTNode parseAtom()
    {
        ASTNode result = parseNumber();
        if ( result != null ) {
            return result;
        }
        result = parseIdentifier();
        if ( result != null ) {
            return result;
        }
        return null;
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
        if ( token.is(TokenType.HASH) )
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
