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
    Set<String> keywords = new HashSet<>(
        Arrays.asList(
            "class", "method", "function", "constructor", "int", "boolean", "char", "void", "var", "static", "field", "let", "do", "if", "else", "while", "return", "true", "false", "null", "this"
        )
    );

    public JackTokenizer(String filename) throws IOException { 
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        readFile(bufferedReader);
        bufferedReader.close();
    }

    private void readFile(BufferedReader reader) throws IOException {
        // reads the source file line by line and handle comments, blank lines, etc
        String line = null;
        boolean multiLineComment = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("/*")) {
                if (line.endsWith("*/")) {
                    // it's a multi line comment that is actually
                    // only a single line
                    continue;
                }
                // starting a multi line comment
                multiLineComment = true;
                continue;
            }
            if (line.endsWith("*/")) {
                // ending a multi line comment
                multiLineComment = false;
                continue;
            }
            if (!multiLineComment && !line.isEmpty() && !line.startsWith("//")) {
                splitLine(line.trim());
            }
        }
    }

    private void splitLine(String line) {
        // splits a line of text into individual tokens
        if (line.length() == 1) {
            tokens.add(line);
            return;
        } 

        // remove any comments at the end of the line
        for (int i = 0; i < line.length()-1; i++) { 
            if (line.charAt(i) == '/' && line.charAt(i+1) == '/') {
                // the rest of this line is a comment and should be ignored
                line = line.substring(0, i);
                break;
            }
        }

        // iterate through each char in the line building the tokens
        String token = "";
        boolean inString = false;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                // current char is a double quote
                if (inString) {
                    // this is the end of the string
                    inString = false;
                    token += line.charAt(i);
                    //tokens.add(token);
                } else {
                    // this is the start of a string
                    token += line.charAt(i);
                    inString = true;
                }
            } else if (inString) {
                // we are inside a string so ignore symbols and whitespace
                token += line.charAt(i);
            } else if (line.charAt(i) == ' ') {
                // current char is white space
                if (!token.isEmpty()) {
                    tokens.add(token);
                }
                token = "";
            } else if (symbols.contains(line.charAt(i))) {
                // current char is a symbol
                if (!token.isEmpty()) {
                    tokens.add(token);
                }
                tokens.add(Character.toString(line.charAt(i)));
                token = "";
            } else {
                token += line.charAt(i);
            }
        }
        if (!token.isEmpty()) {
            tokens.add(token);
        }
    }

    public boolean hasMoreTokens() {
        return tokenIndex < tokens.size() - 1;
    }

    public void advance() {
        tokenIndex++;
    }

    private String currentToken() {
        return tokens.get(tokenIndex);
    }

    public TokenType tokenType() {
        if (currentToken().length() == 1 && symbols.contains(currentToken().charAt(0))) {
            return TokenType.SYMBOL;
        } 
        if (keywords.contains(currentToken())) {
            return TokenType.KEYWORD;
        } 
        if (currentToken().startsWith("\"")) {
            return TokenType.STRING_CONST;
        }
        if (currentToken().matches("-?\\d+")) {
            // integer regex
            return TokenType.INT_CONST;
        }
        return TokenType.IDENTIFIER;
    }

    public String keyWord() {
        return currentToken();
    }

    public String symbol() {
        if (currentToken().equals("<")) {
            return "&lt;";
        }
        if (currentToken().equals(">")) {
            return "&gt;";
        }
        if (currentToken().equals("&")) {
            return "&amp;";
        }
        return currentToken();
    }

    public String identifier() {
        return currentToken();
    }

    public int intVal() {
        return Integer.parseInt(currentToken());
    }

    public String stringVal() {
        return currentToken().substring(1, currentToken().length() - 1);
    }
}
