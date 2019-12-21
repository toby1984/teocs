package de.codesourcery.hack.asm.parser;

public enum TokenType
{
    AT,
    COLON,
    TEXT,
    NUMBER,
    COMMA,
    SEMICOLON,
    OPERATOR,
    HASH, // starts comments
    DOT,
    EOF,
    NEWLINE,
    WHITESPACE,
    IDENTIFIER,
    ROUND_OPEN,
    ROUND_CLOSE,
    CURLY_OPEN,
    CURLY_CLOSE

}
