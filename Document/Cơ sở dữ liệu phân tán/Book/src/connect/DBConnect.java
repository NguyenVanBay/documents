package connect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author NVB
 */
public class DBConnect {

//	 public static Connection getConnection(){
//	 Connection conn = null;
//	 try {
//	 Class.forName("com.mysql.cj.jdbc.Driver");
//	 conn =
//	 DriverManager.getConnection("jdbc:mysql://localhost:3306/book?useUnicode=yes&characterEncoding=UTF-8&useSSL=false",
//	 "root", "1234");
//	 } catch (Exception e) {
//	 e.printStackTrace();
//	 System.out.println("connect false");
//	 }
//	 return conn;
//	 }
//	
//	 public static void main(String[] args) {
//	 new DBConnect();
//	 DBConnect.getConnection();
//	 }

	public static Connection getConnection() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String dbURL = "jdbc:sqlserver://localhost;databaseName=book;user=sa;password=1";
			Connection conn = DriverManager.getConnection(dbURL);
			if (conn != null) {
//				System.out.println("Connected");

				DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
//				System.out.println("Driver name: " + dm.getDriverName());
//				System.out.println("Driver version: " + dm.getDriverVersion());
//				System.out.println("Product name: " + dm.getDatabaseProductName());
//				System.out.println("Product version: " + dm.getDatabaseProductVersion());
				return conn;
			}
		} catch (SQLException | ClassNotFoundException ex) {
			System.err.println("Cannot connect database, " + ex);
		}
		return null;
	}
	
	 public static void main(String[] args) {
	 new DBConnect();
	 DBConnect.getConnection();
	 }

}
