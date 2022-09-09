import java.util.Scanner;

/**
 * @author Héctor Donaldo Maldonado Gómez
 */

// Excepcion para errores
class Excepciones extends Exception {
    // Retorna errores
    String errStr;

    public Excepciones(String str) {
        errStr = str;
    }

    public String toString() {
        return errStr;
    }
}

class Evaluador {

    Scanner sc = new Scanner(System.in);

    final int NINGUNO = 0;
    final int DELIMITADOR = 1;
    final int VARIABLE = 2;
    final int NUMERO = 3;

    // Variables para los tipos de errores de sintaxis
    final int SYNTAXIS = 0;
    final int PARENTESIS = 1;
    final int SINEXP = 2;
    final int DIVENTRECERO = 3;

    final String FINEXP = "\0";

    private String exp;
    private int expIndice;
    private String token;
    private int tipoToken;

    // Almacenado de variables
    private String var[] = new String[10];
    private int valorVar[] = new int[10];

    int i = 0;
    int j = 0;

    // Método de punto de entrada del analizador
    public double evalua(String cadenaExp) throws Excepciones {
        double resultado;

        exp = cadenaExp;
        expIndice = 0;

        obtieneToken();
        if (token.equals(FINEXP)) {
            obtieneError(SINEXP);
        }

        resultado = evaluaSumRes();

        if (!token.equals(FINEXP)) {
            obtieneError(SYNTAXIS);
        }

        return resultado;
    }

    // Método para suma o resta
    private double evaluaSumRes() throws Excepciones {
        char op;
        double resultado;
        double resultadoParcial;

        resultado = evaluaMultDivMod();

        while ((op = token.charAt(0)) == '+' || op == '-') {
            obtieneToken();
            resultadoParcial = evaluaMultDivMod();

            switch (op) {
                case '-':
                    resultado = resultado - resultadoParcial;
                    break;
                case '+':
                    resultado = resultado + resultadoParcial;
                    break;
            }
        }
        return resultado;
    }

    // Método para multiplicacion, division o modulo
    private double evaluaMultDivMod() throws Excepciones {
        char op;
        double resultado;
        double resultadoParcial;

        resultado = evaluaExponenete();

        while ((op = token.charAt(0)) == '*' || op == '/' || op == '%') {
            obtieneToken();
            resultadoParcial = evaluaExponenete();
            switch (op) {
                case '*':
                    resultado = resultado * resultadoParcial;
                    break;
                case '/':
                    if (resultadoParcial == 0.0) {
                        obtieneError(DIVENTRECERO);
                    } else {
                        resultado = resultado / resultadoParcial;
                    }

                    break;
                case '%':
                    if (resultadoParcial == 0.0) {
                        obtieneError(DIVENTRECERO);
                    } else {
                        resultado = resultado % resultadoParcial;
                    }
                    break;
            }
        }
        return resultado;
    }

    // Método que evalua un exponente
    private double evaluaExponenete() throws Excepciones {
        double resultado;
        double resultadoParcial;
        double ex;
        int t;

        resultado = evaluaOperadores();

        if (token.equals("^")) {
            obtieneToken();
            resultadoParcial = evaluaExponenete();
            ex = resultado;
            if (resultadoParcial == 0.0) {
                resultado = 1.0;
            } else {
                resultado = Math.pow(resultado, resultadoParcial);
            }
        }
        return resultado;
    }

    // Método que evalua operador unario + ó -
    private double evaluaOperadores() throws Excepciones {
        double resultado;
        String op;
        op = "";
        if ((tipoToken == DELIMITADOR) && token.equals("+") || token.equals("-")) {
            op = token;
            obtieneToken();
        }
        resultado = evaluaParentesis();
        if (op.equals("-")) {

            resultado = -resultado;
        }
        return resultado;
    }

    // Método que procesa los parentesis
    private double evaluaParentesis() throws Excepciones {
        double resultado;

        if (token.equals("(")) {
            obtieneToken();
            resultado = evaluaSumRes();
            if (!token.equals(")")) {
                obtieneError(PARENTESIS);
            }
            obtieneToken();

        } else {
            resultado = valor();
        }
        return resultado;
    }

    //Método que obtiene el valor de un número
    private double valor() throws Excepciones {
        double resultado = 0.0;
        switch (tipoToken) {
            case NUMERO:
                try {
                    resultado = Double.parseDouble(token);
                } catch (NumberFormatException exc) {
                    obtieneError(SYNTAXIS);
                }
                obtieneToken();
                break;
            //Este caso es donde se le asignan valores a las variables
            case VARIABLE:
                var[i] = token;
                j = 0;
                while (j < i) {
                    if (var[j].equals(token)) {
                        resultado = valorVar[j];
                        break;
                    }
                    j++;
                }
                if (j == i) {
                    System.out.print("Ingresa el valor de ( " + token + " ) \n> ");
                    String num = sc.nextLine();
                    valorVar[i] = Integer.parseInt(num);
                    resultado = Double.parseDouble(num);
                }
                i++;
                obtieneToken();
                break;

            default:
                obtieneError(SYNTAXIS);
                break;
        }
        return resultado;
    }

    //Posibles mensajes en caso de errores
    private void obtieneError(int error) throws Excepciones {
        String[] err = {"ERROR DE SYNTAXIS", "PARENTESIS NO BALANCEADOS", "NO EXISTE EXPRESION", "DIVISION POR CERO"};
        throw new Excepciones(err[error]);
    }

    //Obtiene el siguiente token
    private void obtieneToken() {
        tipoToken = NINGUNO;
        token = "";
        //Busca el final de la expresion
        if (expIndice == exp.length()) {
            token = FINEXP;
            return;
        }
        //Omite el espacio en blanco
        while (expIndice < exp.length() && Character.isWhitespace(exp.charAt(expIndice))) {
            ++expIndice;
        }

        //Termina la expresion
        if (expIndice == exp.length()) {
            token = FINEXP;
            return;
        }

        //Determina si es operador o parentesis
        if (esDelimitador(exp.charAt(expIndice))) {
            token += exp.charAt(expIndice);
            expIndice++;
            tipoToken = DELIMITADOR;
        } else if (Character.isLetter(exp.charAt(expIndice))) {
            while (!esDelimitador(exp.charAt(expIndice))) {
                token += exp.charAt(expIndice);
                expIndice++;
                if (expIndice >= exp.length()) {
                    break;
                }
            }
            tipoToken = VARIABLE;
        } else if (Character.isDigit(exp.charAt(expIndice))) {
            while (!esDelimitador(exp.charAt(expIndice))) {
                token += exp.charAt(expIndice);
                expIndice++;
                if (expIndice >= exp.length()) {
                    break;
                }
            }
            tipoToken = NUMERO;
        } else {
            token = FINEXP;
            return;
        }
    }

    //Devuelve true si el caracter es un delimitador
    private boolean esDelimitador(char c) {
        if (("+-/*^=%()".indexOf(c) != -1)) {
            return true;
        } else {
            return false;
        }
    }
}