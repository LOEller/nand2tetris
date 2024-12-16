package syntax_analyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JackAnalyzer {

    private static void handleFile(String filename) throws IOException {
        JackTokenizer tokenizer = new JackTokenizer(filename);

        String outputFile = String.format("%sT_comp.xml", filename.split("\\.")[0]);

        FileWriter writer = new FileWriter(outputFile);

        writer.write("<tokens>\n");
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.tokenType() == TokenType.SYMBOL) {
                writer.write(
                    String.format("    <symbol> %s </symbol>\n", tokenizer.symbol())
                );
            } else if (tokenizer.tokenType() == TokenType.IDENTIFIER) {
                writer.write(
                    String.format("    <identifier> %s </identifier>\n", tokenizer.identifier())
                );
            } else if (tokenizer.tokenType() == TokenType.INT_CONST) {
                writer.write(
                    String.format("    <integerConstant> %s </integerConstant>\n", tokenizer.intVal())
                );
            } else if (tokenizer.tokenType() == TokenType.STRING_CONST) {
                writer.write(
                    String.format("    <stringConstant> %s </stringConstant>\n", tokenizer.stringVal())
                );
            } else if (tokenizer.tokenType() == TokenType.KEYWORD) {
                writer.write(
                    String.format("    <keyword> %s </keyword>\n", tokenizer.keyWord())
                );
            } 
        }
        writer.write("</tokens>\n");
        writer.close();
    }

    public static void main(String args[]) throws IOException {
        String input = args[0];
        File[] fileList = new File(input).listFiles();

        for (File file : fileList) {
            String fileName = file.getName();
            if (fileName.endsWith(".jack")) {
                handleFile(input + "/" + fileName);
            }
        }
    }
}
