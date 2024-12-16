package syntax_analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

public class JackTokenizer {

    List<String> tokens = new ArrayList<String>();
    int tokenIndex = -1;
    Set<Character> symbols = new HashSet<>(
        Arrays.asList(',', ';', '[', ']', '{', '}', '-', '+', '<', '>', '=', '(', ')', '|', '&', '/', '~', '*', '.'
    ));

    public JackTokenizer(String filename) throws IOException {  
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        readFile(bufferedReader);
        bufferedReader.close();
    }

    private void readFile(BufferedReader reader) throws IOException{
        String line = null;

        // TODO need to handle multi line comments

        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty() && !line.startsWith("//")) {
                String[] words = line.split("\\s");
                for (String s : words) {
                    if (!s.isEmpty()) {
                        splitToken(s);
                    }
                }
            }
        }
    }

    private void splitToken(String tokenString) {
        if (tokenString.length() == 1) {
            tokens.add(tokenString);
            return;
        } 

        String current = "";
        for (int i = 0; i < tokenString.length(); i++) {
            if (symbols.contains(tokenString.charAt(i))) {
                // the current char is a symbol
                if (!current.isEmpty()) {
                    tokens.add(current);
                }
                tokens.add(Character.toString(tokenString.charAt(i)));
                current = "";
            } else {
                current += tokenString.charAt(i);
            }
        }
        if (!current.isEmpty()) {
            tokens.add(current);
        }
    }

    public boolean hasMoreTokens() {
        return tokenIndex < tokens.size() - 1;
    }

    public void advance() {
        tokenIndex++;
    }

    public String currentToken() {
        return tokens.get(tokenIndex);
    }

    public TokenType tokenType() {
        return TokenType.KEYWORD;
    }

    public Keyword keyWord() {
        return Keyword.CLASS;
    }

    public char symbol() {
        return 'a';
    }

    public String identifier() {
        return "abc";
    }

    public int intVal() {
        return 0;
    }

    public String stringVal() {
        return "abc";
    }
}
