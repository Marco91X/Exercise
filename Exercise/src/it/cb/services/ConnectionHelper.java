package it.cb.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Classe utilizzata per connettersi al DB
 * @author mangiacavallim
 *
 */
public class ConnectionHelper {
	private String url;
	private static ConnectionHelper instance;

	private ConnectionHelper() {
		// Paremetri di connessione
		ResourceBundle bundle = ResourceBundle.getBundle("exercise");
		url = bundle.getString("jdbc.connection.url");
	}

	/**
	 * Recupera la connessione al DB
	 * @return la connessione
	 * @throws SQLException se si verifica un'eccezione SQL
	 */
	public static Connection getConnection() throws SQLException {
		if (instance == null) {
			instance = new ConnectionHelper();
		}
		try {
			return DriverManager.getConnection(instance.url);
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Chiude la connessione al DB
	 * @param connection la connessione da chiudere
	 */
	public static void close(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}