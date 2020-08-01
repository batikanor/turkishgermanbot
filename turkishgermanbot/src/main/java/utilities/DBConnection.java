package utilities;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
	private static final String Driver = "org.hsqldb.jdbcDriver";
    private static final String user = "TurkishGermanBot";
    private static final String pwd = "92nezaket239ff";
    private static final String dbPath = "~/DBTurkishGermanBot";
    private static final String url = "jdbc:hsqldb:file:" + dbPath;
    
    public static Connection connect() {
		try {
			System.out.println("Veritabanı'na bağlanmaya çalışılınıyor");
			
			Class.forName(Driver);
			Connection con = DriverManager.getConnection(url, user, pwd);
			return con;
			
		}catch(ClassNotFoundException e) {
			System.out.println("E002: Veritabanı klası bulunamadı");
			e.printStackTrace();
			return null;
		}catch(SQLException e) {
			System.out.println("E002: Veritabanı klası bulundu, ama bağlantı kurulamadı");
		
			return null;
		}
			
	}
    public static boolean addGroupToUnion(long groupId, String unionName) {
    	
    	Connection con = connect();
		PreparedStatement ps;

		
		try {
			ps = con.prepareStatement("INSERT INTO GROUPSUNIONS(GROUPID, UNIONNAME) VALUES(?, ?)");
			
			ps.setLong(1, groupId);
			ps.setString(2, unionName);
			ps.executeUpdate();
			con.commit();
			con.close();
			//Connection con2 = connect();
			//PreparedStatement ps2 = con2.prepareStatement("SELECT TOP 1 GROUPID FROM GROUPSUNIONS WHERE UNIONNAME = ?");
			//ps2.setString(1, unionName);
			//ResultSet rs = ps2.executeQuery();
			//if (rs.next()) {
			//	long h = rs.getLong("GROUPID");
			//	System.out.println("cvp: " + h);
			//	System.out.println(groupId == h);
			//}
			//con2.close();
			return true;
	
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

    }
    public static boolean addFilterToUnion(String filter, String answer, String unionName) {
    	
    	Connection con = connect();
		PreparedStatement ps;

		
		try {
			ps = con.prepareStatement("INSERT INTO UNIONFILTERS(FILTERQUESTION, FILTERANSWER, UNIONNAME) VALUES(?, ?, ?)");
			ps.setString(1, filter);
			Clob clob = con.createClob();
			clob.setString(1, answer);
			ps.setClob(2, clob);
			ps.setString(3, unionName);
			ps.executeUpdate();
			con.commit();
			con.close();
			Connection con2 = connect();
			PreparedStatement ps2 = con2.prepareStatement("SELECT TOP 1 FILTERANSWER FROM UNIONFILTERS WHERE UNIONNAME = ? AND FILTERQUESTION = ?");
			ps2.setString(1, unionName);
			ps2.setString(2, filter);
			ResultSet rs = ps2.executeQuery();
			if (rs.next()) {
				Clob cl = rs.getClob("FILTERANSWER");
				Reader r = cl.getCharacterStream();
				StringBuffer buff = new StringBuffer();
				int ch;
				while ((ch = r.read()) != -1) {
					buff.append("" + (char)ch);
				}
				String st = buff.toString();
				System.out.println("cvp: " + st);
				
			} else {
				System.out.println("wtf?");
			}
			con2.close();
			return true;
	
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

    }
    
    // Table for unions
    // UNIONS: UnionName OwnerID Passwordhash ///< so if not owner, ask for password
    /*
     
    CREATE TABLE PUBLIC.UNIONS
   	(UNIONNAME NVARCHAR(100) NOT NULL,
    OWNERID INTEGER NOT NULL,
    PASSWORDHASH BINARY(16) NOT NULL,
    PRIMARY KEY (UNIONNAME))
    
	CREATE TABLE PUBLIC.GROUPSUNIONS
   	(INDEXID INTEGER NOT NULL IDENTITY,
   	GROUPID BIGINT NOT NULL,
    UNIONNAME NVARCHAR(100) NOT NULL,
    PRIMARY KEY (INDEXID))
   
    CREATE TABLE PUBLIC.UNIONFILTERS
   	(FILTERID INTEGER NOT NULL IDENTITY,
   	FILTERQUESTION NVARCHAR(200) NOT NULL,
   	FILTERANSWER CLOB(5000) NOT NULL,
    UNIONNAME NVARCHAR(100) NOT NULL,
    PRIMARY KEY (FILTERID))
     */

    // Table for groups within unions
    // GROUPSUNIONS: GroupID UnionID 
    
    
    // Table for filters for unions
    // UNIONFILTERS: FILTERID FILTERQUESTION FILTERANSWER UNIONNAME ///< TELEGRAM MESSAGE CHARACTER LIMIT IS 4096
    /// FILTERS SHOULD BE MARKDOWN ENABLED
    /// Whenever a new message comes, check if group is in union, if so, check with that UNIONID and FILTERQUESTION what FILTERANSWER is...
    
    
    /// CREATE A UNION: Ask for name, ask for pass, ask for confirm pass, say that it will be hashed,  done
    /// ADD GROUP TO UNION: after the 'Unions' button is clicked ask them to give the name of the union, then check if userid = ownerid, if not, ask for password... 
    	//bot should be added to the group first. then, send a message to group, ask for an admin to click a button to confirm joining, then you are in.
    
    
    /// ADD A GROUP TO AN UNION
    
}
