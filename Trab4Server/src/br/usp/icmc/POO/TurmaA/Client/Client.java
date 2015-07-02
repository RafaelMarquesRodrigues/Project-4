package br.usp.icmc.POO.TurmaA.Client;

import java.util.HashMap;
import br.usp.icmc.POO.TurmaA.Product.Marketable;
import br.usp.icmc.POO.TurmaA.ClientConnection.*;

public interface Client {
	public void addProduct(Marketable p, Integer i);
	public HashMap<Marketable, Integer> getProducts();
	public String getId();
	public String getName();
	public String getAdress();
	public String getPhone();
	public String getEmail();
	public ClientData getData();
}
