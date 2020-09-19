package src;

import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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
			System.out.println("RmiGame client is ready.");
		} catch (Exception e) {
			System.out.println("RmiGame client failed: " + e);
			e.printStackTrace();
		}

		System.out.println("Client started!");

		int id;
		String remoteHostName = args[1];
		String connectLocation = "rmi://" + remoteHostName + ":3000/Jogo";

		try {
			System.out.println("connecting to client at : " + connectLocation);
			JogoInterface game = (JogoInterface) Naming.lookup(connectLocation);
			System.out.println("calling register at: " + connectLocation + "...");
			if (game != null) {
				id = game.registra();
				System.out.println("my id is: " + id);
			}
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			System.out.println ("Client failed: " + e);
		}

		System.out.println("^C to quit...");
		while (true) {}
	}

}
