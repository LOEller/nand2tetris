package syntax_analyzer;

import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    FileWriter writer;
    JackTokenizer tokenizer;

    public CompilationEngine(JackTokenizer tokenizer, String outputFile) throws IOException {
        writer = new FileWriter(outputFile);
        this.tokenizer = tokenizer;
        this.tokenizer.advance(); // advance to the first token
    }

    public void closeWriter() throws IOException {
        writer.close();
    }

    // IMPORTANT:
    // Each function expects that the tokenizer has been advanced
    // BEFORE it executes so the first token of the relevant lexical element
    // is the current token in the tokenizer. It also advances the tokenizer 
    // before it returns so the next function can
    // expect the same and so on...

    // In other words, each time we write something to the output file,
    // advance the tokenizer immediately after the write. Calling another 
    // compile method DOES NOT require an additional advance after invoking
    // the method.

    public void compileClass() throws IOException {
        writer.write("<class>\n");

        // keyword class
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // then should be an identifier
        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();

        // then open curly brace
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        while (
            this.tokenizer.tokenType() == TokenType.KEYWORD &&
            (this.tokenizer.keyWord().equals("field") || 
            this.tokenizer.keyWord().equals("static"))
        ) {
            // process as many classvarDec as needed
            compileClassVarDec();
        }

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
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );

        // that should have been the final token so do not advance here

        writer.write("</class>\n");
    }

    private void compileClassVarDec() throws IOException {
        writer.write("<classVarDec>\n");

        // write the keyword static or field
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // write the type (another keyword)
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // write the var name identifier
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
        this.tokenizer.advance();

        writer.write("</classVarDec>\n");
    }

    private void compileSubroutine() throws IOException {
        writer.write("<subroutineDec>\n");

        // write the keyword, constructor, method or function
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // write the type keyword
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // write the identifier for subroutine names
        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();

        // open paren
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        if (this.tokenizer.tokenType() != TokenType.SYMBOL) {
            // if there are parameters, compile them
            // (the next token will not be a symbol for close parenthesis)
            compileParameterList();
        } else {
            // if there are no parameters, write empty parameter list
            writer.write("<parameterList>\n</parameterList>\n");
        }

        // close paren
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        compileSubroutineBody();

        writer.write("</subroutineDec>\n");
    }

    private void compileParameterList() throws IOException {
        writer.write("<parameterList>\n");

        // write type
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();
        
        // write var name
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
            this.tokenizer.advance();

            // write type
            writer.write(
                String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
            );
            this.tokenizer.advance();

            // write var name
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
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // zero or more var dec
        while (
            this.tokenizer.tokenType() == TokenType.KEYWORD &&
            this.tokenizer.keyWord().equals("var")
        ) {
            compileVarDec();
        }

        // statements
        compileStatements();

        // close curly brace
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        writer.write("</subroutineBody>\n");
    }

    private void compileVarDec() throws IOException {
        writer.write("<varDec>\n");

        // write keyword var
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // write type
        writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // write var name
        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();

        // handle additional var names
        while (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(",")) {
            // write comma
            writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();

            // write next var name
            writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );
            this.tokenizer.advance();
        }

        // write semicolon
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();
        writer.write("</varDec>\n");
    }

    private void compileStatements() throws IOException {
        writer.write("<statements>\n");

        // zero or more of:
        // let statement | if statement | while statement | do statement | return statement

        while (true) {
            if (this.tokenizer.tokenType() != TokenType.KEYWORD) {
                break;
            } else if (this.tokenizer.keyWord().equals("let")) {
                compileLet();
            } else if (this.tokenizer.keyWord().equals("if")) {
                compileIf();
            } else if (this.tokenizer.keyWord().equals("while")) {
                compileWhile();
            } else if (this.tokenizer.keyWord().equals("do")) {
                compileDo();
            } else if (this.tokenizer.keyWord().equals("return")) {
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
        writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
        this.tokenizer.advance();

        // write subroutine/class/var name
        writer.write(String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier()));
        this.tokenizer.advance();

        // check if there's a dot operator
        if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(".")) {
            writer.write("    <symbol> . </symbol>\n");
            this.tokenizer.advance();

            // write subroutine name after dot
            writer.write(String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier()));
            this.tokenizer.advance();
        }

        // write open parenthesis
        writer.write("    <symbol> ( </symbol>\n");
        this.tokenizer.advance();

        // compile expression list unless the next token is a close parenthesis
        if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(")")) {
            // write an empty expression list
            writer.write("<expressionList>\n</expressionList>\n");
        } else {
            compileExpressionList();
        }

        // write close parenthesis  
        writer.write("    <symbol> ) </symbol>\n");
        this.tokenizer.advance();

        // semicolon
        writer.write(String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol()));
        this.tokenizer.advance();

        writer.write("</doStatement>\n");
    }

    private void compileLet() throws IOException {
        writer.write("<letStatement>\n");

        // keyword let
        writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
        this.tokenizer.advance();

        // write varname
        writer.write(String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier()));
        this.tokenizer.advance();

        // check for array access with []
        if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals("[")) {
            writer.write("    <symbol> [ </symbol>\n");
            this.tokenizer.advance();
            
            compileExpression();

            writer.write("    <symbol> ] </symbol>\n");
            this.tokenizer.advance();
        }

        // equals sign
        writer.write("    <symbol> = </symbol>\n");
        this.tokenizer.advance();

        // expression
        compileExpression();

        // semicolon
        writer.write("    <symbol> ; </symbol>\n");
        this.tokenizer.advance();
        
        writer.write("</letStatement>\n");
    }

    private void compileWhile() {
        // 'while' '(' expression ')' '{' statements '}'
    }

    private void compileReturn() throws IOException{
        writer.write("<returnStatement>\n");

        // keyword 'return' 
        writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())); 
        this.tokenizer.advance();

        // zero or one of expression 
        // TODO for now let's assume there's no expression
        
        // semicolon 
        writer.write("    <symbol> ; </symbol>\n");
        this.tokenizer.advance();

        writer.write("</returnStatement>\n");
    }

    private void compileIf() throws IOException {
        writer.write("<ifStatement>\n");

        // keyword if
        writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
        this.tokenizer.advance();

        // open parenthesis
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // expression
        compileExpression();

        // close parenthesis
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // open curly brace
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // statements
        compileStatements();

        // close curly brace
        writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // the else clause is optional
        if (this.tokenizer.tokenType() == TokenType.KEYWORD && this.tokenizer.keyWord().equals("else")) {
            // keyword else 
            writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
            this.tokenizer.advance();
            
            // open curly brace
            writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();

            // statements
            compileStatements();
            
            // close curly brace
            writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();
        }

        writer.write("</ifStatement>\n");
    }

    private void compileExpression() throws IOException {
        // for initial version, all expressions 
        // are replaced by a single identifier

        writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();
    }

    private void compileTerm() {
        
    }

    private void compileExpressionList() throws IOException {
        writer.write("<expressionList>\n");

        writer.write("</expressionList>\n");
    }
}
