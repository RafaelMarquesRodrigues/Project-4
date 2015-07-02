package br.usp.icmc.POO.TurmaA.ClientConnection;

import java.io.*;

import br.usp.icmc.POO.TurmaA.Request.*;

import java.net.*;

public class UserSending implements Runnable {
	private Socket userSocket;
	private ObjectOutputStream  out;
	
	public UserSending(Socket s){
		userSocket = s;

	}
	
	public void run(){
		try {
			out = new ObjectOutputStream(userSocket.getOutputStream());
		}
		catch(IOException e){
			System.out.println("User sending:" + e);
		}
	}
	
	public void sendMessage(Request r){
		try {
			out.reset();
			out.writeObject(r);
		}
		catch(IOException e){
			System.out.println("Error sending request to server.");
		}
	}
}
