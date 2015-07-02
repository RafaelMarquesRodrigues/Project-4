package br.usp.icmc.POO.TurmaA.Connection;

import br.usp.icmc.POO.TurmaA.Request.*;
import java.io.*;
import java.net.*;

import br.usp.icmc.POO.TurmaA.StoreManager.Manageable;
import br.usp.icmc.POO.TurmaA.Client.*;

public class ConnectionReceiving implements Runnable{
	private Socket userSocket;
	private Client user;
	private Manageable store;
	
	public ConnectionReceiving(Socket s, Manageable m, Client u){
		userSocket = s;
		user = u;
		store = m;
	}
	
	public void run(){
		ObjectInputStream in = null;
		
		try {
			in = new ObjectInputStream(userSocket.getInputStream());
			Request input;
			
			//envia as mensagens do cliente para o server
			while((input = (Request) in.readObject()) != null)
				store.receiveCommand(user.getData(), input);
			
			in.close();
		}
		catch(IOException e){}
		catch(ClassNotFoundException e){}
	}
}
