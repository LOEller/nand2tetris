package vm_translator;

public class CodeWriter {
    public CodeWriter(String outputFile) {
        // opens the outpit file and gets ready to write to it
    }

    public void setFileName(String fileName) {
        // informs the code writer that the translation of a 
        // new VM file is started
    }

    public void writeArithmetic(String command) {
        // writes the assembly code that is the translation of
        // the given arithmetic command
    }

    public void writePushPop(CommandType command, String segment, int index) {
        // writes the assembly code that is the translation of the 
        // given command, where command is either C_PUSH or C_POP
    }

    public void close() {
        // closes the output file
    }
}
