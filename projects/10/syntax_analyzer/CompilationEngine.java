package syntax_analyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CompilationEngine {
    private final FileWriter writer;
    private final JackTokenizer tokenizer;
    private static final List<String> operators = Arrays.asList("+", "-", "*", "/", "&amp;", "|", "&lt;", "&gt;", "=");
    private static final List<String> unaryOperators = Arrays.asList("-", "~");
    
    public CompilationEngine(JackTokenizer tokenizer, String outputFile) throws IOException {
        this.writer = new FileWriter(outputFile);
        this.tokenizer = tokenizer;
        this.tokenizer.advance(); // advance to the first token
    }

    public void closeWriter() throws IOException {
        this.writer.close();
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

    // This is known as the "predictive parsing" pattern in recursive descent parsing.

    public void compileClass() throws IOException {
        this.writer.write("<class>\n");

        // keyword class
        this.writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // then should be an identifier
        this.writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();

        // then open curly brace
        this.writer.write(
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
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );

        // that should have been the final token so do not advance here

        this.writer.write("</class>\n");
    }

    private void compileClassVarDec() throws IOException {
        this.writer.write("<classVarDec>\n");

        // write the keyword static or field
        this.writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // note that type can be either a keyword or an identifier (custom class)
        if (this.tokenizer.tokenType() == TokenType.KEYWORD) {
            this.writer.write(
                String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
            );
        } else {
            this.writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );
        }
        this.tokenizer.advance();

        // varname
        this.writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();

        // there are optionally one or more ", Varname" following the first var name
        while (this.tokenizer.symbol().equals(",")) {
            this.writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();
            this.writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );
            this.tokenizer.advance();
        }

        // should end with a semicolon
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        this.writer.write("</classVarDec>\n");
    }

    private void compileSubroutine() throws IOException {
        this.writer.write("<subroutineDec>\n");

        // write the keyword, constructor, method or function
        this.writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // note that type can be either a keyword or an identifier (custom class)
        if (this.tokenizer.tokenType() == TokenType.KEYWORD) {
            this.writer.write(
                String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
            );
        } else {
            this.writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );
        }
        this.tokenizer.advance();

        // write the identifier for subroutine names
        this.writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();

        // open paren
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        if (this.tokenizer.tokenType() != TokenType.SYMBOL) {
            // if there are parameters, compile them
            // (the next token will not be a symbol for close parenthesis)
            compileParameterList();
        } else {
            // if there are no parameters, write empty parameter list
            this.writer.write("<parameterList>\n</parameterList>\n");
        }

        // close paren
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        compileSubroutineBody();

        this.writer.write("</subroutineDec>\n");
    }

    private void compileParameterList() throws IOException {
        this.writer.write("<parameterList>\n");

        // write type
        this.writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();
        
        // write var name
        this.writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();

        // handle additional parameters (optional)
        while (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(",")) {
            // write comma
            this.writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();

            // write type
            this.writer.write(
                String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
            );
            this.tokenizer.advance();

            // write var name
            this.writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );
            this.tokenizer.advance();
        }

        this.writer.write("</parameterList>\n");
    }

    private void compileSubroutineBody() throws IOException {
        this.writer.write("<subroutineBody>\n");

        // open curly brace
        this.writer.write(
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
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        this.writer.write("</subroutineBody>\n");
    }

    private void compileVarDec() throws IOException {
        this.writer.write("<varDec>\n");

        // write keyword var
        this.writer.write(
            String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
        );
        this.tokenizer.advance();

        // write type
        // NOTE that type can be either a keyword or an identifier (custom class)
        if (this.tokenizer.tokenType() == TokenType.KEYWORD) {
            this.writer.write(
                String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
            );
        } else {
            this.writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );
        }
        this.tokenizer.advance();

        // write var name
        this.writer.write(
            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
        );
        this.tokenizer.advance();

        // handle additional var names
        while (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(",")) {
            // write comma
            this.writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();

            // write next var name
            this.writer.write(
                String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
            );
            this.tokenizer.advance();
        }

        // write semicolon
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();
        this.writer.write("</varDec>\n");
    }

    private void compileStatements() throws IOException {
        this.writer.write("<statements>\n");

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
        
        this.writer.write("</statements>\n");
    }

    private void compileDo() throws IOException {
        this.writer.write("<doStatement>\n");

        // keyword do
        this.writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
        this.tokenizer.advance();

        // write subroutine/class/var name
        this.writer.write(String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier()));
        this.tokenizer.advance();

        // check if there's a dot operator
        if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(".")) {
            this.writer.write("    <symbol> . </symbol>\n");
            this.tokenizer.advance();

            // write subroutine name after dot
            this.writer.write(String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier()));
            this.tokenizer.advance();
        }

        // write open parenthesis
        this.writer.write("    <symbol> ( </symbol>\n");
        this.tokenizer.advance();

        // compile expression list unless the next token is a close parenthesis
        if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(")")) {
            // write an empty expression list
            this.writer.write("<expressionList>\n</expressionList>\n");
        } else {
            compileExpressionList();
        }

        // write close parenthesis  
        this.writer.write("    <symbol> ) </symbol>\n");
        this.tokenizer.advance();

        // semicolon
        this.writer.write(String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol()));
        this.tokenizer.advance();

        this.writer.write("</doStatement>\n");
    }

    private void compileLet() throws IOException {
        this.writer.write("<letStatement>\n");

        // keyword let
        this.writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
        this.tokenizer.advance();

        // write varname
        this.writer.write(String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier()));
        this.tokenizer.advance();

        // check for array access with []
        if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals("[")) {
            this.writer.write("    <symbol> [ </symbol>\n");
            this.tokenizer.advance();
            
            compileExpression();

            this.writer.write("    <symbol> ] </symbol>\n");
            this.tokenizer.advance();
        }

        // equals sign
        this.writer.write("    <symbol> = </symbol>\n");
        this.tokenizer.advance();

        // expression
        compileExpression();

        // semicolon
        this.writer.write("    <symbol> ; </symbol>\n");
        this.tokenizer.advance();
        
        this.writer.write("</letStatement>\n");
    }

    private void compileWhile() throws IOException {
        this.writer.write("<whileStatement>\n");

        // keyword while
        this.writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
        this.tokenizer.advance();

        // open paren
        this.writer.write(String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol()));
        this.tokenizer.advance();

        // expression
        compileExpression();

        // close paren
        this.writer.write(String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol()));
        this.tokenizer.advance();

        // open curly brace
        this.writer.write(String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol()));
        this.tokenizer.advance();

        // statements
        compileStatements();

        // close curly brace
        this.writer.write(String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol()));
        this.tokenizer.advance();

        this.writer.write("</whileStatement>\n");
    }

    private void compileReturn() throws IOException{
        this.writer.write("<returnStatement>\n");

        // keyword 'return' 
        this.writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())); 
        this.tokenizer.advance();

        // zero or one of expression 
        if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(";")) {
            this.writer.write("    <symbol> ; </symbol>\n");
            this.tokenizer.advance();
        } else {
            compileExpression();
            this.writer.write("    <symbol> ; </symbol>\n");
            this.tokenizer.advance();
        }

        this.writer.write("</returnStatement>\n");
    }

    private void compileIf() throws IOException {
        this.writer.write("<ifStatement>\n");

        // keyword if
        this.writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
        this.tokenizer.advance();

        // open parenthesis
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // expression
        compileExpression();

        // close parenthesis
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // open curly brace
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // statements
        compileStatements();

        // close curly brace
        this.writer.write(
            String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
        );
        this.tokenizer.advance();

        // the else clause is optional
        if (this.tokenizer.tokenType() == TokenType.KEYWORD && this.tokenizer.keyWord().equals("else")) {
            // keyword else 
            this.writer.write(String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord()));
            this.tokenizer.advance();
            
            // open curly brace
            this.writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();

            // statements
            compileStatements();
            
            // close curly brace
            this.writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();
        }

        this.writer.write("</ifStatement>\n");
    }

    private void compileExpression() throws IOException {
        this.writer.write("<expression>\n");

        compileTerm();

        while (this.tokenizer.tokenType() == TokenType.SYMBOL && this.operators.contains(this.tokenizer.symbol())) {
            this.writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();

            compileTerm();
        }

        this.writer.write("</expression>\n");
    }

    private void compileTerm() throws IOException {
        this.writer.write("<term>\n");

        // integerConstant
        if (this.tokenizer.tokenType() == TokenType.INT_CONST) {
            this.writer.write(
                String.format("    <integerConstant> %s </integerConstant>\n", this.tokenizer.intVal())
            );
            this.tokenizer.advance();
        // stringConstant
        } else if (this.tokenizer.tokenType() == TokenType.STRING_CONST) {
            this.writer.write(
                String.format("    <stringConstant> %s </stringConstant>\n", this.tokenizer.stringVal())
            );
            this.tokenizer.advance();
        // keywordConstant
        } else if (this.tokenizer.tokenType() == TokenType.KEYWORD) {
            this.writer.write(
                String.format("    <keyword> %s </keyword>\n", this.tokenizer.keyWord())
            );
            this.tokenizer.advance();
        // varName | varName[] | subroutineCall
        // (each of these starts with an identifier)
        } else if (this.tokenizer.tokenType() == TokenType.IDENTIFIER) {    
            String savedIdentifier = this.tokenizer.identifier();
            this.tokenizer.advance();
            
            // Look ahead at next token to determine what kind of term this is
            if (this.tokenizer.tokenType() == TokenType.SYMBOL) {
                if (this.tokenizer.symbol().equals("[")) {  // array access
                    // Write the array name
                    this.writer.write(
                        String.format("    <identifier> %s </identifier>\n", savedIdentifier)
                    );
                    this.writer.write("    <symbol> [ </symbol>\n");
                    this.tokenizer.advance();

                    compileExpression();

                    this.writer.write("    <symbol> ] </symbol>\n");
                    this.tokenizer.advance();
                } else if (this.tokenizer.symbol().equals("(") || this.tokenizer.symbol().equals(".")) {  // subroutine call
                    // Write the initial identifier (class/var name or subroutine name)
                    this.writer.write(
                        String.format("    <identifier> %s </identifier>\n", savedIdentifier)
                    );

                    if (this.tokenizer.symbol().equals(".")) {  // class/var name followed by .subroutineName
                        this.writer.write("    <symbol> . </symbol>\n");
                        this.tokenizer.advance();

                        // Write subroutine name
                        this.writer.write(
                            String.format("    <identifier> %s </identifier>\n", this.tokenizer.identifier())
                        );
                        this.tokenizer.advance();
                    }

                    // Write open paren
                    this.writer.write("    <symbol> ( </symbol>\n");
                    this.tokenizer.advance();

                    // Handle expression list
                    if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(")")) {
                        this.writer.write("<expressionList>\n</expressionList>\n");
                    } else {
                        compileExpressionList();
                    }

                    // Write close paren
                    this.writer.write("    <symbol> ) </symbol>\n");
                    this.tokenizer.advance();
                } else {  // just a variable name
                    this.writer.write(
                        String.format("    <identifier> %s </identifier>\n", savedIdentifier)
                    );
                }
            } else {  // just a variable name
                this.writer.write(
                    String.format("    <identifier> %s </identifier>\n", savedIdentifier)
                );
            }
        // '(' expression ')'
        } else if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals("(")) {
            this.writer.write("    <symbol> ( </symbol>\n");
            this.tokenizer.advance();

            compileExpression();

            this.writer.write("    <symbol> ) </symbol>\n");
            this.tokenizer.advance();
        }
        // unaryOp term
        else if (this.tokenizer.tokenType() == TokenType.SYMBOL && this.unaryOperators.contains(this.tokenizer.symbol())) {
            this.writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();

            compileTerm();
        }

        this.writer.write("</term>\n");
    }

    private void compileExpressionList() throws IOException {
        this.writer.write("<expressionList>\n");

        compileExpression();

        // zero or more of ', expression'
        while (this.tokenizer.tokenType() == TokenType.SYMBOL && this.tokenizer.symbol().equals(",")) {
            this.writer.write(
                String.format("    <symbol> %s </symbol>\n", this.tokenizer.symbol())
            );
            this.tokenizer.advance();

            compileExpression();
        }

        this.writer.write("</expressionList>\n");
    }
}
