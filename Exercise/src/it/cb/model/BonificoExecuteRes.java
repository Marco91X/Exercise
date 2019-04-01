package it.cb.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta la risposta del servizio bonificoExecute
 * @author mangiacavallim
 */
@XmlRootElement
public class BonificoExecuteRes extends GenericResponse {

	private DataBonificoExecute data;

	// Getter and setter
	public DataBonificoExecute getData() {
		return data;
	}

	public void setData(DataBonificoExecute data) {
		this.data = data;
	}

}
