package assembler;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
   public static void main(String args[]) throws FileNotFoundException, IOException{  

      String inputFile = args[0];
      Parser parser = new Parser(inputFile);

      while (true) {
         parser.advance();
         if (parser.finished()) break;
         System.out.println(parser.currentCommand + ',' + parser.commandType());
      }
   }
}