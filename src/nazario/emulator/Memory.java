package nazario.emulator;

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

    public int getUnsignedAddress(int index) {
        return Byte.toUnsignedInt(this.memory[index + 1]) << 8 | Byte.toUnsignedInt(this.memory[index]);
    }

    public byte getValueFromAddress(int index) {
        return this.getValue(getUnsignedAddress(index));
    }

    public int getZeroPageWord(int address) {
        address &= 0xFF;
        int low = Byte.toUnsignedInt(memory[address]);
        int high = Byte.toUnsignedInt(memory[(address + 1) & 0xFF]);
        return (high << 8) | low;
    }

    public byte[] getMemory() {
        return this.memory;
    }
}
