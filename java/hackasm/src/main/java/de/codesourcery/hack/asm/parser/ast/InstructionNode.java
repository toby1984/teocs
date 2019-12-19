package de.codesourcery.hack.asm.parser.ast;

import de.codesourcery.hack.asm.parser.Instruction;
import de.codesourcery.hack.asm.parser.TextRegion;
import org.apache.commons.lang3.Validate;

public class InstructionNode extends ASTNode
{
    public final Instruction insn;

    public InstructionNode(Instruction insn, TextRegion region)
    {
        super(region);
        Validate.notNull( insn, "insn must not be null" );
        this.insn = insn;
    }
}
