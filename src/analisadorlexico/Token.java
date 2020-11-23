package analisadorlexico;

public class Token {

    String lexema;
    String tipo;
    int linha;
    
    public Token(String lexema, String tipo, int linha){
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
    }

}
