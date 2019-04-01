package it.cb.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta un bonifico bancario
 * @author mangiacavallim
 */
@XmlRootElement
public class Bonifico {
	public static final String STATUS_RICHIESTO = "RQ";
	public static final String STATUS_ESEGUITO = "EX";

	private String id;
	private Importo importo;
	private Date dataEsecuzione;
	private String nominativoBeneficiario;
	private String ibanBeneficiario;
	private String causaleBonifico;

	// Getter and setter
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Importo getImporto() {
		return importo;
	}
	public void setImporto(Importo importo) {
		this.importo = importo;
	}
	public Date getDataEsecuzione() {
		return dataEsecuzione;
	}
	public void setDataEsecuzione(Date dataEsecuzione) {
		this.dataEsecuzione = dataEsecuzione;
	}
	public String getNominativoBeneficiario() {
		return nominativoBeneficiario;
	}
	public void setNominativoBeneficiario(String nominativoBeneficiario) {
		this.nominativoBeneficiario = nominativoBeneficiario;
	}
	public String getIbanBeneficiario() {
		return ibanBeneficiario;
	}
	public void setIbanBeneficiario(String ibanBeneficiario) {
		this.ibanBeneficiario = ibanBeneficiario;
	}
	public String getCausaleBonifico() {
		return causaleBonifico;
	}
	public void setCausaleBonifico(String causaleBonifico) {
		this.causaleBonifico = causaleBonifico;
	}
}
