package assembler;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
   public static void main(String args[]) throws FileNotFoundException, IOException{  

      String inputFile = args[0];
      Parser parser = new Parser(inputFile);

      while (parser.hasMoreCommands()) {
         parser.advance();
         System.out.println(parser.currentCommand());
         if (parser.commandType() == CommandType.C_COMMAND) {
            System.out.println("Dest: " + parser.dest());
            System.out.println("Comp: " + parser.comp());
            System.out.println("Jump: " + parser.jump());
         } else {
            System.out.println("Symbol: " + parser.symbol());
         }
      }
   }
}