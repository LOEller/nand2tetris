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
        writeInit();
    }

    public void close() throws IOException {
        // closes the output file
        writer.close();
    }

    public void setFileName(String fileName) {
        // informs the code writer that the translation of a 
        // new VM file is started
        vmFileName = fileName;
    }

    public void writeInit() throws IOException {
        // writes assembly code that affects the VM initialization
        // code that must be placed at the beginning of an output file

        // set stack pointer to 256
        writer.write("@256\n");
        writer.write("D=A\n");
        writer.write("@SP\n");
        writer.write("M=D\n");

        // call Sys.init
        writeCall("Sys.init", 0);
        // this has no arguments right??
    }

    public void writeLabel(String label) throws IOException {
        writer.write(String.format("(%s)\n", label));
    }

    public void writeGoto(String label) throws IOException {
        writer.write(String.format("@%s\n", label));
        writer.write("0;JMP\n");
    }

    public void writeIf(String label) throws IOException {
        // pop top of stack. if it is NOT 0 got to label
        // else continue with next instruction
        decrementStackPointer();
        loadTopOfStackIntoD();
        writer.write(String.format("@%s\n", label));
        writer.write("D;JNE\n");
    }

    public void writeCall(String functionName, int numArgs) throws IOException {
        // call needs to save the state of the current function on the stack
        // and push the arguments necessary for the function being called

        // create a unique label for this function's return address
        String returnAddressLabel = String.format("%s-return-address", functionName);

        // push return-address
        writer.write(String.format("@%s\n", returnAddressLabel));
        writer.write("D=A\n");
        loadStackPointer();
        writer.write("M=D\n");
        incrementStackPointer();

        // push LCL
        writePushPop(CommandType.C_PUSH, "local", 0);
        // push ARG
        writePushPop(CommandType.C_PUSH, "argument", 0);
        // push THIS
        writePushPop(CommandType.C_PUSH, "this", 0);
        // push THAT
        writePushPop(CommandType.C_PUSH, "that", 0);

        // ARG = SP-n-5 
        writer.write("@SP\n");
        writer.write("D=M\n");
        writer.write(String.format("@%d\n", numArgs));
        writer.write("D=D-A\n");
        writer.write("@5\n");
        writer.write("D=D-A\n");
        writer.write("@ARG\n");
        writer.write("M=D\n");

        // LCL = SP
        writer.write("@SP\n");
        writer.write("D=M\n");
        writer.write("@LCL\n");
        writer.write("M=D\n"); 

        // goto f
        writeGoto(functionName);

        // write label for the return address
        writeLabel(returnAddressLabel);
    }

    public void writeReturn() throws IOException {
        // return needs to restore the saved state of the calling function
        // and jump back to the return address

        // R13 - R15 are general purpose registers for the VM implementation
        // using R14 for FRAME and R15 for RET

        // FRAME = LCL
        writer.write("@LCL\n");
        writer.write("D=M\n");
        writer.write("@14\n");
        writer.write("M=D\n"); // put LCL val into R14

        // RET = *(FRAME-5)
        writer.write("@14\n");
        writer.write("D=M\n");
        writer.write("@5\n");
        writer.write("D=D-A\n");
        writer.write("A=D\n");
        writer.write("D=M\n");
        writer.write("@15\n");
        writer.write("M=D\n"); // put val at FRAME-5 into R15

        // *ARG = pop()
        writePushPop(CommandType.C_POP, "argument", 0);

        // SP = ARG + 1
        writer.write("@ARG\n");
        writer.write("D=M\n");
        writer.write("D=D+1\n");
        writer.write("@SP\n");
        writer.write("M=D\n");

        // THAT = *(FRAME-1)
        writer.write("@14\n");
        writer.write("D=M\n");
        writer.write("D=D-1\n");
        writer.write("A=D\n");
        writer.write("D=M\n");
        writer.write("@THAT\n");
        writer.write("M=D\n"); 

        // THIS = *(FRAME-2)
        writer.write("@14\n");
        writer.write("D=M\n");
        writer.write("@2\n");
        writer.write("D=D-A\n");
        writer.write("A=D\n");
        writer.write("D=M\n");
        writer.write("@THIS\n");
        writer.write("M=D\n"); 

        // ARG = *(FRAME-3)
        writer.write("@14\n");
        writer.write("D=M\n");
        writer.write("@3\n");
        writer.write("D=D-A\n");
        writer.write("A=D\n");
        writer.write("D=M\n");
        writer.write("@ARG\n");
        writer.write("M=D\n"); 

        // LCL = *(FRAME-4)
        writer.write("@14\n");
        writer.write("D=M\n");
        writer.write("@4\n");
        writer.write("D=D-A\n");
        writer.write("A=D\n");
        writer.write("D=M\n");
        writer.write("@LCL\n");
        writer.write("M=D\n"); 

        // goto RET
        writer.write("@15\n");
        writer.write("A=M\n");
        writer.write("0;JMP\n");
    }

    public void writeFunction(String functionName, int numLocals) throws IOException {
        // declare a label for the function entry
        writeLabel(functionName);
        // push 0 onto the stack numLocals times
        for (int i = 0; i < numLocals; i++) {
            writePushPop(CommandType.C_PUSH, "constant", 0);
        }
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
