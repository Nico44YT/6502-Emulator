package nazario.emulator;

import nazario.emulator.util.AddressingMode;
import nazario.emulator.util.Instruction;
import nazario.emulator.util.InstructionInfo;

import java.util.ArrayList;
import java.util.List;

/// http://www.6502.org/users/obelisk/6502/reference.html
///
/// http://www.6502.org/users/obelisk/6502/instructions.html
public class InstructionSet {
    public static final List<Instruction> instructionRegistry = new ArrayList<>();

    private static InstructionInfo[] instructions;
    public static InstructionInfo[] getInstructions() {
        if(instructions == null) {
            instructions = new InstructionInfo[0x100];

            instructionRegistry.forEach(instruction -> {
                for (AddressingMode addressingMode : AddressingMode.values()) {
                    InstructionInfo info = instruction.getFactory().apply(addressingMode);

                    if(info == null) continue;

                    info.flagFunction = instruction.getFlagFunctionFactory().apply(addressingMode);
                    instructions[info.opcode] = info;
                }
            });
        }

        return instructions;
    }

    //region * Load / Store Operations *
    /// **LDA - Load Accumulator**
    ///
    /// Loads a byte of memory into the accumulator setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LDA*
    public static Instruction LDA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE -> new InstructionInfo(0xA9, 2, 2, (programCounter, memory, registrars, size, cycles) -> {
            registrars.accumulator = memory.getValue(programCounter + 1);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE -> new InstructionInfo(0xA5, 2, 3, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            registrars.accumulator = memory.getValue(address);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE_X -> new InstructionInfo(0xB5, 2, 4, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            byte x = registrars.registerX;
            registrars.accumulator = memory.getValue((address + x) & 0xFF);
            return (short) (programCounter + size);
        });
        case ABSOLUTE -> new InstructionInfo(0xAD, 3, 4, (programCounter, memory, registrars, size, cycles) -> {
            registrars.accumulator = memory.getValueFromAddress(programCounter + 1);
            return (short) (programCounter + size);
        });
        case ABSOLUTE_X -> new InstructionInfo(0xBD, 3, 4, (programCounter, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            registrars.accumulator = memory.getValueFromAddress(programCounter + 1 + registrars.registerX);
            return (short) (programCounter + size);
        });
        case ABSOLUTE_Y -> new InstructionInfo(0xB9, 3, 4, (programCounter, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            registrars.accumulator = memory.getValueFromAddress(programCounter + 1 + registrars.registerY);

            return (short) (programCounter + size);
        });
        case INDIRECT_X -> new InstructionInfo(0xA1, 2, 6, (programCounter, memory, registrars, size, cycles) -> {
            return (short)0;
        });
        case INDIRECT_Y -> new InstructionInfo(0xB1, 2, 5, (programCounter, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            return (short)0;
        });
        case INDIRECT, ZERO_PAGE_Y, ACCUMULATOR, IMPLIED, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    /// **LDX - Load X Register**
    ///
    /// Loads a byte of memory into the X register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LDX*
    public static Instruction LDX = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE -> new InstructionInfo(0xA2, 2, 2, (programCounter, memory, registrars, size, cycles) -> {
            registrars.registerX = memory.getValue(programCounter + 1);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE -> new InstructionInfo(0xA6, 2, 3, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            registrars.registerX = memory.getValue(address);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE_Y -> new InstructionInfo(0xB6, 2, 4, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            registrars.registerX = memory.getValue(address + registrars.registerY);
            return (short) (programCounter + size);
        });
        case ABSOLUTE -> new InstructionInfo(0xAE, 3, 4, (programCounter, memory, registrars, size, cycles) -> {
            registrars.registerX = memory.getValueFromAddress(programCounter + 1);
            return (short) (programCounter + size);
        });
        case ABSOLUTE_Y -> new InstructionInfo(0xBE, 3, 4, (programCounter, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            registrars.registerX = memory.getValueFromAddress(programCounter + 1 + registrars.registerY);

            return (short) (programCounter + size);
        });
        case INDIRECT, INDIRECT_X, INDIRECT_Y, ZERO_PAGE_X, ACCUMULATOR, ABSOLUTE_X, IMPLIED, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerX == 0;
        registry.negativeFlag = (registry.registerX & 0x80) != 0;
    });

    /// **LDY - Load Y Register**
    ///
    /// Loads a byte of memory into the Y register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LDY*
    public static Instruction LDY = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE -> new InstructionInfo(0xA0, 2, 2, (programCounter, memory, registrars, size, cycles) -> {
            registrars.registerY = memory.getValue(programCounter + 1);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE -> new InstructionInfo(0xA4, 2, 3, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            registrars.registerY = memory.getValue(address);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE_X -> new InstructionInfo(0xB4, 2, 4, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            registrars.registerY = memory.getValue(address + registrars.registerX);
            return (short) (programCounter + size);
        });
        case ABSOLUTE -> new InstructionInfo(0xAC, 3, 4, (programCounter, memory, registrars, size, cycles) -> {
            registrars.registerY = memory.getValueFromAddress(programCounter + 1);
            return (short) (programCounter + size);
        });
        case ABSOLUTE_X -> new InstructionInfo(0xBC, 3, 4, (programCounter, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            registrars.registerY = memory.getValueFromAddress(programCounter + 1 + registrars.registerX);

            return (short) (programCounter + size);
        });
        case INDIRECT, INDIRECT_X, INDIRECT_Y, ZERO_PAGE_Y, ACCUMULATOR, ABSOLUTE_Y, IMPLIED, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerY == 0;
        registry.negativeFlag = (registry.registerY & 0x80) != 0;
    });

    /// **STA - Store Accumulator**
    ///
    /// Stores the contents of the accumulator into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STA*
    public static Instruction STA = new Instruction(addressingMode -> switch(addressingMode) {
        case ZERO_PAGE -> new InstructionInfo(0x85, 2, 3, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            memory.writeValue(address, registrars.accumulator);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE_X -> new InstructionInfo(0x95, 2, 4, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            byte x = registrars.registerX;
            memory.writeValue((address + x) & 0xFF, registrars.accumulator);
            return (short) (programCounter + size);
        });
        case ABSOLUTE -> new InstructionInfo(0x8D, 3, 4, (programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            memory.writeValue(address, registrars.accumulator);
            return (short) (programCounter + size);
        });
        case ABSOLUTE_X -> new InstructionInfo(0x9D, 3, 5, (programCounter, memory, registrars, size, cycles) -> {
            int baseAddress = memory.getUnsignedAddress(programCounter + 1);
            int address = baseAddress + registrars.registerX;
            memory.writeValue(address, registrars.accumulator);

            return (short) (programCounter + size);
        });
        case ABSOLUTE_Y -> new InstructionInfo(0x99, 3, 5, (programCounter, memory, registrars, size, cycles) -> {
            int baseAddress = memory.getUnsignedAddress(programCounter + 1);
            int address = baseAddress + registrars.registerY;
            memory.writeValue(address, registrars.accumulator);

            return (short) (programCounter + size);
        });
        case INDIRECT_X -> new InstructionInfo(0x81, 2, 6, (programCounter, memory, registrars, size, cycles) -> {
            return (short)0;
        });
        case INDIRECT_Y -> new InstructionInfo(0x91, 2, 6, (programCounter, memory, registrars, size, cycles) -> {
            return (short)0;
        });
        case IMMEDIATE, INDIRECT, ZERO_PAGE_Y, ACCUMULATOR, IMPLIED, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **STX - Store X Register**
    ///
    /// Stores the contents of the X register into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STX*
    public static Instruction STX = new Instruction(addressingMode -> switch(addressingMode) {
        case ZERO_PAGE -> new InstructionInfo(0x86, 2, 3, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            memory.writeValue(address, registrars.registerX);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE_Y -> new InstructionInfo(0x96, 2, 4, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            byte y = registrars.registerY;
            memory.writeValue((address + y) & 0xFF, registrars.registerX);
            return (short) (programCounter + size);
        });
        case ABSOLUTE -> new InstructionInfo(0x8E, 3, 4, (programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            memory.writeValue(address, registrars.registerX);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE_X, INDIRECT_X, ABSOLUTE_Y, INDIRECT_Y, ABSOLUTE_X, IMMEDIATE, INDIRECT, ACCUMULATOR, IMPLIED, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **STY - Store Y Register**
    ///
    /// Stores the contents of the Y register into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STY*
    public static Instruction STY = new Instruction(addressingMode -> switch(addressingMode) {
        case ZERO_PAGE -> new InstructionInfo(0x84, 2, 3, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            memory.writeValue(address, registrars.registerY);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE_X -> new InstructionInfo(0x94, 2, 4, (programCounter, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(programCounter + 1);
            byte x = registrars.registerX;
            memory.writeValue((address + x) & 0xFF, registrars.registerY);
            return (short) (programCounter + size);
        });
        case ABSOLUTE -> new InstructionInfo(0x8C, 3, 4, (programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            memory.writeValue(address, registrars.registerY);
            return (short) (programCounter + size);
        });
        case ZERO_PAGE_Y, INDIRECT_X, ABSOLUTE_Y, INDIRECT_Y, ABSOLUTE_X, IMMEDIATE, INDIRECT, ACCUMULATOR, IMPLIED, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});
    //endregion

    //region * Register Transfers *
    /// **TAX - Transfer Accumulator to X**
    ///
    /// Copies the current contents of the accumulator into the X register and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TAX*
    public static Instruction TAX = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0xAA, 1, 2, (programCounter, memory, registrars, size, cycles) -> {
            registrars.registerX = registrars.accumulator;
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerX == 0;
        registry.negativeFlag = (registry.registerX & 0x80) != 0;
    });

    /// **TAY - Transfer Accumulator to Y**
    ///
    /// Copies the current contents of the accumulator into the Y register and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TAY*
    public static Instruction TAY = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0xA8, 1, 2, (programCounter, memory, registrars, size, cycles) -> {
            registrars.registerY = registrars.accumulator;
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerY == 0;
        registry.negativeFlag = (registry.registerY & 0x80) != 0;
    });

    /// **TXA - Transfer X to Accumulator**
    ///
    /// Copies the current contents of the X register into the accumulator and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TXA*
    public static Instruction TXA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x8A, 1, 2, (programCounter, memory, registrars, size, cycles) -> {
            registrars.accumulator = registrars.registerX;
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    /// **TYA - Transfer Y to Accumulator**
    ///
    /// Copies the current contents of the Y register into the accumulator and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TYA*
    public static Instruction TYA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x98, 1, 2, (programCounter, memory, registrars, size, cycles) -> {
            registrars.accumulator = registrars.registerY;
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });
    //endregion

    //region * Stack Operations *
    /// **TSX - Transfer Stack Pointer to X**
    ///
    /// Copies the current contents of the stack register into the X register and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TSX*
    public static Instruction TSX = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0xBA, 1, 2, (programCounter, memory, registrars, size, cycles) -> {
            registrars.registerX = memory.getValue(memory.stackPointer);
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerX == 0;
        registry.negativeFlag = (registry.registerX & 0x80) != 0;
    });

    /// **TXS - Transfer X to Stack Pointer**
    ///
    /// Copies the current contents of the X register into the stack register.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TXS*
    public static Instruction TXS = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x9A, 1, 2, (programCounter, memory, registrars, size, cycles) -> {
            memory.writeValue(memory.stackPointer, registrars.registerX);
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **PHA - Push Accumulator**
    ///
    /// Pushes a copy of the accumulator on to the stack.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PHA*
    public static Instruction PHA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x48, 1, 3, (programCounter, memory, registrars, size, cycles) -> {
            memory.pushToStack(registrars.accumulator);
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **PHP - Push Processor Status**
    ///
    /// Pushes a copy of the status flags on to the stack.
    ///
    /// http://www.6502.org/users/obelisk/6502/reference.html#PHP
    public static Instruction PHP = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x08, 1, 3, (programCounter, memory, registrars, size, cycles) -> {
            memory.pushToStack(registrars.getProcessorStatus());
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **PLA - Pull Accumulator**
    ///
    /// Pulls an 8 bit value from the stack and into the accumulator. The zero and negative flags are set as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PLA*
    public static Instruction PLA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x68, 1, 4, (programCounter, memory, registrars, size, cycles) -> {
            registrars.accumulator = memory.pullFromStack();
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    /// **PLP - Pull Processor Status**
    ///
    /// Pulls an 8 bit value from the stack and into the processor flags. The flags will take on new states as determined by the value pulled.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PLP*
    public static Instruction PLP = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x28, 1, 4, (programCounter, memory, registrars, size, cycles) -> {
            registrars.setProcessorStatus(memory.pullFromStack());
            return (short) (programCounter + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    //endregion

    //region * Logical * TODO
    ///**AND - Logical AND**
    ///
    ///A logical AND is performed, bit by bit, on the accumulator contents using the contents of a byte of memory.
    ///
    ///*http://www.6502.org/users/obelisk/6502/reference.html#AND*
    public static Instruction AND = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE -> new InstructionInfo(0x29, 2, 2, (programCounter, memory, registrars, size, cycles) -> {
            byte value = memory.getValueFromAddress(programCounter + 1);
            registrars.accumulator = (byte) (Byte.toUnsignedInt(registrars.accumulator) & Byte.toUnsignedInt(value));
            return (short) (programCounter + size);
        });
        case ZERO_PAGE -> new InstructionInfo(0x25, 2, 3, (programCounter, memory, registrars, size, cycles) -> {
            byte value = memory.getValueFromAddress(memory.getZeroPageWord(programCounter + 1));
            registrars.accumulator = (byte) (Byte.toUnsignedInt(registrars.accumulator) & Byte.toUnsignedInt(value));
            return (short) (programCounter + size);
        });
        case IMPLIED, ABSOLUTE, ZERO_PAGE_Y, ZERO_PAGE_X, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });
    //endregion

    //region * Arithmetic * TODO
    public static Instruction ADC = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE -> new InstructionInfo(0x69, 2, 2, (programCounter, memory, registrars, size, cycles) -> {
            byte zeroNum = registrars.zeroFlag ? (byte)1 : (byte)0;

            return (short) (programCounter + size);
        });
        case ZERO_PAGE -> null;
        case ZERO_PAGE_X -> null;
        case ABSOLUTE -> null;
        case ZERO_PAGE_Y, INDIRECT_X, ABSOLUTE_Y, INDIRECT_Y, ABSOLUTE_X, INDIRECT, ACCUMULATOR, IMPLIED, RELATIVE -> null;
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    //endregion

    //region * Increments & Decrements * TODO
    //endregion

    //region * Shifts *
    /// **ASL - Arithmetic Shift Left**
    ///
    /// This operation shifts all the bits of the accumulator or memory contents one bit left. Bit 0 is set to 0 and bit 7 is placed in the carry flag. The effect of this operation is to multiply the memory contents by 2 (ignoring 2's complement considerations), setting the carry if the result will not fit in 8 bits.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#ASL*
    private static byte ASL$oldValue;
    private static byte ASL$result;

    public static Instruction ASL = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, INDIRECT, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_Y,
             ABSOLUTE_Y, RELATIVE, IMPLIED -> null;
        case ACCUMULATOR -> new InstructionInfo(0x0A, 1, 2, ((programCounter, memory, registrars, size, cycles) -> {
            ASL$oldValue = registrars.accumulator;
            ASL$result = (byte) ((ASL$oldValue << 1) & 0xFF);

            registrars.accumulator = ASL$result;

            return (short)(programCounter + size);
        }));
        case ZERO_PAGE -> new InstructionInfo(0x06, 2, 5, ((programCounter, memory, registrars, size, cycles) -> {
            int address = Byte.toUnsignedInt(memory.getValue(programCounter + 1));
            ASL$oldValue = memory.getValue(address);
            ASL$result = (byte)(Byte.toUnsignedInt(ASL$oldValue) << 1);

            memory.writeValue(address, ASL$result);

            return (short)(programCounter + size);
        }));
        case ZERO_PAGE_X -> new InstructionInfo(0x16, 2, 6, ((programCounter, memory, registrars, size, cycles) -> {
            int address = (Byte.toUnsignedInt(memory.getValue(programCounter + 1)) + Byte.toUnsignedInt(registrars.registerX)) & 0xFFFF;
            ASL$oldValue = memory.getValue(address);
            ASL$result = (byte) ((ASL$oldValue << 1) & 0xFF);

            memory.writeValue(address, ASL$result);

            return (short)(programCounter + size);
        }));
        case ABSOLUTE -> new InstructionInfo(0x0E, 3, 6, ((programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            ASL$oldValue = memory.getValue(address);
            ASL$result = (byte) ((ASL$oldValue << 1) & 0xFF);

            memory.writeValue(address, ASL$result);

            return (short)(programCounter + size);
        }));
        case ABSOLUTE_X -> new InstructionInfo(0x1E, 3, 7, ((programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            address += (short)Byte.toUnsignedInt(registrars.registerX);
            ASL$oldValue = memory.getValue(address);
            ASL$result = (byte) ((ASL$oldValue << 1) & 0xFF);

            memory.writeValue(address, ASL$result);

            return (short)(programCounter + size);
        }));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (ASL$result & 0x80) != 0;
        registry.carryFlag = (ASL$oldValue & 0x80) != 0;
    });

    /// **LSR - Logical Shift Right**
    ///
    /// Each of the bits in A or M is shift one place to the right. The bit that was in bit 0 is shifted into the carry flag. Bit 7 is set to zero.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LSR*
    private static byte LSR$oldValue;
    private static byte LSR$result;

    public static Instruction LSR = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, INDIRECT, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_Y,
             ABSOLUTE_Y, RELATIVE, IMPLIED -> null;
        case ACCUMULATOR -> new InstructionInfo(0x4A, 1, 2, ((programCounter, memory, registrars, size, cycles) -> {
            LSR$oldValue = registrars.accumulator;
            LSR$result = (byte) ((LSR$oldValue >>> 1) & 0xFF);

            registrars.accumulator = LSR$result;

            return (short)(programCounter + size);
        }));
        case ZERO_PAGE -> new InstructionInfo(0x46, 2, 5, ((programCounter, memory, registrars, size, cycles) -> {
            int address = Byte.toUnsignedInt(memory.getValue(programCounter + 1));
            LSR$oldValue = memory.getValue(address);
            LSR$result = (byte)(Byte.toUnsignedInt(LSR$oldValue) >>> 1);

            memory.writeValue(address, LSR$result);

            return (short)(programCounter + size);
        }));
        case ZERO_PAGE_X -> new InstructionInfo(0x56, 2, 6, ((programCounter, memory, registrars, size, cycles) -> {
            int address = (Byte.toUnsignedInt(memory.getValue(programCounter + 1)) + Byte.toUnsignedInt(registrars.registerX)) & 0xFFFF;
            LSR$oldValue = memory.getValue(address);
            LSR$result = (byte) ((LSR$oldValue >>> 1) & 0xFF);

            memory.writeValue(address, LSR$result);

            return (short)(programCounter + size);
        }));
        case ABSOLUTE -> new InstructionInfo(0x4E, 3, 6, ((programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            LSR$oldValue = memory.getValue(address);
            LSR$result = (byte) ((LSR$oldValue >>> 1) & 0xFF);

            memory.writeValue(address, LSR$result);

            return (short)(programCounter + size);
        }));
        case ABSOLUTE_X -> new InstructionInfo(0x5E, 3, 7, ((programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            address = (address + Byte.toUnsignedInt(registrars.registerX)) & 0xFFFF;
            LSR$oldValue = memory.getValue(address);
            LSR$result = (byte) ((LSR$oldValue >>> 1) & 0xFF);

            memory.writeValue(address, LSR$result);

            return (short)(programCounter + size);
        }));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = LSR$result == 0;
        registry.negativeFlag = (LSR$result & 0x80) != 0;
        registry.carryFlag = (LSR$oldValue & 0x01) != 0;
    });

    /// **ROL - Rotate Left**
    ///
    /// Move each of the bits in either A or M one place to the left. Bit 0 is filled with the current value of the carry flag whilst the old bit 7 becomes the new carry flag value.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#ROL*
    private static byte ROL$oldValue;
    private static byte ROL$result;

    public static Instruction ROL = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, INDIRECT, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_Y,
             ABSOLUTE_Y, RELATIVE, IMPLIED -> null;
        case ACCUMULATOR -> new InstructionInfo(0x2A, 1, 2, ((programCounter, memory, registrars, size, cycles) -> {
            ROL$oldValue = registrars.accumulator;
            int result = ((Byte.toUnsignedInt(ROL$oldValue) << 1) | (registrars.carryFlag ? 1 : 0)) & 0xFF;
            ROL$result = (byte) result;

            registrars.accumulator = ROL$result;

            return (short)(programCounter + size);
        }));
        case ZERO_PAGE -> new InstructionInfo(0x26, 2, 5, ((programCounter, memory, registrars, size, cycles) -> {
            int address = Byte.toUnsignedInt(memory.getValue(programCounter + 1));
            ROL$oldValue = memory.getValue(address);
            int result = ((Byte.toUnsignedInt(ROL$oldValue) << 1) | (registrars.carryFlag ? 1 : 0)) & 0xFF;
            ROL$result = (byte) result;

            memory.writeValue(address, ROL$result);

            return (short)(programCounter + size);
        }));
        case ZERO_PAGE_X -> new InstructionInfo(0x36, 2, 6, ((programCounter, memory, registrars, size, cycles) -> {
            int address = (Byte.toUnsignedInt(memory.getValue(programCounter + 1)) + Byte.toUnsignedInt(registrars.registerX)) & 0xFFFF;
            ROL$oldValue = memory.getValue(address);
            int result = ((Byte.toUnsignedInt(ROL$oldValue) << 1) | (registrars.carryFlag ? 1 : 0)) & 0xFF;
            ROL$result = (byte) result;

            memory.writeValue(address, ROL$result);

            return (short)(programCounter + size);
        }));
        case ABSOLUTE -> new InstructionInfo(0x2E, 3, 6, ((programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            ROL$oldValue = memory.getValue(address);
            int result = ((Byte.toUnsignedInt(ROL$oldValue) << 1) | (registrars.carryFlag ? 1 : 0)) & 0xFF;
            ROL$result = (byte) result;

            memory.writeValue(address, ROL$result);

            return (short)(programCounter + size);
        }));
        case ABSOLUTE_X -> new InstructionInfo(0x3E, 3, 7, ((programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            address = (address + Byte.toUnsignedInt(registrars.registerX)) & 0xFFFF;
            ROL$oldValue = memory.getValue(address);
            int result = ((Byte.toUnsignedInt(ROL$oldValue) << 1) | (registrars.carryFlag ? 1 : 0)) & 0xFF;
            ROL$result = (byte) result;

            memory.writeValue(address, ROL$result);

            return (short)(programCounter + size);
        }));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = ROL$result == 0;
        registry.negativeFlag = (ROL$result & 0x80) != 0;
        registry.carryFlag = (ROL$oldValue & 0x80) != 0;
    });

    /// **ROR - Rotate Right**
    ///
    /// Move each of the bits in either A or M one place to the right. Bit 7 is filled with the current value of the carry flag whilst the old bit 0 becomes the new carry flag value.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#ROR*
    private static byte ROR$oldValue;
    private static byte ROR$result;

    public static Instruction ROR = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, INDIRECT, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_Y,
             ABSOLUTE_Y, RELATIVE, IMPLIED -> null;
        case ACCUMULATOR -> new InstructionInfo(0x6A, 1, 2, ((programCounter, memory, registrars, size, cycles) -> {
            ROR$oldValue = registrars.accumulator;
            int result = ((Byte.toUnsignedInt(ROR$oldValue) >> 1) | (registrars.carryFlag ? 0x80 : 0)) & 0xFF;
            ROR$result = (byte) result;

            registrars.accumulator = ROR$result;

            return (short)(programCounter + size);
        }));
        case ZERO_PAGE -> new InstructionInfo(0x66, 2, 5, ((programCounter, memory, registrars, size, cycles) -> {
            int address = Byte.toUnsignedInt(memory.getValue(programCounter + 1));
            ROR$oldValue = memory.getValue(address);
            int result = ((Byte.toUnsignedInt(ROR$oldValue) >> 1) | (registrars.carryFlag ? 0x80 : 0)) & 0xFF;
            ROR$result = (byte) result;

            memory.writeValue(address, ROR$result);

            return (short)(programCounter + size);
        }));
        case ZERO_PAGE_X -> new InstructionInfo(0x76, 2, 6, ((programCounter, memory, registrars, size, cycles) -> {
            int address = (Byte.toUnsignedInt(memory.getValue(programCounter + 1)) + Byte.toUnsignedInt(registrars.registerX)) & 0xFFFF;
            ROR$oldValue = memory.getValue(address);
            int result = ((Byte.toUnsignedInt(ROR$oldValue) >> 1) | (registrars.carryFlag ? 0x80 : 0)) & 0xFF;
            ROR$result = (byte) result;

            memory.writeValue(address, ROR$result);

            return (short)(programCounter + size);
        }));
        case ABSOLUTE -> new InstructionInfo(0x6E, 3, 6, ((programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            ROR$oldValue = memory.getValue(address);
            int result = ((Byte.toUnsignedInt(ROR$oldValue) >> 1) | (registrars.carryFlag ? 0x80 : 0)) & 0xFF;
            ROR$result = (byte) result;

            memory.writeValue(address, ROR$result);

            return (short)(programCounter + size);
        }));
        case ABSOLUTE_X -> new InstructionInfo(0x7E, 3, 7, ((programCounter, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(programCounter + 1);
            address = (address + Byte.toUnsignedInt(registrars.registerX)) & 0xFFFF;
            ROR$oldValue = memory.getValue(address);
            int result = ((Byte.toUnsignedInt(ROR$oldValue) >> 1) | (registrars.carryFlag ? 0x80 : 0)) & 0xFF;
            ROR$result = (byte) result;

            memory.writeValue(address, ROR$result);

            return (short)(programCounter + size);
        }));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.zeroFlag = ROR$result == 0;
        registry.negativeFlag = (ROR$result & 0x80) != 0;
        registry.carryFlag = (ROR$oldValue & 0x01) != 0;
    });
    //endregion

    //region * Jumps & Calls *
    /// **JMP - Jump**
    ///
    /// Sets the program counter to the address specified by the operand.
    ///
    /// *NB:*
    /// *An original 6502 has does not correctly fetch the target address if the indirect vector falls on a page boundary (e.g. $xxFF where xx is any value from $00 to $FF). In this case fetches the LSB from $xxFF as expected but takes the MSB from $xx00. This is fixed in some later chips like the 65SC02 so for compatibility always ensure the indirect vector is not at the end of the page.*
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#JMP*
    public static Instruction JMP = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE_Y, IMPLIED, RELATIVE -> null;
        case ABSOLUTE -> new InstructionInfo(0x4C, 3, 3, ((programCounter, memory, registrars, size, cycles) -> {
            return (short)(memory.getUnsignedAddress(programCounter + 1));
        }));
        case INDIRECT -> new InstructionInfo(0x6C, 3, 5, ((programCounter, memory, registrars, size, cycles) -> {
            short pointer = (short)(memory.getUnsignedAddress(programCounter + 1));

            if(Constants.JMP_INDIRECT_BUG) {
                int low = Byte.toUnsignedInt(memory.getValue(pointer));
                int high;
                if ((pointer & 0x00FF) == 0x00FF) {
                    // Wraps around within the same 256-byte page
                    high = Byte.toUnsignedInt(memory.getValue(pointer & 0xFF00));
                } else {
                    high = Byte.toUnsignedInt(memory.getValue(pointer + 1));
                }

                return (short) ((high << 8) | low);
            }

            return (short)(memory.getUnsignedAddress(pointer));
        }));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **JSR - Jump to Subroutine**
    ///
    /// The JSR instruction pushes the address (minus one) of the return point on to the stack and then sets the program counter to the target memory address.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#JSR*
    public static Instruction JSR = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE_Y, IMPLIED, RELATIVE -> null;
        case ABSOLUTE -> new InstructionInfo(0x20, 3, 6, ((programCounter, memory, registrars, size, cycles) -> {
            short targetAddress = (short) memory.getUnsignedAddress(programCounter + 1);

            short returnAddress = (short) ((programCounter + size - 1) & 0xFFFF);

            // Push high byte then low byte
            memory.pushToStack((byte) (returnAddress >> 8));
            memory.pushToStack((byte) (returnAddress & 0xFF));

            return targetAddress;
        }));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **RTS - Return from Subroutine**
    ///
    /// The RTS instruction is used at the end of a subroutine to return to the calling routine. It pulls the program counter (minus one) from the stack.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#RTS*
    public static Instruction RTS = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE_Y, ABSOLUTE, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0x60, 1, 6, ((programCounter, memory, registrars, size, cycles) -> {
            // Pull low then high
            int low = Byte.toUnsignedInt(memory.pullFromStack());
            int high = Byte.toUnsignedInt(memory.pullFromStack());
            short targetAddress = (short) ((high << 8) | low);

            return (short) ((targetAddress + size) & 0xFFFF);
        }));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});
    //endregion

    //region * Branches *
    /// **BCC - Branch if Carry Clear**
    ///
    /// If the carry flag is clear then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BCC*
    public static Instruction BCC = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, IMPLIED -> null;
        case RELATIVE -> new InstructionInfo(0x90, 2, 2, (programCounter, memory, registrars, size, cycles) -> { // TODO Cycles(+1 if branch succeeds +2 if to a new page)
            if(!registrars.carryFlag) {
                byte offset = memory.getValue(programCounter + 1);
                return (short) (programCounter + size + offset);
            }

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **BCS - Branch if Carry Set**
    ///
    /// If the carry flag is set then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BCS*
    public static Instruction BCS = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, IMPLIED -> null;
        case RELATIVE -> new InstructionInfo(0xB0, 2, 2, (programCounter, memory, registrars, size, cycles) -> { // TODO Cycles(+1 if branch succeeds +2 if to a new page)
            if(registrars.carryFlag) {
                byte offset = memory.getValue(programCounter + 1);
                return (short) (programCounter + size + offset);
            }

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **BEQ - Branch if Equal**
    ///
    /// If the zero flag is set then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BEQ*
    public static Instruction BEQ = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, IMPLIED -> null;
        case RELATIVE -> new InstructionInfo(0xB0, 2, 2, (programCounter, memory, registrars, size, cycles) -> { // TODO Cycles(+1 if branch succeeds +2 if to a new page)
            if(registrars.zeroFlag) {
                byte offset = memory.getValue(programCounter + 1);
                return (short) (programCounter + size + offset);
            }

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **BMI - Branch if Minus**
    ///
    /// If the negative flag is set then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BMI*
    public static Instruction BMI = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, IMPLIED -> null;
        case RELATIVE -> new InstructionInfo(0x30, 2, 2, (programCounter, memory, registrars, size, cycles) -> { // TODO Cycles(+1 if branch succeeds +2 if to a new page)
            if(registrars.negativeFlag) {
                byte offset = memory.getValue(programCounter + 1);
                return (short) (programCounter + size + offset);
            }

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **BNE - Branch if Not Equal**
    ///
    /// If the zero flag is clear then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BNE*
    public static Instruction BNE = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, IMPLIED -> null;
        case RELATIVE -> new InstructionInfo(0xD0, 2, 2, (programCounter, memory, registrars, size, cycles) -> { // TODO Cycles(+1 if branch succeeds +2 if to a new page)
            if(!registrars.zeroFlag) {
                byte offset = memory.getValue(programCounter + 1);
                return (short) (programCounter + size + offset);
            }

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **BPL - Branch if Positive**
    ///
    /// If the negative flag is clear then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BPL*
    public static Instruction BPL = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, IMPLIED -> null;
        case RELATIVE -> new InstructionInfo(0x10, 2, 2, (programCounter, memory, registrars, size, cycles) -> { // TODO Cycles(+1 if branch succeeds +2 if to a new page)
            if(!registrars.negativeFlag) {
                byte offset = memory.getValue(programCounter + 1);
                return (short) (programCounter + size + offset);
            }

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **BVC - Branch if Overflow Clear**
    ///
    /// If the overflow flag is clear then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BVC*
    public static Instruction BVC = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, IMPLIED -> null;
        case RELATIVE -> new InstructionInfo(0x50, 2, 2, (programCounter, memory, registrars, size, cycles) -> { // TODO Cycles(+1 if branch succeeds +2 if to a new page)
            if(!registrars.overflowFlag) {
                byte offset = memory.getValue(programCounter + 1);
                return (short) (programCounter + size + offset);
            }

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **BVS - Branch if Overflow Set**
    ///
    /// If the overflow flag is set then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BVS*
    public static Instruction BVS = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, IMPLIED -> null;
        case RELATIVE -> new InstructionInfo(0x70, 2, 2, (programCounter, memory, registrars, size, cycles) -> { // TODO Cycles(+1 if branch succeeds +2 if to a new page)
            if(registrars.overflowFlag) {
                byte offset = memory.getValue(programCounter + 1);
                return (short) (programCounter + size + offset);
            }

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});
    //endregion

    //region * Status Flag Changes *
    /// **CLC - Clear Carry Flag**
    ///
    /// Set the carry flag to zero.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLC*
    public static Instruction CLC = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0x18, 1, 2, (programCounter, memory, registrars, size, cycles) -> (short) (programCounter + size));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.carryFlag = false;
    });

    /// **CLD - Clear Decimal Mode**
    ///
    /// Sets the decimal mode flag to zero.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLD*
    public static Instruction CLD = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0xD8, 1, 2, (programCounter, memory, registrars, size, cycles) -> (short) (programCounter + size));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.decimalModeFlag = false;
    });

    /// **CLI - Clear Interrupt Disable**
    ///
    /// Clears the interrupt disable flag allowing normal interrupt requests to be serviced.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLI*
    public static Instruction CLI = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0x58, 1, 2, (programCounter, memory, registrars, size, cycles) -> (short) (programCounter + size));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.interruptDisableFlag = false;
    });

    /// **CLV - Clear Overflow Flag**
    ///
    /// Clears the overflow flag.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLV*
    public static Instruction CLV = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0xB8, 1, 2, (programCounter, memory, registrars, size, cycles) -> (short) (programCounter + size));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.overflowFlag = false;
    });

    /// **SEC - Set Carry Flag**
    ///
    /// Set the carry flag to one.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#SEC*
    public static Instruction SEC = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0x38, 1, 2, (programCounter, memory, registrars, size, cycles) -> (short) (programCounter + size));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.carryFlag = true;
    });

    /// **SED - Set Decimal Flag**
    ///
    /// Set the decimal mode flag to one.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#SED*
    public static Instruction SED = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0xF8, 1, 2, (programCounter, memory, registrars, size, cycles) -> (short) (programCounter + size));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.decimalModeFlag = true;
    });

    /// **SEI - Set Interrupt Flag**
    ///
    /// Set the interrupt disable flag to one.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#SEI*
    public static Instruction SEI = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0x78, 1, 2, (programCounter, memory, registrars, size, cycles) -> (short) (programCounter + size));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.interruptDisableFlag = true;
    });
    //endregion

    //region * System Functions *
    /// **BRK - Force Interrupt**
    ///
    /// The BRK instruction forces the generation of an interrupt request. The program counter and processor status are pushed on the stack then the IRQ interrupt vector at $FFFE/F is loaded into the PC and the break flag in the status set to one.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BRK*
    public static Instruction BRK = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0x00, 1, 7, (programCounter, memory, registrars, size, cycles) -> {

            // TODO ????

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {
        registry.breakFlag = true;
    });

    /// **NOP - No Operation**
    ///
    /// The NOP instruction causes no changes to the processor other than the normal incrementing of the program counter to the next instruction.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#NOP*
    public static Instruction NOP = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0xEA, 1, 2, (programCounter, memory, registrars, size, cycles) -> (short) (programCounter + size));
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {});

    /// **RTI - Return from Interrupt**
    ///
    /// The RTI instruction is used at the end of an interrupt processing routine. It pulls the processor flags from the stack followed by the program counter.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#RTI*
    public static Instruction RTI = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT, RELATIVE -> null;
        case IMPLIED -> new InstructionInfo(0x00, 1, 6, (programCounter, memory, registrars, size, cycles) -> {

            // TODO ????

            return (short) (programCounter + size);
        });
    }, addressingMode -> (programCounter, mem, registry, size, cycles) -> {

    });

    //endregion
}
