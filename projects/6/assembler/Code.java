package assembler;

import java.util.Map;
import static java.util.Map.entry;    


public class Code {

    private static final Map<String, String> jumpMap = Map.ofEntries(
        entry("null", "000"),
        entry("JGT", "001"),
        entry("JEQ", "010"),
        entry("JGE", "011"),
        entry("JLT", "100"),
        entry("JNE", "101"),
        entry("JLE", "110"),
        entry("JMP", "111")
    );

    private static final Map<String, String> destMap = Map.ofEntries(
        entry("null", "000"),
        entry("M", "001"),
        entry("D", "010"),
        entry("MD", "011"),
        entry("A", "100"),
        entry("AM", "101"),
        entry("AD", "110"),
        entry("AMD", "111")
    );

    private static final Map<String, String> compMap = Map.ofEntries(
        entry("0", "0101010"),
		entry("1", "0111111"),
		entry("-1","0111010"),
		entry("D", "0001100"),
		entry("A", "0110000"),
		entry("!D", "0001101"),
		entry("!A", "0110001"),
		entry("-D", "0001111"),
		entry("-A", "0110011"),
		entry("D+1","0011111"),
		entry("A+1","0110111"),
		entry("D+A","0000010"),
		entry("D-A","0010011"),
		entry("A-D","0000111"),
		entry("D&A","0000000"),
		entry("D|A","0010101"),	
		entry("M","1110000"),
		entry("!M","1110001"),
		entry("-M","1110011"),
		entry("M+1","1110111"),
		entry("M-1","1110010"),
		entry("D+M","1000010"),
		entry("D-M","1010011"),
		entry("M-D","1000111"),
		entry("D&M","1000000"),
		entry("D|M","1010101"),
		entry("D-1","0001110"),
		entry("A-1","0110010")
    );


    public static String dest(String mnemonic) {
        // returns the binary code of the dest mnemonic
        return destMap.get(mnemonic);
    }

    public static String comp(String mnemonic) {
        // returns the binary code of the comp mnemonic
        return compMap.get(mnemonic);
    }

    public static String jump(String mnemonic) {
        // returns the binary code of the jump mnemonic
        return jumpMap.get(mnemonic);
    }
}
