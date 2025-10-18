package nazario.emulator.util;

import nazario.emulator.IllegalInstructionSet;

public class IllegalInstruction extends Instruction {
    public IllegalInstruction(InstructionInfo[] infos, InstructionsFunction function) {
        super(infos, function, false);

        IllegalInstructionSet.instructionRegistry.add(this);
    }
}
