package it.cb.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta una transazione bancaria
 * @author mangiacavallim
 */
@XmlRootElement
public class Transazione {

	private String id;

	// Getter and setter
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
