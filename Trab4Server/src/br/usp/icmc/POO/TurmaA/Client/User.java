package br.usp.icmc.POO.TurmaA.Client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import br.usp.icmc.POO.TurmaA.ClientConnection.*;
import br.usp.icmc.POO.TurmaA.Product.*;

public class User implements Serializable, Client {
	private ClientData data;
	private HashMap<Marketable, Integer> products;
	private static final long serialVersionUID = 1L;
	
	public User(ClientData data){
		this.data = data;
		products = new HashMap<Marketable, Integer>();
	}

	public void addProduct(Marketable product, Integer i){
		Optional<Marketable> om = products
								.entrySet()
								.stream()
								.filter((entry) -> entry.getKey().getName().equalsIgnoreCase(product.getName()) && entry.getKey().getProvider().equalsIgnoreCase(product.getProvider()))
								.map(Map.Entry::getKey)
								.findFirst();
		
		if(om.isPresent())
			products.put(om.get(), new Integer(i.intValue() + products.get(om.get()).intValue()));
		else
			products.put(product, i);
	}
	
	public HashMap<Marketable, Integer> getProducts(){
		return products;
	}
	
	public ClientData getData(){
		return data;
	}
	
	public String getId(){
		return data.getId();
	}
	
	public String getName(){
		return data.getName();
	}
	
	public String getAdress(){
		return data.getAdress();
	}
	
	public String getPhone(){
		return data.getPhone();
	}

	public String getEmail(){
		return data.getEmail();
	}
}
