package br.usp.icmc.POO.TurmaA.ClientConnection;

public interface Connectable extends Communicable {
	void manageConnection();
	ClientData getUserData();
}
