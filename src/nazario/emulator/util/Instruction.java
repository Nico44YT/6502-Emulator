package nazario.emulator.util;

import nazario.emulator.InstructionSet;

public class Instruction {

    protected InstructionsFunction function;
    protected InstructionInfo[] infos;

    public Instruction(InstructionInfo[] infos, InstructionsFunction function) {
        this(infos, function, true);
    }

    public Instruction(InstructionInfo[] infos, InstructionsFunction function, boolean addToRegistry) {
        this.function = function;
        this.infos = infos;

        if(addToRegistry) InstructionSet.instructionRegistry.add(this);
    }

    public InstructionsFunction getFunction() {
        return function;
    }

    public InstructionInfo[] getInfos() {
        return infos;
    }
}
