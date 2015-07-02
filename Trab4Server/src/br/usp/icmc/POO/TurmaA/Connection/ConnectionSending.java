package br.usp.icmc.POO.TurmaA.Connection;

import br.usp.icmc.POO.TurmaA.Request.*;
import java.io.*;
import java.net.*;

public class ConnectionSending implements Runnable {
	private Socket userSocket;
	private ObjectOutputStream out;
	
	public ConnectionSending(Socket s){
		userSocket = s;
	}
	
	public void run(){
		try {
			out = new ObjectOutputStream(userSocket.getOutputStream());
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void sendMessage(Request r){
		try {
			out.reset();
			out.writeObject(r);
		}
		catch(IOException e){
			System.out.println("Error sending request to client.");
		}
	}
}
