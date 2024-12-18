package syntax_analyzer;

import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    FileWriter writer;
    JackTokenizer tokenizer;

    public CompilationEngine(JackTokenizer tokenzier, String outputFile) throws IOException {
        writer = new FileWriter(outputFile);
        tokenzier = tokenizer;
    }

    public void closeWriter() throws IOException {
        writer.close();
    }

    public void compileClass() throws IOException {
        writer.write("<class>\n");

        // first thing should be the keyword class
        tokenizer.advance();
        writer.write(
            String.format("    <keyword> %s </keyword>\n", tokenizer.keyWord())
        );

        // then should be an identifier
        tokenizer.advance();
        writer.write(
            String.format("    <identifier> %s </identifier>\n", tokenizer.identifier())
        );

        // then open curly brace
        tokenizer.advance();
        writer.write(
            String.format("    <symbol> %s </symbol>\n", tokenizer.symbol())
        );

        tokenizer.advance();
        while (
            tokenizer.tokenType() == TokenType.KEYWORD &&
            (tokenizer.keyWord() == "field" || 
            tokenizer.keyWord() == "static")
        ) {
            // process as many classverDec as needed
            compileClassVarDec();
        }

        tokenizer.advance();
        while (
            tokenizer.tokenType() == TokenType.KEYWORD &&
            (tokenizer.keyWord() == "constructor" || 
            tokenizer.keyWord() == "method" ||
            tokenizer.keyWord() == "function")
        ) {
            // process as many subroutines as needed
            compileSubroutine();
        }

        // finally a close curly brace
        tokenizer.advance();
        writer.write(
            String.format("    <symbol> %s </symbol>\n", tokenizer.symbol())
        );

        writer.write("</class>\n");
    }

    public void compileClassVarDec() throws IOException {
        writer.write("<classVarDec>\n");

        // write the keyword static or field
        writer.write(
            String.format("    <keyword> %s </keyword>\n", tokenizer.keyWord())
        );

        // write the type (another keyword)
        tokenizer.advance();
        writer.write(
            String.format("    <keyword> %s </keyword>\n", tokenizer.keyWord())
        );

        // write the var name identifier
        tokenizer.advance();
        writer.write(
            String.format("    <identifier> %s </identifier>\n", tokenizer.identifier())
        );

        tokenizer.advance();
        // there are optionally one or more ", Varname" following the first var name
        while (tokenizer.symbol() == ",") {
            writer.write(
                String.format("    <symbol> %s </symbol>\n", tokenizer.symbol())
            );
            tokenizer.advance();
            writer.write(
                String.format("    <identifier> %s </identifier>\n", tokenizer.identifier())
            );
            tokenizer.advance();
        }

        // should end with a semicolon
        writer.write(
            String.format("    <symbol> %s </symbol>\n", tokenizer.symbol())
        );

        writer.write("</classVarDec>\n");
    }

    public void compileSubroutine() {

    }

    public void compileParameterList() {

    }

    public void compileVarDec() {

    }

    public void compileStatements() {

    }

    public void compileDo() {

    }

    public void compileLet() {
        
    }

    public void compileWhile() {
        
    }

    public void compileReturn() {
        
    }

    public void compileIf() {
        
    }

    public void compileExpression() {
        
    }

    public void compileTerm() {
        
    }

    public void compileExpressionList() {
        
    }
}
