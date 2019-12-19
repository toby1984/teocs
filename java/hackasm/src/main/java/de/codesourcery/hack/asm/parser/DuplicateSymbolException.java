package de.codesourcery.hack.asm.parser;

import org.apache.commons.lang3.Validate;

public class DuplicateSymbolException extends RuntimeException
{
    public final Symbol existing;

    public DuplicateSymbolException(String message,Symbol existing) {
        super(message);
        Validate.notNull( existing, "existing must not be null" );
        this.existing = existing;
    }

    public DuplicateSymbolException(Symbol existing)
    {
        super("Symbol already exists: "+existing);
        Validate.notNull( existing, "existing must not be null" );
        this.existing = existing;
    }
}
