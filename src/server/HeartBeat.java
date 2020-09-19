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
		try {
			JogadorInterface player = (JogadorInterface) Naming.lookup(this.remoteHostName);

			while (true) {
				if (player != null) {
					player.cutuca();
					System.out.println("Heart beat from '" + this.remoteHostName + "', it is alive!");
				} else {
					this.interrupt();
				}
				Thread.sleep(3000);
			}
		}
		catch( RemoteException | InterruptedException | NotBoundException | MalformedURLException e) {
			System.out.println("Connection to '" + this.remoteHostName + "' was lost!");
			Jogo.players[playerId] = null;
			this.interrupt();
		}
	}
}
