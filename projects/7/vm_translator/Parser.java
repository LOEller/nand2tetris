package vm_translator;

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
