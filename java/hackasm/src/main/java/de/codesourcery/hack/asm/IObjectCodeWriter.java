package de.codesourcery.hack.asm;

public interface IObjectCodeWriter extends AutoCloseable
{
    int currentAddress();

    void writeWord(int value); // write 16 bits
}
