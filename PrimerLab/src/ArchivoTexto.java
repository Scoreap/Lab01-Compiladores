import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ArchivoTexto {
    public String cadena;
    public int lineaActual;    
    public int columnaActual;

    /*
        *Perimite leer y procesar el arhivo de texto linea por linea, separando y clasificando cada lexema encontrado.
        *
        *@param rutaArchivo: Ruta del archivo de texto que el usuario ingresa
        *@param token: Objeto Token que almacena los tokens reconocidos y reporta errores
        *@return imprime mensajes de exito o error durante la lectura, y almacena tokens clasificados en el objeto Token
     */
    public void leerArchivo(String rutaArchivo, Token token){
        try(Scanner scanner = new Scanner(Files.newBufferedReader(Paths.get(rutaArchivo), StandardCharsets.UTF_8)))
        {
            lineaActual = 1;
            columnaActual = 1;
            while(scanner.hasNextLine()){
                String linea = scanner.nextLine();
                columnaActual = 1;
                //Procesar palabras en la línea actual y clasificar cada una.
                //Aqui el split funciona como un filtro que separa no letras
                String[] palabras = linea.split("[^a-záéíóúñüA-ZÁÉÍÓÚÑÜ0-9_+\\-*/=;]+");
                for(String palabra : palabras){
                    if(!palabra.trim().isEmpty()){                        
                        cadena = palabra.trim();
                        //Clasificar la palabra y almacenarla en el token
                        //A su vez hay contadores de linea y columna para reportar errores con precisión
                        String tipo = token.clasificarCadenas(cadena, lineaActual, columnaActual);
                        token.almacenar(cadena, tipo);
                        columnaActual += palabra.length() + 1;
                    }
                }
                lineaActual++;
            } 
            System.out.println("El archivo ha sido procesado exitosamente.");
        }
        catch(java.nio.file.NoSuchFileException e) {
            System.out.println("Error: El archivo no existe - " + rutaArchivo);
        }
        catch(IOException e) {
            System.out.println("Ha habido un error al leer el archivo... " + e.getMessage());
        }
    }

    /*
        *Permite obtener la ultima cadena (lexema) procesada durante la lectura del archivo.
        *
        *@param no aplica
        *@return devuelve el valor actual de la cadena almacenada
     */
    public String getCadena() {
        return cadena;
    }

    /*
        *Permite asignar o actualizar el valor de la cadena (lexema) almacenada en el objeto.
        *
        *@param cadena: Nueva cadena que se desea guardar
        *@return actualiza la cadena con el valor recibido
     */
    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    //Entre las opciones a considerar
    //La primera linea debe de si o si ser definicion de variable
    //El orden de definicion de variable debe de ser Reservada, Identificador, Operador, Valor, ; todos separados por espacio
    //El programa no soporta comentarios, por lo que si se encuentra con un // o /* se detiene la lectura del archivo
    //Cada linea debe de terminar con un ;, de lo contrario se marca error
    //El programa soporta operaciones, con la gramatica que definimos en el lab 1
    
    //Validación recursiva de expresiones según la gramática:
    //E -> N ((+|-) N)*
    //N -> F ((*|/) F)*
    //F -> (E) | num | id

    /*
        *Permite validar una expresion aritmetica completa utilizando un parser recursivo basado en la gramatica:
        *
        *@param tipos: Lista con los tipos de token de la expresion
        *@param vals: Lista con los lexemas encontrados en la expresion
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param token: Objeto Token utilizado para reportar errores gramaticales
        *@param linea: Numero de linea donde se encontro la expresion
        *@return devuelve true si la expresion es valida, de lo contrario devuelve false y reporta el error
     */
    private boolean validarExpresion(java.util.List<String> tipos, java.util.List<String> vals, java.util.List<Integer> cols, Token token, int linea){
        int[] p = new int[]{0};
        boolean ok = parseE(tipos, vals, cols, p);
        if(!ok){
            int col = (p[0] < cols.size()) ? cols.get(p[0]) : (cols.isEmpty() ? 1 : cols.get(cols.size()-1));
            token.reportarErrorGramatica("Expresión inválida", linea, col);
            return false;
        }
        if(p[0] != tipos.size()){
            int col = (p[0] < cols.size()) ? cols.get(p[0]) : cols.get(cols.size()-1);
            token.reportarErrorGramatica("Tokens extra después de la expresión", linea, col);
            return false;
        }
        return true;
    }

    /*
        *Permite analizar la produccion principal E para sumas y restas:
        *E -> N ((+|-) N)*
        *
        *@param tipos: Lista con los tipos de token de la expresion
        *@param vals: Lista con los lexemas encontrados en la expresion
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param p: Se ocupa como puntero para la posicion actual del token
        *@return devuelve true si se reconoce correctamente la produccion E, de lo contrario devuelve false
     */
    private boolean parseE(java.util.List<String> tipos, java.util.List<String> vals, java.util.List<Integer> cols, int[] p){
        if(!parseN(tipos, vals, cols, p)) return false;
        while(p[0] < tipos.size() && "operador".equals(tipos.get(p[0])) && ("+".equals(vals.get(p[0])) || "-".equals(vals.get(p[0])))){
            p[0]++;
            if(!parseN(tipos, vals, cols, p)) return false;
        }
        return true;
    }

    /*
        *Permite analizar la produccion N para multiplicacion y division:
        *N -> F ((*|/) F)*
        *
        *@param tipos: Lista con los tipos de token de la expresion
        *@param vals: Lista con los lexemas encontrados en la expresion
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param p: Puntero que indica la posicion actual del token
        *@return devuelve true si se reconoce correctamente la produccion N, de lo contrario devuelve false
     */
    private boolean parseN(java.util.List<String> tipos, java.util.List<String> vals, java.util.List<Integer> cols, int[] p){
        if(!parseF(tipos, vals, cols, p)) return false;
        while(p[0] < tipos.size() && "operador".equals(tipos.get(p[0])) && ("*".equals(vals.get(p[0])) || "/".equals(vals.get(p[0])))){
            p[0]++;
            if(!parseF(tipos, vals, cols, p)) return false;
        }
        return true;
    }

    /*
        *Permite analizar la produccion F para una expresion aritmetica:
        *F -> (E) | num | id
        *
        *@param tipos: Lista con los tipos de token de la expresion
        *@param vals: Lista con los lexemas encontrados en la expresion
        *@param cols: Lista con las columnas donde se encontro cada token
        *@param p: Puntero que indica la posicion actual del token 
        *@return devuelve true si el factor es valido, de lo contrario devuelve false
     */
    private boolean parseF(java.util.List<String> tipos, java.util.List<String> vals, java.util.List<Integer> cols, int[] p){
        if(p[0] >= tipos.size()) return false;
        String t = tipos.get(p[0]);
        if("lparen".equals(t)){
            p[0]++;
            if(!parseE(tipos, vals, cols, p)) return false;
            if(p[0] >= tipos.size() || !"rparen".equals(tipos.get(p[0]))) return false;
            p[0]++;
            return true;
        }
        if("num".equals(t) || "id".equals(t)){
            p[0]++;
            return true;
        }
        return false;
    }
}
