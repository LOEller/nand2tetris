package vm_translator;

import java.io.FileWriter;
import java.io.IOException;


public class CodeWriter {
    FileWriter writer;
    int lCommandCounter = 1; // used for generating unique labels
    String vmFileName;

    public CodeWriter(String outputFile) throws IOException {
        // opens the outpit file and gets ready to write to it
        writer = new FileWriter(outputFile);
        setFileName(outputFile.split("\\.")[0]);
    }

    public void close() throws IOException {
        // closes the output file
        writer.close();
    }

    public void setFileName(String fileName) {
        // informs the code writer that the translation of a 
        // new VM file is started
        vmFileName = fileName;
        writeInit();
    }

    public void writeInit() {
        // writes assembly code that affects the VM initialization
        // code that must be placed at the beginning of an output file
    }

    public void writeLabel(String label) throws IOException {
        writer.write(String.format("(%s)\n", label));
    }

    public void writeGoto(String label) throws IOException {
        writer.write(String.format("@%s\n", label));
        writer.write("0;JMP\n");
    }

    public void writeIf(String label) throws IOException {
        // the stack's topmost value is popped
        // if the value is not 0, execution continues
        // from the location marked by label
        // otherwise execution continues from the next
        // command in the program
        decrementStackPointer();
        loadTopOfStackIntoD();
        writer.write(String.format("@%s\n", label));
        writer.write("D;JNE\n");
    }

    public void writeCall(String functionName, int numArgs) throws IOException {
        // todo
    }

    public void writeReturn() throws IOException {
        // todo
    }

    public void writeFunction(String functionName, int numLocals) throws IOException {
        // todo
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
            writer.write(String.format("@%s\n", "IF_" + lCommandCounter));
            writer.write("D;JEQ\n");
            // it is not equal so write false
            loadStackPointer();
            writer.write("M=0\n");
            writeGoto("ELSE_" + lCommandCounter);
            writeLabel("IF_" + lCommandCounter);
            // it is equal so write true
            loadStackPointer();
            writer.write("M=-1\n");
            writeLabel("ELSE_" + lCommandCounter);
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
            writer.write(String.format("@%s\n", "IF_" + lCommandCounter));
            writer.write("D;JLT\n");
            // it is not less than so write false
            loadStackPointer();
            writer.write("M=0\n");
            writeGoto("ELSE_" + lCommandCounter);
            writeLabel("IF_" + lCommandCounter);
            // it is less than so write true
            loadStackPointer();
            writer.write("M=-1\n");
            writeLabel("ELSE_" + lCommandCounter);
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
            writer.write(String.format("@%s\n", "IF_" + lCommandCounter));
            writer.write("D;JGT\n");
            // it is not greater than so write false
            loadStackPointer();
            writer.write("M=0\n");
            writeGoto("ELSE_" + lCommandCounter);
            writeLabel("IF_" + lCommandCounter);
            // it is greater than so write true
            loadStackPointer();
            writer.write("M=-1\n");
            writeLabel("ELSE_" + lCommandCounter);
            incrementStackPointer(); 
            lCommandCounter++;
        }
    }

    public void writePushPop(CommandType command, String segment, int index) throws IOException {
        // writes the assembly code that is the translation of the 
        // given command, where command is either C_PUSH or C_POP

        // load into D either the constant index 
        // or the address of the new value 
        if (segment.equals("constant")) {
            // put index value into D
            writer.write(String.format("@%d\n", index));
            writer.write("D=A\n");
        } else if (segment.equals("local")) {
            // put value at LCL + index into D
            writer.write(String.format("@%d\n", index));
            writer.write("D=A\n");
            writer.write("@LCL\n");
            writer.write("D=D+M\n");
        } else if (segment.equals("argument")) {
            // put value at ARG + index into D
            writer.write(String.format("@%d\n", index));
            writer.write("D=A\n");
            writer.write("@ARG\n");
            writer.write("D=D+M\n");
        } else if (segment.equals("this")) {
            // put value at THIS + index into D
            writer.write(String.format("@%d\n", index));
            writer.write("D=A\n");
            writer.write("@THIS\n");
            writer.write("D=D+M\n");
        } else if (segment.equals("that")) {
            // put value at THAT + index into D
            writer.write(String.format("@%d\n", index));
            writer.write("D=A\n");
            writer.write("@THAT\n");
            writer.write("D=D+M\n");
        } else if (segment.equals("temp")) {
            // put value at 5 + index into D
            writer.write(String.format("@%d\n", index));
            writer.write("D=A\n");
            writer.write("@5\n");
            writer.write("D=D+A\n");
        } else if (segment.equals("pointer")) {
            // put value at 3 + index into D
            writer.write(String.format("@%d\n", index));
            writer.write("D=A\n");
            writer.write("@3\n");
            writer.write("D=D+A\n");
        } else if (segment.equals("static")) {
            writer.write(String.format("@%s.%d\n", vmFileName, index));
            writer.write("D=A\n");
        } 

        if (command == CommandType.C_PUSH) {
            // pushes value specified by D to top of stack
            if (!segment.equals("constant")) {
                // if the segment was NOT constant then D is an address
                // not the literal value
                writer.write("A=D\n");
                writer.write("D=M\n"); 
            }
            loadStackPointer();
            writer.write("M=D\n");
            incrementStackPointer();
        } else if (command == CommandType.C_POP) {
            // pops off top of stack and puts it into location
            // specified by D
            writer.write("@13\n");
            writer.write("M=D\n"); // store D address in R13
            decrementStackPointer();
            loadStackPointer();
            writer.write("D=M\n"); // store top of stack val in D
            writer.write("@13\n");
            writer.write("A=M\n");
            writer.write("M=D\n"); // write val in D to address pointed by R13
        }
    }

    // below are convenience methods used internally by CoderWriter

    private void incrementStackPointer() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M+1\n"); 
    }

    private void decrementStackPointer() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M-1\n"); 
    }

    private void loadStackPointer() throws IOException {
        writer.write("@SP\n");
        writer.write("A=M\n");
    }

    private void loadTopOfStackIntoD() throws IOException {
        loadStackPointer();
        writer.write("D=M\n");
    }
}
