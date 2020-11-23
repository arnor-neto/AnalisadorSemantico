package analisadorlexico;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class AnalisadorSintatico {

    public ArrayList<Token> tokens;
    public int index;

    public ArrayDeque<String> pilhaIdentificadores;

    public ArrayList<String> tokenBuffer;
    public ArrayDeque<IdentificadorTipado> idsTipados;

    public ArrayDeque<String> pilhaControleTipo;

    public void AnalisadorSintatico(String path) throws FileNotFoundException, IOException {

        AnalisadorLexico AL = new AnalisadorLexico();

        //Inicializar pilha dos identificadores
        pilhaIdentificadores = new ArrayDeque<>();

        tokenBuffer = new ArrayList<>();
        idsTipados = new ArrayDeque<>();

        pilhaControleTipo = new ArrayDeque<>();

        tokens = AL.executar(path);

        if (AL.erroInvalido || AL.erroComentario) {
            System.out.println("Houve erro léxico! Análise sintática não será efetuada.");
            System.exit(0);
        }

        index = 0;
        
        try{
            programa();
        }catch(IndexOutOfBoundsException e){
            System.out.println("Estrutura do programa não foi devidamente construída.");
        }
    }

    public void programa() {
        if (tokens.get(index).lexema.equals("program")) {
            pilhaIdentificadores.push("$");
            idsTipados.push(new IdentificadorTipado("$", "mark"));

            index++;

            if (tokens.get(index).tipo.equals("Identificador")) {
                    pilhaIdentificadores.push(tokens.get(index).lexema);

                index++;

                if (tokens.get(index).lexema.equals(";")) {
                    index++;
                    
                    decs_var();
                    decs_subp();
                    comando_composto();

                    if (!tokens.get(index).lexema.equals(".")) {
                        System.out.println("Estava esperando . e recebeu " + tokens.get(index).lexema);
                    }

                } else {
                    System.out.println("Estava esperando ; e recebeu " + tokens.get(index).lexema);
                }

            } else {
                System.out.println("Estava esperando identificador e recebeu " + tokens.get(index).tipo);
            }

        } else {
            System.out.println("Estava esperando program e recebeu " + tokens.get(index).lexema);
        }
    }

    public void decs_var() {
        if (tokens.get(index).lexema.equals("var")) {
            index++;

            lista_dec_var();
        }
    }

    public void lista_dec_var() {

        lista_de_identificadores();

        if (tokens.get(index).lexema.equals(":")) {
            index++;

            String tipo = tipo();

            if (tokens.get(index).lexema.equals(";")) {

                for (String identificador : tokenBuffer){
                    idsTipados.push(new IdentificadorTipado(identificador, tipo));
                }
                tokenBuffer.clear();

                index++;

                lista_dec_var2();

            } else {
                System.out.println("Estava esperando ; e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }
        } else {
            System.out.println("Estava esperando : e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }

    }

    public void lista_dec_var2() {
        if (tokens.get(index).tipo.equals("Identificador")) {
            lista_de_identificadores();

            if (tokens.get(index).lexema.equals(":")) {
                index++;

                String tipo = tipo();

                if (tokens.get(index).lexema.equals(";")) {

                    for (String identificador : tokenBuffer){
                        idsTipados.push(new IdentificadorTipado(identificador, tipo));
                    }
                    tokenBuffer.clear();

                    index++;

                    lista_dec_var2();

                } else {
                    System.out.println("Estava esperando ; e recebeu " + tokens.get(index).lexema);
                    System.exit(0);
                }
            } else {
                System.out.println("Estava esperando : e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }

        }
    }

    public void lista_de_identificadores() {
        if (tokens.get(index).tipo.equals("Identificador")) {

            boolean declarou = false;
            for(String identificador : pilhaIdentificadores){
                if(!identificador.equals("$")){
                    if(identificador.equals(tokens.get(index).lexema)){
                       declarou = true;
                       break;
                    }
                }else{
                    break;
                }
            }
            if(declarou){
                System.out.println("Erro de escopo: variável " + tokens.get(index).lexema + " já declarada.");
                System.exit(0);
            }else{
                pilhaIdentificadores.push(tokens.get(index).lexema);
                tokenBuffer.add(tokens.get(index).lexema);
            }

            index++;

            lista_de_identificadores2();
        } else {
            System.out.println("Estava esperando Identificador e recebeu " + tokens.get(index).tipo);
            System.exit(0);
        }

    }

    public void lista_de_identificadores2() {
        if (tokens.get(index).lexema.equals(",")) {
            index++;

            if (tokens.get(index).tipo.equals("Identificador")) {

                boolean declarou = false;
                for(String identificador : pilhaIdentificadores){
                    if(!identificador.equals("$")){
                        if(identificador.equals(tokens.get(index).lexema)){
                            declarou = true;
                            break;
                        }
                    }else{
                        break;
                    }
                }
                if(declarou){
                    System.out.println("Erro de escopo: variável " + tokens.get(index).lexema + " já declarada.");
                    System.exit(0);
                }else{
                    pilhaIdentificadores.push(tokens.get(index).lexema);
                    tokenBuffer.add(tokens.get(index).lexema);
                }

                index++;

                lista_de_identificadores2();
            } else {
                System.out.println("Estava esperando Identificador e recebeu " + tokens.get(index).tipo);
                System.exit(0);
            }
        }
    }

    public String tipo() {
        if (!(tokens.get(index).lexema.equals("integer") || tokens.get(index).lexema.equals("real") || tokens.get(index).lexema.equals("boolean"))) {
            System.out.println("Estava esperando Tipo e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        } else {
            index++;
            return tokens.get(index - 1).lexema;
        }
        return "";
    }

    public void decs_subp() {
        if (tokens.get(index).lexema.equals("procedure")) {
            dec_subp();

            if (tokens.get(index).lexema.equals(";")) {

                for(String identificador : pilhaIdentificadores){
                    if(!identificador.equals("$")){
                        pilhaIdentificadores.pop();
                    }else{
                        pilhaIdentificadores.pop();
                        break;
                    }
                }

                for(IdentificadorTipado identificador : idsTipados){
                    if(!identificador.identificador.equals("$")){
                        idsTipados.pop();
                    }else{
                        idsTipados.pop();
                        break;
                    }
                }

                index++;

                decs_subp();

            } else {
                System.out.println("Estava esperando ; e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }
        }
    }

    public void dec_subp() {
        if (tokens.get(index).lexema.equals("procedure")) {
            index++;

            if (tokens.get(index).tipo.equals("Identificador")) {

                boolean declarou = false;
                for(String identificador : pilhaIdentificadores){
                    if(!identificador.equals("$")){
                        if(identificador.equals(tokens.get(index).lexema)){
                            declarou = true;
                            break;
                        }
                    }else{
                        break;
                    }
                }
                if(declarou){
                    System.out.println("Erro de escopo: variável " + tokens.get(index).lexema + " já declarada.");
                    System.exit(0);
                }else{
                    pilhaIdentificadores.push(tokens.get(index).lexema);
                    pilhaIdentificadores.push("$");

                    idsTipados.push(new IdentificadorTipado("$", "mark"));
                }

                index++;

                argumentos();

                if (tokens.get(index).lexema.equals(";")) {
                    index++;

                    decs_var();
                    decs_subp();
                    comando_composto();

                } else {
                    System.out.println("Estava esperando ; e recebeu " + tokens.get(index).lexema);
                    System.exit(0);
                }

            } else {
                System.out.println("Estava esperando identificador e recebeu " + tokens.get(index).tipo);
                System.exit(0);
            }
        } else {
            System.out.println("Estava esperando procedure e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
    }

    public void argumentos() {
        if (tokens.get(index).lexema.equals("(")) {
            index++;

            lista_de_parametros();

            if (tokens.get(index).lexema.equals(")")) {
                index++;

            } else {
                System.out.println("Estava esperando ) e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }
        }
    }

    public void lista_de_parametros() {
        lista_de_identificadores();

        if (tokens.get(index).lexema.equals(":")) {
            index++;

            String tipo = tipo();

            for (String identificador : tokenBuffer){
                idsTipados.push(new IdentificadorTipado(identificador, tipo));
            }
            tokenBuffer.clear();

            lista_de_parametros2();
        } else {
            System.out.println("Estava esperando : e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
    }

    public void lista_de_parametros2() {
        if (tokens.get(index).lexema.equals(";")) {
            index++;

            lista_de_identificadores();
            if (tokens.get(index).lexema.equals(":")) {
                index++;

                String tipo = tipo();

                for (String identificador : tokenBuffer){
                    idsTipados.push(new IdentificadorTipado(identificador, tipo));
                }
                tokenBuffer.clear();

                lista_de_parametros2();
            } else {
                System.out.println("Estava esperando : e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }
        }
    }

    public void comando_composto() {
        if (tokens.get(index).lexema.equals("begin")) {
            index++;

            comandos_opcionais();
            if (!(tokens.get(index).lexema.equals("end"))) {
                System.out.println("Estava esperando end e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            } else {
                index++;

            }
        } else {
            System.out.println("Estava esperando begin e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
    }

    public void comandos_opcionais() {
        if (tokens.get(index).tipo.equals("Identificador") || tokens.get(index).lexema.equals("begin") || tokens.get(index).lexema.equals("if") || tokens.get(index).lexema.equals("while") || tokens.get(index).lexema.equals("for")) {
            lista_de_comandos();
        }
    }

    public void lista_de_comandos() {
        comando();
        lista_de_comandos2();
    }

    public void lista_de_comandos2() {
        if (tokens.get(index).lexema.equals(";")) {
            index++;

            comando();
            lista_de_comandos2();
        }
    }

    public void comando() {

        if (tokens.get(index).tipo.equals("Identificador") && tokens.get(index + 1).tipo.equals("Operador de Atribuição")) {

            variavel();

            if (tokens.get(index).tipo.equals("Operador de Atribuição")) {
                index++;

                expressao();
            }

            verificacaoAtribuicao();

        } else if (tokens.get(index).tipo.equals("Identificador")) {
            ativacao_de_procedimento();
        } else if (tokens.get(index).lexema.equals("begin")) {
            comando_composto();
        } else if (tokens.get(index).lexema.equals("if")) {
            index++;

            expressao();

            if (tokens.get(index).lexema.equals("then")) {
                index++;

                comando();
                parte_else();

            } else {
                System.out.println("Estava esperando then e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }
        } else if (tokens.get(index).lexema.equals("while")) {
            index++;

            expressao();

            if (tokens.get(index).lexema.equals("do")) {
                index++;

                comando();
            } else {
                System.out.println("Estava esperando do e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }

        } else if (tokens.get(index).lexema.equals("for")) {
            index++;

            variavel();

            if (tokens.get(index).tipo.equals("Operador de Atribuição")) {
                index++;

                expressao();

                verificacaoAtribuicao();

                if (tokens.get(index).lexema.equals("to")) {
                    index++;

                    expressao();

                    if (tokens.get(index).lexema.equals("do")) {
                        index++;

                        comando_composto();

                    } else {
                        System.out.println("Estava esperando do e recebeu " + tokens.get(index).lexema);
                        System.exit(0);
                    }

                } else {
                    System.out.println("Estava esperando to e recebeu " + tokens.get(index).lexema);
                    System.exit(0);
                }

            } else {
                System.out.println("Estava esperando := e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }

        } else {
            //erro comando
            System.out.println("Estava esperando comando e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
    }

    public void parte_else() {
        if (tokens.get(index).lexema.equals("else")) {
            index++;

            comando();
        }
        // se nao for else, não faz nada porque é vazio
    }

    public void variavel() {
        if (tokens.get(index).tipo.equals("Identificador")) {

            if(!pilhaIdentificadores.contains(tokens.get(index).lexema)){
                System.out.println("O identificador " + tokens.get(index).lexema + " não foi declarado anteriormente.");
                System.exit(0);
            }

            for(IdentificadorTipado identificador : idsTipados) {
                if(identificador.identificador.equals(tokens.get(index).lexema)){
                    pilhaControleTipo.push(identificador.tipo);
                    break;
                }
            }

            index++;

        } else {
            System.out.println("Estava esperando variável e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
    }

    public void ativacao_de_procedimento() {
        if (tokens.get(index).tipo.equals("Identificador")) {

            if(!pilhaIdentificadores.contains(tokens.get(index).lexema)){
                System.out.println("O identificador " + tokens.get(index).lexema + " não foi declarado anteriormente.");
                System.exit(0);
            }

            //Tentar colocar tipo do indentificador na PcT, se for procedure, não adiciona nada.
            for(IdentificadorTipado identificador : idsTipados) {
                if(identificador.identificador.equals(tokens.get(index).lexema)){
                    pilhaControleTipo.push(identificador.tipo);
                    break;
                }
            }

            index++;

            ativacao_de_procedimento2();
        } else {
            System.out.println("Estava esperando identificador e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
    }

    public void ativacao_de_procedimento2() {
        if (tokens.get(index).lexema.equals("(")) {
            index++;

            lista_de_expressoes();
            if (tokens.get(index).lexema.equals(")")) {
                index++;

            } else {
                System.out.println("Estava esperando ) e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            }
        }
        // se o lexema for (, então consome e chama lista de exp, depois ve se é ) e consome. Se o primeiro não for ( dá erro, e se depois de lista de exp nao for ) , da erro tb
        // se não começar com (, é vazio e tá tudo bem
    }

    public void lista_de_expressoes() {
        expressao();
        lista_de_expressoes2();
    }

    public void lista_de_expressoes2() {
        if (tokens.get(index).lexema.equals(",")) {
            index++;

            expressao();
            lista_de_expressoes2();
        }
        // verifica se começa com , se sim é pq é p continuar, se não, é apenas vazio mesmo ou ja o começo de outra coisa
    }

    public void expressao() {
        expressao_simples();
        expressao2();
    }

    public void expressao2() {
        if (tokens.get(index).tipo.equals("Operador Relacional")) {
            op_relacional();
            expressao_simples();
            expressao2();

            verificacaoRelacional();
        }
    }

    public void expressao_simples() {
        if (tokens.get(index).lexema.equals("+") || tokens.get(index).lexema.equals("-")) {
            index++;

            termo();
            expressao_simples2();

        } else {
            termo();
            expressao_simples2();
        }
    }

    public void expressao_simples2() {
        if (tokens.get(index).tipo.equals("Operador Aditivo")) {

            if(tokens.get(index).lexema.equals("+") || tokens.get(index).lexema.equals("-")) {

                op_aditivo();
                termo();
                expressao_simples2();

                verificacaoAritmetica();
            }else if(tokens.get(index).lexema.equals("or")){

                op_aditivo();
                termo();
                expressao_simples2();

                verificacaoLogica();
            }
        }
        //else, nada acontece
    }

    public void termo() {
        fator();
        termo2();
    }

    public void termo2() {
        if (tokens.get(index).tipo.equals("Operador Multiplicativo")) {

            if(tokens.get(index).lexema.equals("/") || tokens.get(index).lexema.equals("*")) {
                op_multiplicativo();

                fator();
                termo2();

                verificacaoAritmetica();
            }else if(tokens.get(index).lexema.equals("and")){

                op_multiplicativo();

                fator();
                termo2();

                verificacaoLogica();
            }
        }
        // se não começar com operador multiplicativo, é vazio
    }

    public void fator() {
        if (tokens.get(index).tipo.equals("Identificador")) {
            ativacao_de_procedimento();
        } else if (tokens.get(index).tipo.equals("Número Inteiro")) {
            index++;
            pilhaControleTipo.push("integer");

        } else if (tokens.get(index).tipo.equals("Número Real")) {
            index++;
            pilhaControleTipo.push("real");

        } else if (tokens.get(index).lexema.equals("true") || tokens.get(index).lexema.equals("false")) {
            index++;
            pilhaControleTipo.push("boolean");
        } else if (tokens.get(index).lexema.equals("(")) {
            index++;

            expressao();

            if (!tokens.get(index).lexema.equals(")")) {
                System.out.println("Estava esperando ) e recebeu " + tokens.get(index).lexema);
                System.exit(0);
            } else {
                index++;

            }
        } else if (tokens.get(index).lexema.equals("not")) {
            pilhaControleTipo.push("boolean");
            index++;

            fator();

            verificacaoLogica();
            
        } else {
            System.out.println("Estava esperando fator e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
    }

    public void op_relacional() {
        if (tokens.get(index).tipo.equals("Operador Relacional")) {
            index++;

        } else {index++;
            System.out.println("Estava esperando operador relacional e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
        // dá erro pq ele realmente espera um operador, então se não tiver, tá errado
    }

    public void op_aditivo() {
        if (tokens.get(index).tipo.equals("Operador Aditivo")) {
            index++;

        } else {
            System.out.println("Estava esperando operador aditivo e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
        // dá erro pq ele realmente espera um operador, então se não tiver, tá errado
    }

    public void op_multiplicativo() {
        if (tokens.get(index).tipo.equals("Operador Multiplicativo")) {
            index++;

        } else {
            System.out.println("Estava esperando operador multiplicativo e recebeu " + tokens.get(index).lexema);
            System.exit(0);
        }
        // dá erro pq ele realmente espera um operador, então se não tiver, tá errado
    }

    //
    //Métodos da Pilha de Controle de Tipo
    //

    public void verificacaoAritmetica(){
        String top = pilhaControleTipo.pop();
        String subtop = pilhaControleTipo.pop();

        if (top.equals("integer") && subtop.equals("integer")){
            pilhaControleTipo.push("integer");
        }else if (top.equals("real") && subtop.equals("real")){
            pilhaControleTipo.push("real");
        } else if (top.equals("integer") && subtop.equals("real")){
            pilhaControleTipo.push("real");
        }else if (top.equals("real") && subtop.equals("integer")){
            pilhaControleTipo.push("real");
        }else{
            if (tokens.get(index).lexema.equals("end")){
                System.out.println("Erro: tipos incompatíveis em operação aritmética na linha " + tokens.get(index - 1).linha);
            }else {
                System.out.println("Erro: tipos incompatíveis em operação aritmética na linha " + tokens.get(index).linha);
            }
            System.exit(0);
        }
    }

    public void verificacaoRelacional(){
        String top = pilhaControleTipo.pop();
        String subtop = pilhaControleTipo.pop();

        if (top.equals("integer") && subtop.equals("integer")){
            pilhaControleTipo.push("boolean");
        }else if (top.equals("real") && subtop.equals("real")){
            pilhaControleTipo.push("boolean");
        } else if (top.equals("integer") && subtop.equals("real")){
            pilhaControleTipo.push("boolean");
        }else if (top.equals("real") && subtop.equals("integer")){
            pilhaControleTipo.push("boolean");
        }else{
            if (tokens.get(index).lexema.equals("end")){
                System.out.println("Erro: tipos incompatíveis em operação relacional na linha " + tokens.get(index - 1).linha);
            }else {
                System.out.println("Erro: tipos incompatíveis em operação relacional na linha " + tokens.get(index).linha);
            }
            System.exit(0);
        }
    }

    public void verificacaoLogica(){
        String top = pilhaControleTipo.pop();
        String subtop = pilhaControleTipo.pop();

        if(top.equals(subtop)){

            pilhaControleTipo.push("boolean");
        }else{
            if (tokens.get(index).lexema.equals("end")){
                System.out.println("Erro: tipos incompatíveis em operação lógica na linha " + tokens.get(index - 1).linha);
            }else {
                System.out.println("Erro: tipos incompatíveis em operação lógica na linha " + tokens.get(index).linha);
            }
            System.exit(0);
        }
    }

    public void verificacaoAtribuicao(){
        String top = pilhaControleTipo.pop();

        if(pilhaControleTipo.getFirst().equals("real") && top.equals("integer")) {
            pilhaControleTipo.pop();
        }else if(pilhaControleTipo.getFirst().equals(top)){
            pilhaControleTipo.pop();
        }else{
            if (tokens.get(index).lexema.equals("end")){
                System.out.println("Erro: tipos incompatíveis em atribuição na linha " + tokens.get(index - 1).linha);
            }else{
                System.out.println("Erro: tipos incompatíveis em atribuição na linha " + tokens.get(index).lexema);
            }
            System.exit(0);
        }
    }
}
