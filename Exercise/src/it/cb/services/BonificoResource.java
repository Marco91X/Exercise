package it.cb.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import it.cb.model.Bonifico;
import it.cb.model.BonificoExecuteRes;
import it.cb.model.BonificoPrepareRes;
import it.cb.model.BonificoVerifyRes;
import it.cb.model.DataBonificoExecute;
import it.cb.model.DataBonificoPrepare;
import it.cb.model.Importo;
import it.cb.model.ImportoTransazione;
import it.cb.model.Risultato;
import it.cb.model.Transazione;

/**
 * Classe che espone i servizi REST
 * @author mangiacavallim
 */
@Path("/private")
public class BonificoResource {
	private static final String PROFILE_TEST = "test";
	
	// DAO per eseguire le chiamate al DB
	BonificoDAO dao = new BonificoDAO();

	/**
	 * Servizio REST che predispone il bonifico
	 * @param idCliente il codice del cliente
	 * @param idConto l'identificativo del conto corrente
	 * @return la risposta del servizio
	 */
	@GET @Path("/cliente/{idCliente}/conto/{idConto}/bonifico/prepare")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public BonificoPrepareRes bonificoPrepare(@PathParam("idCliente") String idCliente, @PathParam("idConto") String idConto) {
		System.out.println("bonificoPrepare");
		BonificoPrepareRes bonificoPrepareRes = new BonificoPrepareRes();
		ResourceBundle bundle = ResourceBundle.getBundle("exercise");
		String profile = bundle.getString("profile");
		if(PROFILE_TEST.equals(profile)) {
			bonificoPrepareRes = mockBonificoPrepare();
		} else {
			boolean result = dao.verifyClienteConto(idCliente, idConto);
			if(result) {
				bonificoPrepareRes.setResult(createResult(Risultato.OUTCOME_SUCCESS));
				bonificoPrepareRes.setData(getDateBonifico());
			} else {
				bonificoPrepareRes.setResult(createResult(Risultato.OUTCOME_ERROR, new String[]{"Cliente non trovato!"}));
			}
		}
		return bonificoPrepareRes;
	}

	/**
	 * Servizio REST che verifica se il bonifico può essere effettuato
	 * @param idCliente il codice del cliente
	 * @param idConto l'identificativo del conto corrente
	 * @param bonifico il bonifico da effettuare
	 * @return la risposta del servizio
	 */
	@POST @Path("cliente/{idCliente}/conto/{idConto}/bonifico/verify")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public BonificoVerifyRes bonificoVerify(@PathParam("idCliente") String idCliente, @PathParam("idConto") String idConto, Bonifico bonifico) {
		System.out.println("bonificoVerify");
		BonificoVerifyRes bonificoVerifyRes = new BonificoVerifyRes();
		ResourceBundle bundle = ResourceBundle.getBundle("exercise");
		String profile = bundle.getString("profile");
		if(PROFILE_TEST.equals(profile)) {
			return mockBonificoVerify();
		} else {
			BigDecimal soglia = new BigDecimal(bundle.getString("bonifico.soglia.importo"));
			if(bonifico.getImporto().getAmount().compareTo(soglia) > 0) {
				bonificoVerifyRes.setResult(createResult(Risultato.OUTCOME_WARNING, new String[]{"Per disporre un bonifico superiore a " + soglia + " euro, contatta il servizio clienti."}));
			} else {
				boolean saldoSufficiente = dao.verificaSaldo(idCliente, idConto, bonifico.getImporto().getAmount());
				if(!saldoSufficiente) {
					bonificoVerifyRes.setResult(createResult(Risultato.OUTCOME_WARNING, new String[]{"Saldo sul conto non sufficiente per effettuare l'operazione! Inserire un nuovo importo"}));
				} else {
					String transactionId = dao.inserisciRichiestaBonifico(idConto, bonifico);
					bonificoVerifyRes.setResult(createResult(Risultato.OUTCOME_SUCCESS));
					Transazione transazione = new Transazione();
					transazione.setId(transactionId);
					bonificoVerifyRes.setTransaction(transazione);
					ImportoTransazione importoTransazione = new ImportoTransazione();
					Importo importo = new Importo();
					importo.setAmount(bonifico.getImporto().getAmount());
					importo.setCurrency(bonifico.getImporto().getCurrency());
					importoTransazione.setTotalAmount(importo);
					importo = new Importo();
					importo.setAmount(new BigDecimal(0));
					importo.setCurrency(bonifico.getImporto().getCurrency());
					importoTransazione.setCommissions(importo);
					bonificoVerifyRes.setData(importoTransazione);
				}
			}
		}
		return bonificoVerifyRes;
	}

	/**
	 * Servizio REST che esegue il bonifico
	 * @param idCliente il codice del cliente
	 * @param idConto l'identificativo del conto corrente
	 * @param idTransazione l'identificativo univoco della transazione
	 * @return la risposta del servizio
	 */
	@PUT @Path("cliente/{idCliente}/conto/{idConto}/bonifico/{idTransazione}/execute")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public BonificoExecuteRes bonificoExecute(@PathParam("idCliente") String idCliente, @PathParam("idConto") String idConto, @PathParam("idTransazione") String idTransazione) {
		System.out.println("bonificoExecute");
		BonificoExecuteRes bonificoExecuteRes = new BonificoExecuteRes();
		ResourceBundle bundle = ResourceBundle.getBundle("exercise");
		String profile = bundle.getString("profile");
		if(PROFILE_TEST.equals(profile)) {
			bonificoExecuteRes = mockBonificoExecute();
		} else {
			String cro = dao.eseguiBonifico(idCliente, idConto, idTransazione);
			bonificoExecuteRes.setResult(createResult(Risultato.OUTCOME_SUCCESS, new String[]{"Bonifico effettuato con successo!", "Una volta prodotta, troverai la contabile delle tue operazioni tra i documenti!"}));
			DataBonificoExecute dataBonificoExecute = new DataBonificoExecute();
			dataBonificoExecute.setCro(cro);
			bonificoExecuteRes.setData(dataBonificoExecute);
		}
		return bonificoExecuteRes;
	}

	/**
	 * Metodo che crea l'ggetto che rappresenta la risposta di un servizio REST
	 * @param outcome il risultato della chiamata REST
	 * @param messages eventuali messaggi da restituire
	 * @return
	 */
	private Risultato createResult(String outcome, String[] messages) {
		Risultato risultato = createResult(outcome);
		risultato.setMessages(messages);
		return risultato;
	}

	/**
	 * Metodo che crea l'ggetto che rappresenta la risposta di un servizio REST
	 * @param outcome il risultato della chiamata REST
	 * @return
	 */
	private Risultato createResult(String outcome) {
		Risultato risultato = new Risultato();
		risultato.setRequestId(UUID.randomUUID().toString());
		risultato.setOutcome(outcome);
		return risultato;
	}

	/**
	 * Crea l'oggetto di output della bonifico prepare
	 * @return
	 */
	private DataBonificoPrepare getDateBonifico() {
		ResourceBundle bundle = ResourceBundle.getBundle("exercise");
		int daysToAdd = Integer.valueOf(bundle.getString("bonifico.soglia.tempo"));
		DataBonificoPrepare data = new DataBonificoPrepare();
		data.setOggi(new Date());
		// Oggi + 1 anno
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, daysToAdd);
		data.setDataLimite(c.getTime());
		return data;
	}

	/**
	 *Mock del servizio
	 * @return la response mockata
	 */
	private BonificoPrepareRes mockBonificoPrepare() {
		BonificoPrepareRes res = new BonificoPrepareRes();
		res.setResult(mockResultSuccess());
		res.setData(getDateBonifico());
		return res;
	}

	/**
	 * Mock del servizio
	 * @return la response mockata
	 */
	private BonificoVerifyRes mockBonificoVerify() {
		BonificoVerifyRes res = new BonificoVerifyRes();
		res.setResult(mockResultSuccess());
		Transazione transazione = new Transazione();
		transazione.setId("c5456ec0-96cb-4ab0-abf8-9e2ff9b54ed0");
		res.setTransaction(transazione);
		ImportoTransazione importoTransazione = new ImportoTransazione();
		Importo importo = new Importo();
		importo.setAmount(new BigDecimal(12));
		importo.setCurrency(Importo.CURRENCY_CODE_EURO);
		importoTransazione.setTotalAmount(importo);
		importo = new Importo();
		importo.setAmount(new BigDecimal(0));
		importo.setCurrency(Importo.CURRENCY_CODE_EURO);
		importoTransazione.setCommissions(importo);
		res.setData(importoTransazione);
		return res;
	}

	/**
	 * Mock del servizio
	 * @return la response mockata
	 */
	private BonificoExecuteRes mockBonificoExecute() {
		BonificoExecuteRes res = new BonificoExecuteRes();
		res.setResult(mockResultSuccess());
		String[] messages = new String[2];
		messages[0] = "Hai effettuato un bonifico a Gino Rossi!";
		messages[1] = "Una volta prodotta, troverai la contabile delle tue operazioni tra i documenti!";
		res.getResult().setMessages(messages);
		DataBonificoExecute dataBonificoExecute = new DataBonificoExecute();
		dataBonificoExecute.setCro("1902151800119068480160403200IT79993");
		res.setData(dataBonificoExecute);
		return res;
	}

	/**
	 * Mock della chiamata REST eseguita con successo
	 * @return oggetto mockato
	 */
	private Risultato mockResultSuccess() {
		Risultato risultato = new Risultato();
		risultato.setOutcome(Risultato.OUTCOME_SUCCESS);
		risultato.setRequestId("XGbsdgrZ5gsAAJ7vHrcAAAAb");
		return risultato;
	}

}
