package src.server;

import src.client.JogadorInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class HeartBeat extends Thread {

	private final String remoteHostName;

	public HeartBeat(String remoteHostName) {
		super();
		this.remoteHostName = remoteHostName;
	}

	@Override
	public void run() {
		try {
			JogadorInterface player = (JogadorInterface) Naming.lookup(this.remoteHostName);

			while (true) {
				if (player != null) {
					player.cutuca();
					System.out.println(this.remoteHostName + " is alive!");
				} else {
					this.interrupt();
				}
				Thread.sleep(3000);
			}
		}
		catch( RemoteException | InterruptedException | NotBoundException | MalformedURLException e) {
			System.out.println("Connection to '" + this.remoteHostName + "' was lost!");
			this.interrupt();
		}
	}
}
