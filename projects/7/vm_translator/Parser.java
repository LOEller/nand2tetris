package vm_translator;

// push constant 7 should become:
//    write the value 7 to the stack (ie location pointed to by SP)
//    increment SP (the value stored in R0)

// push constant 8 should become:
//    write the value 8 to the stack (ie location pointed to by SP)
//    increment SP (the value stored in R0)

// add should become:
    // subtract 1 from the SP
    // put the value at top of stack into D
    // subtract 1 from the SP
    // put D + top of stack value into top of stack
    // increment SP

/* 
    push constant 7
    @7
    D=A 
    @SP
    A=M
    M=D // write 7 to the top of stack
    @SP
    M=M+1 // increment the stack pointer

    push constant 8
    @8
    D=A
    @SP
    A=M
    M=D // write 8 to the top of the stack
    @SP
    M=M+1 // increment the stack pointer

    add
    @SP
    M=M-1 // decrement stack pointer
    A=M
    D=M // put top of stack value into D
    @SP
    M=M-1 // decrement stack pointer
    A=M
    M=D+M // put D + top of stack value into top of stack
    @SP
    M=M+1 // increment stack pointer
*/


public class Parser {

    public Parser(String inputFile) {
        // opens the input file and gets ready to parse it
    }

    public boolean hasMoreCommands() {
        // are there more commands in the input?
        return true;
    }

    public void advance() {
        // makes the next command the current command
        // should only be called is hasMoreCommands is true
    }

    public CommandType commandType() {
        // returns the type of the current VM command
        return CommandType.C_ARITHMETIC;
    }

    public String arg1() {
        // returns the first argument of the current command
        // should not be called if the current command is C_RETURN
        return "";
    }

    public int arg2() {
        // returns the second argument of the current command
        // should be called only if the current command is C_PUSH,
        // C_POP, C_FUNCTION, or C_CALL
        return 0;
    }
}
