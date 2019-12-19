package de.codesourcery.hack.asm.parser;

import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable
{
    public final Symbol scope;
    public final SymbolTable parent;

    private final Map<Identifier, Symbol> data = new HashMap<>();

    public SymbolTable()
    {
        this.scope = null;
        this.parent = null;
    }

    public SymbolTable(Symbol scope,SymbolTable parent)
    {
        Validate.notNull( scope, "scope must not be null" );
        Validate.notNull( parent, "parent must not be null" );
        this.parent = parent;
        this.scope = scope;
        if ( parent.get( scope.name() ) != scope ) {
            throw new IllegalArgumentException( "Parent symbol table must contain scope "+scope );
        }
    }

    public Symbol get(Identifier identifier) {
        Symbol result = data.get( identifier );
        if ( result != null ) {
            return result;
        }
        return parent == null ? null : parent.get( identifier );
    }

    public Symbol define(Identifier name,Object value,Symbol.Type type)
    {
        Validate.notNull( name, "name must not be null" );
        Validate.isTrue( type != Symbol.Type.UNKNOWN );
        Symbol existing = get(name);
        if ( existing != null ) {
            if ( existing.type() != Symbol.Type.UNKNOWN && existing.type() != type ) {
                throw new DuplicateSymbolException("A symbol with name '"+name+"' already exists, can't change type to "+type,existing);
            }
            existing.setValue( value,type );
            return existing;
        }
        final Symbol sym = new Symbol( name, value, type );
        data.put( name, sym );
        return sym;
    }

    public Symbol declare(Identifier name)
    {
        Validate.notNull( name, "name must not be null" );

        Symbol existing = get(name);
        if ( existing != null ) {
            return existing;
        }
        final Symbol sym = new Symbol( name, null );
        data.put( name, sym );
        return sym;
    }

    public Map<Identifier,Symbol> allSymbols() {
        return this.data;
    }
}