package src;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.Semaphore;

public class Jogo extends UnicastRemoteObject implements JogoInterface {

	private static volatile String[] players;
	private static volatile int nextPlayer;
	private static volatile int numPlayers;
	private static volatile boolean isFull;

	public Jogo() throws RemoteException {}

	public int registra() {
		try {
			if (nextPlayer < numPlayers) {
				players[nextPlayer] = getClientHost();
				nextPlayer++;
			}
			else {
				isFull = true;
			}
		} catch (ServerNotActiveException e) {
			System.out.println("Could not establish connection to client");
		}
		return nextPlayer;
	}

	public int joga(int id) {
		// delay de 500-1500ms (aleatorio) + 1% chance de chamar 'finaliza()'
		return -1;
	}

	public int encerra(int id) {
		// desativar no array 'jogadorStatus' o bit (1 -> 0) na posicao 'id'
		return -1;
	}

	private static void validate_args(String [] args) {
		if (args.length != 2) {
			System.out.println("Usage: java AdditionServer <server ip> <num players>");
			System.exit(1);
		}

		try {
			numPlayers = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("Wrong input format for <num players>");
			System.exit(1);
		}

		if (numPlayers == 0) {
			System.out.println("Cannot start game with no players");
			System.exit(1);
		}
	}

	private static void registerServer(String host) {
		try {
			System.setProperty("java.rmi.server.hostname", host);
			LocateRegistry.createRegistry(3000);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("Java RMI registry already exists");
			System.exit(1);
		}

		try {
			String server = "rmi://" + host + ":3000/Jogo";
			Naming.rebind(server, new Jogo());
			System.out.println("RmiGame server is ready.");
		} catch (Exception e) {
			System.out.println("Main thread failed: " + e);
			System.exit(1);
		}
	}

	public static void main(String [] args) throws RemoteException {

		validate_args(args);
		registerServer(args[0]);

		isFull = false;
		nextPlayer = 0;
		players = new String[numPlayers];

		while (!isFull) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Error at sleep: " + e);
			}
		}

		heartBeat();
		callInit();
	}

	private static void callInit() {
		for (int i = 0; i < players.length; i++) {
			String conexao = "rmi://" + players[i] + ":3000/Jogador";
			new Thread() {
				@Override
				public void run() {
					try{
						System.out.println("Initiating heartbeat thread for: " + conexao);
						JogadorInterface jogador = (JogadorInterface) Naming.lookup(conexao);
						jogador.inicia();
						this.interrupt();
					} catch ( RemoteException | NotBoundException | MalformedURLException e) {
						System.out.println("Exception at heartBeat: " + e);
						this.interrupt();
					}
				}
			}.start();
		}
	}

	private static void heartBeat() {
		for (int i = 0; i < players.length; i++) {
			String conexao = "rmi://" + players[i] + ":3000/Jogador";
			new Thread() {
				@Override
				public void run() {
					try {
						System.out.println("Initiating heartbeat thread for: " + conexao);
						JogadorInterface jogador = (JogadorInterface) Naming.lookup(conexao);
						while (true) {
							if (jogador != null) {
								jogador.cutuca();
							} else {
								this.interrupt();
							}
							Thread.sleep(3000);
						}
					}
					catch( RemoteException | InterruptedException | NotBoundException | MalformedURLException e) {
						System.out.println("Exception at heartBeat: " + e);
						this.interrupt();
					}
				}
			}.start();
		}
	}
}
