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
	private static boolean canPlay;

	public Jogador() throws RemoteException {
		playerId = -1;
		canPlay = false;
	}

	public void inicia() throws RemoteException {
		canPlay = true;
	}

	public void finaliza() throws RemoteException {
		// finalizacao do registro do jogador
	}

	public void cutuca() throws RemoteException {
		System.out.println("Telling server Im alive...");
	}

	private static void init(String [] args) {
		if (args.length != 3) {
			System.out.println("Usage: java Jogador <client> <server> <num plays>");
			System.exit(1);
		}

		try {
			numPlays = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.out.println("Wrong input format for <num plays>");
			System.exit(1);
		}

		if (numPlays == 0) {
			System.out.println("Parameter <num plays> must be greater than 0!");
			System.exit(1);
		}

		try {
			System.setProperty("java.rmi.server.hostname", args[0]);
			LocateRegistry.createRegistry(3001);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("java RMI registry already exists.");
		}

		try {
			String client = "rmi://" + args[0] + ":3001/Jogador";
			Naming.rebind(client, new Jogador());
		} catch (Exception e) {
			System.out.println("RmiGame client failed: " + e);
			e.printStackTrace();
		}
	}

	public static void main(String [] args) {
		init(args);
		System.out.println("RmiGame client started, ^C to quit ...");

		JogoInterface game = null;
		String serverHostName = "rmi://" + args[1] + ":3000/Jogo";

		try {
			System.out.println("Connecting to server at : " + serverHostName + " ...");
			game = (JogoInterface) Naming.lookup(serverHostName);
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			System.out.println ("Client failed: " + e);
			e.printStackTrace();
		}

		try {
			System.out.println("Registering at: " + serverHostName + " ...");
			if (game != null) {
				playerId = game.registra();
				System.out.println("Player id is: " + playerId + "!");
			}
		} catch (RemoteException e) {
			System.out.println("Exception when registering: " + e);
			e.printStackTrace();
		}
		while(true) {}

//		while (!canPlay) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				System.out.println("Error at main thread: " + e);
//				e.printStackTrace();
//			}
//		}
//
//		try {
//			System.out.println("Registering at: " + serverHostName + " ...");
//			Random rand = new Random();
//			if (game != null) {
//				for(int i = 0; i < numPlays; i++) {
//					System.out.println("Making play n." + i + " ...");
//					game.joga(playerId);
//					Thread.sleep(rand.nextInt(1500-500)+500);
//				}
//			}
//		} catch (RemoteException | InterruptedException e) {
//			System.out.println("Exception when registering: " + e);
//			e.printStackTrace();
//		}
	}
}
