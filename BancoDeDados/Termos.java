package BancoDeDados;
import java.io.File;
import java.io.RandomAccessFile;

public class Termos {
    private RandomAccessFile arquivo;
    private final String diretorio = "dados";

    public Termos(String nomeArquivo){
        try{
            //Verificar se a pasta do arquivo existe
            File d = new File(diretorio);
            if(!d.exists())
                d.mkdir();

            //Criar o arquivo de gerenciamento de termos
            arquivo = new RandomAccessFile(diretorio+"/"+nomeArquivo+"Termos.idx", "rw");
        } catch(Exception e) {
            //Resolução de erros durante o processo:
            System.out.println("Erro ao criar lista invertida.");
            System.out.println("Estrutura: TERMOS.");
            System.out.print("Motivo: ");
            e.printStackTrace();
        }//end of try
    }//end of Termos

    public long inserir(String nome, long fimDoArquivoID){
        long endereco = -1;
        try{
            //Tamanho do arquivo
            long tamanho = arquivo.length();
            boolean end = false;
            //Proximo lugar a ser acessado na memória
            long prox = 0;
            //Se existir algum dado dentro do arquivo:
            if(tamanho > 8){
                //Loop para verificar se o nome já foi inserido na estrutura
                while(prox < tamanho && !end){
                    arquivo.seek(prox);
                    //Ler a nome presente na posição prox do arquivo
                    String aux = arquivo.readUTF();
                    //Verificar se o nome é o mesmo que o nome que deseja ser inserido no arquivo
                    if(aux.equals(nome)){
                        //Se sim obter o endereço
                        endereco = arquivo.readLong();
                        end = true;
                    } else {
                        //Se não ir para a proxima posição válida.
                        //Vale lembrar que o arquivo é constituido por String nome e long endereço,
                        prox = arquivo.getFilePointer() + 8;
                    }//end of if
                }//end of while
                //Se o nome não existir dentro do arquivo
                if( endereco == -1 ){
                    //Pegar o endereço do fim do arquivo de ids e salvar o nome e seu bucket lá
                    endereco = fimDoArquivoID;
                    arquivo.seek(arquivo.length());
                    arquivo.writeUTF(nome);
                    arquivo.writeLong(endereco);
                }//end of if
            } else {
                arquivo.writeUTF(nome);
                arquivo.writeLong(0);
                endereco = 0;
            }//end of if
        } catch(Exception e) {
            System.out.println("Erro ao inserir nome.");
            System.out.println("Estrutura: TERMO.");
            System.out.print("Motivo: ");
            e.printStackTrace();
        }//end of try
        return endereco;
    }//end of inserir

    public long ler(String nome){
        long endereco = -1;
        try{
            boolean end = false;
            arquivo.seek(0);
            String aux;
            long prox;
            while(!end){
                aux = arquivo.readUTF();
                if(nome.equals(aux)){
                    end = true;
                    endereco = arquivo.readLong();
                } else {
                    prox = arquivo.getFilePointer() + 8;
                    if( prox >= arquivo.length() ){
                        end = true;
                    } else {
                        arquivo.seek(prox);
                    }//end of if
                }//end of if
            }//end of while
        } catch(Exception e) {
            System.out.println("Erro ao ler nome.");
            System.out.println("Estrutura: TERMO.");
            System.out.print("Motivo: ");
            e.printStackTrace();
        }//end of catch
        return endereco;
    }//end of ler
}//end of Termos