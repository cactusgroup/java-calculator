import java.util.ArrayList;

public class Calculator {
    private String input;
    private double output;
    private String error;

    private Op root;

    public Calculator() {
        input = "";
        output = 0.;
        error = "";
        root = new Op();
    }

    private void evaluate() {
        Op.destroy(root);
        Op index = root;
        len = offset = wordno = 0;
        lex();
    }

    // lexer
    private final int MAX_CHARS = 50;
    private char[] text   = new char[MAX_CHARS];
    private int    len    = 0;
    private int    offset = 0;
    private int    wordno = 0;
    private TokenType lex() {
        int current = offset + len;
        for (; current != input.length(); ++current) {
            text[0] = input.charAt(current);
            offset = current;
            len = 1;

            if (input.length() > 1) {
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
                if (text[0] == 's' && text[1] == 'i' && text[2] == 'n' && text[3]== '(')
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
                    } else if (text[0] == '1' && text[1] == '/') {
                        text[2] = input.charAt(current + 2);
                        len = 3;
                        if (text[2] = '(') { return TokenType.INV; }
                    } else {
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
        return TokenType.INVALID;
    }
    
    TokenType lookahead = TokenType.INVALID;
    private boolean match(TokenType token) {
        if (lookahead == TokenType.INVALID) {
            lookahead = lex();
        }
        return token == lookahead;
    }

    private void advance() {
        lookahead = lex();
    }

    // Field modifiers
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