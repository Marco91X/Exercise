package it.cb.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta i dati relativi all'esecuzione del bonifico
 * @author mangiacavallim
 */
@XmlRootElement
public class DataBonificoExecute {

	private String cro;

	// Getter and setter
	public String getCro() {
		return cro;
	}

	public void setCro(String cro) {
		this.cro = cro;
	}

}
