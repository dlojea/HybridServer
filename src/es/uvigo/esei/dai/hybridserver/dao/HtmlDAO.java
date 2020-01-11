package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import es.uvigo.esei.dai.hybridserver.Configuration;

public class HtmlDAO {
	
	private String user;
	private String password;
	private String url;
	
	public HtmlDAO (Properties properties) {
		this.user = properties.getProperty("db.user");
		this.password = properties.getProperty("db.password");
		this.url = properties.getProperty("db.url");
	}
	
	public HtmlDAO(Configuration config) {
		this.user = config.getDbUser();
		this.password = config.getDbPassword();
		this.url = config.getDbURL();
		
	}

	public String get (String uuid) {
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement(
					"SELECT * FROM HTML WHERE uuid = ?"
			)) {
				
				statement.setString(1, uuid);
				
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return result.getString("content");
					} else {
						return null; 
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> list() {
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet result = statement.executeQuery(
						"SELECT * FROM HTML"
				)) {
					List<String> pages = new ArrayList<>();
					
					while (result.next()) {
						pages.add(result.getString("uuid"));
					}
					
					return pages;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean contains (String uuid) {
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement(
					"SELECT * FROM HTML WHERE uuid = ?"
			)) {
				
				statement.setString(1, uuid);
				
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return true;
					} else {
						return false; 
					}
				}	
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void create (String uuid, String content) {
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO HTML (uuid, content) VALUES (?, ?)"
			)) {
				
				statement.setString(1, uuid);
				statement.setString(2, content);
				
				if (statement.executeUpdate() != 1)
					throw new SQLException("Error insertando página");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete (String uuid) {
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement(
				"DELETE FROM HTML WHERE uuid = ?"
			)) {
				
				statement.setString(1, uuid);
				
				if (statement.executeUpdate() != 1)
					throw new SQLException("Error borrando página");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
