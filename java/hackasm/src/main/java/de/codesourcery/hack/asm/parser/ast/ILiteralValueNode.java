package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.ParseContext;

public interface ILiteralValueNode extends IASTNode
{
    Integer value(ParseContext ctx);
}
