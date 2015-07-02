package br.usp.icmc.POO.TurmaA.Store;

import br.usp.icmc.POO.TurmaA.Connection.*;
import br.usp.icmc.POO.TurmaA.StoreManager.*;
import java.io.*;
import java.net.*;

public class Store {
	public static void main(String[] args){
		StoreManager sm = StoreManager.getInstance();
		ServerSocket ss;
		
		try{
			ss = new ServerSocket(12345);
			
			System.out.println("Server started...");
		
			//********* SERVER - USER CONNECTION
			new Thread(() -> {
				Socket sock;
				Socket sockAux;
				Connection c = null;
				try {
					while((sock = ss.accept()) != null){
						sockAux = ss.accept();
						System.out.println("New connetion...");
						//pega a nova conexao com o usuario que a criou
						//comeca uma nova thread para o usuario
						c = new Connection(sock, sockAux, sm);
						new Thread(c).start();
					}
				}
				catch(IOException e){
					System.out.println("User connection ended " + e);
					sm.removeConnection(c);
				}
			}).start();
			//*****************************************
			
			
			help();
			
			//SERVER MANAGEMENT
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			
			try {
				while((input = br.readLine()) != null){
					sm.receiveCommand(input);
					
					if(input.equalsIgnoreCase("exit")) break;
					else if(input.equalsIgnoreCase("help"))	help();
				}
			}
			catch(IOException e){
				System.out.println("Error getting server side user input.");
			}
			
			ss.close();
		}
		catch(IOException e){
			System.out.println("Error creating server. " + e);
		}
	}
	
	private static void help(){
		System.out.println("\nSystem Commands: ");
		System.out.println("add product");
		System.out.println("update stock");
		System.out.println("show stock");
		System.out.println("show out of stock");
		System.out.println("show all");
		System.out.println("show users");
		System.out.println("generate monthly report");
		System.out.println("generate daily report");
		System.out.println("help");
		System.out.println("exit\n");
	}
}
