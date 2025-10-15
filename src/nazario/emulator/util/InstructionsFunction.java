package nazario.emulator.util;

import nazario.emulator.Memory;
import nazario.emulator.Registrars;

@FunctionalInterface
public interface InstructionsFunction {
    short apply(short currentAddressIndex, Memory memory, Registrars registrars, int size, int cycles);
}
