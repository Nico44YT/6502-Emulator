package nazario.emulator.util;

import nazario.emulator.InstructionSet;

import java.util.function.Function;

public class Instruction {

    protected final Function<AddressingMode, InstructionInfo> factory;
    protected final Function<AddressingMode, FlagFunction> flagFunctionFactory;

    public Instruction(Function<AddressingMode, InstructionInfo> factory, Function<AddressingMode, FlagFunction> flagFunctionFactory) {
        this.factory = factory;
        this.flagFunctionFactory = flagFunctionFactory;

        InstructionSet.instructionRegistry.add(this);
    }

    public Function<AddressingMode, InstructionInfo> getFactory() {
        return factory;
    }

    public Function<AddressingMode, FlagFunction> getFlagFunctionFactory() {
        return flagFunctionFactory;
    }
}
