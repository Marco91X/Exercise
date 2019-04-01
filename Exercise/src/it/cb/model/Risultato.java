package it.cb.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che rappresenta il risultato della chiamata REST
 * @author mangiacavallim
 */
@XmlRootElement
public class Risultato {
	public static final String OUTCOME_SUCCESS = "SUCCESS";
	public static final String OUTCOME_WARNING = "WARNING";
	public static final String OUTCOME_ERROR = "ERROR";
	
	private String[] messages;
	private String outcome;
	private String requestId;
	
	// Getter and setter
	public String[] getMessages() {
		return messages;
	}
	public void setMessages(String[] messages) {
		this.messages = messages;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}
