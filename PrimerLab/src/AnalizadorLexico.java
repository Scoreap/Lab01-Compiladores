import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorLexico {
    //ATRIBUTOS
    public Token token;
    public ArchivoTexto entrada;

    //CONSTRUCTOR
    public AnalizadorLexico(String ruta) {
        token = new Token();
        entrada = new ArchivoTexto(); 
    }

    /*
        *Perimite procesar y validar el arhivo de texto de acuerdo a la gramática definida
        *
        *@param ruta: Ruta del archivo de texto que el usuario ingresa
        *@return imprime los token reconocidos y errores lexicos encontrados
     */
    public void analizarArchivo(String ruta){
        try{
            // Lectura exclusiva para clasificación de tokens
            entrada.leerArchivo(ruta, token);
            // Valida la sintaxis de acuerdo a la gramática 
            validarArchivo(ruta);
            if(token.hayErrores()){
                System.out.println("\nAnalisis finalizado con ERRORES LEXICOS");
                token.mostrarErrores();
            } else {
                System.out.println("\nAnalisis finalizado EXITOSAMENTE");
                System.out.println("\nTokens reconocidos: " + token.obtenerTokens().size());
                token.mostrarTokens();
            }
        } catch(Exception e){
            System.out.println("Error durante el analisis: " + e.getMessage());
        }
    }

    
    /*
        *Permite que se valide la gramática, donde se espera: "reservada id operador num ;"
        *@param ruta: Ruta del archivo de texto que el usuario ingresa
    */
    private void validarArchivo(String ruta) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(ruta), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return;
        }

        //Expresión que funciojna como filtro nuevamente
        String splitRegex = "[^a-záéíóúñüA-ZÁÉÍÓÚÑÜ0-9_+\\-*/=;]+";
        boolean primeraLineaEncontrada = false;
        int lineaNum = 0;
        for (String linea : lines) {
            lineaNum++;
            if (linea == null) continue;
            if (linea.contains("//") || linea.contains("/*")) break;
            if (linea.trim().isEmpty()) continue;
            //Aqui 
            primeraLineaEncontrada = primeraLineaEncontrada || true;
            if (!linea.trim().endsWith(";")) {
                int col = linea.length() == 0 ? 1 : linea.length();
                token.reportarErrorLexico("fin de linea esperado ';'", lineaNum, col);
            }
            String[] palabras = linea.split(splitRegex);
            List<String> tipos = new ArrayList<>();
            List<String> vals = new ArrayList<>();
            List<Integer> cols = new ArrayList<>();

            int searchIndex = 0;
            for (String palabra : palabras) {
                if (palabra == null) continue;
                String p = palabra.trim();
                if (p.isEmpty()) continue;
                int idx = linea.indexOf(palabra, searchIndex);
                int columna = (idx >= 0) ? idx + 1 : searchIndex + 1;
                searchIndex = (idx >= 0) ? idx + palabra.length() : searchIndex + palabra.length();

                String tipo = token.clasificarCadenas(p, lineaNum, columna);
                tipos.add(tipo);
                vals.add(p);
                cols.add(columna);
            }

            if (tipos.isEmpty()) continue;


            int last = tipos.size() - 1;
            boolean tieneFinal = "final".equals(tipos.get(last));
            List<String> tiposSinFinal = new ArrayList<>();
            List<String> valsSinFinal = new ArrayList<>();
            List<Integer> colsSinFinal = new ArrayList<>();
            for (int i = 0; i < tipos.size(); i++) {
                if (i == last && tieneFinal) break;
                tiposSinFinal.add(tipos.get(i));
                valsSinFinal.add(vals.get(i));
                colsSinFinal.add(cols.get(i));
            }
            if (!primeraLineaEncontrada) {
                primeraLineaEncontrada = true;
            }

            if (primeraLineaEncontrada && lineaNum == firstNonEmptyLineIndex(lines)) {
                if (!esDeclaracionValida(tipos, vals, cols, lineaNum)) {
                }
                continue;
            }

            if (esDeclaracionValida(tipos, vals, cols, lineaNum)) {
                continue;
            }

            if (tieneFinal) {
                if (!validarExpresionLinea(tiposSinFinal, valsSinFinal, colsSinFinal, lineaNum)) {
                }
            } else {
                if (!validarExpresionLinea(tiposSinFinal, valsSinFinal, colsSinFinal, lineaNum)) {
                }
            }
        }
    }

    /*
        *Permite encontrar el numero de la primera linea con contenido valido, ignorando lineas vacias y comentarios.
        *
        *@param lines: Lista de lineas leidas del archivo de texto
        *@return devuelve el numero de linea (base 1) de la primera linea no vacia y que no sea comentario
     */
    private int firstNonEmptyLineIndex(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i);
            if (l != null && !l.trim().isEmpty() && !l.contains("//") && !l.contains("/*")) return i + 1;
        }
        return 1;
    }

    /*
        *Permite validar si una linea cumple con la declaracion esperada segun la gramatica:
        *"reservada id operador num ;"
        *
        *@param tipos: Lista con los tipos de token identificados en la linea
        *@param vals: Lista con los lexemas encontrados en la linea
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param linea: Numero de linea que se esta validando
        *@return devuelve true si la declaracion es valida, de lo contrario devuelve false y reporta el error
     */
    private boolean esDeclaracionValida(List<String> tipos, List<String> vals, List<Integer> cols, int linea) {
        if (tipos.size() != 5) return false;
        if (!"reservada".equals(tipos.get(0))) {
            token.reportarErrorGramatica("Declaración debe iniciar con palabra reservada", linea, cols.get(0));
            return false;
        }
        if (!"id".equals(tipos.get(1))) {
            token.reportarErrorGramatica("Declaración debe contener identificador", linea, cols.get(1));
            return false;
        }
        if (!"operador".equals(tipos.get(2))) {
            token.reportarErrorGramatica("Declaración debe contener operador (=) después del id", linea, cols.get(2));
            return false;
        }
        if (!"num".equals(tipos.get(3))) {
            token.reportarErrorGramatica("Declaración debe asignar un número", linea, cols.get(3));
            return false;
        }
        if (!"final".equals(tipos.get(4))) {
            token.reportarErrorGramatica("Declaración debe terminar en ';'", linea, cols.get(4));
            return false;
        }
        return true;
    }

    /*
        *Permite validar que una linea represente una expresion aritmetica correcta segun la gramatica definida.
        *Utiliza el parser basado en:
        *E -> N ((+|-) N)*
        *N -> F ((*|/) F)*
        *F -> (E) | num
        *
        *@param tipos: Lista con los tipos de token de la linea (sin incluir el ';' final)
        *@param vals: Lista con los lexemas encontrados en la linea
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param linea: Numero de linea que se esta validando
        *@return devuelve true si la expresion es valida, de lo contrario devuelve false y reporta el error
     */
    private boolean validarExpresionLinea(List<String> tipos, List<String> vals, List<Integer> cols, int linea) {
        int[] p = new int[]{0};
        boolean ok = parseE(tipos, vals, cols, p);
        if (!ok) {
            int col = (p[0] < cols.size()) ? cols.get(p[0]) : (cols.isEmpty() ? 1 : cols.get(cols.size()-1));
            token.reportarErrorGramatica("Expresión inválida", linea, col);
            return false;
        }
        if (p[0] != tipos.size()) {
            int col = (p[0] < cols.size()) ? cols.get(p[0]) : cols.get(cols.size()-1);
            token.reportarErrorGramatica("Tokens extra después de la expresión", linea, col);
            return false;
        }
        return true;
    }

    /*
        *Permite analizar la produccion principal de la expresion (E) de acuerdo a la gramatica:
        *E -> N ((+|-) N)*
        *
        *@param tipos: Lista con los tipos de token de la linea
        *@param vals: Lista con los lexemas encontrados en la linea
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param p: Puntero que indica la posicion actual del token que se esta evaluando
        *@return devuelve true si la estructura E es correcta, de lo contrario devuelve false
     */
    private boolean parseE(List<String> tipos, List<String> vals, List<Integer> cols, int[] p) {
        if (!parseN(tipos, vals, cols, p)) return false;
        while (p[0] < tipos.size() && "operador".equals(tipos.get(p[0])) && ("+".equals(vals.get(p[0])) || "-".equals(vals.get(p[0])))) {
            p[0]++;
            if (!parseN(tipos, vals, cols, p)) return false;
        }
        return true;
    }

    /*
        *Permite analizar la parte multiplicativa/divisiva de la expresion (N) de acuerdo a la gramatica:
        *N -> F ((*|/) F)*
        *
        *@param tipos: Lista con los tipos de token de la linea
        *@param vals: Lista con los lexemas encontrados en la linea
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param p: Puntero que indica la posicion actual del token que se esta evaluando
        *@return devuelve true si la estructura N es correcta, de lo contrario devuelve false
     */
    private boolean parseN(List<String> tipos, List<String> vals, List<Integer> cols, int[] p) {
        if (!parseF(tipos, vals, cols, p)) return false;
        while (p[0] < tipos.size() && "operador".equals(tipos.get(p[0])) && ("*".equals(vals.get(p[0])) || "/".equals(vals.get(p[0])))) {
            p[0]++;
            if (!parseF(tipos, vals, cols, p)) return false;
        }
        return true;
    }

    /*
        *Permite analizar un factor (F) dentro de la expresion, segun la gramatica:
        *F -> (E) | num
        *Nota: segun la especificacion no se permiten ids en las operaciones, solo numeros y parentesis.
        *
        *@param tipos: Lista con los tipos de token de la linea
        *@param vals: Lista con los lexemas encontrados en la linea
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param p: Puntero que indica la posicion actual del token que se esta evaluando
        *@return devuelve true si el factor es valido, de lo contrario devuelve false
     */
    private boolean parseF(List<String> tipos, List<String> vals, List<Integer> cols, int[] p) {
        if (p[0] >= tipos.size()) return false;
        String t = tipos.get(p[0]);
        if ("lparen".equals(t)) {
            p[0]++;
            if (!parseE(tipos, vals, cols, p)) return false;
            if (p[0] >= tipos.size() || !"rparen".equals(tipos.get(p[0]))) return false;
            p[0]++;
            return true;
        }
        if ("num".equals(t)) {
            p[0]++;
            return true;
        }
        return false;
    }

    /*
        *Permite mostrar en consola los tokens reconocidos durante el analisis.
        *
        *@param no aplica
        *@return imprime en pantalla la lista de tokens almacenados en el objeto Token
     */
    public void verTokens(){
        token.mostrarTokens();
    }

}
