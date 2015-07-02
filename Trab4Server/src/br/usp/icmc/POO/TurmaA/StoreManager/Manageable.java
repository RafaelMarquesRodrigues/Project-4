package br.usp.icmc.POO.TurmaA.StoreManager;

import br.usp.icmc.POO.TurmaA.Request.*;
import br.usp.icmc.POO.TurmaA.ClientConnection.*;
import br.usp.icmc.POO.TurmaA.Client.*;

public interface Manageable {
	void addConnection(Connectable c);
	void addUser(Client u);
	void receiveCommand(ClientData u, Request r);
	void receiveCommand(String str);
	void signUpUser(String id, String password);
	void addProduct();
	boolean acceptConnection(String id);
	boolean acceptLogin(String id, String password);
	void removeConnection(Connectable c);
	Client getUser(String id);
}
