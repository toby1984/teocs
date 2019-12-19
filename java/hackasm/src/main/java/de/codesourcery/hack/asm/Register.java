package de.codesourcery.hack.asm;

public enum Register
{
    D,A,M;

    public static Register of(String value)
    {
        if ( "D".equals( value ) || "d".equals( value ) )
        {
            return D;
        }
        else if ( "A".equals( value ) || "a".equals( value ) )
        {
            return A;
        }
        else if ( "M".equals( value ) || "m".equals( value ) )
        {
            return M;
        }
        return null;
    }
}