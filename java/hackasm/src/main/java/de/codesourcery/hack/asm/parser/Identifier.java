package de.codesourcery.hack.asm.parser;

import java.util.Arrays;

public final class Identifier
{
    private static final char[] VALID_FIRST_CHAR = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] VALID_CHARS = "_.0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    static {
        // sort so binary search works properly
        Arrays.sort( VALID_CHARS );
        Arrays.sort( VALID_FIRST_CHAR );
    }

    public final String value;

    public Identifier(String value) {
        this.value = value;
    }

    public static Identifier of(String value) {
        return new Identifier(value);
    }

    public static boolean isValidIdentifier(String s)
    {
        if ( s == null || s.length() == 0 ) {
            return false;
        }
        if ( Arrays.binarySearch( VALID_FIRST_CHAR, s.charAt(0) ) < 0 ) {
            return false;
        }
        for ( int i = 1, len = s.length() ; i < len ; i++ ) {
            if ( Arrays.binarySearch( VALID_CHARS, s.charAt(0) ) < 0 ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( o instanceof Identifier )
        {
            return ( (Identifier) o ).value.equals(this.value);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    @Override
    public String toString()
    {
        return "'"+value+"'";
    }
}