package br.usp.icmc.POO.TurmaA.StoreManager;

import br.usp.icmc.POO.TurmaA.Product.*;
import br.usp.icmc.POO.TurmaA.Request.*;
import br.usp.icmc.POO.TurmaA.ClientConnection.*;
import br.usp.icmc.POO.TurmaA.Client.*;
import br.usp.icmc.POO.TurmaA.Purchase.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.io.*;

public class StoreManager extends StoreMaintenance implements Manageable  {
	private LinkedList<Connectable> connections;
	private static StoreManager store = null;
	
	private StoreManager(){
		connections = new LinkedList<Connectable>();
		users = new LinkedList<Client>();
		products = new HashMap<Marketable, Integer>();
		usersPasswords = new HashMap<String, String>();
		sells = new LinkedList<Purchase>();
		
		File file = new File("data/stock.csv");
		stockData = file.getPath();
		
		file = new File("data/users.csv");
		usersData = file.getPath();
		
		file = new File("data/logins.csv");
		loginsData = file.getPath();
		
		file = new File("data/sells.csv");
		sellsData = file.getPath();
		
		loadData();
	}
	
	public static StoreManager getInstance(){
		if(store == null)
			store = new StoreManager();
		return store; 
	}
	
	public void removeConnection(Connectable c){
		connections.remove(c);
	}
	
	public void addUser(Client u){
		System.out.println("Adding new user...");
		users.add(u);
	}
	
	public void addConnection(Connectable c){
		System.out.println("Adding new connection...");
		connections.add(c);
	}
	
	public synchronized boolean acceptConnection(String id){
		for(Client u : users){
			if(u.getId().equalsIgnoreCase(id))
				return false;
		}
		return true;
	}

	//receive a user command from the store
	public void receiveCommand(ClientData u, Request r){
		Optional<Connectable> oc = connections
			.stream()
			.filter(c -> c.getUserData().getId().equalsIgnoreCase(u.getId()))
			.findFirst();
		
		if(oc.isPresent()){
			if(r.getMessage().equalsIgnoreCase("buy product")){
				makeBuy(oc.get(), r);
			}
			else if(r.getMessage().equalsIgnoreCase("show products")){
				showProducts(oc.get());
			}
			else if(r.getMessage().equalsIgnoreCase("show acquired")){
				showAcquired(oc.get());
			}
		}
		else {
			Request fail = new Request("failed");
			fail.setContent("User not found.");
			oc.get().sendMessage(fail);
		}
	}
	
	//tells the client which products he/she has
	private void showAcquired(Connectable c){
		Map<Marketable, Integer> userProducts = getUser(c.getUserData().getId()).getProducts(); 
		Request returnReq;
		
		if(userProducts.size() > 0){
			returnReq = new Request("products data");
			returnReq.setContent(userProducts);		
		}
		else{
			returnReq = new Request("failed");
			returnReq.setContent("You don't have any products.");
		}
		
		c.sendMessage(returnReq);
	}
	
	//shows all the products at the store
	private void showProducts(Connectable c){
		Request returnReq;
		
		if(this.products.size() > 0){
			returnReq = new Request("products data");
			returnReq.setContent(this.products);
		}
		else{
			returnReq = new Request("failed");
			returnReq.setContent("We don't have any products to sell.");
		}
		
		c.sendMessage(returnReq);	
	}
	
	public synchronized void makeBuy(Connectable c, Request r){
		String[] parts = ((String) r.getContent()).split(",");
		int quantity = Integer.parseInt(parts[1]);
		String name = parts[0];
		String provider = parts[2];

		Optional<Marketable> p = getProduct(name, provider);
		Request buyRequest;
		
		if(p.isPresent() && this.products.get(p.get()).intValue() >= quantity){
			//decrements the amount of products of the type required by the user
			Integer quant = new Integer(this.products.get(p.get()).intValue() - quantity);
			this.products.remove(p.get());
			this.products.put(p.get(), quant);
			//adds the product to the user
			users.get(users.indexOf(getUser(c.getUserData().getId()))).addProduct(p.get(), new Integer(quantity));
			//tells the user the buy was successful
			c.sendMessage(new Request("product"));
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
			
			sells.add(new Purchase(c.getUserData().getId(), new Integer(quantity).toString(), p.get().getName(), p.get().getProvider(), LocalDate.now().format(formatter)));
		}
		else{
			//if the product is not available, returns a fail operation
			buyRequest = new Request("failed");
			//if another provider has the product
			if(!p.isPresent() && getProduct(name).isPresent())
				buyRequest.setContent("This provider doesn't have this product.");
			//if the store doesn't have the product
			else if(!getProduct(name).isPresent())
				buyRequest.setContent("We don't have this product.");
			//the store doesn't have the amount of products required
			else if(products.get(p.get()).intValue() > 0)
				buyRequest.setContent("We only have " + products.get(p.get()).intValue() + " copies of this product.");
			else
				buyRequest.setContent("We're sorry but this product is out of stock.");
		
			c.sendMessage(buyRequest);
		}		
	}
	
	public void signUpUser(String id, String password){
		usersPasswords.put(id, password);
	}
	
	public boolean acceptLogin(String id, String password){
		for(Map.Entry<String, String> entry : usersPasswords.entrySet()){
			if(id.equalsIgnoreCase(entry.getKey()) && password.equalsIgnoreCase(entry.getValue()))
				return true;
		}
		return false;
	}
	
	
}
