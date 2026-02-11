# LABORATORIO 1
Universidad Rafael Landivar
Compiladores
Diana Gutierrez

## Grupo

Sophia Corea - 1185324
Javier Monje - 1260524

## Definicion de gramatica y ER

Este apartado define la gramatica con expresiones regulares, simbolos terminales y no terminales.
S ➝ (B | O | H)
Se define la forma en la que las declaraciones de variables deben de estar escritas ya sea en bin, oct o hex.
Las expresiones regulares dicen cuantos o cuales digitos deben de ser aceptados

B ➝ Bin id = numbin\s;\n
O ➝ Oct id = numoct\s;\n
H ➝ Hex id = numhex\s;\n
numbin = (0|1)
+
numoct = [0-7]
+
numhex = [0-9|A-F]
+

Forma en que las operaciones deben de estar escritas
E ➝ E + N | E – N | N
N ➝ N * F | N / F | F
F ➝ (E) | (numbin | numoct | numhex | id)
id ➝ [A-Za-z]\s;\s
+

Para la gramatica de la escritura nos basamos en lo siguiente

I ➝ S(S|O)*

Donde se fuerza que le primer linea debe de ser una declaracion de variable

Un ejemplo de nuestra gramática sería

bin
##  Manejo de errores

Hay dos tipos principales de errores, los tokens desconocidos y los errores cuando no se sigue la gramática.
Todos los tokesns son guardados en un HashMap, para almacenar su tipo y su "nombre", en el momento que un signo no encuentra una definicion se almacena como "desconocido" y tira un errorA

Para la gramatica en "AnalizadorLexico" hay parse para cada expresion en la gramtica, por ejemplo en las operaciones se va comparando a la gramatica realizada y detecta si hay tokens extras o si falta algun token. O si las lineas no terminaron en punto y coma, tambien se detectan.

En el metodo "ArchivoTexto" mientras se analiza el texto se cuenta linea y columna, para precision en el error
Las pruebas son:
test.txt
test2.txt
test3.txt
