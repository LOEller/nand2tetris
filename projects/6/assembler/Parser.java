package assembler;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
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
        // reads all lines from a buffered reader to an array and strips whitespace
        List<String> lines = new ArrayList<String>();
        String line = null;

        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("\\s+","");
            lines.add(line);
        }

        // convert array list of lines to an array
        return lines.toArray(new String[lines.size()]);
    }

    public Boolean hasMoreCommands() {
        return commandIndex < commands.length - 1;
    }

    public void advance() throws IOException {
        // reads the next command from the input and makes it the current
        // command. Should only be called if hasMoreCommands is true.
        // Initially there is no current command
        commandIndex++;
    }

    public String currentCommand() {
        return commands[commandIndex];
    }

    public CommandType commandType() {
        // gets the type of the current command
        char first = currentCommand().charAt(0);
        if (first == '@') {
            return CommandType.A_COMMAND;
        } else if (first == '(') {
            return CommandType.L_COMMAND;
        } else {
            return CommandType.C_COMMAND;
        }
    }

    public String symbol() {
        // returns the symbol or decimal of the current command
        // should only be called when current command is A or L 
        if (commandType() == CommandType.A_COMMAND) {
            // A command
            return currentCommand().substring(1);
        } else {
            // L command
            return currentCommand().substring(1, currentCommand().length() - 1);
        }
    }

    public String dest() {
        // returns the dest mnemonic in the current C command
        // should only be called if the current command is a C command
        if (currentCommand().contains("=")) {
            return currentCommand().split("=")[0];
        } else {
            return "";
        }
    }

    public String comp() {
        // returns the comp mnemonic in the current C command
        // should only be called if the current command is a C command
        if (currentCommand().contains("=") && currentCommand().contains(";")) {
            // dest=comp;jump
            return currentCommand().split("=")[1].split(";")[0];
        } else if (currentCommand().contains("=")) {
            // dest=comp
            return currentCommand().split("=")[1];
        } else {
            // comp;jump
            return currentCommand().split(";")[0];
        }
    }

    public String jump() {
        // returns the jump mnemonic in the current C command
        // should only be called if the current command is a C command
        if (currentCommand().contains(";")) {
            return currentCommand().split("[;]")[1];
        } else {
            return "";
        }
    }
}
