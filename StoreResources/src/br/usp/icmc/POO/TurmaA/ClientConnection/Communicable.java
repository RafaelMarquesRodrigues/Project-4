package br.usp.icmc.POO.TurmaA.ClientConnection;

import br.usp.icmc.POO.TurmaA.Request.*;

public interface Communicable {
	void sendMessage(Request r);
}
