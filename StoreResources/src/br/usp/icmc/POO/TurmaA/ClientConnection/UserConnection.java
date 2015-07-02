package br.usp.icmc.POO.TurmaA.ClientConnection;

import java.net.*;
import br.usp.icmc.POO.TurmaA.Request.*;
	
public class UserConnection implements Connectable{
	private Socket userSocket;
	private UserSending sender;
	private ClientData userData;
		
	public UserConnection(Socket s){
		userSocket = s;
	}
		
	public void manageConnection(){
		sender = new UserSending(userSocket);
		try {
			new Thread(new UserReceiving(userSocket)).start();
			new Thread(sender).start();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void sendMessage(Request r){
		sender.sendMessage(r);
	}
	
	public void setUserData(ClientData data){
		userData = data;
	}
	
	public ClientData getUserData(){
		return userData;
	}
}
