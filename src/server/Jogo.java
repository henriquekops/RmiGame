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
	public static volatile int nextPlayer;
	private static volatile int numPlayers;
	private static volatile boolean isFull;
	private static Random rand;

	public Jogo() throws RemoteException {
		isFull = false;
		nextPlayer = 0;
		rand = new Random();
		players = new String[numPlayers];
	}

	public int registra() {
		/*
		 * Register a client as a known host
		 */
		int playerId = -1;
		try {
			String clientHostName = getClientHost();
			System.out.println("> client '" + clientHostName + "' is registering ...");
			if (!isFull) {
				players[nextPlayer] = clientHostName;
				nextPlayer += 1;
				playerId = nextPlayer;
			}
			else {
				System.out.println("> client '" + clientHostName + "' was rejected, no more players can join!");
			}
			if (nextPlayer >= numPlayers) {
				isFull = true;
			}
		} catch (ServerNotActiveException e) {
			System.out.println("> could not establish connection to client!");
		}
		return playerId;
	}

	public int joga(int id) {
		/*
		 * Simulate a client play by id with 1% of chance to disconnect
		 */
		String host = players[id-1];
		System.out.println("> client '" + host + "' is playing ...");
		if (rand.nextInt(100) == 0) {
			System.out.println("> client '" + players[id-1] + "' took 1% chance disconnection!");
			players[id-1] = null;
			return 0;
		}
		return 1;
	}

	public int encerra(int id) {
		/*
		 * Close client connection by id
		 */
		try {
			String clientHostName = "rmi://" + players[id-1] + ":3001/Jogador";
			System.out.println("> closing connection with '" + clientHostName + "' ...");
			JogadorInterface player =  (JogadorInterface) Naming.lookup(clientHostName);
			player.finaliza();
			players[id-1] = null;
		} catch (NotBoundException | RemoteException | MalformedURLException e) {
			System.out.println("> exception occurred when finishing a client connection: " + e);
			e.printStackTrace();
		}
		return 1;
	}

	private static void init(String [] args) {
		/*
		 * Initialize server validating arguments
		 */
		if (args.length != 2) {
			System.out.println("Usage: java AdditionServer <server> <num players>");
			System.exit(1);
		}

		try {
			numPlayers = Integer.parseInt(args[1]);
			if (numPlayers == 0) {
				System.out.println("Cannot start game with no players!");
				System.exit(1);
			}
		} catch (NumberFormatException e) {
			System.out.println("Wrong input format for <num players>");
			System.exit(1);
		}

		try {
			System.setProperty("java.rmi.server.hostname", args[0]);
			LocateRegistry.createRegistry(3000);
			System.out.println("Java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("Java RMI registry already exists.");
			System.exit(1);
		}

		try {
			String remoteHost = "rmi://" + args[0] + ":3000/Jogo";
			Naming.rebind(remoteHost, new Jogo());
		} catch (Exception e) {
			System.out.println("Exception when registering server: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String [] args) {
		/*
		 * Main thread
		 */
		init(args);
		System.out.println("RmiGame server started, ^C to quit...");

		JogadorInterface player;
		String clientHostName = null;

		// wait players
		System.out.println("> waiting for players...");
		try {
			while (!isFull) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			System.out.println("> exception when waiting for players: " + e);
			e.printStackTrace();
		}

		// init heartbeats
		System.out.println("> starting heartbeats...");
		for (int i = 0; i < players.length; i++) {
			clientHostName = "rmi://" + players[i] + ":3001/Jogador";
			HeartBeat t = new HeartBeat(clientHostName, i);
			t.start();
		}

		// start game
		System.out.println("> starting game...");
		for (int i = 0; i < players.length; i++) {
			try {
				clientHostName = "rmi://" + players[i] + ":3001/Jogador";
				player = (JogadorInterface) Naming.lookup(clientHostName);
				if (player != null) {
					System.out.println("> client '" + players[i] + "' can play!");
					player.inicia();
				}
			}
			catch( RemoteException | NotBoundException | MalformedURLException e) {
				System.out.println("> while starting client '" + clientHostName + "' disconnected ...");
			}
		}
		System.out.println("> game started!");

		// wait for all clients to quit
		try{
			while(!Arrays.stream(players).allMatch(Objects::isNull)) {
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			System.out.println("> exception at server busy wait: " + e);
			e.printStackTrace();
			System.exit(1);
		}

		// shutdown
		System.out.println("> no more active clients, server shutdown!");
		System.exit(1);
	}
}
