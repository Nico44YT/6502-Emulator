package nazario.emulator.util;

public class InstructionInfo {
    public FlagFunction flagFunction;
    public final int opcode;
    public final int size;
    public final int cycles;
    public final InstructionsFunction function;

    public InstructionInfo(int opcode, int size, int cycles, InstructionsFunction function) {
        this.opcode = opcode;
        this.size = size;
        this.cycles = cycles;
        this.function = function;
    }
}
