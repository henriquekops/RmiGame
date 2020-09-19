package src.server;

import src.client.JogadorInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ServerThread extends Thread {

	private final String remoteHostName;
	private final String function;

	public ServerThread(String remoteHostName, String function) {
		super();
		this.remoteHostName = remoteHostName;
		this.function = function;
	}

	@Override
	public void run() {
		switch (this.function) {
			case "cutuca":
				callCutuca();
			case "inicia":
				callInicia();
			case "finaliza":
				callFinaliza();
			default:
				this.interrupt();
		}
	}

	private void callCutuca() {
		try {
			JogadorInterface jogador = (JogadorInterface) Naming.lookup(this.remoteHostName);

			while (true) {
				if (jogador != null) {
					jogador.cutuca();
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

	private void callInicia() {
		try {
			JogadorInterface jogador = (JogadorInterface) Naming.lookup(this.remoteHostName);

			if (jogador != null) {
				jogador.inicia();
				System.out.println(this.remoteHostName + " started game!");
			} else {
				this.interrupt();
			}
		}
		catch( RemoteException | NotBoundException | MalformedURLException e) {
			System.out.println("Connection to '" + this.remoteHostName + "' was lost!");
			this.interrupt();
		}
	}

	private void callFinaliza() {
		try {
			JogadorInterface jogador = (JogadorInterface) Naming.lookup(this.remoteHostName);

			if (jogador != null) {
				jogador.finaliza();
				System.out.println(this.remoteHostName + " finished!");
			} else {
				this.interrupt();
			}
		}
		catch( RemoteException | NotBoundException | MalformedURLException e) {
			System.out.println("Connection to '" + this.remoteHostName + "' was lost!");
			this.interrupt();
		}
	}
}
