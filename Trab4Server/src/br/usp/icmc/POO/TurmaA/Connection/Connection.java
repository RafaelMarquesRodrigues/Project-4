package br.usp.icmc.POO.TurmaA.Connection;

import br.usp.icmc.POO.TurmaA.Client.User;
import br.usp.icmc.POO.TurmaA.Request.*;
import br.usp.icmc.POO.TurmaA.StoreManager.*;
import br.usp.icmc.POO.TurmaA.Client.*;
import br.usp.icmc.POO.TurmaA.ClientConnection.*;

import java.net.*;
import java.io.*;

public class Connection implements Connectable, Runnable{
	private Socket userSocket;
	private Socket sockAux;
	private Manageable store;
	private Client user;
	private ConnectionSending sender;
	
	public Connection(Socket s, Socket sa, Manageable m){
		userSocket = s;
		store = m;
		sockAux = sa;
	}
	
	public void run(){
		createUser();
		manageConnection();
	}
	
	public void manageConnection(){
		System.out.println("Managing server side connection...");
		sender = new ConnectionSending(userSocket);
		
		try {
			new Thread(sender).start();
			new Thread(new ConnectionReceiving(userSocket, store, user)).start();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void createUser(){
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		
		try {
			boolean userOk = false;
			in = new ObjectInputStream(sockAux.getInputStream());
			out = new ObjectOutputStream(sockAux.getOutputStream());
			String option = ((String) in.readObject());
			
			if(option.equalsIgnoreCase("exit")){
				in.close();
				out.close();
				sockAux.close();
				userSocket.close();
				return;
			}
			
			//checks if the user is attempting a login
			if(option.equalsIgnoreCase("login")){
				while(!userOk){
					String id = (String) in.readObject();
					String password = (String) in.readObject();
					
					if(store.acceptLogin(id, password)){
						System.out.println("User " + id + " connected.");
						userOk = true;
						//informs the store app that the connection was successfull
						out.writeObject(true);
						user = store.getUser(id);
					}
					else{
						System.out.println("Couldn't connect user. Restarting login process...");
						out.writeObject(false);
					}
				}
				
			}
			//the OnlineStore.java will only let the words "login" and "sign up" pass to the server, so we don't need to check
			//others string values
			else{
				while(!userOk){
					//sign up the user
					System.out.println("Getting new user data...");
					
					//reads the sign up information
					String data = (String) in.readObject();
					String parts[] = data.split(",");
					
					boolean ok = store.acceptConnection(parts[0]);
					
					if(ok){
						System.out.println("ID ok. Adding new user...");
						userOk = true;
						//creates a new set of data and a new user
						ClientData userData = new UserData(parts[0], parts[1], parts[2], parts[3], parts[4]);
						user = new User(userData);
						//adds the user password/id to the store
						store.signUpUser(parts[0], parts[5]);
						//tells the online store the operation was ok
						out.writeObject(true);
						//adds the user to the store
						store.addUser(user);
					}
					else{
						System.out.println("ID already in use. Restarting sign up process...");
						out.writeObject(false);
					}
				}
			}
			
			//sends the userData back to the client 
			//out.writeObject(user.getData());
			out.close();
			in.close();
			sockAux.close();
			//adds the connection to the store
			store.addConnection(this);
		}
		catch(IOException e){
			System.out.println("Error creating user. " + e);
			try{
				in.close();
				out.close();
				userSocket.close();
				sockAux.close();
			}
			catch(IOException ioe){
				System.out.println("Error closing connetion. " + ioe);
			}
		}
		catch(ClassNotFoundException e){
			System.out.println("Error creating user. " + e);
		}
	}

	public ClientData getUserData(){
		return user.getData();
	}
	
	public Client getUser(){
		return user;
	}
	
	public void sendMessage(Request r){
		sender.sendMessage(r);
	}
}
