package it.cb.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta un importo
 * @author mangiacavallim
 */
@XmlRootElement
public class Importo {
	public static final String CURRENCY_CODE_EURO = "EUR";

	private BigDecimal amount;
	private String currency;

	// Getter and setter
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
