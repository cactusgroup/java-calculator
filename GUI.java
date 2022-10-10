import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Caret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;

public class GUI {
    private JFrame frame;
    private Calculator calc;

    private JTextField input;
    private JLabel output;
    private JTextArea error;
    private JScrollPane errorScroll;
    private JButton plus;
    private JButton minus;
    private JButton times;
    private JButton divide;
    private JButton sin;
    private JButton cos;
    private JButton tan;
    private JButton inv;
    private JButton lparen;
    private JButton rparen;
    private JButton clearError;

    public GUI() {
        calc = new Calculator();

        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(825,300));
        JComponent c = (JComponent) frame.getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        input = new JTextField(72);
        input.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                if (e.getDot() == 0 && e.getMark() == 0) {
                    // return to start of text to overwrite text
                } else if (e.getDot() != e.getMark()) {
                    // text is selected
                } else {
                    // new text written
                    calc.setInput(input.getText());
                    output.setText(String.valueOf(calc.getOutput()));
                    error.append(calc.getError());
                }
            }
        });

        output = new JLabel("0.");
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 32));
        output.setPreferredSize(new Dimension(800, 100));

        error = new JTextArea(5, 80);
        error.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        error.setEditable(false);

        errorScroll = new JScrollPane(error);
        errorScroll
            .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        plus = genButton("+");
        minus = genButton("-");
        times = genButton("*");
        divide = genButton("/");
        sin = genButton("sin(");
        cos = genButton("cos(");
        tan = genButton("tan(");
        inv = genButton("1/(");
        lparen = genButton("(");
        rparen = genButton(")");

        clearError = new JButton("Clear Error");
        clearError.setPreferredSize(new Dimension(160,50));
        clearError.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                error.setText("");
                calc.clearError();
            }
        });

        c.add(input);
        c.add(plus);
        c.add(minus);
        c.add(times);
        c.add(divide);
        c.add(sin);
        c.add(cos);
        c.add(tan);
        c.add(inv);
        c.add(lparen);
        c.add(rparen);
        c.add(output);
        c.add(errorScroll);
        c.add(clearError);

        frame.pack();
    }

    private JButton genButton(String label) {
        JButton b = new JButton(label);
        b.setPreferredSize(new Dimension(80,50));
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " " + label + " ");
            }
        });
        return b;
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                new GUI().show();
            }
        });
    }
}