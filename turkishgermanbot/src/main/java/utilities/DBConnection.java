package utilities;

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
			System.out.println("upd: " + groupId);
			ps.setLong(1, groupId);
			ps.setString(2, unionName);
			ps.executeUpdate();
			con.commit();
			con.close();
			Connection con2 = connect();
			PreparedStatement ps2 = con2.prepareStatement("SELECT TOP 1 GROUPID FROM GROUPSUNIONS WHERE UNIONNAME = ?");
			ps2.setString(1, unionName);
			ResultSet rs = ps2.executeQuery();
			if (rs.next()) {
				long h = rs.getLong("GROUPID");
				System.out.println("cvp: " + h);
				System.out.println(groupId == h);
			}
			con2.close();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

    }
    
    // Table for unions
    // UNIONS: UnionName OwnerID Passwordhash ///< so if not owner, ask for password
    /*
     
    CREATE TABLE PUBLIC.UNIONS
   	(UNIONNAME VARCHAR(130) NOT NULL,
    OWNERID INTEGER NOT NULL,
    PASSWORDHASH BINARY(16) NOT NULL,
    PRIMARY KEY (UNIONNAME))
    
	CREATE TABLE PUBLIC.GROUPSUNIONS
   	(INDEXID INTEGER NOT NULL IDENTITY,
   	GROUPID BIGINT NOT NULL,
    UNIONNAME VARCHAR(130) NOT NULL,
    PRIMARY KEY (INDEXID))
   
    
     */

    // Table for groups within unions
    // GROUPSUNIONS: GroupID UnionID 
    
    
    // Table for filters for unions
    // UNIONFILTERS: FILTERID FILTERQUESTION FILTERANSWER UNIONID
    
    /// Whenever a new message comes, check if group is in union, if so, check with that UNIONID and FILTERQUESTION what FILTERANSWER is...
    
    
    /// CREATE A UNION: Ask for name, ask for pass, ask for confirm pass, say that it will be hashed,  done
    /// ADD GROUP TO UNION: after the 'Unions' button is clicked ask them to give the name of the union, then check if userid = ownerid, if not, ask for password... 
    	//bot should be added to the group first. then, send a message to group, ask for an admin to click a button to confirm joining, then you are in.
    
    
    /// ADD A GROUP TO AN UNION
    
}
