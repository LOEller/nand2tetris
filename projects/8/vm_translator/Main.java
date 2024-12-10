package vm_translator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Main {
    public static void main(String args[]) throws IOException, FileNotFoundException {
        String input = args[0];
        
        String outputFile;
        boolean directoryInput = false;
        if (input.contains(".vm")) {
            // input is a single vm file
            outputFile = input.split("\\.")[0] + ".asm";
        } else {
            // input is a directory with multiple vm files as input
            String[] paths = input.split("/");
            outputFile = input + "/" + paths[paths.length-1] + ".asm";
            directoryInput = true;
        }

        CodeWriter codeWriter = new CodeWriter(outputFile);

        if (directoryInput) {
            // call handleFile on each .vm file in the directory
            File[] fileList = new File(input).listFiles();
            for (File file : fileList) {
                String fileName = file.getName();
                if (fileName.endsWith(".vm")) {
                    handleFile(input + "/" + fileName, codeWriter);
                }
            }
        } else {
            // simply call handleFile on the single file
            handleFile(input, codeWriter);
        }
        
        codeWriter.close();
    }

    private static void handleFile(String fileName, CodeWriter codeWriter) throws IOException, FileNotFoundException {
        Parser parser = new Parser(fileName);
        // get just the last name in the path of the filename
        String stripExtension = fileName.split("\\.")[0];
        String[] paths = stripExtension.split("/");
        codeWriter.setFileName(paths[paths.length-1]);

        while (parser.hasMoreCommands()) {
            parser.advance();

            if (parser.commandType() == CommandType.C_ARITHMETIC) {
                codeWriter.writeArithmetic(parser.command());
            } else if (parser.commandType() == CommandType.C_LABEL) {
                codeWriter.writeLabel(parser.arg1());
            } else if (parser.commandType() == CommandType.C_GOTO) {
                codeWriter.writeGoto(parser.arg1());
            } else if (parser.commandType() == CommandType.C_IF) {
                codeWriter.writeIf(parser.arg1());
            } else if (parser.commandType() == CommandType.C_FUNCTION) {
                codeWriter.writeFunction(parser.arg1(), parser.arg2());
            } else if (parser.commandType() == CommandType.C_RETURN) {
                codeWriter.writeReturn();
            } else if (parser.commandType() == CommandType.C_CALL) {
                codeWriter.writeCall(parser.arg1(), parser.arg2());
            } else if (
                parser.commandType() == CommandType.C_PUSH || 
                parser.commandType() == CommandType.C_POP
            ) {
                codeWriter.writePushPop(
                    parser.commandType(),
                    parser.arg1(), 
                    parser.arg2()
                );
            }
        }
    }
}
