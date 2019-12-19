package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.TextRegion;

public class RegisterNode extends ASTNode
{
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

    public final Register register;

    public RegisterNode(String value, TextRegion region)
    {
        super(region);
        this.register = Register.of( value );
        if ( this.register == null )
            throw new IllegalArgumentException( "Not a register: '" + value + "'" );
    }

    public static boolean isValidRegister(String value)
    {
        return Register.of( value ) != null;
    }
}
