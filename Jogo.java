import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;

public class Jogo extends UnicastRemoteObject implements JogoInterface {

	private static volatile int numJogadores;
	private static volatile int proxJogador;
	private static volatile int[] jogadorStatus;
	private static volatile String[] jogadorHostNames;

	public Jogo() throws RemoteException {
	}

	public int registra() {
		// ativar no array 'jogadorStatus' o bit (0 -> 1) na posicao 'proxJogador'
		return -1;
	}

	public int joga(int id) {
		// delay de 500-1500ms (aleatorio) + 1% chance de chamar 'finaliza()'
		return -1;
	}

	public int encerra(int id) {
		// desativar no array 'jogadorStatus' o bit (1 -> 0) na posicao 'id'
		return -1;
	}

	public static void main(String [] args) throws RemoteException {

		// Receber numero de jogadores e ip do servidor por parametro

		// Iniciar variaveis privadas de acordo com a entrada do programa

		// Enquanto o array 'jogadorStatus' nao estiver com todos os bits em 1
		// aguardar demais jogadores

		// Chamar o metodo 'inicia()' de todos os jogadores olhando para o array
		// jogadorHostNames

		// Manter um while(true) para checagem de 3s em 3s !! em qual ip?
		// Talvez abrir uma thread pra cada jogador?

	}

}
