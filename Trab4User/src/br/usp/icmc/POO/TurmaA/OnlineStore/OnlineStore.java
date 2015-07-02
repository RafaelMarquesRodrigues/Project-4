package br.usp.icmc.POO.TurmaA.OnlineStore;

import br.usp.icmc.POO.TurmaA.Request.*;
import br.usp.icmc.POO.TurmaA.ClientConnection.*;
import java.io.*;
import java.net.*;

public class OnlineStore {
	private static UserConnection uc;
	private static BufferedReader br;
	
	public static void main(String[] args){
		try {
			String input;
			Socket s = new Socket("localhost", 12345);
			Socket us = new Socket("localhost", 12345);
			
			//gets the user's id
			br = new BufferedReader(new InputStreamReader(System.in));
			
			//makes login/sign up
			if(!connect(us)){
				br.close();
				s.close();
				us.close();
				return;
			}
			
			us.close();
			
			//creates a new connection for the user
			uc = new UserConnection(s);

			//starts the connection with the server
			uc.manageConnection();
			
			help();
			
				//receives user input and send to the server
				while((input = br.readLine()) != null){
					if(input.equalsIgnoreCase("buy product"))
						buyProduct();
					else if(input.equalsIgnoreCase("exit"))
						break;
					else if(input.equalsIgnoreCase("help"))
						help();
					else
						uc.sendMessage(new Request(input));
				}

				s.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public static boolean connect(Socket us){
		try{
			boolean connectionOk = false;
			ObjectOutputStream out = new ObjectOutputStream(us.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(us.getInputStream());
			String input = null;
			System.out.println("Welcome to the store !");
			
			while(!connectionOk){
			
				System.out.println("Do you want to login or sign up ? [login/sign up/exit]");
			
				input = br.readLine();
				
				if(input.equalsIgnoreCase("login") || input.equalsIgnoreCase("sign up"))
					connectionOk = true;
				else if(input.equalsIgnoreCase("exit")){
					out.writeObject("exit");
					in.close();
					out.close();
					return false;
				}
				else
					System.out.println("Please enter a valid option.");
			}
			
			connectionOk = false;
			
			if(input.equalsIgnoreCase("login")){
				//inform the server that we will make a login
				out.writeObject("login");
				
				while(!connectionOk){
					System.out.println("Please enter your id: ");
					out.writeObject(br.readLine());
					System.out.println("Please enter your password: ");
					out.writeObject(br.readLine());
					
					//waits for the server to check the data received
					connectionOk = (boolean) in.readObject();
					
					if(!connectionOk)
						System.out.println("Wrong username or password. Please try again.");
					else
						System.out.println("Connected.");
				}
			}
			else if(input.equalsIgnoreCase("sign up")){
				//inform the server that we will make a sign up
				out.writeObject("sign up");
				
				while(!connectionOk){
					String userData = "";
					String separator = ",";
					
					System.out.println("(UNIQUE) Please enter user id: ");
					userData += br.readLine() + separator;
					System.out.println("Name ");
					userData +=  br.readLine() + separator;
					System.out.println("Adress ");
					userData += br.readLine() + separator;
					System.out.println("Phone ");
					userData += br.readLine() + separator;
					System.out.println("Email ");
					userData += br.readLine() + separator;
					System.out.println("Passwor ");
					userData += br.readLine();
					//sends the data to the server
					out.writeObject(userData);
					connectionOk = (boolean) in.readObject();

					if(!connectionOk)
						System.out.println("ID already in use. Please choose another id.\n");
					else
						System.out.println("Connection accepted.");
				}
			}		
			
			out.close();
			in.close();
		}
		catch(IOException e){
			System.out.println("Error receiving messages " + e);
		}
		catch(ClassNotFoundException e){
			System.out.println("Class not found.");
		}
		
		return true;
	}
	
	public static void buyProduct(){
		System.out.println("Enter the name of the product you wanna buy");

		try{
			String name = br.readLine();
			String quant;
			System.out.println("Enter the quantity of the product you wanna buy");
			
			do
				quant = br.readLine();
			while(!isInteger(quant));
	
			System.out.println("Enter the provider you wanna buy from");
			Request r = new Request("buy product");
			r.setContent(name + "," + quant + "," + br.readLine());
			uc.sendMessage(r);

		}
		catch(IOException e){
			System.out.println("Error getting user input when purchasing product.");
		}	
	}
	
	private static boolean isInteger(String s){
		try{
			Integer.parseInt(s);
		}
		catch(Exception e){
			System.out.println("Please enter a valid number.");
			return false;
		}
		
		return true;
	}
	
	private static void help(){
		System.out.println("Available commands are: ");
		System.out.println("show products");
		System.out.println("show acquired");
		System.out.println("buy product");
		System.out.println("help");
		System.out.println("exit");
	}
}
