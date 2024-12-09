package vm_translator;

import java.io.FileWriter;
import java.io.IOException;


public class CodeWriter {
    FileWriter writer;
    int lCommandCounter = 1; // used for generating unique labels

    public CodeWriter(String outputFile) throws IOException {
        // opens the outpit file and gets ready to write to it
        writer = new FileWriter(outputFile);
    }

    public void setFileName(String fileName) {
        // informs the code writer that the translation of a 
        // new VM file is started
    }

    private void incrementStackPointer() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M+1\n"); 
    }

    private void decrementStackPointer() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M-1\n"); 
    }

    public void loadStackPointer() throws IOException {
        writer.write("@SP\n");
        writer.write("A=M\n");
    }

    public void loadTopOfStackIntoD() throws IOException {
        loadStackPointer();
        writer.write("D=M\n");
    }

    public void writeArithmetic(String command) throws IOException {
        // writes the assembly code that is the translation of
        // the given arithmetic command

        // Notes: True is -1, False, is 0. Writing "@-1" causes really weird errors

        // "lt" does not mean "top of stack" < "second from top of stack"
        // it means "second from top of stack" < "top of stack"

        if (command.equals("add")) {
            // adds the values on top of stack and puts result on top of stack
            decrementStackPointer();
            loadTopOfStackIntoD();
            decrementStackPointer();
            loadStackPointer();
            writer.write("M=D+M\n"); 
            incrementStackPointer(); 
        } else if (command.equals("sub")) {
            // subtracts the values on top of stack and puts result on top of stack
            decrementStackPointer();
            loadTopOfStackIntoD();
            decrementStackPointer();
            loadStackPointer();
            writer.write("M=M-D\n"); 
            incrementStackPointer(); 
        } else if (command.equals("and")) {
            // computes bitwise and of values on top of stack and puts 
            // result on top of stack
            decrementStackPointer();
            loadTopOfStackIntoD();
            decrementStackPointer();
            loadStackPointer();
            writer.write("M=D&M\n"); 
            incrementStackPointer(); 
        } else if (command.equals("or")) {
            // computes bitwise or of values on top of stack and puts 
            // result on top of stack 
            decrementStackPointer();
            loadTopOfStackIntoD();
            decrementStackPointer();
            loadStackPointer();
            writer.write("M=D|M\n"); 
            incrementStackPointer(); 
        } else if (command.equals("not")) {
            // computes bitwise not of value on top of stack and puts result on stack
            decrementStackPointer();
            loadStackPointer();
            writer.write("M=!M\n");
            incrementStackPointer(); 
        } else if (command.equals("neg")) {
            // computes arithmetic negation of value on top of stack and puts result on stack
            decrementStackPointer();
            loadStackPointer();
            writer.write("M=-M\n");
            incrementStackPointer(); 
        } else if (command.equals("eq")) {
            // compares values on top of stack and puts true on top of stack
            // if they are equal else false
            decrementStackPointer();
            loadTopOfStackIntoD();
            decrementStackPointer();
            loadStackPointer();
            writer.write("D=M-D\n"); 
            writer.write("@IF_" + lCommandCounter + "\n");
            writer.write("D;JEQ\n");
            // it is not equal so write false
            loadStackPointer();
            writer.write("M=0\n");
            writer.write("@ELSE_" + lCommandCounter + "\n");
            writer.write("0;JMP\n");
            writer.write("(IF_" + lCommandCounter + ")\n");
            // it is equal so write true
            loadStackPointer();
            writer.write("M=-1\n");
            writer.write("(ELSE_" + lCommandCounter + ")\n");
            incrementStackPointer(); 
            lCommandCounter++;
        } else if (command.equals("lt")) {
            // puts true on top of stack if first val is less than second val
            // else false
            decrementStackPointer();
            loadTopOfStackIntoD();
            decrementStackPointer();
            loadStackPointer();
            writer.write("D=M-D\n"); 
            writer.write("@IF_" + lCommandCounter + "\n");
            writer.write("D;JLT\n");
            // it is not less than so write false
            loadStackPointer();
            writer.write("M=0\n");
            writer.write("@ELSE_" + lCommandCounter + "\n");
            writer.write("0;JMP\n");
            writer.write("(IF_" + lCommandCounter + ")\n");
            // it is less than so write true
            loadStackPointer();
            writer.write("M=-1\n");
            writer.write("(ELSE_" + lCommandCounter + ")\n");
            incrementStackPointer(); 
            lCommandCounter++;
        } else if (command.equals("gt")) {
            // puts true on top of stack if first val is greater than second val
            // else false
            decrementStackPointer();
            loadTopOfStackIntoD();
            decrementStackPointer();
            loadStackPointer();
            writer.write("D=M-D\n"); 
            writer.write("@IF_" + lCommandCounter + "\n");
            writer.write("D;JGT\n");
            // it is not greater than so write false
            loadStackPointer();
            writer.write("M=0\n");
            writer.write("@ELSE_" + lCommandCounter + "\n");
            writer.write("0;JMP\n");
            writer.write("(IF_" + lCommandCounter + ")\n");
            // it is greater than so write true
            loadStackPointer();
            writer.write("M=-1\n");
            writer.write("(ELSE_" + lCommandCounter + ")\n");
            incrementStackPointer(); 
            lCommandCounter++;
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
