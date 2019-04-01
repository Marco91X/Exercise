package it.cb.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta la risposta del servizio bonificoVerify
 * @author mangiacavallim
 */
@XmlRootElement
public class BonificoVerifyRes extends GenericResponse {

	private ImportoTransazione data;
	private Transazione transaction;

	// Getter and setter
	public ImportoTransazione getData() {
		return data;
	}
	public void setData(ImportoTransazione data) {
		this.data = data;
	}
	public Transazione getTransaction() {
		return transaction;
	}
	public void setTransaction(Transazione transaction) {
		this.transaction = transaction;
	}


}
