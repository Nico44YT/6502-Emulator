package nazario.emulator;

public class Constants {

    /// An original 6502 has does not correctly fetch the target address if the indirect vector falls on a page boundary (e.g. $xxFF where xx is any value from $00 to $FF). In this case fetches the LSB from $xxFF as expected but takes the MSB from $xx00. This is fixed in some later chips like the 65SC02 so for compatibility always ensure the indirect vector is not at the end of the page.
    ///
    /// *http://www.6502.org/users/obelisk/6502/reference.html#JMP*
    public static final boolean JMP_INDIRECT_BUG = false;
}
