import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

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
        String tempvar;
        File file = new File("./Instructions.java");

        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(
                "public class Instructions {\n" +
                "   public static double calculate() {\n" +
                "       double  t0, t1, t2, t3, t4, t5, t6, t7,\n" +
                "               t8, t9,t10,t11,t12,t13,t14,t15,\n" +
                "              t16,t17,t18,t19,t20,t21,t22,t23,\n" +
                "              t24,t25,t26,t27,t28,t29,t30,t31;\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        len = offset = wordno = 0;
        lookahead = TokenType.INVALID;
        tempvar = expression();
        try {
            bw.write(
                "       return    " + tempvar + "   ;\n" +
                "   }\n" +
                "}\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        freename(tempvar);

        // compile source in Instructions.java
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int status = compiler.run(null, null, null, "-g", file.getPath());
        System.err.println("status = " + status);

        if (status == 0) { //compilation success
            // load the binary and invoke the calculate() method
            try {
                ClassLoader loader = URLClassLoader.newInstance(
                    new URL[] { new URL("file:///" + file.getPath()) });
                System.err.println("classloader = " + loader);
                
                Class<?> clazz = Class.forName("Instructions", true, loader);
                System.err.println("class = " + clazz);

                Method calculate = clazz.getDeclaredMethod("calculate", (Class<?>[]) null);
                System.err.println("method = " + calculate);
                
                output = (Double) calculate.invoke(null, (Object[]) null);
                System.err.println("noncasted output = " + calculate.invoke(null, (Object[]) null));
                System.err.println("output = " + output);
                System.err.println();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
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
                text[current] = input.charAt(current);
            } catch (Exception e) {
                // end of input
                return TokenType.EOI;
            }
            offset = current;
            len = 1;

            if (input.length() - current > 1) {
                text[current + 1] = input.charAt(current + 1);
                if (Character.isWhitespace(text[current])
                    && !Character.isWhitespace(text[current + 1])) {
                    ++wordno;
                }
            }

            if ((text[current] == 's' || text[current] == 'c' || text[current] == 't') && input.length() >= 4)
            {
                text[current + 2] = input.charAt(current + 2);
                text[current + 3] = input.charAt(current + 3);
                len = 4;
                if (text[current] == 's' && text[current + 1] == 'i' && text[current + 2] == 'n' && text[current + 3] == '(')
                {   return TokenType.SIN;   }
                else if (text[current] == 'c' && text[current + 1] == 'o' && text[current + 2] == 's' && text[current + 3] == '(')
                {   return TokenType.COS;   }
                else if (text[current] == 't' && text[current + 1] == 'a' && text[current + 2] == 'n' && text[current + 3] == '(')
                {   return TokenType.TAN;   }
            }

            switch (text[current]) {
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
                        while(current < input.length() &&
                              (Character.isDigit(input.charAt(current)) ||
                               '.' == input.charAt(current) ||
                               '+' == input.charAt(current) ||
                               '-' == input.charAt(current) ||
                               'e' == input.charAt(current))) {
                            if (current - offset >= MAX_CHARS) {
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
            return "---";
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
    private String expression() {
        if (match(TokenType.EOI)) {
            error += wordno + ": expression incomplete";
            return "---";
        }

        String tempvar, tempvar2;

        tempvar = term();
        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            String op = match(TokenType.PLUS) ? " += " : " -= ";
            advance();
            tempvar2 = term();
            try {
                bw.write(tempvar + op + tempvar2 + ";\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            freename(tempvar2);
        }
        return tempvar;
    }

    private String term() {
        String tempvar, tempvar2;

        tempvar = factor();
        while (match(TokenType.TIMES) || match(TokenType.DIVIDE)) {
            String op = match(TokenType.TIMES) ? " *= " : " /= ";
            advance();
            tempvar2 = factor();
            try {
                bw.write(tempvar + op + tempvar2 + ";\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            freename(tempvar2);
        }
        return tempvar;
    }

    private String factor() {
        String tempvar = "";

        if (match(TokenType.NUM)) {
            tempvar = newname();
            try {
                StringBuilder sb = new StringBuilder();
                for (int i = offset; i < offset + len; ++i) {
                    sb.append(text[i]);
                }
                bw.write(tempvar + " = " + sb.toString() + ";\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            advance();
        } else if (match(TokenType.SIN) || match(TokenType.COS) ||
                   match(TokenType.TAN) || match(TokenType.LPAREN)) {
            String op = match(TokenType.SIN) ? " = Math.sin(" :
                        match(TokenType.COS) ? " = Math.cos(" :
                        match(TokenType.TAN) ? " = Math.tan(" : "";
            advance();
            tempvar = expression();
            try {
                if (op != "") {
                    bw.write(tempvar + op + tempvar + ");\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (match(TokenType.RPAREN)) {
                advance();
            } else {
                error += wordno + ": Mismatched parenthesis\n\n";
            }
        } else {
            error += wordno + ": Number expected\n\n";
        }
        return tempvar;
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
}