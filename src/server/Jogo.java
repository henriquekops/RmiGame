package src.server;

import src.client.JogadorInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Jogo extends UnicastRemoteObject implements JogoInterface {

	public static volatile String[] players;
	private static volatile int nextPlayer;
	private static volatile int numPlayers;
	private static volatile boolean isFull;
	private static volatile boolean started;

	public Jogo() throws RemoteException {
		isFull = false;
		started = false;
		nextPlayer = 0;
		players = new String[numPlayers];
	}

	public int registra() {
		int playerId = -1;

		try {
			String clientHostName = getClientHost();
			System.out.println(clientHostName + " is registering ...");
			if (!started && nextPlayer < numPlayers) {
				players[nextPlayer] = clientHostName;
				nextPlayer += 1;
				playerId = nextPlayer;
			}
			if (nextPlayer >= numPlayers) {
				System.out.println("Game is full, no more player can join!");
				isFull = true;
			}
			if (started) {
				System.out.println("Game already started, no more player can join!");
			}
		} catch (ServerNotActiveException e) {
			System.out.println("Could not establish connection to client!");
		}

		return playerId;
	}

	public int joga(int id) {
		String host = players[id-1];
		System.out.println("Client ''" + host + "' is playing ...");
		boolean disconnect = new Random().nextInt(100) == 0;
		if (disconnect) {
			System.out.println("Client '" + players[id-1] + "' took 1% chance disconnection!");
			players[id-1] = null;
			return 0;
		}
		return 1;
	}

	public int encerra(int id) {
		try {
			String clientHostName = "rmi://" + players[id-1] + ":3001/Jogador";
			System.out.println("Closing connection with '" + clientHostName + "' ...");
			JogadorInterface player =  (JogadorInterface) Naming.lookup(clientHostName);
			player.finaliza();
			players[id-1] = null;
		} catch (NotBoundException | RemoteException | MalformedURLException e) {
			System.out.println("Exception occurred when finishing a client connection: " + e);
			e.printStackTrace();
		}
		return 1; // o que retornar aqui?
	}

	private static void init(String [] args) {
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

		try {
			System.setProperty("java.rmi.server.hostname", args[0]);
			LocateRegistry.createRegistry(3000);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("Java RMI registry already exists");
			System.exit(1);
		}

		try {
			String remoteHost = "rmi://" + args[0] + ":3000/Jogo";
			Naming.rebind(remoteHost, new Jogo());
		} catch (Exception e) {
			System.out.println("Main thread failed: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String [] args) {
		init(args);
		System.out.println("RmiGame server started, ^C to quit...");

		// wait players
		System.out.println("Waiting for players...");
		while (!isFull) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Error at main thread: " + e);
				e.printStackTrace();
			}
		}
		started = true;

		// init heartbeats
		System.out.println("Starting heartbeats...");
		for (int i = 0; i < players.length; i++) {
			String clientHostName = "rmi://" + players[i] + ":3001/Jogador";
			HeartBeat t = new HeartBeat(clientHostName, i);
			t.start();
		}

		// start game
		System.out.println("Starting game...");
		String clientHostName;
		JogadorInterface player;
		for (int i = 0; i < players.length; i++) {
			try {
				clientHostName = "rmi://" + players[i] + ":3001/Jogador";
				player = (JogadorInterface) Naming.lookup(clientHostName);
				if (player != null) {
					System.out.println("Client '" + players[i] + "' can play!");
					player.inicia();
				}
				else {
					System.out.println("Client '" + players[i] + "' lost connection!");
					players[i] = null;
				}
			} catch( RemoteException | NotBoundException | MalformedURLException e) {
				System.out.println("Exception when initializing game: " + e);
				e.printStackTrace();
			}
		}

		// wait for all connections to finish
		while(!Arrays.stream(players).allMatch(Objects::isNull)) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Exception at server busy wait: " + e);
				e.printStackTrace();
			}
		}
		System.out.println("No more players, server shutdown ...");
		System.exit(0);
	}
}
