package assembler;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
   public static void main(String args[]) throws FileNotFoundException, IOException {  
      String inputFile = args[0];
      Parser parser = new Parser(inputFile);

      // First pass fills the symbol table with all the program labels
      SymbolTable symbolTable = new SymbolTable();
      int addressCounter = 0;

      while (parser.hasMoreCommands()) {
         parser.advance();
         if (parser.commandType() == CommandType.C_COMMAND || parser.commandType() == CommandType.A_COMMAND) {
            addressCounter++;
         }  else {
            // L command
            symbolTable.addEntry(parser.symbol(), addressCounter);
         }
      }

      // Second pass handles variables and generates machine code
      String outputFile = inputFile.split("\\.")[0] + ".hack";
      FileWriter writer = new FileWriter(outputFile);

      int currentRamAddress = 16; // the next available RAM address
      parser.reset();
      while (parser.hasMoreCommands()) {
         parser.advance();
         if (parser.commandType() == CommandType.C_COMMAND) {
            String comp = Code.comp(parser.comp());
            String dest = Code.dest(parser.dest());
            String jump = Code.jump(parser.jump());
            writer.write(String.format("111%s%s%s\n", comp, dest, jump));
         } else if (parser.commandType() == CommandType.A_COMMAND) {
            // try to parse the symbol as an integer
            Integer symbolInteger = null;
            try {
               symbolInteger = Integer.parseInt(parser.symbol());
            } catch (NumberFormatException e) {
               // this is a symbolic A instruction 
               if (symbolTable.contains(parser.symbol())) {
                  // symbol is already in the table
                  symbolInteger = symbolTable.getAddress(parser.symbol());
               } else {
                  // symbol represents a new variable
                  symbolTable.addEntry(parser.symbol(), currentRamAddress);
                  symbolInteger = currentRamAddress;
                  currentRamAddress++;
               }
            }
            String symbolBinary = String.format(
               "%16s", 
               Integer.toBinaryString(symbolInteger)).replace(" ", "0"
            );
            writer.write(String.format("%s\n", symbolBinary));
         }
      }
      writer.close();
   }
}