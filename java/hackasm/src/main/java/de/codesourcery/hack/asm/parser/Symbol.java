package de.codesourcery.hack.asm.parser;

import org.apache.commons.lang3.Validate;

public class Symbol
{
    public enum Type {
        LABEL,
        UNKNOWN,
        MACRO
    }

    public final Identifier name;
    private Object value;
    private Type type;

    public Symbol(Identifier name, Object value)
    {
        this(name,value,Type.UNKNOWN);
    }

    public Symbol(Identifier name, Object value,Type type)
    {
        Validate.notNull( name, "name must not be null" );
        Validate.notNull( type, "type must not be null" );
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public Identifier name()
    {
        return name;
    }

    public Object value()
    {
        return value;
    }

    public Type type()
    {
        return type;
    }

    public void setValue(Object value, Type type) {
        Validate.notNull( type, "type must not be null" );
        Validate.isTrue( type != Type.UNKNOWN );
        if ( this.type != Type.UNKNOWN && this.type != type ) {
            throw new IllegalStateException("Refusing to change type of symbol from "+this.type+" -> "+type);
        }
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "Symbol{" +
               "name=" + name +
               ", type=" + type +
               ", value=" + value +
               '}';
    }
}
