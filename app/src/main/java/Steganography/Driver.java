package Steganography;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Driver {
	public static String fetch(String UserName) {
		String DbUserName ="postgres" ;
		String DbPassword = "postgres";
		
		String JdbcUrl = "jdbc:postgresql://localhost:5432/steganography";

		try {
			Connection connection = DriverManager.getConnection(JdbcUrl, DbUserName, DbPassword);
			
			String sql = "select password from userinfo where username=?;";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, UserName);
			ResultSet rs = statement.executeQuery();
			connection.close();
			if(rs.next()) {
				return rs.getString(1);
			}
			else {
				return null;
			}
		} 
		catch (SQLException e) {
			System.out.println("Error in connecting to Postgresql database");
			e.printStackTrace();
			return null;
		}
	}

    public static void put(String UserName, String Password) {
		//DB variables	table name , postgres username and password
		String DbUserName ="postgres" ;
		String DbPassword = "postgres";
		
		//User input to  be added here along with password
		// UserName = "johndoe";
		// Password = "Encryptionpassword";
		
		String JdbcUrl = "jdbc:postgresql://localhost:5432/steganography";

		try {
			Connection connection = DriverManager.getConnection(JdbcUrl, DbUserName, DbPassword);
			// System.out.println("Successfully connected to PostgreSQL Db");
			// EncData = Encrypt.EncryptText(UserName, Password, Text);
			// Decrypt.DecryptText(EncData[0], UserName, Password);
			// System.out.println(Base64.getEncoder().encodeToString(EncData[0]));
			//Password = EncData; Base64.getEncoder().encodeToString(EncData[0]);
			
			String sql = "INSERT INTO userinfo (username,password) VALUES (?,?);";
			PreparedStatement statement = connection.prepareStatement(sql);
			
			//Setting uname and password in DB
			statement.setString(1, UserName);
			statement.setString(2, Password);
			int rows = statement.executeUpdate();
			if(rows > 0) {
				System.out.println("Values inserted into the database");
			}
			connection.close();
		} 
		catch (SQLException e) {
			System.out.println("Error in connecting to Postgresql database");
			e.printStackTrace();
		}
    }
}
