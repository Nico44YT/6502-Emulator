package nazario.emulator;

import nazario.emulator.util.AddressingMode;

public class Memory {
    protected byte[] memory;
    protected short stackPointer;

    public Memory() {
        this.memory = new byte[0x10000];
        this.stackPointer = 0x0100;
    }

    public void pushToStack(byte value) {
        this.memory[stackPointer++] = value;
    }

    public byte pullFromStack() {
        return this.memory[stackPointer--];
    }

    public short getStackPointer() {
        return this.stackPointer;
    }

    public void writeValue(int index, byte value) {
        this.memory[index & 0xFFFF] = value;
    }

    public byte getValue(int index) {
        return this.memory[index & 0xFFFF];
    }

    public byte getStackPointerValue() {
        return this.getValue(this.getStackPointer());
    }

    public void writeToStackPointer(byte value) {
        this.writeValue(this.getStackPointer(), value);
    }

    public byte[] getMemory() {
        return this.memory;
    }

    ///If {@link AddressingMode} is equal to {@link AddressingMode#ACCUMULATOR ACCUMULATOR} it will return the value that is stored in the accumulator from {@link Registrars}.
    public byte getValueFromAddress(int programCounter, AddressingMode addressingMode, Registrars registrars) {
        if(addressingMode == AddressingMode.ACCUMULATOR) return registrars.accumulator;

        return getValue(
                getAddress(programCounter, addressingMode, registrars)
        );
    }

    ///If {@link AddressingMode} is equal to {@link AddressingMode#ACCUMULATOR ACCUMULATOR} it will set the accumulator from {@link Registrars} to value.
    public void writeValueToAddress(int programCounter, AddressingMode addressingMode, Registrars registrars, byte value) {
        if(addressingMode == AddressingMode.ACCUMULATOR) {
            registrars.accumulator = value;
            return;
        }

        short address = getAddress(programCounter, addressingMode, registrars);
        writeValue(address, value);
    }

    public short getAddress(int programCounter, AddressingMode addressingMode, Registrars registrars) {
        short address = switch(addressingMode) {
            case IMMEDIATE -> (short) ((programCounter + 1) & 0xFFFF);

            case RELATIVE -> {
                byte offset = getValue(programCounter + 1);
                yield (short)(programCounter + offset);
            }

            case ZERO_PAGE, ZERO_PAGE_X, ZERO_PAGE_Y ->
                    (short) Byte.toUnsignedInt(getValue(programCounter + 1));

            case ABSOLUTE, ABSOLUTE_X, ABSOLUTE_Y ->
                    (short) (((Byte.toUnsignedInt(getValue(programCounter + 2)) << 8)
                            | Byte.toUnsignedInt(getValue(programCounter + 1))) & 0xFFFF);

            case INDIRECT_X -> {
                int operand = Byte.toUnsignedInt(getValue(programCounter + 1));
                int pointer = (operand + Byte.toUnsignedInt(registrars.registerX)) & 0xFF; // zero-page wraparound

                int low = Byte.toUnsignedInt(getValue(pointer));
                int high = Byte.toUnsignedInt(getValue((pointer + 1) & 0xFF)); // also wraps zero-page
                yield (short) ((high << 8) | low);
            }

            case INDIRECT_Y -> {
                int operand = Byte.toUnsignedInt(getValue(programCounter + 1));
                int low = Byte.toUnsignedInt(getValue(operand));
                int high = Byte.toUnsignedInt(getValue((operand + 1) & 0xFF));
                int baseAddress = (high << 8) | low;
                yield (short) ((baseAddress + Byte.toUnsignedInt(registrars.registerY)) & 0xFFFF);
            }

            default -> throw new UnsupportedOperationException("Unsupported addressing mode: " + addressingMode);
        };

        // Apply register offset where appropriate
        return switch(addressingMode) {
            case ZERO_PAGE_X, ABSOLUTE_X -> (short) ((address + Byte.toUnsignedInt(registrars.registerX)) & 0xFFFF);
            case ZERO_PAGE_Y, ABSOLUTE_Y -> (short) ((address + Byte.toUnsignedInt(registrars.registerY)) & 0xFFFF);
            default -> address; // no further offset
        };
    }
}
