package contact;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {
	Connection con=null;
	
	public Connection startConnection() {
		if(con==null)
		{	
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","db_shubh","db_shubh");
				System.out.println("Connection created");
			return con; 
			}
			catch (ClassNotFoundException e) {
				System.out.println("Class not found try again");
			}
			catch (SQLException e) {
				System.out.println("Some error occurred try again");
			}
		}
		
		return con;
		
		
	}
	
	public void stopConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Failed to close connection.");
		}
	}
}
