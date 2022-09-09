import java.io.*;
import java.util.Scanner;

/**
 * @author Héctor Donaldo Maldonado Gómez
 *
 */


public class Operaciones {
    public static void main(String args[]) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true) {

            System.out.print("Ingrese la expresión aritmética a evaluar \n> ");
            String expresion = sc.nextLine();
            if (expresion.equalsIgnoreCase("exit") || expresion.equals("0")) {
                System.exit(0);
            }

            try {
                System.out.println("Resultado = " + resultado(expresion) + "\n");
            } catch (Excepciones e) {
                System.out.println(e);
            }
        }
    }

    public static String resultado(String expresion) throws Excepciones {
        Evaluador AnalizarVar = new Evaluador();
        double resultDouble = AnalizarVar.evalua(expresion);
        int resultInt = (int) resultDouble;

        // Estandarizandarización de la salida
        if(resultDouble == resultInt){
            return String.valueOf(resultInt);
        }else{
            return String.valueOf(resultDouble);
        }
    }
}

