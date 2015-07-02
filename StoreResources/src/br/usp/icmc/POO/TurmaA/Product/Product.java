package br.usp.icmc.POO.TurmaA.Product;

import java.io.Serializable;
	 
public class Product implements Serializable, Marketable{
	private static final long serialVersionUID = 1L;
	private final String name;
	private final double price;
	private final String validity;
	private final String provider;
	
	public Product(String n, double p, String v, String pr){
		name = n;
		price = p;
		validity = v;
		provider = pr;
	}
	
	public String getName(){
		return name;
	}
	
	public double getPrice(){
		return price;
	}
	
	public String getValidity(){
		return validity;
	}
	
	public String getProvider(){
		return provider;
	}
	
	@Override
	public boolean equals(Object o){
		Product p = (Product) o;
		return this.name.equals(p.getName()) && this.provider.equals(p.getProvider());
	}
}
