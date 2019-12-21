package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.ParseContext;
import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

public class NumberNode extends ASTNode implements ILiteralValueNode
{
    public enum Type {
        DECIMAL {
            @Override
            public int toInt(String value)
            {
                return Integer.parseInt( value );
            }
        },
        HEXADECIMAL {
            @Override
            public int toInt(String value)
            {
                if ( value.startsWith( "$" ) ) {
                    return Integer.parseInt( value.substring( 1 ) , 16 );
                }
                return Integer.parseInt( value.substring( 2 ),16 );
            }
        },
        BINARY {
            @Override
            public int toInt(String value)
            {
                if ( value.startsWith( "$" ) )
                {
                    return Integer.parseInt( value.substring( 1 ), 2 );
                }
                return Integer.parseInt( value.substring( 2 ), 2 );
            }
        };

        public abstract int toInt(String value);
    }

    public final String stringValue;
    public final Type type;

    public NumberNode(String value, TextRegion region) {
        super(region);
        Validate.notBlank( value, "value must not be null or blank");
        Validate.notNull( region, "region must not be null" );
        this.stringValue = value;
        this.type = getType(value);
    }

    private NumberNode(String stringValue, Type type) {
        this.stringValue = stringValue;
        this.type = type;
    }

    @Override
    public ASTNode copyNodeInternal()
    {
        return new NumberNode(this.stringValue,this.type);
    }

    public int value() {
        return type.toInt( this.stringValue );
    }

    private static Type getType(String value) {
        if ( isBinaryNumber( value ) ) {
            return Type.BINARY;
        }
        if ( isHexNumber( value ) ) {
            return Type.HEXADECIMAL;
        }
        if ( isDecimalNumber( value ) ) {
            return Type.DECIMAL;
        }
        throw new IllegalArgumentException( "Unknown number literal: '" + value + "'" );
    }

    public static boolean isValidNumber(String s)
    {
        return s != null && ( isDecimalNumber(s) || isHexNumber(s) || isBinaryNumber( s ) );
    }

    private static boolean isBinaryNumber(String x)
    {
        final String raw;
        if ( x.startsWith( "%" ) )
        {
            raw = x.substring(1);
        }
        else if ( x.startsWith("0b" ) )
        {
            raw = x.substring( 2 );
        } else {
            return false;
        }
        boolean gotDigits = false;
        for (int i = 1, len = raw.length(); i < len; i++)
        {
            switch (raw.charAt( i ))
            {
                case '0': case '1': gotDigits=true; break;
                default:
                    return false;
            }
        }
        return gotDigits;
    }

    private static boolean isDecimalNumber(String s)
    {
        boolean gotDigits = false;
        for (int i = 0, len = s.length(); i < len; i++)
        {
            switch (s.charAt( i ))
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    gotDigits = true;
                    break;
                default:
                    return false;
            }
        }
        return gotDigits;
    }

    private static boolean isHexNumber(String s)
    {
        String prefix = null;
        if ( s.startsWith( "0x" ) ) {
            prefix = "0x";
        } else if ( s.startsWith( "0X" ) ) {
            prefix = "0X";
        } else if ( s.startsWith( "$" ) ) {
            prefix = "$";
        }
        if ( prefix != null )
        {
            boolean gotDigits = false;
            for ( int i = prefix.length(), len = s.length() ; i < len ; i++ ) {
                switch( s.charAt(i) ) {
                    case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                    case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                    case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                        gotDigits = true;
                        break;
                    default:
                        return false;
                }
            }
            return gotDigits;
        }
        return false;
    }

    @Override
    public Integer value(ParseContext ctx)
    {
        return value();
    }

    @Override
    public String toString()
    {
        return "number: "+stringValue;
    }
}