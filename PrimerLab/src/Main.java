/*
    @author: Sophia Corea
    @author: Javier Monje
 */

public class Main {

    public static void main(String[] args) throws Exception {
        Token token = new Token();
        App app = new App();
        
        System.out.println("Bienvenido al analizador lexico");
        token.mostrarTokens();
        app.iniciar();
        

    }
}
