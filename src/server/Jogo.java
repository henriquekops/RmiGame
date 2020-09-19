package src.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;

public class Jogo extends UnicastRemoteObject implements JogoInterface {

	private static volatile String[] players;
	private static volatile int nextPlayer;
	private static volatile int numPlayers;
	private static volatile boolean isFull;

	private static final String CUTUCA = "cutuca";
	private static final String FINALIZA = "finaliza";
	private static final String INICIA = "inicia";

	public Jogo() throws RemoteException {}

	public int registra() {
		int clientId = -1;

		try {
			String host = getClientHost();
			System.out.println(host + " is registering...");
			if (nextPlayer < numPlayers) {
				players[nextPlayer] = host;
				nextPlayer += 1;
				clientId = nextPlayer;
			}
			if (nextPlayer >= numPlayers) {
				System.out.println("game is full!");
				isFull = true;
			}
		} catch (ServerNotActiveException e) {
			System.out.println("Could not establish connection to client!");
		}

		return clientId;
	}

	public int joga(int id) {
		// delay de 500-1500ms (aleatorio) + 1% chance de chamar 'finaliza()'
		return -1;
	}

	public int encerra(int id) {
		// desativar no array 'jogadorStatus' o bit (1 -> 0) na posicao 'id'
		return -1;
	}

	private static void validateArgs(String [] args) {
		if (args.length != 2) {
			System.out.println("Usage: java AdditionServer <server> <num players>");
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
			String remoteHost = "rmi://" + host + ":3000/Jogo";
			Naming.rebind(remoteHost, new Jogo());
			System.out.println("RmiGame server is ready.\n^C to quit...");
		} catch (Exception e) {
			System.out.println("Main thread failed: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String [] args) throws RemoteException {

		validateArgs(args);
		registerServer(args[0]);

		isFull = false;
		nextPlayer = 0;
		players = new String[numPlayers];

		System.out.println("waiting for players...");
		while (!isFull) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Error at main thread: " + e);
				e.printStackTrace();
			}
		}

		System.out.println("game started!");
		callCutuca();
//		callInit();
	}

	private static void callInit() {
		for (int i = 0; i < players.length; i++) {
			ServerThread t = new ServerThread(createRemoteUri(players[i]), INICIA);
			t.start();
		}
	}

	private static void callCutuca() {
		for (int i = 0; i < players.length; i++) {
			ServerThread t = new ServerThread(createRemoteUri(players[i]), CUTUCA);
			t.run();
		}
	}

	private static String createRemoteUri(String remoteHostName) {
		return "rmi://" + remoteHostName + ":3001/Jogador";
	}
}
