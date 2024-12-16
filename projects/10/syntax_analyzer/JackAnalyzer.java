package syntax_analyzer;

import java.io.IOException;

public class JackAnalyzer {
    public static void main(String args[]) throws IOException {
        String filename = args[0];
        JackTokenizer tokenizer = new JackTokenizer(filename);

        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            System.out.println(tokenizer.currentToken());
        }
        return;
    }
}
