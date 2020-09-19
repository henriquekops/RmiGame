package src.client;

import src.server.JogoInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;

public class Jogador extends UnicastRemoteObject implements JogadorInterface {

	private static int idJogador;

	public Jogador() throws RemoteException {}

	public void inicia() throws RemoteException {
		// permite que o cliente comece a realizar jogadas no servidor
	}

	public void finaliza() throws RemoteException {
		// finalizacao do registro do jogador
	}

	public void cutuca() throws RemoteException {
		System.out.println("Telling server Im alive...");
	}

	private static void init(String [] args) {
		if (args.length != 2) {
			System.out.println("Usage: java Jogador <client> <server>");
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

		String remoteHostName = args[1];
		String connectLocation = "rmi://" + remoteHostName + ":3000/Jogo";

		try {
			System.out.println("Connecting to server at : " + connectLocation + " ...");
			JogoInterface game = (JogoInterface) Naming.lookup(connectLocation);
			System.out.println("Registering at: " + connectLocation + " ...");
			if (game != null) {
				idJogador = game.registra();
				System.out.println("Player id is: " + idJogador + "!");
			}
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			System.out.println ("Client failed: " + e);
			e.printStackTrace();
		}

		while (true) {}
	}

}
