package it.cb.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta la risposta del servizio bonificoPrepare
 * @author mangiacavallim
 */
@XmlRootElement
public class BonificoPrepareRes extends GenericResponse {

	private DataBonificoPrepare data;

	// Getter and setter
	public DataBonificoPrepare getData() {
		return data;
	}

	public void setData(DataBonificoPrepare data) {
		this.data = data;
	}

}
