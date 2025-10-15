package nazario.emulator;

import nazario.emulator.util.InstructionInfo;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Memory memory = new Memory();
        Registrars registrars = new Registrars();
        byte[] memoryArray = memory.getMemory();

        memoryArray[0] = (byte)0xA2; //ldx
        memoryArray[1] = (byte)0x0A; //#10
        memoryArray[2] = (byte)0x86; //stx
        memoryArray[3] = (byte)0x0A; //$0A
        memoryArray[4] = (byte)0x4C; //jmp
        memoryArray[5] = (byte)0xFF; //$FF
        memoryArray[6] = (byte)0xFF; //FF

        for(int index = 0;index < memoryArray.length;index++) {
            byte value = memoryArray[index];

            if(value == 0) continue;

            InstructionInfo instruction = InstructionSet.getInstructions()[Byte.toUnsignedInt(value)];
            index = Short.toUnsignedInt(instruction.function.apply(index, memory, registrars, instruction.size, instruction.cycles));
            instruction.flagFunction.apply(index, memory, registrars, instruction.size, instruction.cycles);

            index--;
        }

        System.out.println(Arrays.toString(memoryArray));
    }
}
