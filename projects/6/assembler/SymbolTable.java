package assembler;

import java.util.Map;
import java.util.HashMap;


public class SymbolTable {
    Map<String, Integer> table;

    public SymbolTable() {
        table = new HashMap<String, Integer>();
        addEntry("SP", 0);
        addEntry("SP", 0);
        addEntry("LCL", 1);
        addEntry("ARG", 2);
        addEntry("THIS", 3);
        addEntry("THAT", 4);
        addEntry("SCREEN", 16384);
        addEntry("KBD", 24576);

        // add R0-R15 to symbol table
        for (int i = 0; i < 16; i++) {
            String symbol = String.format("R%s", Integer.toString(i));
            addEntry(symbol, i);
        }
    }

    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return table.get(symbol);
    }
    
}
