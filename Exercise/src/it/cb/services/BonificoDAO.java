package it.cb.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

import it.cb.model.Bonifico;

/**
 * Il DAO del bonifico per recuperare i dati da DB/storage
 * @author mangiacavallim
 */
public class BonificoDAO {
	// Usato per generare il CRO del bonifico
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	/**
	 * Verifica l'associazione cliente - conto corrente
	 * @param idCliente il codice del cliente
	 * @param idConto l'identificativo del conto corrente
	 * @return <code>true</code> se l'associazione è corretta, <code>false</code> altrimenti
	 */
	public boolean verifyClienteConto(String idCliente, String idConto) {
		boolean result = false;
		Connection c = null;
		String sql = "SELECT CIF FROM CLIENTE_CONTO WHERE CIF = ? AND CONTO = ?";
		try {
			c = ConnectionHelper.getConnection();
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, idCliente);
			ps.setString(2, idConto);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return result;
	}

	/**
	 * Verifica se il saldo sul conto corrente è sufficiente per effettuare un'operazione
	 * @param idCliente il codice del cliente
	 * @param idConto l'identificativo del conto corrente
	 * @param importoBonifico
	 * @return <code>true</code> se il saldo è sufficiente, <code>false</code> altrimenti
	 */
	public boolean verificaSaldo(String idCliente, String idConto, BigDecimal importoBonifico) {
		BigDecimal saldoContoCorrente = recuperaSaldo(idCliente, idConto);
		return saldoContoCorrente.compareTo(importoBonifico) >= 0;
	}

	/**
	 * Recupera il saldo del conto corrente
	 * @param idCliente il codice del cliente
	 * @param idConto l'identificativo del conto corrente
	 * @return il saldo recuperato
	 */
	private BigDecimal recuperaSaldo(String idCliente, String idConto) {
		BigDecimal saldoContoCorrente = new BigDecimal(0);
		Connection c = null;
		String sql = "SELECT C.SALDO FROM CLIENTE_CONTO CC INNER JOIN CONTO_CORRENTE C ON CC.CONTO = C.NUMERO WHERE CC.CIF = ? AND CC.CONTO = ?";
		try {
			c = ConnectionHelper.getConnection();
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, idCliente);
			ps.setString(2, idConto);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				saldoContoCorrente = processRowSaldoConto(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return saldoContoCorrente;
	}

	/**
	 * Inserisce la richiesta di bonifico
	 * @param idConto il conto corrente da cui effettuare il bonifico
	 * @param bonifico il bonifico da inserire
	 * @return l'id della transazione
	 */
	public String inserisciRichiestaBonifico(String idConto, Bonifico bonifico) {
		// Id bonifico = UUID
		String idBonifico = UUID.randomUUID().toString();
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = ConnectionHelper.getConnection();
			ps = c.prepareStatement("INSERT INTO BONIFICO (ID, CONTO_RICHIEDENTE, IMPORTO, VALUTA, DATA_ESECUZIONE, NOMINATIVO_BENEFICIARIO, IBAN_BENEFICIARIO, CAUSALE, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, idBonifico);
			ps.setString(2, idConto);
			ps.setBigDecimal(3, bonifico.getImporto().getAmount());
			ps.setString(4, bonifico.getImporto().getCurrency());
			ps.setDate(5, new java.sql.Date(bonifico.getDataEsecuzione().getTime()));
			ps.setString(6, bonifico.getNominativoBeneficiario());
			ps.setString(7, bonifico.getIbanBeneficiario());
			ps.setString(8, bonifico.getCausaleBonifico());
			ps.setString(9, Bonifico.STATUS_RICHIESTO);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return idBonifico;
	}

	/**
	 * Esegue un bonifico, detraendo dal saldo il relativo importo
	 * @param idCliente il codice del cliente
	 * @param idConto l'identificativo del conto corrente
	 * @param idTransazione la transazione del bonifico da eseguire
	 * @return il cRO del bonifico
	 */
	public String eseguiBonifico(String idCliente, String idConto, String idTransazione) {
		// CRO = random string
		String cro = generaCro();
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = ConnectionHelper.getConnection();
			// Seleziona importo bonifico
			ps = c.prepareStatement("SELECT IMPORTO FROM BONIFICO WHERE CONTO_RICHIEDENTE = (SELECT CONTO FROM CLIENTE_CONTO WHERE CIF = ? AND CONTO = ?)");
			ps.setString(1, idCliente);
			ps.setString(2, idConto);
			ResultSet rs = ps.executeQuery();
			BigDecimal importoBonifico = new BigDecimal(0);
			if(rs.next()) {
				importoBonifico = processRowImportoBonifico(rs);
			}
			// Aggiorna bonifico
			ps = c.prepareStatement("UPDATE BONIFICO SET STATUS = ?, CRO = ? WHERE ID = ?");
			ps.setString(1, Bonifico.STATUS_ESEGUITO);
			ps.setString(2, cro);
			ps.setString(3, idTransazione);
			ps.executeUpdate();
			// Aggiorna saldo
			ps = c.prepareStatement("UPDATE CONTO_CORRENTE SET SALDO = (SALDO - ?) WHERE NUMERO = ?");
			ps.setBigDecimal(1, importoBonifico);
			ps.setString(2, idConto);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return cro;
	}

	/**
	 * Funzione che genera il CRO di un bonifico
	 * @return
	 */
	private String generaCro() {
		Random random = new Random();
		StringBuilder builder = new StringBuilder(11);
		for (int i = 0; i < 11; i++) {
			builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}
		return builder.toString();
	}

	/**
	 * Estrare dal result set in input il saldo del conto corrente
	 * @param rs il result set
	 * @return il saldo
	 * @throws SQLException se si verifica un'eccezione SQL
	 */
	private BigDecimal processRowSaldoConto(ResultSet rs) throws SQLException {
		return rs.getBigDecimal("SALDO");
	}

	/**
	 * Estrare dal result set in input l'importo del bonifico
	 * @param rs il result set
	 * @return l'importo del bonifico
	 * @throws SQLException se si verifica un'eccezione SQL
	 */
	private BigDecimal processRowImportoBonifico(ResultSet rs) throws SQLException {
		return rs.getBigDecimal("IMPORTO");
	}
}
