package br.usp.icmc.POO.TurmaA.ClientConnection;

public class UserData implements ClientData{
	private final String id;
	private final String name;
	private final String adress;
	private final String phone;
	private final String email;
	
	public UserData(String id, String name, String adress, String phone, String email){
		this.id = id;
		this.name = name;
		this.adress = adress;
		this.phone = phone;
		this.email = email;
	}
	
	public String getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getAdress(){
		return adress;
	}
	
	public String getPhone(){
		return phone;
	}

	public String getEmail(){
		return email;
	}
}
