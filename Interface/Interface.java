package Interface;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import Props.Nome;
import BancoDeDados.*;
import Utils.*;

//Classe para uma interface simples de criação e leitura de items na estrutura da lista invertida.
public class Interface {
    private Lista lista;
    private CRUD<Nome> banco;

    public Interface(){
        try{
            //Criar uma nova lista invertida
            lista = new Lista("Lista invertida");
            //Criar um novo CRUD
            banco = new CRUD<>("CRUD", Nome.class.getConstructor());
        } catch(Exception e) {
            System.out.println("Erro ao criar interface, por favor tente novamente.");
            System.out.print("Motivo: ");
            e.printStackTrace();
        }//end of try
    }//end of Interface constructor

    //Menu principal de operações do usuario:
    public void menu(){
        Scanner entrada = new Scanner(System.in);
        boolean end = false;
        while(!end){
            Utils.limparTela();
            //Dados na tela:
            System.out.println("Lista Invertida V1.0");
            System.out.println("========================\n");
            System.out.println("Bem-vindo!\n");
            System.out.println("1) Inserir um nome na estrutura");
            System.out.println("2) Pesquisar por termos");
            System.out.println("3) Informações sobre o projeto e o criador\n");
            System.out.println("0) Sair\n");
            System.out.print("Digite a operação a ser efetuada: ");
            //Obter a opção desejada do usuário:
            int opcao = entrada.nextInt();
            entrada.nextLine();
            switch(opcao){
                //Encerrar o programa:
                case 0:
                    end = true;
                    break;
                //Inserir um novo nome na estrutura:
                case 1:
                    inserir(entrada);
                    break;
                //Pesquisar por um nome na estrutura:
                case 2:
                    pesquisar(entrada);
                    break;
                //Mostrar todas as informações do programa:
                case 3:
                    informacoes(entrada);
                    break;
                //Opão inválida:
                default:
                    System.out.println("Infelizmente a opção digitada não é válida, tente novamente.");
                    Utils.travarTela(entrada);
                    break;
            }//end of switch
        }//end of while
        entrada.close();
    }//end of void menu

    //Método para a inserção de nomes:
    private void inserir(Scanner entrada){
        try{
            Utils.limparTela();
            System.out.println("Seja bem-vindo à interface de inserções na estrutura!\n");
            boolean end = false;
            //Loop de inserção:
            while(!end){
                Utils.limparTela();
                if(Utils.askQuestion("Deseja inserir um novo nome na estrutura? ", entrada)){
                    //Obter o nome a ser inserido:
                    System.out.print("Digite o nome a ser inserido na estrutura: ");
                    String nome = entrada.nextLine();
                    //Verificar sua válidade:
                    if(nome.trim().isEmpty())
                        throw new Exception("Nome inválido.");
                    
                    //Criar uma nova entidade:
                    Nome inserir = new Nome(-1,nome);
                    //Inserir a entidade no banco de dados:
                    int id = banco.create(inserir);
                    //Inserir o nome e o id na lista invertida:
                    lista.create(id, nome);
                } else {
                    end = true;
                    Utils.travarTela(entrada);
                }//end of if
            }//end of while
        } catch(Exception e){
            System.out.println("Erro ao inserir um novo nome na estrutura.");
            System.out.println("Motivo: ");
            e.printStackTrace();
            Utils.travarTela(entrada);
        }//end of try
    }//end of inserir

    //Método para a pesquisa de termos:
    private void pesquisar(Scanner entrada){
        try{
            //Criar um novo vetor adicionando os termos que o usuário deseja buscar:
            ArrayList<String> termos = new ArrayList<>();
            boolean end = false;
            //Loop de adição de termos:
            while(!end){
                if(Utils.askQuestion("Deseja adicionar mais um termo a busca? ", entrada)){
                    //Obter o nome a ser inserido:
                    System.out.print("Digite o termo a ser inserido: ");
                    String adicionar = entrada.nextLine();
                    //Verificar sua validade:
                    if(adicionar.trim().isEmpty())
                        throw new Exception("Termo inválido.");
                    
                    //Adicionar ao vetor:
                    termos.add(adicionar);
                } else {
                    end = true;
                    Utils.travarTela(entrada);
                }//end of if
            }//end of while
            //Verificar se existe algum termo para a busca:
            if(termos.size() <= 0)
                throw new Exception("Nenhum termo para a busca.");

            //Arranjo para a inserção:
            String[] ler = new String[termos.size()];
            //Inserir os termos do vetor no arranjo:
            ler = termos.toArray(ler);
            //Chamar o método de leitura e receber todos os ids da estrutura:
            int[] ids = lista.read(ler);
            for(int i = 0; i < ids.length; i ++){
                //Ler todos os ids dentro do banco de dados:
                System.out.println((i+1) + ". " + banco.read(ids[i]));
            }//end of for
            Utils.travarTela(entrada);
        } catch(Exception e) {
            System.out.println("Erro ao pesquisar um elemento na estrutura.");
            System.out.print("Motivo: ");
            e.printStackTrace();
            Utils.travarTela(entrada);
        }//end of try
    }//end of pesquisar

    //Apenas alguns comentários sobre a estrutura e o seu criador:
    private void informacoes(Scanner entrada){
        try{
            Utils.limparTela();
            //Obter todos os dados dentro do arquivo readme.txt
            Scanner arquivo = new Scanner(new FileReader("./readme.txt")).useDelimiter("\\n");
            while(arquivo.hasNext()){
                System.out.println( arquivo.next() );
            }//end of while
            System.out.println("\n");
            arquivo.close();
            Utils.travarTela(entrada);
        } catch(Exception e) {
            System.out.println("Erro ao mostrar informações do programa. ");
            System.out.print("Motivo: ");
            e.printStackTrace();
        }//end of try
    }//end of informacoes
}//end of classe Interface