package nazario.emulator.util;

import nazario.emulator.Memory;
import nazario.emulator.Registrars;

@FunctionalInterface
public interface InstructionsFunction {
    short apply(short programCounter, Memory memory, Registrars registrars, int size, int cycles);
}
