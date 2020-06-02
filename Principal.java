import Interface.Interface;

public class Principal {
    public static void main(String[] args){
        try{
            Interface interface1 = new Interface();
            interface1.menu();
        } catch(Exception e) {
            e.printStackTrace();
        }//end of try
    }//end of main

}