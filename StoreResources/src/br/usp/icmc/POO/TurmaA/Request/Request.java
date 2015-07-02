package br.usp.icmc.POO.TurmaA.Request;

import java.io.Serializable;
import br.usp.icmc.POO.TurmaA.ClientConnection.*;
	
public class Request implements Serializable {
	private String message;
	private Object content;
	private ClientData data;
	private static final long serialVersionUID = 1L;
	
	public Request(String str){
		message = str;
	}
	
	public Request(String str, ClientData data){
		message = str;
		this.data = data;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setContent(Object o){
		content = o;
	}
	
	public Object getContent(){
		return content;
	}
	
	public ClientData getData(){
		return data;
	}
}
