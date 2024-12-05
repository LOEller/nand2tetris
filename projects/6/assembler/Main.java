package assembler;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
   public static void main(String args[]) throws FileNotFoundException, IOException{  
      String inputFile = args[0];
      Parser parser = new Parser(inputFile);

      while (parser.hasMoreCommands()) {
         parser.advance();
         if (parser.commandType() == CommandType.C_COMMAND) {
            String comp = Code.comp(parser.comp());
            String dest = Code.dest(parser.dest());
            String jump = Code.jump(parser.jump());
            System.out.println("111" + comp + dest + jump);
         } else {
            // A command
            int symbolInteger = Integer.parseInt(parser.symbol());
            String symbolBinary = String.format(
               "%16s", 
               Integer.toBinaryString(symbolInteger)).replace(" ", "0"
            );
            System.out.println(symbolBinary);
         }
      }
   }
}