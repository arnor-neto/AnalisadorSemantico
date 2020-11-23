package analisadorlexico;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    
    public static void main(String args[])throws FileNotFoundException, IOException{
        AnalisadorSintatico AS = new AnalisadorSintatico();
        AS.AnalisadorSintatico("benchmark-arquivos_testes\\Test4.pas");
    }
}
