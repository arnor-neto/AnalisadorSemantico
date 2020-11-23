package analisadorlexico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AnalisadorLexico {

    public static ArrayList<Token> tokens;
    public boolean erroInvalido;
    public boolean erroComentario;
    

    public ArrayList executar(String path) throws FileNotFoundException, IOException {

        tokens = new ArrayList<>();
        erroInvalido = false;
        String msgErro = "";

        erroComentario = false;

        //Conjunto de palavras reservadas
        ArrayList palavrasReservadas = new ArrayList<String>();
        palavrasReservadas.add("program");
        palavrasReservadas.add("var");
        palavrasReservadas.add("integer");
        palavrasReservadas.add("real");
        palavrasReservadas.add("boolean");
        palavrasReservadas.add("procedure");
        palavrasReservadas.add("begin");
        palavrasReservadas.add("end");
        palavrasReservadas.add("if");
        palavrasReservadas.add("then");
        palavrasReservadas.add("else");
        palavrasReservadas.add("while");
        palavrasReservadas.add("do");
        palavrasReservadas.add("not");
        palavrasReservadas.add("for");
        palavrasReservadas.add("to");

        File file = new File(path);
        
        int numLinha = 0;

        BufferedReader in = new BufferedReader(new FileReader(file));
        
        File saida = new File("saida.txt");
        saida.createNewFile();

        while (in.ready() && !erroInvalido) {
            //loop do programa inteiro
            String linha = in.readLine();
            numLinha++;

            for (int i = 0; i < linha.length(); i++) {
                //loop de cada linha do programa

                if (erroComentario) {

                    if (linha.charAt(i) == '}') {
                        erroComentario = false;
                    }

                } else if (linha.charAt(i) == '{') {
                    erroComentario = true;
                } else if (Character.isLetter(linha.charAt(i))) {
                    String lexema = "";
                    while (Character.isLetter(linha.charAt(i)) || Character.isDigit(linha.charAt(i)) || linha.charAt(i) == '_') {
                        lexema += linha.charAt(i);
                        i++;
                        if (i == linha.length()) {
                            break;
                        }
                    }

                    if (i != linha.length()) {

                        if (linha.charAt(i) == ' ' || linha.charAt(i) == '\n' || linha.charAt(i) == '\t') {
                            //caracter ignorável 
                        } else {
                            i--; //verificou que o caracter no próximo loop não é um ignorável nem letra/dígito
                        }
                    }

                    if (palavrasReservadas.contains(lexema)) {
                        tokens.add(new Token(lexema, "Palavra Reservada", numLinha));
                    } else if (lexema.equals("or")) {
                        tokens.add(new Token(lexema, "Operador Aditivo", numLinha));
                    } else if (lexema.equals("and")) {
                        tokens.add(new Token(lexema, "Operador Multiplicativo", numLinha));
                    }else if (lexema.equals("true") || lexema.equals("false")) {
                        tokens.add(new Token(lexema, "Valor Booleano", numLinha));
                    } else {
                        tokens.add(new Token(lexema, "Identificador", numLinha));
                    }

                } else if (Character.isDigit(linha.charAt(i))) {

                    String lexema = "";
                    boolean primeiroPonto = true;

                    while (Character.isDigit(linha.charAt(i)) || (linha.charAt(i) == '.' && primeiroPonto)) {
                        if (linha.charAt(i) == '.') {
                            primeiroPonto = false;
                        }
                        lexema += linha.charAt(i);
                        i++;

                        if (i == linha.length()) {
                            break;
                        }
                    }

                    if (i != linha.length()) {

                        if (linha.charAt(i) == ' ' || linha.charAt(i) == '\n' || linha.charAt(i) == '\t') {
                            //caracter ignorável 
                        } else {
                            i--; //verificou que o caracter no próximo loop não é um ignorável nem letra/dígito
                        }
                    }

                    if (primeiroPonto) {
                        tokens.add(new Token(lexema, "Número Inteiro", numLinha));
                    } else {
                        tokens.add(new Token(lexema, "Número Real", numLinha));
                    }

                } else if (linha.charAt(i) == ';' || linha.charAt(i) == '.' || linha.charAt(i) == ',' || linha.charAt(i) == '(' || linha.charAt(i) == ')') {
                    String lexema = "";
                    lexema += linha.charAt(i);
                    tokens.add(new Token(lexema, "Delimitador", numLinha));
                    
                } else if (linha.charAt(i) == ':') {

                    String lexema = "";
                    lexema += linha.charAt(i);

                    if (linha.charAt(i + 1) == '=') {
                        i++;
                        lexema += linha.charAt(i);
                        tokens.add(new Token(lexema, "Operador de Atribuição", numLinha));
                    } else {
                        tokens.add(new Token(lexema, "Delimitador", numLinha));
                    }
                    
                } else if (linha.charAt(i) == '=') {
                    String lexema = "";
                    lexema += linha.charAt(i);
                    tokens.add(new Token(lexema, "Operador Relacional", numLinha));
                    
                } else if (linha.charAt(i) == '<') {
                    String lexema = "";
                    lexema += linha.charAt(i);

                    if (linha.charAt(i + 1) == '=') {
                        i++;
                        lexema += linha.charAt(i);
                        tokens.add(new Token(lexema, "Operador Relacional", numLinha));
                    } else if (linha.charAt(i + 1) == '>') {
                        i++;
                        lexema += linha.charAt(i);
                        tokens.add(new Token(lexema, "Operador Relacional", numLinha));
                    } else {
                        tokens.add(new Token(lexema, "Operador Relacional", numLinha));
                    }
                    
                } else if (linha.charAt(i) == '>') {
                    String lexema = "";
                    lexema += linha.charAt(i);

                    if (linha.charAt(i + 1) == '=') {
                        i++;
                        lexema += linha.charAt(i);
                        tokens.add(new Token(lexema, "Operador Relacional", numLinha));
                    } else {
                        tokens.add(new Token(lexema, "Operador Relacional", numLinha));
                    }
                    
                } else if (linha.charAt(i) == '+' || linha.charAt(i) == '-') {
                    String lexema = "";
                    lexema += linha.charAt(i);
                    tokens.add(new Token(lexema, "Operador Aditivo", numLinha));
                    
                } else if (linha.charAt(i) == '*' || linha.charAt(i) == '/') {
                    String lexema = "";
                    lexema += linha.charAt(i);
                    tokens.add(new Token(lexema, "Operador Multiplicativo", numLinha));
                    
                } else if (linha.charAt(i) == ' ' || linha.charAt(i) == '\n' || linha.charAt(i) == '\t') {
                    //caracter ignorável: Não faz nada
                } else {
                    msgErro = "Erro encontrado: Caracter inválido '" + linha.charAt(i) + "', na linha " + numLinha + ".";
                    erroInvalido = true; //dispara o booleano de erro para caracter inválido
                    break; //termina o loop da linha
                }
            }
        }

        FileWriter fw = new FileWriter("saida.txt");
        
        fw.write(String.format("%-12s| %-23s | %-3s \n", "Token", "Tipo", "Linha"));
        fw.write(String.format("----------------------------------------------\n"));
        
        for (Token t : tokens) {
            fw.write(String.format("%-12s| %-23s | %-3d \n", t.lexema, t.tipo, t.linha));
        }
        if (erroInvalido) {
            fw.write(msgErro + "\n");
            System.out.print(msgErro + "\n");
        }
        if (erroComentario) {
            fw.write("Erro encontrado: Comentário não fechado. \n");
            System.out.print("Erro encontrado: Comentário não fechado. \n");
        }
        fw.close();
        
        return tokens;
    }
}
