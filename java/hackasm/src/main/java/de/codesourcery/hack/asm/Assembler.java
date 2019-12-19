package de.codesourcery.hack.asm;

import de.codesourcery.hack.asm.parser.*;
import de.codesourcery.hack.asm.parser.ast.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

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

        ast.visit((IASTNode.IterationVisitor<Integer>) (node, ctx) ->
        {
            if ( node instanceof InstructionNode ) {
                genCode((InstructionNode) node, parseCtx, writer);
                ctx.dontGoDeeper();
            }
        });
        return bout.toByteArray();
    }

    private void genCode(InstructionNode insn, ParseContext ctx, IObjectCodeWriter writer) {

        int value;
        if ( insn.isLoadA ) {
            value = evaluate( insn.child(0), ctx );
            if ( value > 0 && (value & ~0b0111_1111_1111_1111) != 0 ) {
                throw new IllegalArgumentException("Address out of range: 0x"+Integer.toHexString(value)+": "+insn);
            }
        }
        else
        {
            // C instruction
            value  = 1 << 15;

            // check RHS to figure out which operation to perform
            final List<OperatorNode> assignments = insn.findAll(IASTNode::isOperator).stream().map(OperatorNode.class::cast).collect(Collectors.toList());
            if ( assignments.size() > 1 ) {
                throw new IllegalStateException("Expression contains multiple assignment operators, at most 1 is allowed: "+insn);
            }

            int destinationBits = 0;
            if ( assignments.size() == 1 ) {

            }
            int funcBits = 0;

            int jumpBits = 0;
            final List<JumpNode> jump = insn.findAll(IASTNode::isJump).stream().map(JumpNode.class::cast).collect(Collectors.toList());
            if ( jump.size() > 1 ) {
                throw new IllegalStateException("Expression contains multiple jump instructions, at most 1 is allowed: "+insn);
            }
            if ( jump.size() == 1 ) {
                switch( jump.get(0).jump )
                {
                    case NONE:
                        break;
                    case UNCONDITIONAL:
                        break;
                    case EQ:
                        break;
                    case NEQ:
                        break;
                    case GT:
                        break;
                    case GTE:
                        break;
                    case LT:
                        break;
                    case LTE:
                        break;
                    default:
                        throw new RuntimeException("Internal error, unhandled jump condition");
                }
            }
            value |= (destinationBits | jumpBits | funcBits );
        }
        writer.writeWord( value );
    }

    private Integer evaluate(ASTNode ast, ParseContext ctx) {

        if ( ast instanceof ILiteralValueNode ) {
            Integer value = ((ILiteralValueNode) ast).value(ctx);
            if ( value == null ) {
                throw new RuntimeException("Failed to evaluate "+ast);
            }
            return value;
        }
        else if ( ast instanceof OperatorNode )
        {
            final Operator op = ((OperatorNode) ast).operator;
            switch(op) {
                case UNARY_MINUS:
                    return -evaluate( ast.child(0), ctx );
                case BITWISE_NOT:
                    return ~evaluate( ast.child(0), ctx );
                case PLUS:
                    return evaluate( ast.child(0), ctx ) + evaluate( ast.child(1), ctx );
                case MINUS:
                    return evaluate( ast.child(0), ctx ) - evaluate( ast.child(1), ctx );
                case BITWISE_AND:
                    return evaluate( ast.child(0), ctx ) & evaluate( ast.child(1), ctx );
                case BITWISE_OR:
                    return evaluate( ast.child(0), ctx ) | evaluate( ast.child(1), ctx );
                default:
                    throw new RuntimeException("Don't know how to evaluate operator "+ast);
            }
        }
        throw new RuntimeException("Don't know how to evaluate AST node "+ast);
    }
}
