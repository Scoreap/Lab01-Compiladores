import java.util.Scanner;

public class App {
    Scanner scanner = new Scanner(System.in);
    public App() {
    }

    public void iniciar(){
        programa(false);
    }

    public void programa(boolean Salir){
        while(!Salir){
            try{
                imprimirMenu();
                int opcion = obtenerOpcionValida();
                switch(opcion){
                    case 1:
                        System.out.println("Ingrese la ruta del archivo a analizar:");
                        String rutaArchivo = scanner.nextLine().trim();
                        AnalizadorLexico analizador = new AnalizadorLexico(rutaArchivo);
                        programaSubMenu(false, analizador, rutaArchivo);
                        break;
                    case 2:
                        Salir = true;
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción no válida, por favor intente de nuevo.");
                }
            } catch (Exception e){
                System.out.println("Error de entrada: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    public void programaSubMenu(boolean Salir, AnalizadorLexico analizador, String rutaArchivo){
        while(!Salir){
            try{
                imprimirSubMenu();
                int opcion = obtenerOpcionValida();
                switch(opcion){
                    case 1:
                        System.out.println("Analizando...");
                        analizador.analizarArchivo(rutaArchivo);
                        break;
                    case 2:
                        System.out.println("\nMostrando tokens...");
                        analizador.verTokens();
                        break;
                    case 3:
                        Salir = true;
                        System.out.println("Volviendo al menú principal...");
                        System.out.println("Limpiando tokens");
                        
                        break;
                    default:
                        System.out.println("Opción no válida, por favor intente de nuevo.");
                }
            } catch (Exception e){
                System.out.println("Error de entrada: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }   
    
    public void imprimirMenu(){
        System.out.println("1. Analizar un archivo");
        System.out.println("2. Salir");
    }

    public void imprimirSubMenu(){
        System.out.println("1. Ver evaluación");
        System.out.println("2. Ver tokens");
        System.out.println("3. Volver al menú principal");
    }

    private int obtenerOpcionValida(){
        while(true){
            try{
                String entrada = scanner.nextLine().trim();
                if(entrada.isEmpty()){
                    System.out.println("Por favor ingrese un número válido:");
                    continue;
                }
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e){
                System.out.println("Entrada inválida. Por favor ingrese un número válido:");
            }
        }
    }
}