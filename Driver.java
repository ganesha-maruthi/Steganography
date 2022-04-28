import EncDec.Decrypt;
import EncDec.Encrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Driver {
   
    public static void main(String []args) {
    
    //DB variables	table name , postgres username and password
    String DbName="UserInfo";
    String DbUserName ="postgres" ;
    String DbPassword = "postgres";
    
    //User input to  be added here along with password
 	String UserName = "johndoe";
    String Text = "Random Text to be Encrypted";
    String Password = "Encryptionpassword";
    
    String JdbcUrl = "jdbc:postgresql://localhost:5432/Steganography";
    byte [][] EncData;
 
    
    try {
			Connection connection = DriverManager.getConnection(JdbcUrl,DbUserName,DbPassword);
			System.out.println("Successfully connected to PostgreSQL Db");
		    EncData = Encrypt.EncryptText(Password,Text);
		    Decrypt.DecryptText(EncData[0],UserName,Password);
		    
		    
			String sql = "INSERT INTO userinfo (username,password) VALUES (?,?);";
			PreparedStatement statement = connection.prepareStatement(sql);
			
			//Setting uname and password in DB
			statement.setString(1, UserName);
			statement.setString(2, Password);
			int rows = statement.executeUpdate();
			if(rows>0) {
				
				System.out.println("Values inserted into the database");
			}
			connection.close();
		
	
	} catch (SQLException e) {
		System.out.println("Error in connecting to Postgresql database");
		e.printStackTrace();
	}
    
    
    
    


    }
}
