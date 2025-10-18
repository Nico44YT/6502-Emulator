package nazario.emulator.util;

public record InstructionInfo(int opcode, int size, int cycles, AddressingMode mode) {
}
