package de.codesourcery.hack.asm.parser;

public enum AddressingMode
{
    REGISTER,
    IMMEDIATE, // MOVE #1, A
    INDIRECT // MOVE 123,A    MOVE [M],
}
