package nazario.emulator;

public class Registrars {
    protected byte registerX;
    protected byte registerY;
    protected byte accumulator;

    protected boolean carryFlag;
    protected boolean zeroFlag;
    protected boolean breakFlag;
    protected boolean negativeFlag;
    protected boolean decimalModeFlag;
    protected boolean interruptDisableFlag;
    protected boolean overflowFlag;

    public byte getProcessorStatus() {
        byte status = 0;
        if (negativeFlag)       status |= (1 << 7);
        if (overflowFlag)       status |= (1 << 6);
        status |= (1 << 5); // Unused bit, always set to 1 when pushed
        if (breakFlag)          status |= (1 << 4);
        if (decimalModeFlag)        status |= (1 << 3);
        if (interruptDisableFlag)  status |= (1 << 2);
        if (zeroFlag)           status |= (1 << 1);
        if (carryFlag)          status |= (1 << 0);
        return status;
    }

    public void setProcessorStatus(byte status) {
        negativeFlag      = (status & (1 << 7)) != 0;
        overflowFlag      = (status & (1 << 6)) != 0;

        breakFlag         = (status & (1 << 4)) != 0;
        decimalModeFlag       = (status & (1 << 3)) != 0;
        interruptDisableFlag = (status & (1 << 2)) != 0;
        zeroFlag          = (status & (1 << 1)) != 0;
        carryFlag         = (status & (1 << 0)) != 0;
    }

}
