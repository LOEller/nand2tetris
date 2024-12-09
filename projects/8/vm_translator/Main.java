package vm_translator;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException, FileNotFoundException {
        String inputFile = args[0];
        Parser parser = new Parser(inputFile);

        String outputFile = inputFile.split("\\.")[0] + ".asm";
        CodeWriter codeWriter = new CodeWriter(outputFile);

        while (parser.hasMoreCommands()) {
            parser.advance();

            if (parser.commandType() == CommandType.C_ARITHMETIC) {
                codeWriter.writeArithmetic(parser.command());
            } else if (parser.commandType() == CommandType.C_PUSH || parser.commandType() == CommandType.C_POP) {
                codeWriter.writePushPop(
                    parser.commandType(),
                    parser.arg1(), 
                    parser.arg2()
                );
            }
        }
        codeWriter.close();
    }
}
