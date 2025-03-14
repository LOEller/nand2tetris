package syntax_analyzer;

import java.io.File;
import java.io.IOException;
public class JackAnalyzer {

    private static void compileFile(String filename) throws IOException {

        System.out.println("Compiling file: " + filename);

        JackTokenizer tokenizer = new JackTokenizer(filename);

        String outputFile = String.format("%s_comp.xml", filename.split("\\.")[0]);

        CompilationEngine engine = new CompilationEngine(tokenizer, outputFile);

        // every file should consist of a single class
        engine.compileClass();
        engine.closeWriter();
    }

    public static void main(String args[]) throws IOException {
        String input = args[0];
        File[] fileList = new File(input).listFiles();

        // assume input is always a directory name
        // iterate over each file in directory and compile
        for (File file : fileList) {
            String fileName = file.getName();
            if (fileName.endsWith(".jack")) {
                compileFile(input + "/" + fileName);
            }
        }
    }
}
