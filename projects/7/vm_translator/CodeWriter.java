package vm_translator;

import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    FileWriter writer;

    public CodeWriter(String outputFile) throws IOException {
        // opens the outpit file and gets ready to write to it
        writer = new FileWriter(outputFile);
    }

    public void setFileName(String fileName) {
        // informs the code writer that the translation of a 
        // new VM file is started
    }

    public void writeArithmetic(String command) throws IOException {
        // writes the assembly code that is the translation of
        // the given arithmetic command

        // true is -1
        // false is 0

        if (command == "add") {
            // adds the values on top of stack and puts result on top of stack
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("M=D+M\n"); 
            writer.write("@SP\n");
            writer.write("M=M+1\n"); 
        } else if (command == "sub") {
            // subtracts the values on top of stack and puts result on top of stack
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("M=D-M\n"); 
            writer.write("@SP\n");
            writer.write("M=M+1\n"); 
        } else if (command == "and") {
            // computes bitwise and of values on top of stack and put result on stack
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("M=D&M\n"); 
            writer.write("@SP\n");
            writer.write("M=M+1\n");
        } else if (command == "or") {
            // computes bitwise or of values on top of stack and put result on stack
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("M=D|M\n"); 
            writer.write("@SP\n");
            writer.write("M=M+1\n");
        } else if (command == "not") {
            // computes bitwise not of value on top of stack and puts result on stack
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("M=!M\n");
            writer.write("@SP\n");
            writer.write("M=M+1\n");
        } else if (command == "neg") {
            // computes arithmetic negation of value on top of stack and puts result on stack
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("M=-M\n");
            writer.write("@SP\n");
            writer.write("M=M+1\n");
        } else if (command == "eq") {
            // compares values on top of stack and puts true on top of stack
            // if they are equal
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("D=M\n"); // put first val into D
            writer.write("@SP\n");
            writer.write("M=M-1\n"); 
            writer.write("A=M\n");
            writer.write("M=M-D\n"); // put diff into M
            writer.write("M=!M\n"); // negate
            // now the value in M is 0 if they are NOT equal
            writer.write("@SP\n");
            writer.write("M=M+1\n");
        }
    }

    public void writePushPop(CommandType command, String segment, int index) throws IOException {
        // writes the assembly code that is the translation of the 
        // given command, where command is either C_PUSH or C_POP

        if (command == CommandType.C_PUSH) {
            // pushes a constant index on to the stack
            writer.write(String.format("@%d\n", index));
            writer.write("D=A\n");
            writer.write("@SP\n");
            writer.write("A=M\n");
            writer.write("M=D\n");
            writer.write("@SP\n");
            writer.write("M=M+1\n");
        }
    }

    public void close() throws IOException {
        // closes the output file
        writer.close();
    }
}
