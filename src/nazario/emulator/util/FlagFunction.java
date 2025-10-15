package nazario.emulator.util;

import nazario.emulator.Memory;
import nazario.emulator.Registrars;

@FunctionalInterface
public interface FlagFunction {
    void apply(int programCounter, Memory memory, Registrars registrars, int size, int cycles);
}
