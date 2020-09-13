import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoInterface extends Remote {
    public int registra() throws RemoteException;
    public int finaliza(int id) throws RemoteException;
    public int encerra(int id) throws RemoteException;
}
