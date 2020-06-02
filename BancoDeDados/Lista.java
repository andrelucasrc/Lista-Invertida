package BancoDeDados;
import Utils.*;
//Gerenciador da lista invertida, gerencia os termos e os ids:
public class Lista {
    private Termos termos;
    private Identificacao lista;             //Responsável pela lista de ids.
    private final String[] stopwords = { "de","da" };   //Termos inválidos para os nomes.

    //Construtor:
    public Lista(String nomeArquivo) throws Exception{
        //Inicializa o gerenciador de termos:
        termos = new Termos(nomeArquivo);
        //Inicializa o gerenciador de ids:
        lista = new Identificacao(nomeArquivo);
    }//end of lista

    //Método para a criação de novo nome na estrutura:
    public void create(int id, String nome ) throws Exception{
        //Verificar se o nome é válido:
        if(nome.length() <= 0)
            throw new Exception("Nome inválido.");
        
        //Separar o nome em termos:
        String[] nomes = nome.split(" ");
        int quant = 0;                          //Para salvar a quantidade de stopwords no nome.
        //Loop para verificar a quantidade de palavras não válidas no nome:
        for(int i = 0; i < nomes.length; i++ ){
            //Remover os ascentos do nome e faze-los minúsculos:
            nomes[i] = Utils.formatar(nomes[i]);
            //Loop de verificação de stopword
            for(int j = 0; j < stopwords.length; j++){
                //Verificar se uma stop word
                if(stopwords[j].equals(nomes[i])){
                    //Flag para nome inválido:
                    nomes[i] = null;
                    quant++;
                    //Encerrar o loop de verificações de stopword:
                    j = stopwords.length;
                }//end of if
            }//end of for stopwords
        }//end of for nomes
        //Verificar se existe alguma stopword
        if(quant > 0){
            //Se sim remover ela do arranjo:
            String[] aux = new String[nomes.length - quant];
            int count = 0;
            //Loop para a inserção de termos válidos:
            for( int i = 0; i < nomes.length; i++){
                //Verificar se não é inválido:
                if(nomes[i] != null){
                    //Inserir dentro do arranjo auxiliar:
                    aux[count] = nomes[i];
                    //Incrementar o contador:
                    count++;
                }//end of if
            }//end of for
            //Substituir o arranjo;
            nomes = aux;
            aux = null;
        }//end of if

        //Inserir os termos dentro de cada uma das duas estruturas:
        for(int i = 0; i < nomes.length; i++){
            //Inserir dentro da estrutura de termos e obter sua posição:
            long position = termos.inserir(nomes[i], lista.getTamanho());
            //Inserir dentro da estrtura de ids dentro da posição obtida na etapa anterior:
            lista.inserir(id,position);
        }//end of for
    }//end of create

    //Método para a leitura de termos dentro da estrutura de lista invertida:
    public int[] read(String[] nomes) throws Exception{
        //Vetor de retorno:
        int[] ids = null;
        //Obter todos os endereços de acord com os termos dados:
        long[] endereco = new long[nomes.length];
        for(int i = 0; i<nomes.length; i++){
            //Formatar o termo dado para a busca na estrutura:
            nomes[i] = Utils.formatar(nomes[i]);
            //Obter o endereço do termo:
            endereco[i] = termos.ler(nomes[i]);
        }//end of for

        //Arranjo para a auxiliar na interseção de termos:
        int[] aux;

        //Ler o primeiro endereço da estrutura:
        ids = lista.ler(endereco[0]);

        //Ir lendo e juntado os termos da estrutura:
        for(int i = 1; i < endereco.length; i++){
            //Ler o nova lista de ids:
            aux = lista.ler(endereco[i]);
            //Fazer a interseção com o ids anterior anterior:
            ids = intersecao(ids, aux);
        }//end of for

        aux = null;

        return ids;
    }//end of read

    //Método para efetuar a interseção de duas listas de inteiros:
    private int[] intersecao(int[] l1, int[] l2){
        //Arranjo de retorno:
        int[] inter = null;
        //Verificar qual é o vetor menor, criar um arranjo com o tamanho do menor vetor:
        if( l1.length < l2.length ){
            inter = new int[l1.length];
        } else {
            inter = new int[l2.length];
        }//end of if
        //Contador para verificar quantos termos são iguais:
        int contador = 0;
        //Loop para a verificação da primeira lista de termos:
        for(int i = 0; i < l1.length; i++){
            //Loop para a verificação da segunda lista de termos:
            for(int j = 0; j < l2.length; j++){
                //Verificar se o item i da primeira lista é igual ao item j da segunda lista:
                if(l1[i] == l2[j]){
                    //Encerrar o loop:
                    j = l2.length;
                    //Salvar na arranjo de retorno na posição do contador:
                    inter[contador] = l1[i];
                    //Incrementar o contador:
                    contador++;
                }//end of if
            }//end of for
        }//end of for
        //Verificar se existem menos termos iguais que a menos lista:
        if(contador < inter.length){
            //Se sim salvar uma lista menor com apenas os int válidos:
            int[] aux = new int[contador];
            //Loop de inserção:
            for(int i = 0; i < contador; i++){
                aux[i] = inter[i];
            }//end of for
            //Salvar o auxiliar como retorno de endereço:
            inter = aux;
            aux = null;
        }//end of if
        return inter;
    }//end of intersecao
}//end of lista