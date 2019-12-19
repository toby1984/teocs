package de.codesourcery.hack.asm.parser;

import de.codesourcery.hack.asm.parser.ast.NumberNode;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public class Lexer
{
    private final Scanner scanner;
    private final List<Token> tokens = new ArrayList<>();

    private boolean skipWhitespace = true;

    private final StringBuilder buffer = new StringBuilder();

    public Lexer(Scanner scanner)
    {
        Validate.notNull( scanner, "scanner must not be null" );
        this.scanner = scanner;
    }

    public boolean eof() {
        return peek().isEOF();
    }

    public Token peek() {
        if ( tokens.isEmpty() ) {
            parse();
        }
        return tokens.get(0);
    }

    public Token next() {
        if ( tokens.isEmpty() ) {
            parse();
        }
        return tokens.remove(0);
    }

    private void parse() {

        buffer.setLength( 0 );

        if ( skipWhitespace )
        {
            while ( ! scanner.eof() && isWhitespace( scanner.peek() ) ) {
                scanner.next();
            }
        }
        else
        {
            while ( ! scanner.eof() && isWhitespace( scanner.peek() ) ) {
                buffer.append( scanner.next() );
            }
            if ( buffer.length() > 0 ) {
                tokens.add( new Token( buffer.toString(), TokenType.WHITESPACE, scanner.location() ) );
                return;
            }
        }

        /*
    TEXT,
    DIGITS,
         */

        Location start = scanner.location();
        while ( ! scanner.eof() && tokens.isEmpty() )
        {
            final char c = scanner.peek();
            if ( c == '\n' ) {
                parseBuffer(start);
                tokens.add( new Token( scanner.next() , TokenType.NEWLINE, scanner.location() ) );
                return;
            }
            if ( isWhitespace( c ) ) {
                break;
            }
            switch(c) {
                case ',':
                    parseBuffer(start);
                    tokens.add( new Token( scanner.next() , TokenType.COMMA, scanner.location() ) );
                    return;
                case '.':
                    parseBuffer(start);
                    tokens.add( new Token( scanner.next() , TokenType.DOT, scanner.location() ) );
                    return;
                case ':':
                    parseBuffer(start);
                    tokens.add( new Token( scanner.next() , TokenType.COLON, scanner.location() ) );
                    return;
                case '#':
                    parseBuffer(start);
                    tokens.add( new Token( scanner.next() , TokenType.HASH, scanner.location() ) );
                    return;
                case ';':
                    parseBuffer(start);
                    tokens.add( new Token( scanner.next() , TokenType.SEMICOLON, scanner.location() ) );
                    return;
                case '/':
                    parseBuffer(start);
                    tokens.add( new Token( scanner.next() , TokenType.SLASH, scanner.location() ) );
                    return;
                case '@':
                    parseBuffer(start);
                    tokens.add( new Token( scanner.next() , TokenType.AT, scanner.location() ) );
                    return;
            }
            buffer.append( scanner.next() );
        }
        parseBuffer(start);
        if ( scanner.eof() ) {
            tokens.add( new Token("",TokenType.EOF,scanner.location()) );
        }
    }

    private void parseBuffer(Location start)
    {
        final String value = buffer.toString();
        if ( value.length() == 0 ) {
            return;
        }

        if ( NumberNode.isValidNumber( value ) ) {
            tokens.add( new Token( value, TokenType.NUMBER, start ) );
        }
        else if ( Instruction.isValid( value ) )
        {
            tokens.add( new Token( value, TokenType.INSTRUCTION, start ) );
        }
        else if ( Identifier.isValidIdentifier( value ) )
        {
            tokens.add( new Token( value, TokenType.IDENTIFIER, start ) );
        }
        else {
            tokens.add( new Token( value, TokenType.TEXT, start ) );
        }
    }

    private static boolean isWhitespace(char c) {
        return c == '\t' || c == ' ';
    }

    public void setSkipWhitespace(boolean skipWhitespace)
    {
        this.skipWhitespace = skipWhitespace;
    }
}
