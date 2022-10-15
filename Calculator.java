import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Calculator {
    private String input;
    private double output;
    private String error;

    public Calculator() {
        input = "";
        output = 0.;
        error = "";
    }

    
    private BufferedWriter bw = null;
    private void evaluate() {
        try {
            bw = new BufferedWriter(new FileWriter(new File("./instructions.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        len = offset = wordno = 0;
        lookahead = TokenType.INVALID;
        expression();
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // lexer
    // TokenType lex()
    // boolean match(TokenType)
    // void advance()
    private final int MAX_CHARS = 50;
    private char[] text   = new char[MAX_CHARS];
    private int    len    = 0;
    private int    offset = 0;
    private int    wordno = 0;
    private TokenType lex() {
        int current = offset + len;
        for (; current != input.length(); ++current) {
            try {
                text[0] = input.charAt(current);
            } catch (Exception e) {
                // end of input
                return TokenType.EOI;
            }
            offset = current;
            len = 1;

            if (input.length() - current > 1) {
                text[1] = input.charAt(current + 1);
                if (Character.isWhitespace(text[0])
                    && !Character.isWhitespace(text[1])) {
                    ++wordno;
                }
            }

            if ((text[0] == 's' || text[0] == 'c' || text[0] == 't') && input.length() >= 4)
            {
                text[2] = input.charAt(current + 2);
                text[3] = input.charAt(current + 3);
                len = 4;
                if (text[0] == 's' && text[1] == 'i' && text[2] == 'n' && text[3] == '(')
                {   return TokenType.SIN;   }
                else if (text[0] == 'c' && text[1] == 'o' && text[2] == 's' && text[3] == '(')
                {   return TokenType.COS;   }
                else if (text[0] == 't' && text[1] == 'a' && text[2] == 'n' && text[3] == '(')
                {   return TokenType.TAN;   }
            }

            switch (text[0]) {
                case '+': return TokenType.PLUS;
                case '-': return TokenType.MINUS;
                case '*': return TokenType.TIMES;
                case '/': return TokenType.DIVIDE;
                case '(': return TokenType.LPAREN;
                case ')': return TokenType.RPAREN;

                case '\n':
                case '\t':
                case ' ' : break;

                default:
                {
                    if (!Character.isDigit(input.charAt(current))) {
                        error += "Invalid character " + input.charAt(current) + " at word number " + wordno + "\n\n";
                    }
                    else {
                        while(current < input.length()
                              && Character.isDigit(input.charAt(current))) {
                            if (current - offset == MAX_CHARS) {
                                error += "Truncating numeric value at word number " + wordno + ".\n" +
                                         "  Limit 50 decimal digits.\n\n";
                                break;
                            }
                            text[current] = input.charAt(current);
                            ++current;
                        }
                        len = current - offset;
                        return TokenType.NUM;
                    }
                    break;
                }
            }
        }
        return TokenType.EOI;
    }
    
    private TokenType lookahead = TokenType.INVALID;
    private boolean match(TokenType token) {
        if (lookahead == TokenType.INVALID) {
            lookahead = lex();
        }
        return token == lookahead;
    }

    private void advance() {
        lookahead = lex();
    }

    // temporary name pool
    // String newname()
    // freename(String)
    private String names[] = { "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
                               "t8", "t9","t10","t11","t12","t13","t14","t15",
                              "t16","t17","t18","t19","t20","t21","t22","t23",
                              "t24","t25","t26","t27","t28","t29","t30","t31" };
    private int nameidx = 0;
    private String newname() {
        if (nameidx >= names.length) {
            error += wordno + ": Expression too complex\n\n";
            
        }
        return names[nameidx++];
    }

    private void freename(String s) {
        if (nameidx > 0) {
            --nameidx;
        } else {
            error += wordno + ": (Internal error) Name stack underflow";
        }
    }

    // parser
    // expression()
    // term()
    // factor()
    private void expression() {
        if (match(TokenType.EOI)) {
            return;
        }

        String tempvar, tempvar2;
        term();
        
        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            advance();
            term();
        }
    }

    private void term() {
        factor();

        while (match(TokenType.TIMES) || match(TokenType.DIVIDE)) {
            advance();
            factor();
        }
    }

    private void factor() {
        if (match(TokenType.NUM)) {
            advance();
        } else if (match(TokenType.SIN) || match(TokenType.COS) ||
                   match(TokenType.TAN) || match(TokenType.LPAREN)) {
            advance();
            expression();
            if (match(TokenType.RPAREN)) {
                advance();
            } else {
                error += wordno + ": Mismatched parenthesis\n\n";
            }
        } else {
            error += wordno + ": Number expected\n\n";
        }
    }

    // Field modifiers
    // setInput(String)
    // double getOutput()
    // String getError()
    public void setInput(String s) {
        input = s;
        evaluate();
    }

    public double getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }

    public void clearError() {
        error = "";
    }

    // for lexer, parser
    private enum TokenType {
        // Do not change the order
        EOI,
        INVALID,
        NUM,
        PLUS,
        MINUS,
        TIMES,
        DIVIDE,
        SIN,
        COS,
        TAN,
        INV,
        LPAREN,
        RPAREN;
    }

    private class Op {
        public String value;
        public Op left;
        public Op middle;
        public Op right;
        public Op up;
        public Op() {
            value = "";
            left = middle = right = up = null;
        }
        public boolean isLeaf() {
            boolean lT = false, rT = false;
            if (left != null) {
                try {
                    Double.parseDouble(left.value);
                } catch (NumberFormatException ignore) {
                    return false;
                }
                lT = true;
            }
            if (right != null) {
                try {
                    Double.parseDouble(right.value);
                } catch (NumberFormatException ignore) {
                    return false;
                }
                rT = true;
            }
            return lT && rT;
        }
        public static void destroy(Op root) {
            if (root.left != null) {
                destroy(root.left);
                root.left = null;
            }
            if (root.middle != null) {
                destroy(root.middle);
                root.middle = null;
            }
            if (root.right != null) {
                destroy(root.right);
                root.right = null;
            }
            if (root.up != null) {
                root.up = null;
            }
        }
    }
}