package nazario.emulator.util;

import nazario.emulator.Memory;
import nazario.emulator.Registrars;

@FunctionalInterface
public interface FlagFunction {
    void apply(short programCounter, Memory memory, Registrars registrars, int size, int cycles);
}
