import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
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
                }
            }
        });

        output = new JLabel("0.");
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 32));

        plus = new JButton("+");
        plus.setPreferredSize(new Dimension(80,50));
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " + ");
            }
        });
        minus = new JButton("-");
        minus.setPreferredSize(new Dimension(80,50));
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " - ");
            }
        });
        times = new JButton("*");
        times.setPreferredSize(new Dimension(80,50));
        times.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " * ");
            }
        });
        divide = new JButton("/");
        divide.setPreferredSize(new Dimension(80,50));
        divide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " / ");
            }
        });
        sin = new JButton("sin(");
        sin.setPreferredSize(new Dimension(80,50));
        sin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " sin( ");
            }
        });
        cos = new JButton("cos(");
        cos.setPreferredSize(new Dimension(80,50));
        cos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " cos( ");
            }
        });
        tan = new JButton("tan(");
        tan.setPreferredSize(new Dimension(80,50));
        tan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " tan( ");
            }
        });
        inv = new JButton("1/(");
        inv.setPreferredSize(new Dimension(80,50));
        inv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " 1/( ");
            }
        });
        lparen = new JButton("(");
        lparen.setPreferredSize(new Dimension(80,50));
        lparen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " ( ");
            }
        });
        rparen = new JButton("(");
        rparen.setPreferredSize(new Dimension(80,50));
        rparen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(input.getText() + " ) ");
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

        frame.pack();
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