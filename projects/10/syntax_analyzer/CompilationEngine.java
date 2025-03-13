package syntax_analyzer;

import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    FileWriter writer;
    JackTokenizer tokenizer;

    public CompilationEngine(JackTokenizer tokenizer, String outputFile) throws IOException {
        writer = new FileWriter(outputFile);
        this.tokenizer = tokenizer;
    }

    public void closeWriter() throws IOException {
        writer.close();
    }

    public void compileClass() throws IOException {
        writer.write("<class>\n");

        // first thing should be the keyword class
        this.tokenizer.advance();
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );

        // then should be an identifier
        this.tokenizer.advance();
        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );

        // then open curly brace
        this.tokenizer.advance();
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );

        this.tokenizer.advance();
        while (
            this.tokenizer.tokenType() == TokenType.KEYWORD &&
            (this.tokenizer.keyWord().equals("field") || 
            this.tokenizer.keyWord().equals("static"))
        ) {
            // process as many classverDec as needed
            compileClassVarDec();
        }

        this.tokenizer.advance();
        while (
            this.tokenizer.tokenType() == TokenType.KEYWORD &&
            (this.tokenizer.keyWord().equals("constructor") || 
            this.tokenizer.keyWord().equals("method") ||
            this.tokenizer.keyWord().equals("function"))
        ) {
            // process as many subroutines as needed
            compileSubroutine();
        }

        // finally a close curly brace
        this.tokenizer.advance();
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );

        writer.write("</class>\n");
    }

    private void compileClassVarDec() throws IOException {
        writer.write("<classVarDec>\n");

        // write the keyword static or field
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );

        // write the type (another keyword)
        this.tokenizer.advance();
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );

        // write the var name identifier
        this.tokenizer.advance();
        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );

        this.tokenizer.advance();
        // there are optionally one or more ", Varname" following the first var name
        while (this.tokenizer.symbol().equals(",")) {
            writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();
            writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );
            this.tokenizer.advance();
        }

        // should end with a semicolon
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );

        writer.write("</classVarDec>\n");
    }

    private void compileSubroutine() throws IOException {
        writer.write("<subroutineDec>\n");

        // write the keyword, constructor, method or function
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );

        // write the type keyword
        this.tokenizer.advance();
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );

        // write the identifier for subroutine names
        this.tokenizer.advance();
        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );

        // open paren
        this.tokenizer.advance();
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );

        this.tokenizer.advance();
        compileParameterList();

        // close paren
        this.tokenizer.advance();
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );

        compileSubroutineBody();

        writer.write("</subroutineDec>\n");
    }

    private void compileParameterList() throws IOException {
        writer.write("<parameterList>\n");

        // write type
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        
        // write var name
        this.tokenizer.advance();
        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );

        this.tokenizer.advance();
        // handle additional parameters (optional)
        while (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(",")) {
            // write comma
            writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );

            // write type
            writer.write(
                String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
            );

            // write var name
            this.tokenizer.advance();
            writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );

            this.tokenizer.advance();
        }

        writer.write("</parameterList>\n");
    }

    private void compileSubroutineBody() throws IOException {
        writer.write("<subroutineBody>\n");

        // open curly brace
        // zero or more var dec
        // statements
        // close curly brace


        writer.write("</subroutineBody>\n");
    }

    private void compileVarDec() throws IOException {
        writer.write("<varDec>\n");

        // keyword var
        // type
        // var name

        // zero or more of:
        // comma
        // var name

        // semicolon

        writer.write("</varDec>\n");
    }

    private void compileStatements() throws IOException {
        writer.write("<statements\n");

        // zero or more of:
        // let statement | if statement | while statement | do statement | return statement

        while (true) {
            if (this.tokenizer.tokenType() != TokenType.KEYWORD) {
                break;
            } else if (this.tokenizer.keyWord().equals("let")) {
                this.tokenizer.advance();
                compileLet();
            } else if (this.tokenizer.keyWord().equals("if")) {
                this.tokenizer.advance();
                compileIf();
            } else if (this.tokenizer.keyWord().equals("while")) {
                this.tokenizer.advance();
                compileWhile();
            } else if (this.tokenizer.keyWord().equals("do")) {
                this.tokenizer.advance();
                compileDo();
            } else if (this.tokenizer.keyWord().equals("return")) {
                this.tokenizer.advance();
                compileReturn();
            } else {
                break;
            }
        }
        
        writer.write("</statements>\n");
    }

    private void compileDo() throws IOException {
        writer.write("<doStatement>\n");

        // keyword do

        // subroutine call  
        //  identifier subroutine name
        //  open paren
        //  expression list
        //  close paren
        // ... 

        // semicolon

        writer.write("</doStatement>\n");
    }

    private void compileLet() {
        // 'let' varName ('[' expression ']')? '=' expression ';'
    }

    private void compileWhile() {
        // 'while' '(' expression ')' '{' statements '}'
    }

    private void compileReturn() throws IOException{
        writer.write("<returnStatement>\n");

        // keyword 'return' 
        // zero or one of expression 
        // semicolon 

        writer.write("</returnStatement>\n");
    }

    private void compileIf() {
        
    }

    private void compileExpression() throws IOException {
        // for initial version, all expressions 
        // are replaced by a single identifier

        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        
    }

    private void compileTerm() {
        
    }

    private void compileExpressionList() {
        
    }
}
