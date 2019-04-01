package it.cb.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta le date di risposta della bonifico prepare
 * @author mangiacavallim
 */
@XmlRootElement
public class DataBonificoPrepare {

	private Date oggi;
	private Date dataLimite;

	// Getter and setter
	public Date getOggi() {
		return oggi;
	}
	public void setOggi(Date oggi) {
		this.oggi = oggi;
	}
	public Date getDataLimite() {
		return dataLimite;
	}
	public void setDataLimite(Date dataLimite) {
		this.dataLimite = dataLimite;
	}

}
