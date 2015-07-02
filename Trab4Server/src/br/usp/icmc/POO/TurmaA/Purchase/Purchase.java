package br.usp.icmc.POO.TurmaA.Purchase;

public class Purchase {
	String buyer;
	String product;
	String date;
	String quantity;
	String provider;
	
	public Purchase(String buyer, String quantity, String product, String provider, String date){
		this.buyer = buyer;
		this.date = date;
		this.product = product;
		this.quantity = quantity;
		this.provider = provider;
	}
	
	public String getBuyer(){
		return buyer;
	}
	
	public String getProduct(){
		return product;
	}

	public String getQuantity(){
		return quantity;
	}
	
	public String getProvider(){
		return provider;
	}
	
	public String getDate(){
		return date;
	}
}
