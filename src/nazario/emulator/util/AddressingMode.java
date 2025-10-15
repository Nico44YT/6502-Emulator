package nazario.emulator.util;

import nazario.emulator.InstructionSet;

/// The 6502 processor provides several ways in which memory locations can be addressed. Some instructions support several different modes while others may only support one. In addition the two index registers can not always be used interchangeably. This lack of orthogonality in the instruction set is one of the features that makes the 6502 trickier to program well.
///
/// http://www.6502.org/users/obelisk/6502/addressing.html
public enum AddressingMode {
    /// **Relative**
    ///
    /// Relative addressing mode is used by branch instructions (e.g. BEQ, BNE, etc.) which contain a signed 8 bit relative offset (e.g. -128 to +127) which is added to program counter if the condition is true. As the program counter itself is incremented during instruction execution by two the effective address range for the target instruction must be with -126 to +129 bytes of the branch.
    ///
    /// *http://www.6502.org/users/obelisk/6502/addressing.html#REL*
    RELATIVE,

    /// **Immediate**
    ///
    /// Immediate addressing allows the programmer to directly specify an 8 bit constant within the instruction. It is indicated by a '#' symbol followed by an numeric expression.
    ///
    ///*http://www.6502.org/users/obelisk/6502/addressing.html#IMM*
    IMMEDIATE,

    /// **Zero Page**
    ///
    /// An instruction using zero page addressing mode has only an 8 bit address operand. This limits it to addressing only the first 256 bytes of memory (e.g. $0000 to $00FF) where the most significant byte of the address is always zero. In zero page mode only the least significant byte of the address is held in the instruction making it shorter by one byte (important for space saving) and one less memory fetch during execution (important for speed).
    ///
    /// An assembler will automatically select zero page addressing mode if the operand evaluates to a zero page address and the instruction supports the mode (not all do).
    ///
    ///*http://www.6502.org/users/obelisk/6502/addressing.html#ZPG*
    ZERO_PAGE,

    /// **Zero Page,X**
    ///
    /// The address to be accessed by an instruction using indexed zero page addressing is calculated by taking the 8 bit zero page address from the instruction and adding the current value of the {@link nazario.emulator.Registrars#registerX X register} to it. For example if the {@link nazario.emulator.Registrars#registerX X register} contains $0F and the instruction {@link InstructionSet#LDA LDA} $80,X is executed then the {@link nazario.emulator.Registrars#accumulator accumulator} will be loaded from $008F (e.g. $80 + $0F => $8F).
    ///
    /// NB:
    /// The address calculation wraps around if the sum of the base address and the register exceed $FF. If we repeat the last example but with $FF in the {@link nazario.emulator.Registrars#registerX X register} then the accumulator will be loaded from $007F (e.g. $80 + $FF => $7F) and not $017F.
    ///
    ///*http://www.6502.org/users/obelisk/6502/addressing.html#ZPX*
    ZERO_PAGE_X,

    /// **Zero Page,Y**
    ///
    /// The address to be accessed by an instruction using indexed zero page addressing is calculated by taking the 8 bit zero page address from the instruction and adding the current value of the {@link nazario.emulator.Registrars#registerY Y register} to it. This mode can only be used with the {@link InstructionSet#LDX LDX} and {@link InstructionSet#STX STX} instructions.
    ///
    ///*http://www.6502.org/users/obelisk/6502/addressing.html#ZPY*
    ZERO_PAGE_Y,

    /// **Absolute**
    ///
    /// Instructions using absolute addressing contain a full 16 bit address to identify the target location.
    ///
    ///*http://www.6502.org/users/obelisk/6502/addressing.html#ABS*
    ABSOLUTE,

    /// **Absolute,X**
    ///
    /// The address to be accessed by an instruction using X register indexed absolute addressing is computed by taking the 16 bit address from the instruction and added the contents of the {@link nazario.emulator.Registrars#registerX X register}. For example if X contains $92 then an STA $2000,X instruction will store the accumulator at $2092 (e.g. $2000 + $92).
    ///
    ///*http://www.6502.org/users/obelisk/6502/addressing.html#ABX*
    ABSOLUTE_X,

    /// **Absolute,Y**
    ///
    /// The Y register indexed absolute addressing mode is the same as the previous mode only with the contents of the {@link nazario.emulator.Registrars#registerY Y register} added to the 16 bit address from the instruction.
    ///
    ///*http://www.6502.org/users/obelisk/6502/addressing.html#ABY*
    ABSOLUTE_Y,

    ///*http://www.6502.org/users/obelisk/6502/addressing.html#IND*
    INDIRECT,   // JMP only

    /// **Indexed Indirect / (Indirect,X)**
    ///
    ///  Indexed indirect addressing is normally used in conjunction with a table of address held on zero page. The address of the table is taken from the instruction and the X register added to it (with zero page wrap around) to give the location of the least significant byte of the target address.
    ///
    /// *http://www.6502.org/users/obelisk/6502/addressing.html#IDX*
    INDIRECT_X,

    /// **Indirect Indexed / (Indirect),Y**
    ///
    /// Indirect indirect addressing is the most common indirection mode used on the 6502. In instruction contains the zero page location of the least significant byte of 16 bit address. The Y register is dynamically added to this value to generated the actual target address for operation.
    ///
    /// *http://www.6502.org/users/obelisk/6502/addressing.html#IDY*
    INDIRECT_Y, // (Indirect),Y

    /// **Accumulator**
    ///
    /// Some instructions have an option to operate directly upon the accumulator. The programmer specifies this by using a special operand value, 'A'. For example:
    ///
    ///
    ACCUMULATOR,

    /// **Implicit / Implied**
    ///
    /// For many 6502 instructions the source and destination of the information to be manipulated is implied directly by the function of the instruction itself and no further operand needs to be specified. Operations like {@link InstructionSet#CLC 'Clear Carry Flag' (CLC)} and {@link InstructionSet#RTS 'Return from Subroutine' (RTS)} are implicit.
    ///
    ///*http://www.6502.org/users/obelisk/6502/addressing.html#IMP*
    IMPLIED
}
