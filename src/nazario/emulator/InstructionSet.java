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
                    try{
                        InstructionInfo info = instruction.getFactory().apply(addressingMode);
                        info.flagFunction = instruction.getFlagFunctionFactory().apply(addressingMode);
                        instructions[info.opcode] = info;
                    }catch (Exception e) {}
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
        case IMMEDIATE -> new InstructionInfo(0xA9, 2, 2, (index, memory, registrars, size, cycles) -> {
            registrars.accumulator = memory.getValue(index + 1);
            return (short) (index + size);
        });
        case ZERO_PAGE -> new InstructionInfo(0xA5, 2, 3, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            registrars.accumulator = memory.getValue(address);
            return (short) (index + size);
        });
        case ZERO_PAGE_X -> new InstructionInfo(0xB5, 2, 4, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            byte x = registrars.registerX;
            registrars.accumulator = memory.getValue((address + x) & 0xFF);
            return (short) (index + size);
        });
        case ABSOLUTE -> new InstructionInfo(0xAD, 3, 4, (index, memory, registrars, size, cycles) -> {
            registrars.accumulator = memory.getValueFromAddress(index + 1);
            return (short) (index + size);
        });
        case ABSOLUTE_X -> new InstructionInfo(0xBD, 3, 4, (index, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            registrars.accumulator = memory.getValueFromAddress(index + 1 + registrars.registerX);
            return (short) (index + size);
        });
        case ABSOLUTE_Y -> new InstructionInfo(0xB9, 3, 4, (index, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            registrars.accumulator = memory.getValueFromAddress(index + 1 + registrars.registerY);

            return (short) (index + size);
        });
        case INDIRECT_X -> new InstructionInfo(0xA1, 2, 6, (index, memory, registrars, size, cycles) -> {
            return (short)0;
        });
        case INDIRECT_Y -> new InstructionInfo(0xB1, 2, 5, (index, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            return (short)0;
        });
        case INDIRECT, ZERO_PAGE_Y, ACCUMULATOR, IMPLIED -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    /// **LDX - Load X Register**
    ///
    /// Loads a byte of memory into the X register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LDX*
    public static Instruction LDX = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE -> new InstructionInfo(0xA2, 2, 2, (index, memory, registrars, size, cycles) -> {
            registrars.registerX = memory.getValue(index + 1);
            return (short) (index + size);
        });
        case ZERO_PAGE -> new InstructionInfo(0xA6, 2, 3, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            registrars.registerX = memory.getValue(address);
            return (short) (index + size);
        });
        case ZERO_PAGE_Y -> new InstructionInfo(0xB6, 2, 4, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            registrars.registerX = memory.getValue(address + registrars.registerY);
            return (short) (index + size);
        });
        case ABSOLUTE -> new InstructionInfo(0xAE, 3, 4, (index, memory, registrars, size, cycles) -> {
            registrars.registerX = memory.getValueFromAddress(index + 1);
            return (short) (index + size);
        });
        case ABSOLUTE_Y -> new InstructionInfo(0xBE, 3, 4, (index, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            registrars.registerX = memory.getValueFromAddress(index + 1 + registrars.registerY);

            return (short) (index + size);
        });
        case INDIRECT, INDIRECT_X, INDIRECT_Y, ZERO_PAGE_X, ACCUMULATOR, ABSOLUTE_X, IMPLIED -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerX == 0;
        registry.negativeFlag = (registry.registerX & 0x80) != 0;
    });

    /// **LDY - Load Y Register**
    ///
    /// Loads a byte of memory into the Y register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LDY*
    public static Instruction LDY = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE -> new InstructionInfo(0xA0, 2, 2, (index, memory, registrars, size, cycles) -> {
            registrars.registerY = memory.getValue(index + 1);
            return (short) (index + size);
        });
        case ZERO_PAGE -> new InstructionInfo(0xA4, 2, 3, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            registrars.registerY = memory.getValue(address);
            return (short) (index + size);
        });
        case ZERO_PAGE_X -> new InstructionInfo(0xB4, 2, 4, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            registrars.registerY = memory.getValue(address + registrars.registerX);
            return (short) (index + size);
        });
        case ABSOLUTE -> new InstructionInfo(0xAC, 3, 4, (index, memory, registrars, size, cycles) -> {
            registrars.registerY = memory.getValueFromAddress(index + 1);
            return (short) (index + size);
        });
        case ABSOLUTE_X -> new InstructionInfo(0xBC, 3, 4, (index, memory, registrars, size, cycles) -> { // TODO add one cycle if a page is crossed
            registrars.registerY = memory.getValueFromAddress(index + 1 + registrars.registerX);

            return (short) (index + size);
        });
        case INDIRECT, INDIRECT_X, INDIRECT_Y, ZERO_PAGE_Y, ACCUMULATOR, ABSOLUTE_Y, IMPLIED -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerY == 0;
        registry.negativeFlag = (registry.registerY & 0x80) != 0;
    });

    /// **STA - Store Accumulator**
    ///
    /// Stores the contents of the accumulator into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STA*
    public static Instruction STA = new Instruction(addressingMode -> switch(addressingMode) {
        case ZERO_PAGE -> new InstructionInfo(0x85, 2, 3, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            memory.writeValue(address, registrars.accumulator);
            return (short) (index + size);
        });
        case ZERO_PAGE_X -> new InstructionInfo(0x95, 2, 4, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            byte x = registrars.registerX;
            memory.writeValue((address + x) & 0xFF, registrars.accumulator);
            return (short) (index + size);
        });
        case ABSOLUTE -> new InstructionInfo(0x8D, 3, 4, (index, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(index + 1);
            memory.writeValue(address, registrars.accumulator);
            return (short) (index + size);
        });
        case ABSOLUTE_X -> new InstructionInfo(0x9D, 3, 5, (index, memory, registrars, size, cycles) -> {
            int baseAddress = memory.getUnsignedAddress(index + 1);
            int address = baseAddress + registrars.registerX;
            memory.writeValue(address, registrars.accumulator);

            return (short) (index + size);
        });
        case ABSOLUTE_Y -> new InstructionInfo(0x99, 3, 5, (index, memory, registrars, size, cycles) -> {
            int baseAddress = memory.getUnsignedAddress(index + 1);
            int address = baseAddress + registrars.registerY;
            memory.writeValue(address, registrars.accumulator);

            return (short) (index + size);
        });
        case INDIRECT_X -> new InstructionInfo(0x81, 2, 6, (index, memory, registrars, size, cycles) -> {
            return (short)0;
        });
        case INDIRECT_Y -> new InstructionInfo(0x91, 2, 6, (index, memory, registrars, size, cycles) -> {
            return (short)0;
        });
        case IMMEDIATE, INDIRECT, ZERO_PAGE_Y, ACCUMULATOR, IMPLIED -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {});

    /// **STX - Store X Register**
    ///
    /// Stores the contents of the X register into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STX*
    public static Instruction STX = new Instruction(addressingMode -> switch(addressingMode) {
        case ZERO_PAGE -> new InstructionInfo(0x86, 2, 3, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            memory.writeValue(address, registrars.registerX);
            return (short) (index + size);
        });
        case ZERO_PAGE_Y -> new InstructionInfo(0x96, 2, 4, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            byte y = registrars.registerY;
            memory.writeValue((address + y) & 0xFF, registrars.registerX);
            return (short) (index + size);
        });
        case ABSOLUTE -> new InstructionInfo(0x8E, 3, 4, (index, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(index + 1);
            memory.writeValue(address, registrars.registerX);
            return (short) (index + size);
        });
        case ZERO_PAGE_X, INDIRECT_X, ABSOLUTE_Y, INDIRECT_Y, ABSOLUTE_X, IMMEDIATE, INDIRECT, ACCUMULATOR, IMPLIED -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {});

    /// **STY - Store Y Register**
    ///
    /// Stores the contents of the Y register into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STY*
    public static Instruction STY = new Instruction(addressingMode -> switch(addressingMode) {
        case ZERO_PAGE -> new InstructionInfo(0x84, 2, 3, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            memory.writeValue(address, registrars.registerY);
            return (short) (index + size);
        });
        case ZERO_PAGE_X -> new InstructionInfo(0x94, 2, 4, (index, memory, registrars, size, cycles) -> {
            byte address = memory.getValue(index + 1);
            byte x = registrars.registerX;
            memory.writeValue((address + x) & 0xFF, registrars.registerY);
            return (short) (index + size);
        });
        case ABSOLUTE -> new InstructionInfo(0x8C, 3, 4, (index, memory, registrars, size, cycles) -> {
            int address = memory.getUnsignedAddress(index + 1);
            memory.writeValue(address, registrars.registerY);
            return (short) (index + size);
        });
        case ZERO_PAGE_Y, INDIRECT_X, ABSOLUTE_Y, INDIRECT_Y, ABSOLUTE_X, IMMEDIATE, INDIRECT, ACCUMULATOR, IMPLIED -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {});
    //endregion

    //region * Register Transfers *
    /// **TAX - Transfer Accumulator to X**
    ///
    /// Copies the current contents of the accumulator into the X register and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TAX*
    public static Instruction TAX = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0xAA, 1, 2, (index, memory, registrars, size, cycles) -> {
            registrars.registerX = registrars.accumulator;
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerX == 0;
        registry.negativeFlag = (registry.registerX & 0x80) != 0;
    });

    /// **TAY - Transfer Accumulator to Y**
    ///
    /// Copies the current contents of the accumulator into the Y register and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TAY*
    public static Instruction TAY = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0xA8, 1, 2, (index, memory, registrars, size, cycles) -> {
            registrars.registerY = registrars.accumulator;
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerY == 0;
        registry.negativeFlag = (registry.registerY & 0x80) != 0;
    });

    /// **TXA - Transfer X to Accumulator**
    ///
    /// Copies the current contents of the X register into the accumulator and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TXA*
    public static Instruction TXA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x8A, 1, 2, (index, memory, registrars, size, cycles) -> {
            registrars.accumulator = registrars.registerX;
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    /// **TYA - Transfer Y to Accumulator**
    ///
    /// Copies the current contents of the Y register into the accumulator and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TYA*
    public static Instruction TYA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x98, 1, 2, (index, memory, registrars, size, cycles) -> {
            registrars.accumulator = registrars.registerY;
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
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
        case IMPLIED -> new InstructionInfo(0xBA, 1, 2, (index, memory, registrars, size, cycles) -> {
            registrars.registerX = memory.getValue(memory.stackPointer);
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.registerX == 0;
        registry.negativeFlag = (registry.registerX & 0x80) != 0;
    });

    /// **TXS - Transfer X to Stack Pointer**
    ///
    /// Copies the current contents of the X register into the stack register.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TXS*
    public static Instruction TXS = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x9A, 1, 2, (index, memory, registrars, size, cycles) -> {
            memory.writeValue(memory.stackPointer, registrars.registerX);
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {});

    /// **PHA - Push Accumulator**
    ///
    /// Pushes a copy of the accumulator on to the stack.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PHA*
    public static Instruction PHA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x48, 1, 3, (index, memory, registrars, size, cycles) -> {
            memory.pushToStack(registrars.accumulator);
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {});

    /// **PHP - Push Processor Status**
    ///
    /// Pushes a copy of the status flags on to the stack.
    ///
    /// http://www.6502.org/users/obelisk/6502/reference.html#PHP
    public static Instruction PHP = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x08, 1, 3, (index, memory, registrars, size, cycles) -> {
            memory.pushToStack(registrars.getProcessorStatus());
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {});

    /// **PLA - Pull Accumulator**
    ///
    /// Pulls an 8 bit value from the stack and into the accumulator. The zero and negative flags are set as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PLA*
    public static Instruction PLA = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x68, 1, 4, (index, memory, registrars, size, cycles) -> {
            registrars.accumulator = memory.pullFromStack();
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    /// **PLP - Pull Processor Status**
    ///
    /// Pulls an 8 bit value from the stack and into the processor flags. The flags will take on new states as determined by the value pulled.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PLP*
    public static Instruction PLP = new Instruction(addressingMode -> switch(addressingMode) {
        case IMPLIED -> new InstructionInfo(0x28, 1, 4, (index, memory, registrars, size, cycles) -> {
            registrars.setProcessorStatus(memory.pullFromStack());
            return (short) (index + size);
        });
        case IMMEDIATE, ZERO_PAGE_X, ZERO_PAGE, ABSOLUTE, ZERO_PAGE_Y, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
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
        case IMMEDIATE -> new InstructionInfo(0x29, 2, 2, (index, memory, registrars, size, cycles) -> {
            byte value = memory.getValueFromAddress(index + 1);
            registrars.accumulator = (byte) (Byte.toUnsignedInt(registrars.accumulator) & Byte.toUnsignedInt(value));
            return (short) (index + size);
        });
        case ZERO_PAGE -> new InstructionInfo(0x25, 2, 3, (index, memory, registrars, size, cycles) -> {
            byte value = memory.getValueFromAddress(memory.getZeroPageWord(index + 1));
            registrars.accumulator = (byte) (Byte.toUnsignedInt(registrars.accumulator) & Byte.toUnsignedInt(value));
            return (short) (index + size);
        });
        case IMPLIED, ABSOLUTE, ZERO_PAGE_Y, ZERO_PAGE_X, ABSOLUTE_X, ABSOLUTE_Y, INDIRECT, INDIRECT_X,
             INDIRECT_Y, ACCUMULATOR -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });
    //endregion

    //region * Arithmetic *
    public static Instruction ADC = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE -> new InstructionInfo(0x69, 2, 2, (index, memory, registrars, size, cycles) -> {
            byte zeroNum = registrars.zeroFlag ? (byte)1 : (byte)0;

            return (short) (index + size);
        });
        case ZERO_PAGE -> null;
        case ZERO_PAGE_X -> null;
        case ABSOLUTE -> null;
        case ZERO_PAGE_Y, INDIRECT_X, ABSOLUTE_Y, INDIRECT_Y, ABSOLUTE_X, INDIRECT, ACCUMULATOR, IMPLIED -> throw new UnsupportedOperationException();
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.zeroFlag = registry.accumulator == 0;
        registry.negativeFlag = (registry.accumulator & 0x80) != 0;
    });

    //endregion

    //region * Status Flag Changes *
    /// **CLC - Clear Carry Flag**
    ///
    /// Set the carry flag to zero.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLC*
    public static Instruction CLC = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT -> null;
        case IMPLIED -> new InstructionInfo(0x18, 1, 2, (index, memory, registrars, size, cycles) -> (short) (index + size));
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.carryFlag = false;
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
             ABSOLUTE, ABSOLUTE_Y, INDIRECT -> null;
        case IMPLIED -> new InstructionInfo(0x00, 1, 7, (index, memory, registrars, size, cycles) -> {

            // TODO ????

            return (short) (index + size);
        });
    }, addressingMode -> (index, mem, registry, size, cycles) -> {
        registry.breakFlag = true;
    });

    /// **NOP - No Operation**
    ///
    /// The NOP instruction causes no changes to the processor other than the normal incrementing of the program counter to the next instruction.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#NOP*
    public static Instruction NOP = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT -> null;
        case IMPLIED -> new InstructionInfo(0xEA, 1, 2, (index, memory, registrars, size, cycles) -> (short) (index + size));
    }, addressingMode -> (index, mem, registry, size, cycles) -> {});

    /// **RTI - Return from Interrupt**
    ///
    /// The RTI instruction is used at the end of an interrupt processing routine. It pulls the processor flags from the stack followed by the program counter.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#RTI*
    public static Instruction RTI = new Instruction(addressingMode -> switch(addressingMode) {
        case IMMEDIATE, ZERO_PAGE, ACCUMULATOR, INDIRECT_Y, INDIRECT_X, ZERO_PAGE_X, ZERO_PAGE_Y, ABSOLUTE_X,
             ABSOLUTE, ABSOLUTE_Y, INDIRECT -> null;
        case IMPLIED -> new InstructionInfo(0x00, 1, 6, (index, memory, registrars, size, cycles) -> {

            // TODO ????

            return (short) (index + size);
        });
    }, addressingMode -> (index, mem, registry, size, cycles) -> {

    });

    //endregion
}
