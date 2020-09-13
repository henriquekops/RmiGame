import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;

public class Jogador extends UnicastRemoteObject implements JogadorInterface {

private static int idJogador;


    public Jogador() throws RemoteException {
        this.idJogador = 0;
    }

    public static void main(String args[]){
    }

    public static void inicia() throws RemoteException{
    }

    public static void finaliza() throws RemoteException{
    }

    public static void cutuca() throws RemoteException{
    }

}