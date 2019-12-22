package de.codesourcery.hack.asm;

public interface IObjectCodeWriter extends AutoCloseable
{
    enum Segment {
        RAM,
        ROM;
    }

    void setCurrentSegment(Segment segment);

    Segment currentSegment();

    int currentAddress();

    void writeWord(int value); // write 16 bits
}
