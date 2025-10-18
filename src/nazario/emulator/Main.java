package nazario.emulator;

import nazario.emulator.util.InstructionInfo;
import nazario.emulator.util.InstructionsFunction;
import nazario.emulator.util.Pair;

public class Main {
    public static void main(String[] args) {
        Memory memory = new Memory();
        Registrars registrars = new Registrars();

        Loader.loadIntoMemory("G:\\Projects\\6502 Emulator\\resources\\test.bin", 0x0600, memory);
        byte[] memoryArray = memory.getMemory();

        for(int programCounter = 0x600; programCounter < memoryArray.length; programCounter++) {
            byte value = memoryArray[programCounter];

            if(value == 0) {
                System.out.println("BRK!");
                break;
            }

            Pair<InstructionInfo, InstructionsFunction> instructionPair = InstructionSet.getInstructions()[Byte.toUnsignedInt(value)];
            programCounter = Short.toUnsignedInt(
                    instructionPair.right().apply(
                            programCounter,
                            memory,
                            registrars,
                            instructionPair.left()
                    )
            );
            programCounter--;
        }

        Loader.dumpMemory("G:\\Projects\\6502 Emulator\\resources\\dump.bin", memory);
    }
}
