package de.codesourcery.hack.asm.parser;

import org.apache.commons.lang3.Validate;

public class Scanner
{
    private final String input;

    private int index;
    private int line=1;
    private int column=1;

    public Scanner(String input) {
        Validate.notNull( input, "input must not be null" );
        this.input = input;
    }

    public boolean eof() {
        return index >= input.length();
    }

    public char next() {
        char result = input.charAt(index++);
        if ( result == '\n' ) {
            line++;
            column = 1;
        }
        return result;
    }

    public char peek() {
        return input.charAt(index);
    }

    public Location location() {
        return new Location(line,column,index);
    }
}
