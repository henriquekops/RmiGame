package src.server;

import src.client.Jogador;
import src.client.JogadorInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class HeartBeat extends Thread {

	private final String remoteHostName;
	private final int playerId;

	public HeartBeat(String remoteHostName, int playerId) {
		super();
		this.remoteHostName = remoteHostName;
		this.playerId = playerId;
	}

	@Override
	public void run() {
		/*
		 * Run heartbeat thread for client health check
		 */
		try {
			JogadorInterface player = (JogadorInterface) Naming.lookup(this.remoteHostName);

			while (true) {
				if (player != null) {
					player.cutuca();
					System.out.println("heart beat from client '" + this.remoteHostName + "', it is alive!");
				} else {
					this.interrupt();
				}
				Thread.sleep(3000);
			}
		}
		catch( RemoteException | InterruptedException | NotBoundException | MalformedURLException e) {
			System.out.println("lost connection to client '" + this.remoteHostName + "'!");
			Jogo.players[playerId] = null;
			this.interrupt();
		}
	}
}
