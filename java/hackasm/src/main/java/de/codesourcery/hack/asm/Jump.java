package de.codesourcery.hack.asm;

import org.apache.commons.lang3.StringUtils;

public enum Jump
{
    NONE(null),
    UNCONDITIONAL("JMP"),
    EQ("JEQ"),
    NEQ("JNE"),
    GT("JGT"),
    GTE("JGTE"),
    LT("JLT"),
    LTE("JLE");

    public final String literal;

    Jump(String literal)
    {
        this.literal = literal;
    }

    public static Jump of(String s) {
        if (StringUtils.isEmpty(s) ) {
            return NONE;
        }
        for ( Jump j : values() ) {
            if ( j != NONE && j.literal.equalsIgnoreCase(s ) ) {
                return j;
            }
        }
        return null;
    }
}
