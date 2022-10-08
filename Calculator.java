import javax.swing.SwingWorker;
import java.util.StringTokenizer;

public class Calculator {
    private String input;
    private double output;
    private Op root;

    public Calculator() {
        input = "";
        output = 0.;
        root = new Op();
    }

    public void setInput(String s) {
        input = s;
        evaluate();
    }

    private void evaluate() {
        Op.destroy(root);
        Op index = root;
        StringTokenizer tokens = new StringTokenizer(input);
        while (tokens.hasMoreTokens()) {
            String t = tokens.nextToken();
            try {
                Double.parseDouble(t);
                
            }
            catch (NumberFormatException ignore) {}
        }
    }

    public double getOutput() {
        return output;
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
            if (root.left == null &&
                root.middle == null &&
                root.right == null) {
            } else if (root.left != null) {
                destroy(root.left);
                root.left = null;
            } else if (root.middle != null) {
                destroy(root.middle);
                root.middle = null;
            } else if (root.right != null) {
                destroy(root.right);
                root.right = null;
            }
        }
    }
}