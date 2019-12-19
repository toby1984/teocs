package de.codesourcery.hack.asm.parser;

import de.codesourcery.hack.asm.parser.ast.AST;
import de.codesourcery.hack.asm.parser.ast.CommentNode;
import de.codesourcery.hack.asm.parser.ast.InstructionNode;
import de.codesourcery.hack.asm.parser.ast.LabelNode;
import de.codesourcery.hack.asm.parser.ast.NumberNode;
import de.codesourcery.hack.asm.parser.ast.RegisterNode;
import de.codesourcery.hack.asm.parser.ast.StatementNode;
import org.junit.Test;

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
        final AST ast = parse("; this is a comment" );
        assertNotNull(ast);
        assertTrue( ast.hasChildren() );
        assertTrue( ast.child(0) instanceof StatementNode );
        StatementNode stmt = (StatementNode) ast.child( 0);
        assertTrue( stmt.hasChildren() );
        assertTrue( stmt.child(0) instanceof CommentNode );
        assertEquals( "; this is a comment" , ((CommentNode) stmt.firstChild()).value );
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
        final AST ast = parse("label: ; this is a comment" );
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
        assertEquals( "; this is a comment" , ((CommentNode) stmt.child(1)).value );
    }

    @Test
    public void parseLabelLineWithCommentAndInstruction()
    {
        final AST ast = parse("label: move 42 , a ; this is a comment" );
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
        assertEquals( Instruction.MOVE , insn.insn );
        assertEquals( 2 , insn.childCount() );

        // source
        assertTrue( insn.child(0) instanceof NumberNode );
        assertEquals( 42 , ((NumberNode) insn.child(0)).value() );

        // destination
        assertTrue( insn.child(1) instanceof RegisterNode );
        assertEquals( RegisterNode.Register.A , ((RegisterNode) insn.child(1)).register );

        // check comment
        assertTrue( stmt.child(2) instanceof CommentNode );
        assertEquals( "; this is a comment" , ((CommentNode) stmt.child(2)).value );
    }

    private AST parse(String s) {
        this.parser = new Parser();
        return parser.parse( new Lexer( new Scanner(s) ) );
    }
}