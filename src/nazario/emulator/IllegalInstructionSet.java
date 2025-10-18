package nazario.emulator;

import nazario.emulator.util.*;

import java.util.ArrayList;
import java.util.List;

public class IllegalInstructionSet {
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

    public static Pair<InstructionInfo, InstructionsFunction>[] addIllegalInstructions(Pair<InstructionInfo, InstructionsFunction>[] instructionSet) {
        Pair<InstructionInfo, InstructionsFunction>[] illegalInstructions = getInstructions();

        for(int i = 0;i<instructionSet.length;i++) {
            if(instructionSet[i] == null) {
                instructionSet[i] = illegalInstructions[i];
            }
        }
        return instructionSet;
    }

    public static Instruction NOP = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x80, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0x82, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xC2, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0xE2, 2, 2, AddressingMode.IMMEDIATE),
            new InstructionInfo(0x89, 2, 2, AddressingMode.IMMEDIATE),

            new InstructionInfo(0x04, 2, 2, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x14, 2, 2, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x34, 2, 2, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x44, 2, 2, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x54, 2, 2, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x64, 2, 2, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x74, 2, 2, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xD4, 2, 2, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xF4, 2, 2, AddressingMode.ZERO_PAGE_X),

            new InstructionInfo(0x1A, 1, 2, AddressingMode.IMPLIED),
            new InstructionInfo(0x3A, 1, 2, AddressingMode.IMPLIED),
            new InstructionInfo(0x5A, 1, 2, AddressingMode.IMPLIED),
            new InstructionInfo(0x7A, 1, 2, AddressingMode.IMPLIED),
            new InstructionInfo(0xDA, 1, 2, AddressingMode.IMPLIED),
            new InstructionInfo(0xFA, 1, 2, AddressingMode.IMPLIED),

            new InstructionInfo(0x0C, 3, 2, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x1C, 3, 2, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x3C, 3, 2, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x5C, 3, 2, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x7C, 3, 2, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0xDC, 3, 2, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0xFC, 3, 2, AddressingMode.ABSOLUTE_X)
    }, (programCounter, memory, registrars, instructionInfo) -> (short)(programCounter + instructionInfo.size()));

    public static Instruction JAM = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x02, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0x12, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0x22, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0x32, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0x42, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0x52, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0x62, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0x72, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0x92, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0xB2, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0xD2, 1, 0, AddressingMode.IMPLIED),
            new InstructionInfo(0xF2, 1, 0, AddressingMode.IMPLIED),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        throw new ProcessorJamInstructionCalled("ProgramCounter: " + programCounter + ", Opcode: " + instructionInfo.opcode());
    });

    /// **ALR (ASR)**
    ///
    /// AND oper + LSR
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#ALR*
    public static Instruction ALR = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x4B, 2, 2, AddressingMode.IMMEDIATE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.AND.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.LSR.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });


    /// **ANC**
    ///
    /// AND oper + set C as ASL
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#ANC*
    public static Instruction ANC = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x0B, 2, 2, AddressingMode.IMMEDIATE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.AND.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        //TODO A AND oper, bit(7) -> C

        return (short)(programCounter + instructionInfo.size());
    });

    /// **ANC (ANC2)**
    ///
    /// AND oper + set C as ROL
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#ANC2*
    public static Instruction ANC2 = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x2B, 2, 2, AddressingMode.IMMEDIATE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.AND.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        //TODO A AND oper, bit(7) -> C

        return (short)(programCounter + instructionInfo.size());
    });

    /// **ANE (XAA)**
    ///
    /// \* OR X + AND oper
    ///
    /// A base value in A is determined based on the contents of A and a constant, which may be typically $00, $ff, $ee, etc. The value of this constant depends on temperature, the chip series, and maybe other factors, as well.
    /// In order to eliminate these uncertainties from the equation, use either 0 as the operand or a value of $FF in the accumulator.
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#ANE*
    public static Instruction ANE = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x8B, 2, 2, AddressingMode.IMMEDIATE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.AND.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        //fuck this, I don't wanna add temperature

        return (short)(programCounter + instructionInfo.size());
    });

    /// **ARR**
    ///
    /// AND oper + ROR
    ///
    /// This operation involves the adder:
    /// V-flag is set according to (A AND oper) + oper
    /// The carry is not set, but bit 7 (sign) is exchanged with the carry
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#ARR*
    public static Instruction ARR = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x6B, 2, 2, AddressingMode.IMMEDIATE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.AND.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.ROR.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **DCP (DCM)**
    ///
    /// DEC oper + CMP oper
    ///
    /// Decrements the operand and then compares the result to the accumulator.
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#DCP*
    public static Instruction DCP = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0xC7, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xD7, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xCF, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xDF, 3, 7, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0xDB, 3, 7, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0xC3, 2, 8, AddressingMode.INDIRECT_X),
            new InstructionInfo(0xD3, 2, 8, AddressingMode.INDIRECT_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.DEC.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.CMP.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **ISC (ISB, INS)**
    ///
    /// INC oper + SBC oper
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#ISC*
    public static Instruction ISC = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0xE7, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xF7, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0xEF, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xFF, 3, 7, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0xFB, 3, 7, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0xE3, 2, 8, AddressingMode.INDIRECT_X),
            new InstructionInfo(0xF3, 2, 8, AddressingMode.INDIRECT_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.INC.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.SBC.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **LAS (LAR)**
    ///
    /// LDA/TSX oper
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#LAS*
    public static Instruction LAS = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0xBB, 3, 4, AddressingMode.ABSOLUTE_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.LDA.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.TSX.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **LAX**
    ///
    /// LDA oper + LDX oper
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#LAX*
    public static Instruction LAX = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0xA7, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0xB7, 2, 4, AddressingMode.ZERO_PAGE_Y),
            new InstructionInfo(0xAF, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0xBF, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0xA3, 2, 6, AddressingMode.INDIRECT_X),
            new InstructionInfo(0xB3, 2, 5, AddressingMode.INDIRECT_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.LDA.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.LDX.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **LXA (LAX immediate)**
    ///
    /// Store * AND oper in A and X
    ///
    /// __Highly unstable, involves a 'magic' constant, see {@link #ANE}__
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#LXA*
    public static Instruction LXA = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0xA7, 2, 2, AddressingMode.IMMEDIATE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.LDA.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.LDX.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        //Again I'm NOT adding temperature

        return (short)(programCounter + instructionInfo.size());
    });

    /// **RLA**
    ///
    /// ROL oper + AND oper
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#RLA*
    public static Instruction RLA = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x27, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x37, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x2F, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x3F, 3, 7, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x3B, 3, 7, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x23, 2, 8, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x33, 2, 8, AddressingMode.INDIRECT_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.ROL.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.AND.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **RRA**
    ///
    /// ROR oper + ADC oper
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#RRA*
    public static Instruction RRA = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x67, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x77, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x6F, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x7F, 3, 7, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x7B, 3, 7, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x63, 2, 8, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x73, 2, 8, AddressingMode.INDIRECT_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.ROR.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.ADC.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SAX (AXS, AAX)**
    ///
    /// A AND X -> M
    ///
    /// A and X are put on the bus at the same time (resulting effectively in an AND operation) and stored in M
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#SAX*
    public static Instruction SAX = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x87, 2, 3, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x97, 2, 4, AddressingMode.ZERO_PAGE_Y),
            new InstructionInfo(0x8F, 3, 4, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x83, 2, 6, AddressingMode.INDIRECT_X),
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = (byte)(registrars.accumulator & registrars.registerX);

        memory.writeValueToAddress(
                programCounter,
                instructionInfo.mode(),
                registrars,
                value
        );

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SBX (AXS, SAX)**
    ///
    /// (A AND X) - oper -> X
    ///
    /// CMP and DEX at once, sets flags like {@linkplain InstructionSet#CMP CMP}
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#SBX*
    public static Instruction SBX = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0xCB, 2, 2, AddressingMode.IMMEDIATE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte operand = memory.getValueFromAddress(programCounter, instructionInfo.mode(), registrars);
        byte value = (byte)(registrars.accumulator & registrars.registerX);

        registrars.registerX = (byte)((Byte.toUnsignedInt(value) - Byte.toUnsignedInt(operand)) & 0xFF);

        registrars.carryFlag = registrars.accumulator >= value;

        registrars.zeroFlag = (registrars.accumulator == value);
        registrars.negativeFlag = (registrars.registerX & 0x80) != 0;

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SHA (AHX, AXA)**
    ///
    /// A AND X AND (H+1) -> M
    ///
    /// Stores A AND X AND (high-byte of addr. + 1) at addr.
    /// __unstable: sometimes 'AND (H+1)' is dropped, page boundary crossings may not work (with the high-byte of the value used as the high-byte of the address)__
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#SHA*
    public static Instruction SHA = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x9F, 3, 4, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x93, 2, 5, AddressingMode.INDIRECT_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = (byte)(registrars.accumulator & registrars.registerX & memory.getValue(programCounter + (instructionInfo.size() - 1)));

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, value);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SHX (A11, SXA, XAS)**
    ///
    /// X AND (H+1) -> M
    ///
    /// Stores X AND (high-byte of addr. + 1) at addr.
    /// __unstable: sometimes 'AND (H+1)' is dropped, page boundary crossings may not work (with the high-byte of the value used as the high-byte of the address)__
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#SHY*
    public static Instruction SHX = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x9E, 3, 5, AddressingMode.ABSOLUTE_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = (byte)(registrars.registerX & memory.getValue(programCounter + 2));

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, value);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SHY (A11, SYA, SAY)**
    ///
    /// Y AND (H+1) -> M
    ///
    /// Stores Y AND (high-byte of addr. + 1) at addr.
    /// __unstable: sometimes 'AND (H+1)' is dropped, page boundary crossings may not work (with the high-byte of the value used as the high-byte of the address)__
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#SHY*
    public static Instruction SHY = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x9F, 3, 5, AddressingMode.ABSOLUTE_X)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        byte value = (byte)(registrars.registerY & memory.getValue(programCounter + 2));

        memory.writeValueToAddress(programCounter, instructionInfo.mode(), registrars, value);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SLO (ASO)**
    ///
    /// ASL oper + ORA oper
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#SLO*
    public static Instruction SLO = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x07, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x17, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x0F, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x1F, 3, 7, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x1B, 3, 8, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x03, 2, 8, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x13, 2, 8, AddressingMode.INDIRECT_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.ASL.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.ORA.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **SRE (LSE)**
    ///
    /// LSR oper + EOR oper
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#SRE*
    public static Instruction SRE = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x47, 2, 5, AddressingMode.ZERO_PAGE),
            new InstructionInfo(0x57, 2, 6, AddressingMode.ZERO_PAGE_X),
            new InstructionInfo(0x4F, 3, 6, AddressingMode.ABSOLUTE),
            new InstructionInfo(0x5F, 3, 7, AddressingMode.ABSOLUTE_X),
            new InstructionInfo(0x5B, 3, 8, AddressingMode.ABSOLUTE_Y),
            new InstructionInfo(0x43, 2, 8, AddressingMode.INDIRECT_X),
            new InstructionInfo(0x53, 2, 8, AddressingMode.INDIRECT_Y)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.LSR.getFunction().apply(programCounter, memory, registrars, instructionInfo);
        InstructionSet.EOR.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });

    /// **TAS (XAS, SHS)**
    ///
    /// A AND X -> SP, A AND X AND (H+1) -> M
    ///
    /// Puts A AND X in SP and stores A AND X AND (high-byte of addr. + 1) at addr.
    /// __unstable: sometimes 'AND (H+1)' is dropped, page boundary crossings may not work (with the high-byte of the value used as the high-byte of the address)__
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#TAS*
    public static Instruction TAS = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0x9B, 3, 5, AddressingMode.ABSOLUTE_Y),
    }, (programCounter, memory, registrars, instructionInfo) -> {

        //Don't wanna implement at the moment
        throw new RuntimeException("TAS illegal instruction is not implemented!");

    });

    /// **USBC (SBC)**
    ///
    /// SBC oper + NOP
    ///
    /// *https://www.masswerk.at/6502/6502_instruction_set.html#SRE*
    public static Instruction USBC = new IllegalInstruction(new InstructionInfo[]{
            new InstructionInfo(0xEB, 2, 5, AddressingMode.IMMEDIATE)
    }, (programCounter, memory, registrars, instructionInfo) -> {
        InstructionSet.SBC.getFunction().apply(programCounter, memory, registrars, instructionInfo);

        return (short)(programCounter + instructionInfo.size());
    });
}
