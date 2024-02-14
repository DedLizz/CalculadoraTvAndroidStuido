package brandon.bytes.calculadoratv;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    private TextView display;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.resultadoID);

    }
    public void calcular(View view) {
        Button boton = (Button) view;
        String textoBoton = boton.getText().toString();
        String textoDisplay = display.getText().toString();
        String concatenar = textoDisplay + textoBoton;

        String mostrar = quitarCerosIzquierda(concatenar);

        //display.setText(mostrar);

        if (textoBoton.equals("Reset")){
            display.setText(String.valueOf("0"));
            return;
        }

        if (textoBoton.equals("=")) { // Cambiado == por equals()
            double resultado = 0.0;
            try {
                resultado = eval(display.getText().toString());
                display.setText(String.valueOf(resultado)); // Mostrar el resultado en el display
            } catch (Exception e) {
                display.setText(e.toString());
            }
        } else {
            display.setText(mostrar); // Si no es "=", simplemente actualiza el display con el texto concatenado
        }
    }

    public String quitarCerosIzquierda(String str) {
        int i = 0;
        while (i < str.length() && str.charAt(i) == '0') {
            i++;
        }

        StringBuffer sb = new StringBuffer(str);
        sb.replace(0, i, "");
        return sb.toString();
    }

    public double eval(String str) {
        return new Object() {
            int pos = -1;
            int ch = 0;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus
                double x;
                int startPos = pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
                return x;
            }
        }.parse();
    }
}




