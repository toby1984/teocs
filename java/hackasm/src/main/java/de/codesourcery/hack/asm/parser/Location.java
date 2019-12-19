package de.codesourcery.hack.asm.parser;

import org.apache.commons.lang3.Validate;

public final class Location
{
    public final int line;
    public final int column;
    public final int offset;

    public Location(int line, int column, int offset)
    {
        Validate.isTrue( line > 0 );
        Validate.isTrue( column > 0 );
        Validate.isTrue( column >= 0 );
        this.line = line;
        this.column = column;
        this.offset = offset;
    }

    public TextRegion toRegion(int length) {
        return new TextRegion( this, new Location(line,column+length,offset+length));
    }

    @Override
    public String toString()
    {
        return "Location{" +
               "line=" + line +
               ", column=" + column +
               ", offset=" + offset +
               '}';
    }
}
