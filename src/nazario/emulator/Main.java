package nazario.emulator;

import nazario.emulator.util.InstructionInfo;
import nazario.emulator.util.InstructionsFunction;
import nazario.emulator.util.Pair;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Pair<InstructionInfo, InstructionsFunction>[] instructions = InstructionSet.getInstructions();
        if(Arrays.stream(args).toList().contains("--illegal-opcodes")) IllegalInstructionSet.addIllegalInstructions(instructions);

        Memory memory = new Memory();
        Registrars registrars = new Registrars();

        Loader.loadIntoMemory("G:\\Projects\\6502 Emulator\\resources\\test.bin", 0x0600, memory);
        byte[] memoryArray = memory.getMemory();

        for(int programCounter = 0x0600; programCounter < memoryArray.length; programCounter++) {
            try{
                byte value = memoryArray[programCounter];

                if(value == 0) {
                    System.out.println("BRK!");
                    break;
                }

                Pair<InstructionInfo, InstructionsFunction> instructionPair = instructions[Byte.toUnsignedInt(value)];
                programCounter = Short.toUnsignedInt(
                        instructionPair.right().apply(
                                programCounter,
                                memory,
                                registrars,
                                instructionPair.left()
                        )
                );
                programCounter--;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        Loader.dumpMemory("G:\\Projects\\6502 Emulator\\resources\\dump.bin", memory);
    }
}
