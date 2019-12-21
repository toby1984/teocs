package de.codesourcery.hack.asm;

import de.codesourcery.hack.asm.parser.*;
import de.codesourcery.hack.asm.parser.ast.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Assembler
{
    public static void main(String[] args) throws IOException
    {
        final InputStream in;
        final OutputStream out;
        switch(args.length)
        {
            case 1:
                in = System.in;
                out = new FileOutputStream( new File(args[0] ) );
                break;
            case 2:
                in  = new FileInputStream( new File(args[0] ) );
                out = new FileOutputStream( new File(args[1] ) );
                break;
            default:
                throw new RuntimeException("Syntax: " +
                                           "<input file> <output file> - Reads source from input file, writes to output file" +
                                           "<outputfile> => Reads source from StdIn, writes to output file");
        }

        try ( out )
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
            final List<String> lines = new ArrayList<>();
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                lines.add( line );
            }
            final String src = String.join( "\n", lines );
            final byte[] data = new Assembler().assemble( src );
            out.write( data );
        }
    }

    public byte[] assemble(String source)
    {
        final Parser p = new Parser();

        // parse source
        final AST ast = p.parse( new Lexer( new Scanner( source ) ) );

        // expand macros
        final ParseContext parseCtx = p.getContext();

        ast.visit((IASTNode.IterationVisitor<Integer>) (node, ctx) ->
        {
            if ( node instanceof MacroInvocation )
            {
                final MacroInvocation inv = (MacroInvocation) node;
                final Identifier name = inv.getName();

                final Symbol sym = parseCtx.symbolTable().get(name);
                if ( sym == null ) {
                    throw new RuntimeException("Unknown macro "+name);
                }
                if ( ! sym.is(Symbol.Type.MACRO ) ) {
                    throw new RuntimeException("Expected macro "+name+" but found "+sym);
                }
                final MacroDefinition def = (MacroDefinition) sym.value();
                final int expectedArgCount = def.getArgumentCount();
                final int actualArgCount = inv.getArgumentCount();
                if ( expectedArgCount != actualArgCount) {
                    throw new RuntimeException("Macro " + name + " expected " + expectedArgCount
                                                   + " but invocation has "+ actualArgCount);
                }
                node.replaceWith( expandMacroInvocation( inv, def, parseCtx ) );
            }
        });

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
                bout.write( ( value >> 8 & 0xff ) ); // hi byte
                bout.write( value & 0xff ); // low-byte
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
            if ( node instanceof DirectiveNode )
            {
                final DirectiveNode.Directive d = ((DirectiveNode) node).directive;
                switch(d) {
                    case MACRO:
                        ctx.dontGoDeeper();
                        break;
                    case WORD:
                        for ( ASTNode child : node.children() )
                        {
                            final int value = evaluate( child, parseCtx );
                            if ( (value & 0xffff0000) != 0 && ( value & 0xffff0000) != 0xffff0000 )
                            {
                                throw new IllegalArgumentException("Value out of 16-bit range: "+value);
                            }
                            writer.writeWord( value );
                        }
                        ctx.dontGoDeeper();
                        break;
                    default:
                        throw new IllegalStateException( "Unexpected value: " + d );
                }
            }
            else if ( node instanceof InstructionNode ) {
                genCode((InstructionNode) node, parseCtx, writer);
                ctx.dontGoDeeper();
            }
        });
        return bout.toByteArray();
    }

    private ASTNode expandMacroInvocation(MacroInvocation inv, MacroDefinition def, ParseContext ctx)
    {
        final List<Token> body = def.getBody();
        final String src = body.stream().map( x -> x.value ).collect(Collectors.joining());

        // try to parse as expression first
        Lexer lexer = new Lexer( new Scanner(src) );
        Parser p = setupParser(def, ctx, lexer);

        ASTNode ast = p.parseExpression();
        if ( ast == null || ! lexer.eof() )
        {
            // try to parse as statements
            lexer = new Lexer( new Scanner(src) );
            p = setupParser(def, ctx, lexer);
            ast = p.parseProgram();
            if ( ast == null || ! lexer.eof() ) {
                throw new RuntimeException("Failed to parse body of macro "+def.getName());
            }
        }
        // now replace all identifiers inside the macro body
        // with copies of the like-named parameters
        ast.visit((IASTNode.IterationVisitor<Integer>) (node, ctx1) ->
        {
            if ( node.isIdentifier() )
            {
                int argIdx = def.getArgumentIndex(node.asIdentifierNode().name);
                if ( argIdx != -1 ) {
                    final ASTNode argCopy = inv.getArguments().get(argIdx).copySubtree();
                    node.replaceWith(argCopy);
                }
            }
        });
        return ast;
    }

    private Parser setupParser(MacroDefinition def, ParseContext ctx, Lexer lexer)
    {
        final SymbolTable table = new SymbolTable( ctx.symbolTable().get( def.getName() ), ctx.symbolTable() );
        final ParseContext subCtx= new ParseContext(table);
        return new Parser(lexer, subCtx);
    }

    public static int bytesToWord(byte[] array,int offset) {
        byte hiByte = array[offset];
        byte lowByte = array[offset+1];
        return ((hiByte & 0xff) << 8) | (lowByte & 0xff);
    }

    public static void wordToBytes(int word,byte[] array,int offset) {
        array[offset]   = (byte) ((word & 0xff00)>>8);
        array[offset+1] = (byte) (word & 0x00ff);
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
            value  = 0b1110_0000_0000_0000;

            // check RHS to figure out which operation to perform
            final List<OperatorNode> assignments = insn.findAll(IASTNode::isOperator).stream()
                                                   .map(OperatorNode.class::cast)
                                                   .filter(x -> x.operator == Operator.ASSIGNMENT )
                                                   .collect(Collectors.toList());
            if ( assignments.size() > 1 ) {
                throw new IllegalStateException("Expression contains multiple assignment operators, at most 1 is allowed: "+insn);
            }

            /*
             * determine function bits
             */
            final ASTNode func;
            if ( assignments.size() == 1 ) {
                func = assignments.get(0).rhs();
            } else {
                if ( insn.childCount() > 2 ) {
                    throw new IllegalStateException("Expected at most two child nodes, got "+insn.childCount());
                }
                func = insn.firstChild();
                if ( func instanceof JumpNode) {
                    throw new IllegalStateException("Unexpected jump node");
                }
            }

            final int funcBits;
            final List<OperatorNode> operators =
            func.findAll( IASTNode::isOperator ).stream().map( OperatorNode.class::cast ).collect( Collectors.toList());

            if ( operators.size() > 1 ) {
                throw new IllegalStateException("Expected at most one operator but got "+operators);
            }
            if ( operators.isEmpty() || ( operators.get(0).operator == Operator.UNARY_MINUS && operators.get(0).firstChild().isNumberLiteral() ) )
            {
                // either a constant value or a register
                if ( func instanceof IdentifierNode ) {
                    switch( toRegister( func ) )
                    {
                        case D:
                            funcBits = 0b0001100; // D
                            break;
                        case A:
                            funcBits = 0b0110000; // A
                            break;
                        case M:
                            funcBits = 0b1110000; // M
                            break;
                        default:
                            throw new RuntimeException("Unhandled switch/case: "+toRegister( func ));
                    }
                } else {
                    // constant
                    final int constant = evaluate( func, ctx );
                    switch( constant ) {
                        case 0:
                            funcBits = 0b0101010; // 0
                            break;
                        case 1:
                            funcBits = 0b0111111; // 1
                            break;
                        case -1:
                            funcBits = 0b0111010; // -1
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported constant "+constant+", only 0/1/-1 are allowed");
                    }
                }
            } else {
                // exactly one operator
                final OperatorNode opNode = operators.get( 0 );
                switch( opNode.operator )
                {
                    case UNARY_MINUS:
                        if ( opNode.firstChild().isNumberLiteral() ) {
                            int val = evaluate( opNode.firstChild(), ctx );
                            if ( val != -1 ) {
                                throw new IllegalArgumentException( "Only -1 is supported, got "+val );
                            }
                            funcBits = 0b0111010; // -1
                        }
                        else
                        {
                            switch (toRegister( opNode.firstChild() ))
                            {
                                case D:
                                    funcBits = 0b0001111; // -D
                                    break;
                                case A:
                                    funcBits = 0b0110011; // -A
                                    break;
                                case M:
                                    funcBits = 0b1110011; // -M
                                    break;
                                default:
                                    throw new RuntimeException( "Internal error, unhandled register " + toRegister( opNode.firstChild() ) );
                            }
                        }
                        break;
                    case BITWISE_NOT:
                        switch( toRegister( opNode.firstChild() ) )
                        {
                            case D:
                                funcBits = 0b0001101; // !D
                                break;
                            case A:
                                funcBits = 0b0110001; // !A
                                break;
                            case M:
                                funcBits = 0b1110001; // !M
                                break;
                            default:
                                throw new RuntimeException( "Internal error, unhandled register " + toRegister( opNode.firstChild() ) );
                        }
                        break;
                    case MINUS:
                    case PLUS:
                        ASTNode lhs = opNode.lhs();
                        ASTNode rhs = opNode.rhs();

                        List<Register> regs = new ArrayList<>();
                        NumberNode number = null;
                        regs.add( toRegister( lhs ) );
                        if ( rhs instanceof NumberNode ) {
                            number = (NumberNode) rhs;
                            if ( evaluate( number, ctx ) != 1 ) {
                                throw new IllegalArgumentException( "Only literal '1' is allowed as a constant expression, got "+number );
                            }
                        } else {
                            regs.add( toRegister( rhs ) );
                        }

                        if ( number != null ) { // <register> <+|-> <value>
                            if ( opNode.operator == Operator.PLUS ) {
                                switch( regs.iterator().next() ) {
                                    case D:
                                        funcBits = 0b0011111; // D + 1
                                        break;
                                    case A:
                                        funcBits = 0b0110111; // A + 1
                                        break;
                                    case M:
                                        funcBits = 0b1110111; // M + 1
                                        break;
                                    default:
                                        throw new RuntimeException( "Internal error, unhandled switch/case: "+regs.iterator().next());
                                }
                            } else {
                                switch( regs.iterator().next() ) {
                                    case D:
                                        funcBits = 0b0001110; // D - 1
                                        break;
                                    case A:
                                        funcBits = 0b0110010; // A - 1
                                        break;
                                    case M:
                                        funcBits = 0b1110010; // M - 1
                                        break;
                                    default:
                                        throw new RuntimeException( "Internal error, unhandled switch/case: "+regs.iterator().next());
                                }
                            }
                        } else { // <register> <+|-> <register>

                            Register r0 = regs.get(0);
                            Register r1 = regs.get(1);

                            if ( r0 == Register.M && r1 == Register.D) {
                                // M-D
                                funcBits = 0b1000111;
                            } else if ( r0 == Register.A && r1 == Register.D) {
                                // A-D
                                funcBits = 0b0000111;
                            } else if ( r0 == Register.D && r1 == Register.A) {
                                // D+A or D-A
                                funcBits = opNode.operator == Operator.PLUS ? 0b0000010 : 0b0010011;
                            } else if ( r0 == Register.D && r1 == Register.M ) {
                                // D+M or D-M
                                funcBits = opNode.operator == Operator.PLUS ? 0b1000010 : 0b1010011;
                            } else {
                                throw new IllegalArgumentException( "Unsupported register combination "+regs );
                            }
                        }

                        break;
                    case BITWISE_AND:
                    case BITWISE_OR:
                        Register r0 = toRegister( opNode.lhs() );
                        Register r1 = toRegister( opNode.rhs() );
                        if ( r0 == Register.D && r1 == Register.A ) {
                            // D&A   OR   D|A
                            funcBits = opNode.operator == Operator.BITWISE_AND ? 0b0000000 : 0b0010101;
                        } else if ( r0 == Register.D && r1 == Register.M ) {
                            // D&M   OR   D|M
                            funcBits = opNode.operator == Operator.BITWISE_AND ? 0b1000000 : 0b1010101;
                        } else {
                            throw new IllegalArgumentException( "Invalid register combination "+r0+","+r1 );
                        }
                        break;
                    default:
                        throw new IllegalArgumentException( "Unhandled operator "+opNode );
                }
            }

            /*
             * determine destination bits
             */
            final int destinationBits;
            if ( assignments.size() == 1 ) {
                final ASTNode lhs = assignments.get( 0 ).lhs();
                if ( ! (lhs instanceof IdentifierNode ) ) {
                    throw new IllegalStateException("LHS of assignment is no identifier but "+lhs+" ?");
                }
                boolean writeA=false, writeD=false, writeM = false;
                for ( char c : ((IdentifierNode) lhs).name.value.toLowerCase().toCharArray() )
                {
                    switch( c ) {
                        case 'd':
                            if ( writeD ) {
                                throw new IllegalStateException("Duplicate register '"+c+"' on LHS of assignment");
                            }
                            writeD = true;
                            break;
                        case 'm':
                            if ( writeM ) {
                                throw new IllegalStateException("Duplicate register '"+c+"' on LHS of assignment");
                            }
                            writeM = true;
                            break;
                        case 'a':
                            if ( writeA ) {
                                throw new IllegalStateException("Duplicate register '"+c+"' on LHS of assignment");
                            }
                            writeA = true;
                            break;
                        default:
                            throw new IllegalStateException("Invalid destination '"+c+"' in "+lhs);
                    }
                }
                destinationBits = ( writeA ? 1<<2 : 0) | ( writeD ? 1<<1 : 0) | ( writeM ? 1<<0 : 0);
            } else {
                destinationBits = 0b000;
            }

            /*
             * determine jump bits
             */
            final List<JumpNode> jump = insn.findAll(IASTNode::isJump).stream().map(JumpNode.class::cast).collect(Collectors.toList());
            if ( jump.size() > 1 ) {
                throw new IllegalStateException("Expression contains multiple jump instructions, at most 1 is allowed: "+insn);
            }
            final int jumpBits;
            if ( jump.size() == 1 ) {
                switch( jump.get(0).jump )
                {
                    case NONE:
                        jumpBits = 0b000;
                        break;
                    case GT:
                        jumpBits = 0b001;
                        break;
                    case EQ:
                        jumpBits = 0b010;
                        break;
                    case GTE:
                        jumpBits = 0b011;
                        break;
                    case LT:
                        jumpBits = 0b100;
                        break;
                    case NEQ:
                        jumpBits = 0b101;
                        break;
                    case LTE:
                        jumpBits = 0b110;
                        break;
                    case UNCONDITIONAL:
                        jumpBits = 0b111;
                        break;
                    default:
                        throw new RuntimeException("Internal error, unhandled jump condition");
                }
            } else {
                jumpBits = 0b000; // no jump
            }
            value |=  (funcBits << 6 ) | (destinationBits << 3) | jumpBits;
        }
        writer.writeWord( value );
    }

    private static Register toRegister(ASTNode node)
    {
        if ( ! (node instanceof IdentifierNode) ) {
            throw new IllegalArgumentException("Expected a register but got "+node);
        }
        final Register reg = Register.of( ((IdentifierNode) node).name.value );
        if ( reg == null ) {
            throw new IllegalArgumentException( "Expected a register but got "+node );
        }
        return reg;
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
