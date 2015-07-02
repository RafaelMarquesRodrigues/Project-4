package br.usp.icmc.POO.TurmaA.ClientConnection;

import br.usp.icmc.POO.TurmaA.Product.Marketable;
import br.usp.icmc.POO.TurmaA.Request.*;

import java.io.*;
import java.net.*;
import java.util.Map;

public class UserReceiving implements Runnable{
	private Socket userSocket;
	
	public UserReceiving(Socket s){
		userSocket = s;
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		ObjectInputStream in = null;
		
		try {
			in = new ObjectInputStream(userSocket.getInputStream());
			Request input;
			
			//envia as mensagens do cliente para o server
			while((input = (Request) in.readObject()) != null){
				if(input.getMessage().equalsIgnoreCase("product"))
					System.out.println("New product acquired.");
				else if(input.getMessage().equalsIgnoreCase("products data"))
					showProducts((Map<Marketable, Integer>) input.getContent());
				else if(input.getMessage().equalsIgnoreCase("failed"))
					System.out.println("Error: " + input.getContent());
			}
			
			in.close();
		}
		catch(IOException e){}
		catch(ClassNotFoundException e){}
	}
	
	private void showProducts(Map<Marketable, Integer> products){
		products.entrySet()
			.stream()
			.forEach((entry) ->{
				Marketable p = entry.getKey();
				System.out.println("Product " + p.getName() + " - Price: " + p.getPrice() + " - Validity: " + p.getValidity()
				+ " - Provider: " + p.getProvider() + 
				(entry.getValue().intValue() > 0 ?  " - Quantity: " + (entry.getValue() + (entry.getValue() == 1 ? " copy" : " copies")) : " - Out of Stock"));
		});
	}
}
