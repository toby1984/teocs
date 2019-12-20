package de.codesourcery.hack.asm.parser;

import de.codesourcery.hack.asm.Jump;
import de.codesourcery.hack.asm.parser.ast.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ParserTest
{
    private Parser parser;

    public void setup() {
        parser = new Parser();
    }

    @Test
    public void parse()
    {
        final AST ast = parse("" );
        assertNotNull(ast);
    }

    @Test
    public void parseCommentLine()
    {
        final AST ast = parse("# this is a comment" );
        assertNotNull(ast);
        assertTrue( ast.hasChildren() );
        assertTrue( ast.child(0) instanceof StatementNode );
        StatementNode stmt = (StatementNode) ast.child( 0);
        assertTrue( stmt.hasChildren() );
        assertTrue( stmt.child(0) instanceof CommentNode );
        assertEquals( "# this is a comment" , ((CommentNode) stmt.firstChild()).value );
    }

    @Test
    public void parseLabelLine()
    {
        final AST ast = parse("label:" );
        assertNotNull(ast);
        assertTrue( ast.hasChildren() );
        assertTrue( ast.child(0) instanceof StatementNode );
        StatementNode stmt = (StatementNode) ast.child( 0);
        assertTrue( stmt.hasChildren() );
        assertTrue( stmt.child(0) instanceof LabelNode );
        assertEquals( "label" , ((LabelNode) stmt.firstChild()).name.value );

        final SymbolTable symbolTable = parser.getContext().symbolTable();
        assertNotNull( symbolTable );
        final Identifier name = Identifier.of( "label" );
        final Symbol symbol = symbolTable.get( name );
        assertNotNull( symbol );
        assertEquals( name, symbol.name );
        assertEquals( Symbol.Type.LABEL, symbol.type() );
        assertNull( symbol.value() );
    }

    @Test
    public void parseLabelLineWithComment()
    {
        final AST ast = parse("label: # this is a comment" );
        assertNotNull(ast);
        assertTrue( ast.hasChildren() );
        assertTrue( ast.child(0) instanceof StatementNode );
        StatementNode stmt = (StatementNode) ast.child( 0);
        assertTrue( stmt.hasChildren() );
        assertTrue( stmt.child(0) instanceof LabelNode );
        assertEquals( "label" , ((LabelNode) stmt.firstChild()).name.value );

        final SymbolTable symbolTable = parser.getContext().symbolTable();
        assertNotNull( symbolTable );
        final Identifier name = Identifier.of( "label" );
        final Symbol symbol = symbolTable.get( name );
        assertNotNull( symbol );
        assertEquals( name, symbol.name );
        assertEquals( Symbol.Type.LABEL, symbol.type() );
        assertNull( symbol.value() );

        // check comment
        assertTrue( stmt.child(1) instanceof CommentNode );
        assertEquals( "# this is a comment" , ((CommentNode) stmt.child(1)).value );
    }

    @Test
    public void parseLabelLineWithCommentAndInstruction()
    {
        final AST ast = parse("label: @42 # this is a comment" );
        assertNotNull(ast);
        assertTrue( ast.hasChildren() );
        assertTrue( ast.child(0) instanceof StatementNode );
        StatementNode stmt = (StatementNode) ast.child( 0);
        assertTrue( stmt.hasChildren() );
        assertTrue( stmt.child(0) instanceof LabelNode );
        assertEquals( "label" , ((LabelNode) stmt.firstChild()).name.value );

        final SymbolTable symbolTable = parser.getContext().symbolTable();
        assertNotNull( symbolTable );
        final Identifier name = Identifier.of( "label" );
        final Symbol symbol = symbolTable.get( name );
        assertNotNull( symbol );
        assertEquals( name, symbol.name );
        assertEquals( Symbol.Type.LABEL, symbol.type() );
        assertNull( symbol.value() );

        // check instruction
        assertTrue( stmt.child(1) instanceof InstructionNode );
        final InstructionNode insn = (InstructionNode) stmt.child( 1 );
        assertTrue( insn.isLoadA );

        // source
        assertTrue( insn.child(0) instanceof NumberNode );
        assertEquals( 42 , ((NumberNode) insn.child(0)).value() );

        // check comment
        assertTrue( stmt.child(2) instanceof CommentNode );
        assertEquals( "# this is a comment" , ((CommentNode) stmt.child(2)).value );
    }

    @Test
    public void parseWordDirective()
    {
        final AST ast = parse( ".word 0x1234" );
        assertNotNull( ast );
        assertTrue( ast.hasChildren() );
        assertTrue( ast.child( 0 ) instanceof StatementNode );
        StatementNode stmt = (StatementNode) ast.child( 0 );
        assertTrue( stmt.hasChildren() );
        assertTrue( stmt.child( 0 ) instanceof DirectiveNode );

        final DirectiveNode insn = (DirectiveNode) stmt.firstChild();
        assertEquals( DirectiveNode.Directive.WORD, insn.directive );
    }

    @Test
    public void parseFunkyInstruction()
    {
        final AST ast = parse("amd=d&m;jmp");
        assertNotNull(ast);
        assertTrue(ast.hasChildren());
        assertTrue(ast.child(0) instanceof StatementNode);
        StatementNode stmt = (StatementNode) ast.child(0);
        assertTrue(stmt.hasChildren());
        assertTrue(stmt.child(0) instanceof InstructionNode);

        final InstructionNode insn = (InstructionNode) stmt.firstChild();
        assertFalse( insn.isLoadA );

        final List<ASTNode> operators = insn.findAll(x -> x instanceof OperatorNode );
        assertEquals(2, operators.size() );
        assertTrue(operators.stream().anyMatch(x -> x instanceof OperatorNode && ((OperatorNode) x).operator == Operator.ASSIGNMENT));
        assertTrue(operators.stream().anyMatch(x -> x instanceof OperatorNode && ((OperatorNode) x).operator == Operator.BITWISE_AND));

        final ASTNode assignment = insn.findFirst(x -> x instanceof OperatorNode && ((OperatorNode) x).operator == Operator.ASSIGNMENT );
        assertNotNull(assignment);
        assertEquals( Identifier.of("amd"), ((IdentifierNode) assignment.lhs()).name );

        final ASTNode and = insn.findFirst(x -> x instanceof OperatorNode && ((OperatorNode) x).operator == Operator.BITWISE_AND);
        assertNotNull(assignment);
        assertEquals( Identifier.of("d"), ((IdentifierNode) and.lhs()).name );
        assertEquals( Identifier.of("m"), ((IdentifierNode) and.rhs()).name );

        final ASTNode jmp = insn.findFirst(x -> x instanceof JumpNode);
        assertNotNull(jmp);
        assertEquals( Jump.UNCONDITIONAL, ((JumpNode) jmp).jump );
    }

    private AST parse(String s) {
        this.parser = new Parser();
        return parser.parse( new Lexer( new Scanner(s) ) );
    }
}