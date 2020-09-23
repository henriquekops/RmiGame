package src.client;

import src.server.JogoInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.Random;

public class Jogador extends UnicastRemoteObject implements JogadorInterface {

	private static int playerId;
	private static int numPlays;
	private static volatile boolean canPlay;
	private static volatile boolean shutdown;

	public Jogador() throws RemoteException {
		playerId = -1;
		canPlay = false;
		shutdown = false;
	}

	public void inicia() {
		/*
		 * Let client play
		 */
		canPlay = true;
	}

	public void finaliza() {
		/*
		 * Let client quit
		 */
		shutdown = true;
	}

	public void cutuca() {
		/*
		 * Tell server that this client is alive
		 */
		System.out.println("> telling server Im alive...");
	}

	private static void init(String [] args) {
		/*
		 * Start client validating arguments
		 */
		if (args.length != 3) {
			System.out.println("Usage: java Jogador <client> <server> <num plays>");
			System.exit(1);
		}

		try {
			numPlays = Integer.parseInt(args[2]);
			if (numPlays == 0) {
				System.out.println("Parameter <num plays> must be greater than 0!");
				System.exit(1);
			}
		} catch (NumberFormatException e) {
			System.out.println("Wrong input format for <num plays>");
			System.exit(1);
		}

		try {
			System.setProperty("java.rmi.server.hostname", args[0]);
			LocateRegistry.createRegistry(3001);
			System.out.println("Java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("Java RMI registry already exists.");
		}

		try {
			String client = "rmi://" + args[0] + ":3001/Jogador";
			Naming.rebind(client, new Jogador());
		} catch (Exception e) {
			System.out.println("Exception when registering client: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String [] args) {
		/*
		 * Main thread
		 */
		init(args);
		System.out.println("RmiGame client started, ^C to quit ...");

		JogoInterface game = null;
		String serverHostName = "rmi://" + args[1] + ":3000/Jogo";

		// connect to server
		try {
			System.out.println("> connecting to server at : " + serverHostName + " ...");
			game = (JogoInterface) Naming.lookup(serverHostName);
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			System.out.println("> could not connect to server!");
			System.exit(1);
		}

		// register
		try {
			System.out.println("> registering at: " + serverHostName + " ...");
			if (game != null) {
				playerId = game.registra();
				if (playerId < 0) {
					System.out.println("> game is full or already started, good bye!");
					System.exit(1);
				} else {
					System.out.println("> registered with player id: " + playerId + "!");
				}
			} else {
				System.out.println("> lost connection to server!");
				System.exit(1);
			}
		} catch (RemoteException e) {
			System.out.println("> exception when registering: " + e);
			e.printStackTrace();
		}

		// wait for game to start
		while (!canPlay) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("> exception while waiting game to start: " + e);
				e.printStackTrace();
			}
		}

		// play
		System.out.println("> starting to play ...");
		try {
			Random rand = new Random();
			if (game != null) {
				for (int i = 0; i < numPlays; i++) {
					System.out.println("> making play n." + (i + 1) + " ...");
					if (game.joga(playerId) == 0) {
						System.out.println("> disconnected from server, good bye!");
						System.exit(1);
					}
					Thread.sleep(rand.nextInt(1500 - 500) + 500);
				}
			} else {
				System.out.println("> lost connection to server, good bye!");
				System.exit(1);
			}
		} catch (RemoteException | InterruptedException e) {
			System.out.println("> exception when registering: " + e);
			e.printStackTrace();
		}
		System.out.println("> finished playing!");

		// end connection
		try {
			if (game != null) {
				game.encerra(playerId);
				System.out.println("> asking server to close my connection ...");
			} else {
				System.out.println("> lost connection to server, good bye!");
				System.exit(1);
			}
		} catch (RemoteException e) {
			System.out.println("> exception occurred when closing connection: " + e);
			e.printStackTrace();
			System.exit(1);
		}

		// wait for shutdown
		try {
			while (!shutdown) {
				Thread.sleep(5000);
				System.out.println("> waiting server to disconnect me ...");
			}
		} catch (InterruptedException e) {
			System.out.println("> exception while waiting to quit: " + e);
			e.printStackTrace();
		}

		// shutdown
		System.out.println("> client shutdown, bye!");
		System.exit(1);
	}
}
