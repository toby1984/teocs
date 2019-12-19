package de.codesourcery.hack.asm.parser;

import org.apache.commons.lang3.Validate;

public class Token
{
    public final TokenType type;
    public final Location location;
    public final String value;

    public Token(char value,TokenType type, Location location)
    {
        this(Character.toString(value),type,location);
    }

    public Token(String value,TokenType type, Location location)
    {
        Validate.notNull( value, "value must not be null" );
        Validate.notNull( type, "type must not be null" );
        Validate.notNull( location, "location must not be null" );
        this.type = type;
        this.location = location;
        this.value = value;
    }

    public TextRegion region() {
        return location.toRegion( value.length() );
    }

    public boolean is(TokenType t) {
        return t.equals(this.type);
    }

    public boolean isEOF() {
        return is(TokenType.EOF);
    }

    public boolean isNewline() {
        return is(TokenType.NEWLINE);
    }
}
