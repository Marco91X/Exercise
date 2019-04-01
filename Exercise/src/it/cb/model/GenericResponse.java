package it.cb.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta la risposta generica di una chiamata REST
 * @author mangiacavallim
 */
@XmlRootElement
public class GenericResponse {

	private Risultato result;

	// Getter and setter
	public Risultato getResult() {
		return result;
	}

	public void setResult(Risultato result) {
		this.result = result;
	}	

}
