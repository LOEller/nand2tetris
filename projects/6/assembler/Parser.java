package assembler;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Parser {

    BufferedReader reader;
    String currentCommand;

    public Parser(String fileName) throws FileNotFoundException, IOException {
        // opens the file stream and gets ready to parse it
        this.reader = new BufferedReader(new FileReader(fileName));
        this.currentCommand = "";
    }

    public Boolean finished() {
        // have we finished parsing the input file?
        return this.currentCommand == null;
    }

    public void advance() throws IOException {
        // reads the next command from the input and makes it
        // the current command
        this.currentCommand = this.reader.readLine();
    }

    public CommandType commandType() {
        // gets the type of the current command
        char first = this.currentCommand.charAt(0);
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
        // should only be called for A or L commands ie
        // if @xxx or (xxx) return xxx
        if (commandType() == CommandType.A_COMMAND) {
            return this.currentCommand.substring(1);
        } else {
            return this.currentCommand.substring(1, this.currentCommand.length() - 1);
        }
    }

    public String dest() {
        // returns the dest mnemonic in the current C command
        // should only be called if the current command is a C command
        return "";
    }

    public String comp() {
        // returns the comp mnemonic in the current C command
        // should only be called if the current command is a C command
        return "";
    }

    public String jump() {
        // returns the jump mnemonic in the current C command
        // should only be called if the current command is a C command
        return "";
    }
}
