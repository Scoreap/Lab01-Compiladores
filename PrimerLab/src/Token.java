  import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Token {
    //Definición de los tipos de tokens con sus patrones correspondientes utilizando la libreria de REGEX
    //Junto con sus constructores y métodos para obtener el patrón de cada tipo de token 
    public enum TipoToken{
        RESERVADA("\\b(bin|oct|hex|Bin|Oct|Hex)\\b"),
        NUM("\\d+"),
        ID("[a-zA-Z_][a-zA-Z0-9_]*"),
        LPAREN("\\("),
        RPAREN("\\)"),
        OPERADOR("[+\\-*/=]"),
        FINAL(";"),
        SALTO("\n"),
        DESCONOCIDO("");
        private final String patron;
        TipoToken(String patron) {
            this.patron = patron;
        }

        public String getPatron() {
            return patron;
        }
    }

    private HashMap<String, String> tokens;
    private ArrayList<String> erroresLexicos;
    public Token() {
        tokens = new HashMap<>();
        erroresLexicos = new ArrayList<>();

    }
    //Apartado de clasificación de cadenas, donde se evalúa cada cadena de entrada 
    //comparando con los patrones definidos para cada tipo de token 
    //y se devuelve el tipo correspondiente como una cadena.
    public String clasificarCadenas(String cadena, int linea, int columna) {
        TipoToken tipo;
        if (Pattern.matches("(bin|oct|hex|Bin|Oct|Hex)", cadena)) {
            tipo = TipoToken.RESERVADA;
        } else if (Pattern.matches("\\d+", cadena)) {
            tipo = TipoToken.NUM;
        } else if (Pattern.matches("[a-zA-Z_][a-zA-Z0-9_]*", cadena)) {
            tipo = TipoToken.ID;
        } else if (Pattern.matches("\\(", cadena)) {
            tipo = TipoToken.LPAREN;
        } else if (Pattern.matches("\\)", cadena)) {
            tipo = TipoToken.RPAREN;
        } else if (Pattern.matches("[+\\-*/=]", cadena)) {
            tipo = TipoToken.OPERADOR;
        } else if (Pattern.matches(";", cadena)) {
            tipo = TipoToken.FINAL;
        } else if (Pattern.matches("\\n", cadena)) {
            tipo = TipoToken.SALTO;
        } else {
            tipo = TipoToken.DESCONOCIDO;
        }
        switch (tipo) {
            case RESERVADA:
                return "reservada";
            case NUM:
                return "num";
            case ID:
                return "id";
            case LPAREN:
                return "lparen";
            case RPAREN:
                return "rparen";
            case OPERADOR:
                return "operador";
            case FINAL:
                return "final";
            case SALTO:
                return "linea";
            default:
                reportarErrorLexico(cadena, linea, columna);
                return "desconocido";
        }
    }

    // Método para reportar errores de gramática (se consideran errores léxicos para el flujo actual)
    public void reportarErrorGramatica(String mensaje, int linea, int columna) {
        String error = "Error Léxico: error de gramática '" + mensaje + "' en línea " + linea + " columna " + columna;
        erroresLexicos.add(error);
    }

    //Aqui guardamos los tokens reconocidos en un HashMap,
    //donde la clave es el valor del token y el valor es su tipo.
    public void almacenar(String valor, String tipo) {
        if (!tokens.containsKey(valor) && !tipo.equals("desconocido")) {
            tokens.put(valor, tipo);
        }
    }

    public void mostrarTokens(){
        if(tokens.isEmpty()){
            System.out.println("No hay tokens almacenados.");
            return;
        }
        System.out.println("\n------ Tokens Encontrados ------");
        int contador = 1;
        for(String valor : tokens.keySet()){
            System.out.println(contador + ". " + valor + " (tipo: " + tokens.get(valor) + ")");
            contador++;
        }
        System.out.println("\n");
    }

    //Método para reportar errores léxicos cuando se encuentra un token desconocido
    public void reportarErrorLexico(String cadena, int linea, int columna) {
        String error = "Error Léxico: token desconocido '" + cadena + "' en línea " + linea + " columna " + columna;
        erroresLexicos.add(error);
    }

    //Método para mostrar todos los errores léxicos encontrados
    public void mostrarErrores(){
        if (erroresLexicos.isEmpty()) {
            System.out.println("No se encontraron errores léxicos.");
        } else {
            System.out.println("\n------ ERRORES LÉXICOS ENCONTRADOS ------");
            for(String error : erroresLexicos){
                System.out.println(error);
            }
        }
    }

    //Método para obtener el HashMap de tokens
    public HashMap<String, String> obtenerTokens(){
        return tokens;
    }

    //Método para verificar si hay errores
    public boolean hayErrores(){
        return !erroresLexicos.isEmpty();
    }
}
