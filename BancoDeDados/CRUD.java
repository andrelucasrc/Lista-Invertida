package BancoDeDados;
import java.io.*;
import java.lang.reflect.*;
import Props.Registro;

public class CRUD<T extends Registro>{
	private final String diretorio = "dados";
	
	private RandomAccessFile arquivo;
	private HashExtensivel indiceDireto;
	private ArvoreBMais_String_Int indiceIndireto;
	private final short tamanhoCabecalho = 12;
	private Constructor<T> construtor; 
	
	public CRUD(String nomeArquivo,Constructor<T> construtor) throws Exception{
		File d = new File(this.diretorio);
		this.construtor = construtor;
		if(!d.exists())
			d.mkdir();
		arquivo = new RandomAccessFile(this.diretorio+"/"+nomeArquivo+".db", "rw");
		if(arquivo.length()<tamanhoCabecalho){
      		arquivo.writeInt(0);  // cabeçalho
			arquivo.writeLong(-1);
		}//end of if
		indiceDireto = new HashExtensivel(10, this.diretorio+"/diretorio."+nomeArquivo+".idx", 
                           				 this.diretorio+"/cestos."+nomeArquivo+".idx");
		indiceIndireto = new ArvoreBMais_String_Int(10,this.diretorio+"/arvoreB."+nomeArquivo+".idx");
	}//end of CRUD constructor
	
	//Dada a posição do lixo a ser removido, este método percorre todos os lixos dentro do
	//arquivo e procura o lixo antecessor ao que deve ser removido e salva na posição do lixo
	//sucessor ao que deve ser removido.
	private void lixoAnterior(long remover,long proximo) throws Exception{
		arquivo.seek(4);
		long ponteiro = arquivo.readLong();
		boolean end = false;
		long temp = -1;
		while(ponteiro != -1 && !end){
			arquivo.seek(ponteiro+4);
			temp = ponteiro;
			ponteiro = arquivo.readLong();
			if(ponteiro == remover){
				arquivo.seek(temp+4);
				arquivo.writeLong(proximo);
				end = true;
			}//end of if
		}//end of while
	}//end of lixoAnterior
	
	//Arquivo responsavel pela criação de registros do tipo lixo
	private boolean criarLixo(long pos){
		boolean create = true;
		try{
			arquivo.seek(4);
			long prox = arquivo.readLong();
			long ant = -1;
			boolean end = false;
			while(!end){
				if(prox != -1){
					if(prox > pos){
						arquivo.seek(pos);
						if(arquivo.readChar() != '*')
							throw new Exception("Registro não está habilitado para ser deletado.");
						arquivo.seek(pos+4);
						arquivo.writeLong(prox);
						if(ant == -1){
							arquivo.seek(4);
							arquivo.writeLong(pos);
							end = true;
						} else {
							lixoAnterior(prox,pos);
							end = true;
						}//end of else
					} else if( prox < pos) {
						arquivo.seek(prox + 4);
						ant = prox;
						prox = arquivo.readLong();
					} else {
						throw new Exception("Registro já foi deletado.");
					}//end of if
				} else {
					if(ant == -1) {
						arquivo.seek(4);
					} else {
						arquivo.seek(ant+4);
					}//end of if
					arquivo.writeLong(pos);
					arquivo.seek(pos+4);
					arquivo.writeLong(-1);
					end = true;
				}//end of if
			}//end of loop
		} catch(Exception e){
			create = false;
		}//end of catch
		return create;
	}//end of criarLixo
	
	private long encontrarLixo(short tamanho){
		long pos = -1;
		boolean end = false;
		try{
			arquivo.seek(4);
			pos = arquivo.readLong();
			while(!end){
				if(pos != -1){
					arquivo.seek(pos);
					if(arquivo.readChar() != '*')
						throw new Exception("Registro não está habilitado a ser deletado");
					short newSize = arquivo.readShort();
					if(newSize >= tamanho){
						end = true;
					} else {
						pos = arquivo.readLong();
					}//end of else
				} else {
					throw new Exception("Não existe o lixo com o tamanho desejado.");
				}//end of if
			}//end of while
		} catch(Exception e){
			//Erro ao encontrar lixo
		}//end of catch
		return pos;
	}//end of encontrarLixo
	
	private boolean deletarLixo(long pos){
		boolean deletado = true;
		boolean end = false;
		try{
			if(pos < 12)
				throw new Exception("Posição inválida");

			
			arquivo.seek(pos + 4);
			long posDelete = arquivo.readLong();
			
			long anterior = 4;
			long ponteiro = -1;

			while(!end){
				arquivo.seek(anterior);
				ponteiro = arquivo.readLong();
				if(pos == ponteiro){
					arquivo.seek(anterior);
					arquivo.writeLong(posDelete);
					end = true;
				} else if( pos > ponteiro){
					anterior = ponteiro;
				} else {
					throw new Exception("Posição invalida");
				}//end of else
			}//end of while
		} catch( Exception e ) {
			deletado = false;
		}//end of catch
		return deletado;
	}//end of deletarLixo
	
	//Método responsável pela criação de um novo item no registro
	public int create(T entidade){
		int sucesso = -1;
		int id = -1;
		long pos = -1;
		try{
			//Primeiro é lido qual o id que deve ser armazenado no cabeçalho do arquivo.
			arquivo.seek(0);
			id = arquivo.readInt();
			//O id é setado no objeto
			entidade.setID(id);
			//O arranjo de bytes a ser armazenado no arquivo é obtido.
         	byte[] data = entidade.toByteArray();
			short tamanho = (short) data.length;
			//É verificado a existência de lixo dentro do arquivo, aonde o espaço possa ser reciclado.	
			pos = encontrarLixo(tamanho);
			if(pos == -1){
				pos = arquivo.length();
			} else {
				deletarLixo(pos);
			}//end of if
			//Os dados são escritos na posição encontrada pelos algoritmos anteriores.
			arquivo.seek(pos);
			arquivo.writeChar(' ');
			arquivo.writeShort(data.length);
			arquivo.write(data);
			//Os dados são escritos nos indices dos arquivos
			indiceDireto.create(id, pos);
			indiceIndireto.create(entidade.chaveSecundaria(),id);
			//O id é atualizado e retornado para o usuario
			arquivo.seek(0);
			sucesso = id;
			arquivo.writeInt(id+1);
		} catch(Exception e) {
			sucesso = -1;
		}//end of catch
		return sucesso;
	}//end of create
	
	//Metódo de leitura de dados no arquivo atraves de um id
	public T read(int id){
		T entidade = null;
		try{
			//A posição é verificada dentro do indice
			long pos = indiceDireto.read(id);
			if(pos == -1)
				throw new Exception("Não foi possível encontrar o item desejado");
			//Se por algum motivo o registro já foi deletado, o registro é invalido
			arquivo.seek(pos);
			char deleted = arquivo.readChar();
			if(deleted == '*')
				throw new Exception("Registro inválido");

			//Se o tamanho do registro for diferente do que foi lido dentro do arquivo existe um inconssistência.
			short size = arquivo.readShort();
			byte[] data = new byte[size];
			if(size != arquivo.read(data))
				throw new Exception("Inconssistência nos dados lidos");

			entidade = this.construtor.newInstance();
			entidade.fromByteArray(data);
		} catch(Exception e){
			System.out.print("Deu ruim meu parceiro, por causa da leitura: ");
         e.printStackTrace();
		}//end of catch
		return entidade;
	}//end of read

	//Dentro do indice é verificado a chave secundaria pegando a posição e chamando o metodo de leitura.
	public T read(String chaveSecundaria){
		T entidade = null;
		int id = -1;
		try{
			id = indiceIndireto.read(chaveSecundaria);
		}catch(Exception e){
			System.out.print("Deu ruim meu parceiro, ao ler chave secundaria: ");
         	e.printStackTrace();
		}//end of catch
		if(id != -1)
			entidade = this.read(id);
		return entidade;
	}//end of read
	
	//Método de atualização de entidade
	//O item é deletado e logo após é inserido novamente dentro do arquivo, verificando o tamanho do arquivo
	//e o seu lixo.
	public boolean update(T entidade){
		boolean update = true;
		try{
			this.delete(entidade.getID());
			byte[] data = entidade.toByteArray();
			short tamanho = (short) data.length;
			long pos = encontrarLixo(tamanho);
			if(pos == -1){
				pos = arquivo.length();
			} else {
				deletarLixo(pos);
			}//end of if
			arquivo.seek(pos);
			arquivo.writeChar(' ');
			arquivo.writeShort(data.length);
			arquivo.write(data);
			indiceDireto.create(entidade.getID(),pos);
			indiceIndireto.create(entidade.chaveSecundaria(),entidade.getID());
		} catch(Exception e){
			System.out.println("Erro ao atualizar usuario, por esse motivo: ");
			update = false;
			e.printStackTrace();
		}//end of catch
		return update;
	}//end of update

	//Método de delete de registro
	public boolean delete(int id){
		boolean delete = true;
		try{
			if(id < 0)
				throw new Exception("ID inválido. ");
			//Primeiro é verificado a posição
			long pos = indiceDireto.read(id);
			if(pos == -1)
				throw new Exception("ID não existe na base de dados.");
			//O registro é deletado dentro dos indices e a lapide é marcada.
			indiceIndireto.delete(this.read(id).chaveSecundaria());
			indiceDireto.delete(id);
			arquivo.seek(pos);
			arquivo.writeChar('*');
			criarLixo(pos);
		} catch(Exception e){
			delete = false;
			System.out.print("Deu ruim, meu parceiro, ao deletar: ");
			e.printStackTrace();
		}//end of catch
		return delete;
	}//end of delete
}//end of class CRUD
