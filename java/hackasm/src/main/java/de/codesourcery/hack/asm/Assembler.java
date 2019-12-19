package de.codesourcery.hack.asm;

import de.codesourcery.hack.asm.parser.Instruction;
import de.codesourcery.hack.asm.parser.Lexer;
import de.codesourcery.hack.asm.parser.ParseContext;
import de.codesourcery.hack.asm.parser.Parser;
import de.codesourcery.hack.asm.parser.Scanner;
import de.codesourcery.hack.asm.parser.Symbol;
import de.codesourcery.hack.asm.parser.ast.AST;
import de.codesourcery.hack.asm.parser.ast.ASTNode;
import de.codesourcery.hack.asm.parser.ast.IASTNode;
import de.codesourcery.hack.asm.parser.ast.InstructionNode;
import de.codesourcery.hack.asm.parser.ast.LabelNode;

import java.io.ByteArrayOutputStream;

public class Assembler
{
    public byte[] assemble(String source)
    {
        final Parser p = new Parser();

        // parse source
        final AST ast = p.parse( new Lexer( new Scanner( source ) ) );

        final ParseContext parseCtx = p.getContext();

        // first pass: assign addresses to labels
        // easy as each instruction is 16 bytes only
        ast.visit( new IASTNode.IterationVisitor<Integer>()
        {
            private int insnCount;

            @Override
            public void visit(ASTNode node, IASTNode.IterationContext<Integer> ctx)
            {
                if ( node instanceof InstructionNode ) {
                    insnCount++;
                } else if ( node instanceof LabelNode ) {
                    final Symbol symbol = parseCtx.symbolTable().get( ( (LabelNode) node ).name );
                    // memory uses 16-bit addressing
                    symbol.setValue( insnCount , symbol.type() );
                }
            }
        });

        // second pass: generate code
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final IObjectCodeWriter writer = new IObjectCodeWriter()
        {
            private int address; // memory uses word (16-bit) addressing

            @Override
            public int currentAddress()
            {
                return address; // memory uses word (16-bit) addressing
            }

            @Override
            public void writeWord(int value)
            {
                address+=1; // memory uses word (16-bit) addressing
            }

            @Override
            public void close() throws Exception
            {
                bout.close();
            }
        };

        ast.visit( new IASTNode.IterationVisitor<Integer>()
        {
            @Override
            public void visit(ASTNode node, IASTNode.IterationContext<Integer> ctx)
            {
                if ( node instanceof InstructionNode ) {
                    ((InstructionNode) node).insn.genCode(writer,parseCtx);
                }
            }
        });

        return bout.toByteArray();
    }
}
