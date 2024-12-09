package vm_translator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Parser {
    String[] commands;
    int commandIndex = -1;

    public Parser(String fileName) throws FileNotFoundException, IOException {
        // opens the file stream and gets ready to parse it
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        commands = readFile(bufferedReader);
        bufferedReader.close();
    }

    private String[] readFile(BufferedReader reader) throws IOException{
        // reads all lines from a buffered reader to an array
        // ignores comments, strips white space, and ignores blank lines
        List<String> lines = new ArrayList<String>();
        String line = null;

        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty() && !line.startsWith("//")) {
                lines.add(line);
            }
        }

        // convert array list of lines to an array
        return lines.toArray(new String[lines.size()]);
    }

    public boolean hasMoreCommands() {
        return commandIndex < commands.length - 1;
    }

    public void advance() throws IOException {
        // reads the next command from the input and makes it the current
        // command. Should only be called if hasMoreCommands is true.
        // Initially there is no current command
        commandIndex++;
    }

    private String[] commandComponents() {
        return commands[commandIndex].split("\\s");
    }

    public CommandType commandType() {
        // returns the type of the current VM command
        switch(commandComponents()[0]) {
            case "push":
                return CommandType.C_PUSH;
            case "pop":
                return CommandType.C_POP;
            case "add":
                return CommandType.C_ARITHMETIC;
            case "sub":
                return CommandType.C_ARITHMETIC;
            case "neg":
                return CommandType.C_ARITHMETIC;
            case "eq":
                return CommandType.C_ARITHMETIC;
            case "gt":
                return CommandType.C_ARITHMETIC;
            case "lt":
                return CommandType.C_ARITHMETIC;
            case "and":
                return CommandType.C_ARITHMETIC;
            case "or":
                return CommandType.C_ARITHMETIC;
            case "not":
                return CommandType.C_ARITHMETIC;
            default:
                return null;
        }
    }

    public String command() {
        // returns the current command
        return commandComponents()[0];
    }

    public String arg1() {
        // returns the first argument of the current command
        // should not be called if the current command is C_RETURN
        return commandComponents()[1];
    }

    public int arg2() {
        // returns the second argument of the current command
        // should be called only if the current command is C_PUSH,
        // C_POP, C_FUNCTION, or C_CALL
        return Integer.parseInt(commandComponents()[2]);
    }
}
