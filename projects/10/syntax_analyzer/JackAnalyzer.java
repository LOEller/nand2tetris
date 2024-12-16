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
            if (tokenizer.tokenType() == TokenType.symbol) {
                writer.write(
                    String.format(
                        "    <%s> %s </%s>\n", 
                        tokenizer.tokenType(), 
                        tokenizer.symbol(),
                        tokenizer.tokenType()
                    )
                );
            } else if (tokenizer.tokenType() == TokenType.identifier) {
                writer.write(
                    String.format(
                        "    <%s> %s </%s>\n", 
                        tokenizer.tokenType(), 
                        tokenizer.identifier(),
                        tokenizer.tokenType()
                    )
                );
            } else if (tokenizer.tokenType() == TokenType.integerConstant) {
                writer.write(
                    String.format(
                        "    <%s> %s </%s>\n", 
                        tokenizer.tokenType(), 
                        tokenizer.intVal(),
                        tokenizer.tokenType()
                    )
                );
            } else if (tokenizer.tokenType() == TokenType.stringConstant) {
                writer.write(
                    String.format(
                        "    <%s> %s </%s>\n", 
                        tokenizer.tokenType(), 
                        tokenizer.stringVal(),
                        tokenizer.tokenType()
                    )
                );
            } else if (tokenizer.tokenType() == TokenType.keyword) {
                writer.write(
                    String.format(
                        "    <%s> %s </%s>\n", 
                        tokenizer.tokenType(), 
                        tokenizer.keyWord(),
                        tokenizer.tokenType()
                    )
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
