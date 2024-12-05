package assembler;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
   public static void main(String args[]) throws FileNotFoundException, IOException{  
      String inputFile = args[0];
      Parser parser = new Parser(inputFile);

      String outputFile = inputFile.split("\\.")[0] + ".hack";
      FileWriter writer = new FileWriter(outputFile);

      while (parser.hasMoreCommands()) {
         parser.advance();
         if (parser.commandType() == CommandType.C_COMMAND) {
            String comp = Code.comp(parser.comp());
            String dest = Code.dest(parser.dest());
            String jump = Code.jump(parser.jump());
            writer.write(String.format("111%s%s%s\n", comp, dest, jump));
         } else {
            // A command
            int symbolInteger = Integer.parseInt(parser.symbol());
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