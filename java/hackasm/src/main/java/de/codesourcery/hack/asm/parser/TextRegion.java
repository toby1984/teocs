package de.codesourcery.hack.asm.parser;

import org.apache.commons.lang3.Validate;

public class TextRegion
{
    public Location start;
    public Location end;

    public TextRegion(Location start, Location end)
    {
        Validate.notNull( start, "start must not be null" );
        Validate.notNull( end, "end must not be null" );
        if ( start.offset <= end.offset ) {
            this.start = start;
            this.end = end;
        } else {
            this.start = end;
            this.end = start;
        }
    }

    public TextRegion copy() {
        return new TextRegion( this.start, this.end );
    }

    public int length() {
        return end.offset - start.offset;
    }

    public int startOffset() {
        return start.offset;
    }

    public int endOffset() {
        return end.offset;
    }

    /**
     *
     * @param other
     * @return <code>this</code> instance (for chaining)
     */
    public TextRegion merge(TextRegion other) {
        final Location newStart = this.start.offset < other.start.offset ? this.start : other.start;
        final Location newEnd   = this.end.offset > other.end.offset ? this.end : other.end;
        this.start = newStart;
        this.end = newEnd;
        return this;
    }
}
