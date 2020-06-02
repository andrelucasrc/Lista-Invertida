package BancoDeDados;
import java.io.File;
import java.io.RandomAccessFile;

//Gerenciador de registro para o registro de ids.
public class Identificacao {
    private final String diretorio = "dados";
    private RandomAccessFile arquivo;             //Responsável pela lista de ids

    public Identificacao(String nomeArquivo){
        try{
            //Verificar se a pasta do arquivo existe
            File d = new File(diretorio);
            if(!d.exists())
                d.mkdir();

            //Criar o arquivo de gerenciamento de IDS
            arquivo = new RandomAccessFile(diretorio+"/"+nomeArquivo+"IDS.idx", "rw");
        } catch(Exception e) {
            //Resolução de erros durante o processo:
            System.out.println("Erro ao criar lista invertida.");
            System.out.println("Estrutura: IDENTIFICAÇÃO.");
            System.out.print("Motivo: ");
            e.printStackTrace();
        }//end of try
    }//end of Identificacao

    //Método para obter o tamanho total do arquivo de ids
    public long getTamanho(){
        long tamanho = -1;
        try{
            tamanho = arquivo.length();
        } catch(Exception e){
            System.out.println("Erro ao recuperar tamanho do arquivo de ids.");
        }//end of catch
        return tamanho;
    }//end of getTamanho

    //Método para a inserção de um novo id no bucket.
    public void inserir(int id, long position){
        try{
            //Verificar se é necessário criar um novo bucket:
            if(position >= arquivo.length())
                criarEstrutura(position);

            //Ir para a posição enviada no arquivo:
            arquivo.seek(position);
            //Ler a quantidade de itens do bucket:
            short quant = arquivo.readShort();
            //Se o bucket estiver cheio:
            if(quant >= 10){
                //Ir para a posição aonde é salvo o proximo bucket:
                arquivo.seek(position + 42);
                //Ler o endereço salvo:
                long endereco = arquivo.readLong();
                long inserir = arquivo.length();
                //Verificar se existe um proximo bucket
                if(endereco < 0){
                    //Se não existir criar um bucket:
                    criarEstrutura(inserir);
                    //Ir para a posição que salva o endereço do proximo bucket:
                    arquivo.seek(position + 42);
                    //Salvar a posição de inserção do bucket:
                    arquivo.writeLong(inserir);
                    //Mudar o endereço de inserção:
                    endereco = inserir;
                }//end of if
                //Chamar o método de inserção novamente:
                inserir(id, endereco);
            //Se for possíver inserir um novo id no bucket:
            } else {
                //Cálcular a próxima posição de inserção:
                long pos = position + (quant*4) + 2;
                //Ir para a próxima posição de inserção:
                arquivo.seek(pos);
                //Escrever o id na posição:
                arquivo.writeInt(id);
                //Atualizar a quantidade de ids no bucket:
                arquivo.seek(position);
                arquivo.writeShort(quant+1);
            }//end of if
        } catch(Exception e) {
            System.out.println("Erro ao inserir nome.");
            System.out.println("Estrutura: IDENTIFICAÇÃO.");
            System.out.print("Motivo: ");
            e.printStackTrace();
        }//end of try
    }//end of inserirID

    //Método para a criação de um novo bucket:
    private void criarEstrutura(long position) throws Exception{
        //Ir para a posição desejada:
        arquivo.seek(position);
        //Escrever 0 na quantidade de itens do bucket:
        arquivo.writeShort(0);
        //Preencher com 10 int ids inválidos, -1 neste caso:
        for(int i = 0; i < 10; i++){
            arquivo.writeInt(-1);
        }//end of for
        //Escrever -1 para o próximo bucket, pois ainda não existe outro bucket:
        arquivo.writeLong(-1);
    }//end of criarEstrutura

    //Método para a leitura de itens dentro de um determinado bucket:
    public int[] ler(long endereco){
        //Arranjo contendo todos os ids:
        int[] ids = null;
        try{
            //Verificar se o endereço é válido:
            if(endereco < 0)
                throw new Exception("Nome não consta na base de dados.");
            
            //Ir para o endereço passado:
            arquivo.seek(endereco);
            //Obter a quantidade de itens dentro do bucket:
            short quant = arquivo.readShort();
            //Ir para o endereço salvo do próximo bucket:
            arquivo.seek(endereco + 42);
            //Ler o endereço salvo:
            long prox = arquivo.readLong();
            //Arranjo aonde será salvo o retorno da chamada recursiva:
            int[] aux = null;
            //Para a verificação de retorno da chamada recursiva:
            int tamanho = 0;
            //Verificar se existe um próximo bucket:
            if( prox >= 0){
                //Chamada recusiva do método:
                aux = ler(prox);
                //Tamanho do arranjo de retorno:
                tamanho = aux.length;
            }//end of if

            //Ir para a posição aonde se inicia todos os ids do bucket:
            arquivo.seek(endereco + 2);
            //Inserir todos os ids dentro da estrutura:
            ids = new int[quant];
            //Ler todos os ids:
            for(int j = 0; j < quant; j++){
                ids[j] = arquivo.readInt();
            }//end of for

            //Caso exista algum retorno dentro da chamada recursiva juntar o arranjo do bucket atual,
            //com o arranjo de retorno:
            if(tamanho > 0){
                //Arranjo de auxilio para acresentar no arranjo final:
                int[] append = new int[ids.length + aux.length];
                //Inserir os itens do arranjo de retorno no de acréscimo:
                for(int i = 0; i < ids.length; i++){
                    append[i] = ids[i];
                }//end of for
                //Inserir os itens do arranjo da recursidade no de acréscimo:
                for(int i = 0; i < aux.length; i++){
                    append[ids.length + i] = aux[i];
                }//end of for
                //Salvar o arranjo de retorno como o de acréscimo:
                ids = append;
            }//end of if
        } catch(Exception e) {
            ids = null;
            System.out.println("Erro ao ler nome.");
            System.out.println("Estrutura: IDENTIFICAÇÃO.");
            System.out.print("Motivo: ");
            e.printStackTrace();
        }//end of try
        return ids;
    }//end of ler
}//end of Identificacao