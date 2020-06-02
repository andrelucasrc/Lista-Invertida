package Props;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//Entidade para passar no requisito para ser salva dentro do CRUD:
public class Nome implements Registro {
    String nomeEntidade;
    int idEntidade;

    public Nome(){
        this(-1, "");
    }//end of void constructor

    public Nome(int id, String nome){
        idEntidade = id;
        nomeEntidade = nome;
    }//end of full constructor

    public void setID(int id) {
        idEntidade = id;
    }//end of setID

    public int getID() {
        return idEntidade;
    }//end of getID

    public String chaveSecundaria(){
        return nomeEntidade;
    }//end of chaveSecundaria

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(ba);
        data.writeInt(idEntidade);
        data.writeUTF(nomeEntidade);
        return ba.toByteArray();
    }//end of toByteArray

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bytes = new ByteArrayInputStream(ba);
        DataInputStream data = new DataInputStream(bytes);
        idEntidade = data.readInt();
        nomeEntidade = data.readUTF();
    }//end of fromByteArray  

    public String toString(){
        return nomeEntidade;
    }//end of toString
}//end of Nome