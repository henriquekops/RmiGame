import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;

public class Jogador extends UnicastRemoteObject implements JogadorInterface {

	private static int idJogador;

	public Jogador() throws RemoteException {
		this.idJogador = 0;
	}

	public void inicia() throws RemoteException {
		//Chamado pelo servidor em cada instancia do jogador
		//para comecar o jogo propriamente dito
	}

	public void finaliza() throws RemoteException {
		//O encerramento de um certo jogador quando
		//o jogador termina o jogo/o jogo termina o jogador
	}

	public void cutuca() throws RemoteException {
		//O servidor checa o jogador a cada 3s
		//para ver se ele ainda esta ativo/vivo
	}

	public static void main(String [] args) {
	}

}
