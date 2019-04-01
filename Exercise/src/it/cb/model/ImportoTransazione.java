package it.cb.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta gli importi di una transazione
 * @author mangiacavallim
 */
@XmlRootElement
public class ImportoTransazione {

	private Importo totalAmount;
	private Importo commissions;

	// Getter and setter
	public Importo getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Importo totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Importo getCommissions() {
		return commissions;
	}
	public void setCommissions(Importo commissions) {
		this.commissions = commissions;
	}
}
