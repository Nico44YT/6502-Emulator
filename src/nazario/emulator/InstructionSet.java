package nazario.emulator;

import nazario.emulator.util.*;

import java.util.ArrayList;
import java.util.List;

/// http://www.6502.org/users/obelisk/6502/reference.html
///
/// http://www.6502.org/users/obelisk/6502/instructions.html
public class InstructionSet {
    public static final List<Instruction> instructionRegistry = new ArrayList<>();

    private static Pair<InstructionInfo, InstructionsFunction>[] instructions;
    public static Pair<InstructionInfo, InstructionsFunction>[] getInstructions() {
        if(instructions == null) {
            instructions = new Pair[0x100];

            instructionRegistry.forEach(instructionContainer -> {
                for (InstructionInfo info : instructionContainer.getInfos()) {
                    if(instructions[info.opcode()] != null) throw new RuntimeException("Instruction with opcode " + info.opcode() + " already registered!");
                    instructions[info.opcode()] = new Pair<>(info, instructionContainer.getFunction());
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
    public static Instruction LDA = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xA9, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xA5, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xB5, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xAD, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xBD, 3, 4, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0xB9, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0xA1, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0xB1, 2, 5, AddressingMode.INDIRECT_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        registrars.accumulator = value;

        registrars.zeroFlag = value == 0;
        registrars.negativeFlag = (value & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **STA - Store Accumulator**
    ///
    /// Stores the contents of the accumulator into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STA*
    public static Instruction STA = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x85, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x95, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x8D, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x9D, 3, 5, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x99, 3, 5, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x81, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x91, 2, 6, AddressingMode.INDIRECT_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, registrars.accumulator);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **LDX - Load X Register**
    ///
    /// Loads a byte of memory into the X register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LDX*
    public static Instruction LDX = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xA2, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xA6, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xB6, 2, 4, AddressingMode.ZERO_PAGE_Y),
            new InstructionInfo(0xAE, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xBE, 3, 4, AddressingMode.ABSOLUTE_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        registrars.registerX = value;

        registrars.zeroFlag = value == 0;
        registrars.negativeFlag = (value & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **STX - Store X Register**
    ///
    /// Stores the contents of the X register into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STX*
    public static Instruction STX = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x86, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x96, 2, 4, AddressingMode.ZERO_PAGE_Y),
            new InstructionInfo(0x8E, 3, 4, AddressingMode.ABSOLUTE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, registrars.registerX);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **LDY - Load Y Register**
    ///
    /// Loads a byte of memory into the Y register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LDY*
    public static Instruction LDY = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xA0, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xA4, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xB4, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xAC, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xBC, 3, 4, AddressingMode.ABSOLUTE_X),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        registrars.registerY = value;

        registrars.zeroFlag = value == 0;
        registrars.negativeFlag = (value & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **STY - Store Y Register**
    ///
    /// Stores the contents of the Y register into memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#STY*
    public static Instruction STY = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x84, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x94, 2, 4, AddressingMode.ZERO_PAGE_Y),
            new InstructionInfo(0x8C, 3, 4, AddressingMode.ABSOLUTE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, registrars.registerY);

        return (short)(programCounter + instructionInfo.size());
    });
    //endregion

    //region * Register Transfers *
    /// **TAX - Transfer Accumulator to X**
    ///
    /// Copies the current contents of the accumulator into the X register and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TAX*
    public static Instruction TAX = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xAA, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {

        registrars.registerX = registrars.accumulator;

        registrars.zeroFlag = registrars.registerX == 0;
        registrars.negativeFlag = (registrars.registerX & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **TXA - Transfer X to Accumulator**
    ///
    /// Copies the current contents of the X register into the accumulator and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TAX*
    public static Instruction TXA = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x8A, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {

        registrars.accumulator = registrars.registerX;

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.negativeFlag = (registrars.accumulator & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **TAY - Transfer Accumulator to Y**
    ///
    /// Copies the current contents of the accumulator into the Y register and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TAY*
    public static Instruction TAY = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xA8, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {

        registrars.registerY = registrars.accumulator;

        registrars.zeroFlag = registrars.registerY == 0;
        registrars.negativeFlag = (registrars.registerY & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **TYA - Transfer X to Accumulator**
    ///
    /// Copies the current contents of the Y register into the accumulator and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TYA*
    public static Instruction TYA = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x98, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {

        registrars.accumulator = registrars.registerY;

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.negativeFlag = (registrars.accumulator & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });
    //endregion

    //region * Stack Operations *
    /// **TSX - Transfer Stack Pointer to X**
    ///
    /// Copies the current contents of the stack register into the X register and sets the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TSX*
    public static Instruction TSX = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xBA, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.registerX = memory.getStackPointerValue();

        registrars.zeroFlag = registrars.registerX == 0;
        registrars.negativeFlag = (registrars.registerX & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **TXS - Transfer X to Stack Pointer**
    ///
    /// Copies the current contents of the X register into the stack register.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#TXS*
    public static Instruction TXS = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x9A, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        memory.writeToStackPointer(registrars.registerX);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **PHA - Push Accumulator**
    ///
    /// Pushes a copy of the accumulator on to the stack.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PHA*
    public static Instruction PHA = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x48, 1, 3, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        memory.pushToStack(registrars.accumulator);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **PHP - Push Processor Status**
    ///
    /// Pushes a copy of the status flags on to the stack.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PHP*
    public static Instruction PHP = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x08, 1, 3, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        memory.pushToStack(registrars.getProcessorStatus());

        return (short)(programCounter + instructionInfo.size());
    });

    /// **PLA - Pull Accumulator**
    ///
    /// Pulls an 8 bit value from the stack and into the accumulator. The zero and negative flags are set as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PLA*
    public static Instruction PLA = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x68, 1, 4, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.accumulator = memory.pullFromStack();

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.negativeFlag = (registrars.accumulator & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **PLP - Pull Processor Status**
    ///
    /// Pulls an 8 bit value from the stack and into the processor flags. The flags will take on new states as determined by the value pulled.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#PLP*
    public static Instruction PLP = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x28, 1, 4, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.setProcessorStatus(memory.pullFromStack());

        return (short)(programCounter + instructionInfo.size());
    });
    //endregion

    //region * Logical *
    /// **AND - Logical AND**
    ///
    /// A logical AND is performed, bit by bit, on the accumulator contents using the contents of a byte of memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#AND*
    public static Instruction AND = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x29, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0x25, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x35, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x2D, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x3D, 3, 4, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x39, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x21, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x31, 2, 5, AddressingMode.INDIRECT_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        registrars.accumulator &= value;

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.negativeFlag = (registrars.accumulator & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **EOR - Exclusive OR**
    ///
    /// An exclusive OR is performed, bit by bit, on the accumulator contents using the contents of a byte of memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#EOR*
    public static Instruction EOR = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x49, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0x45, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x55, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x4D, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x5D, 3, 4, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x59, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x41, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x51, 2, 5, AddressingMode.INDIRECT_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        registrars.accumulator ^= value;

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.negativeFlag = (registrars.accumulator & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **ORA - Logical Inclusive OR**
    ///
    /// An inclusive OR is performed, bit by bit, on the accumulator contents using the contents of a byte of memory.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#ORA*
    public static Instruction ORA = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x09, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0x05, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x15, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x0D, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x1D, 3, 4, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x19, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x01, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x11, 2, 5, AddressingMode.INDIRECT_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        registrars.accumulator |= value;

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.negativeFlag = (registrars.accumulator & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **BIT - Bit Test**
    ///
    /// This instruction is used to test if one or more bits are set in a target memory location. The mask pattern in A is ANDed with the value in memory to set or clear the zero flag, but the result is not kept. Bits 7 and 6 of the value from memory are copied into the N and V flags.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BIT*
    public static Instruction BIT = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x24, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x2C, 3, 4, AddressingMode.ABSOLUTE),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        int targetResult = Byte.toUnsignedInt(registrars.accumulator) & Byte.toUnsignedInt(value);

        registrars.zeroFlag = targetResult == 0;
        registrars.negativeFlag = (value & 0x80) != 0;
        registrars.overflowFlag = (value & 0x40) != 0;

        return (short)(programCounter + instructionInfo.size());
    });
    //endregion

    //region * Arithmetic *
    /// **ADC - Add with Carry**
    ///
    /// This instruction adds the contents of a memory location to the accumulator together with the carry bit.
    /// If overflow occurs the carry bit is set; this enables multiple byte addition to be performed.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#ADC*
    public static Instruction ADC = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x69, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0x65, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x75, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x6D, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x7D, 3, 4, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x79, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x61, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x71, 2, 5, AddressingMode.INDIRECT_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        int carryIn = registrars.carryFlag ? 1 : 0;
        int a = Byte.toUnsignedInt(registrars.accumulator);
        int b = Byte.toUnsignedInt(value);
        int result = a + b + carryIn;

        registrars.carryFlag = (result > 0xFF);
        registrars.accumulator = (byte)(result & 0xFF);

        // Signed overflow detection:
        // Overflow occurs if the sign bits of A and B are the same,
        // but the sign bit of the result differs.
        registrars.overflowFlag = (((a ^ result) & (b ^ result) & 0x80) != 0);

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.negativeFlag = (registrars.accumulator & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SBC - Subtract with Carry**
    ///
    /// This instruction subtracts the contents of a memory location to the accumulator together with the not of the carry bit.
    /// If overflow occurs the carry bit is clear, this enables multiple byte subtraction to be performed.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#SBC*
    public static Instruction SBC = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xE9, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xE5, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xF5, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xED, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xFD, 3, 4, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0xF9, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0xE1, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0xF1, 2, 5, AddressingMode.INDIRECT_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        int carryIn = registrars.carryFlag ? 0 : 1; // Invert carry for subtraction
        int a = Byte.toUnsignedInt(registrars.accumulator);
        int b = Byte.toUnsignedInt(value);
        int result = a - b - carryIn;

        registrars.accumulator = (byte)(result & 0xFF);

        // Carry clear = borrow occurred
        registrars.carryFlag = (result >= 0);

        // Signed overflow: happens if sign(A) != sign(Result) and sign(A) != sign(B)
        registrars.overflowFlag = (((a ^ result) & (~b ^ result) & 0x80) != 0);

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.negativeFlag = (registrars.accumulator & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **CMP - Compare**
    ///
    /// This instruction compares the contents of the accumulator with another memory held value and sets the zero and carry flags as appropriate.
    ///
    /// Carry: A >= M
    /// Zero:  A == M
    /// Negative: Bit 7 of result (A - M)
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CMP*
    public static Instruction CMP = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xC9, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xC5, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xD5, 2, 4, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xCD, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xDD, 3, 4, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0xD9, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0xC1, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0xD1, 2, 5, AddressingMode.INDIRECT_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        int value = Byte.toUnsignedInt(memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars));
        int accumulator = Byte.toUnsignedInt(registrars.accumulator);

        int result = accumulator - value;

        // Carry clear = borrow occurred (i.e., A < M)
        registrars.carryFlag = accumulator >= value;

        registrars.zeroFlag = (accumulator == value);
        registrars.negativeFlag = (result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **CMX - Compare X Register**
    ///
    /// This instruction compares the contents of the X register with another memory held value and sets the zero and carry flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CMX*
    public static Instruction CMX = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xE0, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xE4, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xEC, 3, 4, AddressingMode.ABSOLUTE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        int value = Byte.toUnsignedInt(memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars));
        int registerX = Byte.toUnsignedInt(registrars.registerX);

        int result = registerX - value;

        // Carry clear = borrow occurred (i.e., A < M)
        registrars.carryFlag = registerX >= value;

        registrars.zeroFlag = (registerX == value);
        registrars.negativeFlag = (result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **CMX - Compare Y Register**
    ///
    /// This instruction compares the contents of the Y register with another memory held value and sets the zero and carry flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CMY*
    public static Instruction CMY = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xC0, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xC4, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xCC, 3, 4, AddressingMode.ABSOLUTE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        int value = Byte.toUnsignedInt(memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars));
        int registerY = Byte.toUnsignedInt(registrars.registerY);

        int result = registerY - value;

        // Carry clear = borrow occurred (i.e., A < M)
        registrars.carryFlag = registerY >= value;

        registrars.zeroFlag = (registerY == value);
        registrars.negativeFlag = (result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });
    //endregion

    //region * Increments & Decrements *
    private static byte incdec$oldValue;
    private static byte incdec$result;

    ///**INC - Increment Memory**
    ///
    /// Adds one to the value held at a specified memory location setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#INC*
    public static Instruction INC = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xE6, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xF6, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xEE, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xFE, 3, 7, AddressingMode.ABSOLUTE_X)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        incdec$oldValue = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        incdec$result = (byte) ((Byte.toUnsignedInt(incdec$oldValue) + 1) & 0xFF);

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, incdec$result);

        registrars.zeroFlag = incdec$result == 0;
        registrars.negativeFlag = (incdec$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });
    ///**INX - Increment X Register**
    ///
    /// Adds one to the X register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#INX*
    public static Instruction INX = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xE8, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        incdec$oldValue = registrars.registerX;

        incdec$result = (byte) ((Byte.toUnsignedInt(incdec$oldValue) + 1) & 0xFF);

        registrars.registerX = incdec$result;

        registrars.zeroFlag = incdec$result == 0;
        registrars.negativeFlag = (incdec$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    ///**INY - Increment Y Register**
    ///
    /// Adds one to the Y register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#INX*
    public static Instruction INY = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xC8, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        incdec$oldValue = registrars.registerY;

        incdec$result = (byte) ((Byte.toUnsignedInt(incdec$oldValue) + 1) & 0xFF);

        registrars.registerY = incdec$result;

        registrars.zeroFlag = incdec$result == 0;
        registrars.negativeFlag = (incdec$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    ///**DEC - Decrement Memory**
    ///
    /// Subtracts one from the value held at a specified memory location setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#DEC*
    public static Instruction DEC = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xC6, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xD6, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xCE, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xDE, 3, 7, AddressingMode.ABSOLUTE_X)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        incdec$oldValue = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        incdec$result = (byte) ((Byte.toUnsignedInt(incdec$oldValue) - 1) & 0xFF);

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, incdec$result);

        registrars.zeroFlag = incdec$result == 0;
        registrars.negativeFlag = (incdec$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    ///**DEX - Decrement X Register**
    ///
    /// Subtracts one from the X register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#DEX*
    public static Instruction DEX = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xCA, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        incdec$oldValue = registrars.registerX;

        incdec$result = (byte) ((Byte.toUnsignedInt(incdec$oldValue) - 1) & 0xFF);

        registrars.registerX = incdec$result;

        registrars.zeroFlag = incdec$result == 0;
        registrars.negativeFlag = (incdec$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    ///**DEY - Increment Y Register**
    ///
    /// Subtracts one from the Y register setting the zero and negative flags as appropriate.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#DEY*
    public static Instruction DEY = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x88, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        incdec$oldValue = registrars.registerY;

        incdec$result = (byte) ((Byte.toUnsignedInt(incdec$oldValue) - 1) & 0xFF);

        registrars.registerY = incdec$result;

        registrars.zeroFlag = incdec$result == 0;
        registrars.negativeFlag = (incdec$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });
    //endregion

    //region * Shifts *
    private static byte shift$oldValue;
    private static byte shift$result;

    /// **ASL - Arithmetic Shift Left**
    ///
    /// This operation shifts all the bits of the accumulator or memory contents one bit left. Bit 0 is set to 0 and bit 7 is placed in the carry flag. The effect of this operation is to multiply the memory contents by 2 (ignoring 2's complement considerations), setting the carry if the result will not fit in 8 bits.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#ASL*
    public static Instruction ASL = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x0A, 1, 2, AddressingMode.ACCUMULATOR),
            new InstructionInfo(0x06, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x16, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x0E, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x1E, 3, 7, AddressingMode.ABSOLUTE_X)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        shift$oldValue = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        shift$result = (byte)((Byte.toUnsignedInt(shift$oldValue) << 1) & 0xFF);

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, shift$result);

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.carryFlag = (shift$oldValue & 0x80) != 0;
        registrars.negativeFlag = (shift$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **LSR - Logical Shift Right**
    ///
    /// Each of the bits in A or M is shift one place to the right. The bit that was in bit 0 is shifted into the carry flag. Bit 7 is set to zero.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#LSR*
    public static Instruction LSR = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x4A, 1, 2, AddressingMode.ACCUMULATOR),
            new InstructionInfo(0x46, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x56, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x4E, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x5E, 3, 7, AddressingMode.ABSOLUTE_X)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        shift$oldValue = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        shift$result = (byte)((Byte.toUnsignedInt(shift$oldValue) >>> 1) & 0xFF);

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, shift$result);

        registrars.zeroFlag = shift$result == 0;
        registrars.carryFlag = (shift$oldValue & 0x01) != 0;
        registrars.negativeFlag = (shift$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **ROL - Rotate Left**
    ///
    /// Move each of the bits in either A or M one place to the left. Bit 0 is filled with the current value of the carry flag whilst the old bit 7 becomes the new carry flag value.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#ROL*
    public static Instruction ROL = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x2A, 1, 2, AddressingMode.ACCUMULATOR),
            new InstructionInfo(0x26, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x36, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x2E, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x3E, 3, 7, AddressingMode.ABSOLUTE_X)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        shift$oldValue = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        shift$result = (byte)((Byte.toUnsignedInt(shift$oldValue) << 1) & 0xFF);
        shift$result |= (byte)(registrars.carryFlag ? 1 : 0);

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, shift$result);

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.carryFlag = (shift$oldValue & 0x80) != 0;
        registrars.negativeFlag = (shift$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **ROR - Rotate Right**
    ///
    /// Move each of the bits in either A or M one place to the right. Bit 7 is filled with the current value of the carry flag whilst the old bit 0 becomes the new carry flag value.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#ROR*
    public static Instruction ROR = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x6A, 1, 2, AddressingMode.ACCUMULATOR),
            new InstructionInfo(0x66, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x76, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x6E, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x7E, 3, 7, AddressingMode.ABSOLUTE_X)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        shift$oldValue = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);

        shift$result = (byte)((Byte.toUnsignedInt(shift$oldValue) >> 1) & 0xFF);
        shift$result |= (byte)(registrars.carryFlag ? 0x80 : 0);

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, shift$result);

        registrars.zeroFlag = registrars.accumulator == 0;
        registrars.carryFlag = (shift$oldValue & 0x1) != 0;
        registrars.negativeFlag = (shift$result & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
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
    public static Instruction JMP = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x4C, 3, 3, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x6C, 3, 5, AddressingMode.INDIRECT)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        short address = memory.getAddress(programCounter, instructionInfo.mode(), registrars);

        if(!Constants.JMP_INDIRECT_BUG || instructionInfo.mode() == AddressingMode.ABSOLUTE) return address;

        if((address & 0x00FF) == 0x00FF) { //TODO maybe wrong?
            int low = Byte.toUnsignedInt((byte)(address & 0x00FF));
            int high = Byte.toUnsignedInt((byte)(address & 0xFF00));

            return (short)((high << 8) | low);
        }

        return address;
    });

    /// **JSR - Jump to Subroutine**
    ///
    /// The JSR instruction pushes the address (minus one) of the return point on to the stack and then sets the program counter to the target memory address.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#JSR*
    public static Instruction JSR = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x20, 3, 6, AddressingMode.ABSOLUTE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        short targetAddress = memory.getAddress(programCounter, instructionInfo.mode(), registrars);
        short returnAddress = (short)((programCounter + instructionInfo.size() - 1) & 0xFFFF);

        memory.pushToStack((byte)(returnAddress >> 8));
        memory.pushToStack((byte)(returnAddress & 0xFF));

        return targetAddress;
    });

    /// **RTS - Return from Subroutine**
    ///
    /// The RTS instruction is used at the end of a subroutine to return to the calling routine. It pulls the program counter (minus one) from the stack.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#RTS*
    public static Instruction RTS = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x60, 1, 6, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        int low = Byte.toUnsignedInt(memory.pullFromStack());
        int high = Byte.toUnsignedInt(memory.pullFromStack()) << 8;

        return (short)((high | low) & 0xFFFF);
    });
    //endregion

    //region * Branches *
    /// **BCC - Branch if Carry Clear**
    ///
    /// If the carry flag is clear then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BCC*
    public static Instruction BCC = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x90, 2, 2, AddressingMode.RELATIVE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        if(registrars.carryFlag) return (short)(programCounter + instructionInfo.size());
        return (short)(Short.toUnsignedInt(memory.getAddress(programCounter, instructionInfo.mode(), registrars)) + instructionInfo.size());
    });
    /// **BCS - Branch if Carry Set**
    ///
    /// If the carry flag is set then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BCS*
    public static Instruction BCS = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xB0, 2, 2, AddressingMode.RELATIVE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        if(!registrars.carryFlag) return (short)(programCounter + instructionInfo.size());
        return (short)(Short.toUnsignedInt(memory.getAddress(programCounter, instructionInfo.mode(), registrars)) + instructionInfo.size());
    });

    /// **BNE - Branch if Not Equal**
    ///
    /// If the zero flag is clear then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BNE*
    public static Instruction BNE = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xD0, 2, 2, AddressingMode.RELATIVE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        if(registrars.zeroFlag) return (short)(programCounter + instructionInfo.size());
        return (short)(Short.toUnsignedInt(memory.getAddress(programCounter, instructionInfo.mode(), registrars)) + instructionInfo.size());
    });

    /// **BEQ - Branch if Equal**
    ///
    /// If the zero flag is set then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BEQ*
    public static Instruction BEQ = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xF0, 2, 2, AddressingMode.RELATIVE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        if(!registrars.zeroFlag) return (short)(programCounter + instructionInfo.size());
        return (short)(Short.toUnsignedInt(memory.getAddress(programCounter, instructionInfo.mode(), registrars)) + instructionInfo.size());
    });

    /// **BPL - Branch if Positive**
    ///
    /// If the negative flag is clear then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BPL*
    public static Instruction BPL = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x10, 2, 2, AddressingMode.RELATIVE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        if(registrars.negativeFlag) return (short)(programCounter + instructionInfo.size());
        return (short)(Short.toUnsignedInt(memory.getAddress(programCounter, instructionInfo.mode(), registrars)) + instructionInfo.size());
    });

    /// **BMI - Branch if Minus**
    ///
    /// If the negative flag is set then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BMI*
    public static Instruction BMI = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x30, 2, 2, AddressingMode.RELATIVE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        if(!registrars.negativeFlag) return (short)(programCounter + instructionInfo.size());
        return (short)(Short.toUnsignedInt(memory.getAddress(programCounter, instructionInfo.mode(), registrars)) + instructionInfo.size());
    });

    /// **BVC - Branch if Overflow Clear**
    ///
    /// If the overflow flag is clear then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BVC*
    public static Instruction BVC = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x50, 2, 2, AddressingMode.RELATIVE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        if(registrars.overflowFlag) return (short)(programCounter + instructionInfo.size());
        return (short)(Short.toUnsignedInt(memory.getAddress(programCounter, instructionInfo.mode(), registrars)) + instructionInfo.size());
    });

    /// **BVS - Branch if Overflow Set**
    ///
    /// If the overflow flag is set then add the relative displacement to the program counter to cause a branch to a new location.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BVS*
    public static Instruction BVS = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x70, 2, 2, AddressingMode.RELATIVE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        if(!registrars.overflowFlag) return (short)(programCounter + instructionInfo.size());
        return (short)(Short.toUnsignedInt(memory.getAddress(programCounter, instructionInfo.mode(), registrars)) + instructionInfo.size());
    });

    //endregion

    //region * Status Flag Changes *
    /// **CLC - Clear Carry Flag**
    ///
    /// Set the carry flag to zero.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLC*
    public static Instruction CLC = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x18, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.carryFlag = false;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **CLD - Clear Decimal Mode**
    ///
    /// Sets the decimal mode flag to zero.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLD*
    public static Instruction CLD = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xD8, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.decimalModeFlag = false;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **CLI - Clear Interrupt Disable**
    ///
    /// Clears the interrupt disable flag allowing normal interrupt requests to be serviced.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLI*
    public static Instruction CLI = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x58, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.interruptDisableFlag = false;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **CLV - Clear Overflow Flag**
    ///
    /// Clears the overflow flag.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#CLV*
    public static Instruction CLV = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xB8, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.overflowFlag = false;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SEC - Set Carry Flag**
    ///
    /// Set the carry flag to one.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#SEC*
    public static Instruction SEC = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x38, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.carryFlag = true;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SED - Set Decimal Flag**
    ///
    /// Set the decimal mode flag to one.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#SED*
    public static Instruction SED = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xF8, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.decimalModeFlag = true;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SEI - Set Interrupt Flag**
    ///
    /// Set the interrupt disable flag to one.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#SEI*
    public static Instruction SEI = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x78, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.interruptDisableFlag = true;

        return (short)(programCounter + instructionInfo.size());
    });
    //endregion

    //region * System Functions *
    /// **BRK - Force Interrupt**
    ///
    /// The BRK instruction forces the generation of an interrupt request. The program counter and processor status are pushed on the stack then the IRQ interrupt vector at $FFFE/F is loaded into the PC and the break flag in the status set to one.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#BRK*
    public static Instruction BRK = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x00, 1, 7, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.breakFlag = true;

        //TODO

        return (short)(programCounter + instructionInfo.size());
    });

    /// **NOP - No Operation**
    ///
    /// The NOP instruction causes no changes to the processor other than the normal incrementing of the program counter to the next instruction.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#NOP*
    public static Instruction NOP = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0xEA, 1, 2, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> (short)(programCounter + instructionInfo.size()));

    /// **RTI - Return from Interrupt**
    ///
    /// The RTI instruction is used at the end of an interrupt processing routine. It pulls the processor flags from the stack followed by the program counter.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#RTI*
    public static Instruction RTI = new Instruction(new InstructionInfo[]{
            new InstructionInfo(0x40, 1, 6, AddressingMode.IMPLIED)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        registrars.breakFlag = false;

        //TODO

        return (short)(programCounter + instructionInfo.size());
    });
    //endregion
}
