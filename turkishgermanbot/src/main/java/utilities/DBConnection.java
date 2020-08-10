package utilities;


import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import objects.DiceStats;
import turkishgermanbot.Main;

public class DBConnection {

    
    public static Connection connect() {
		try {
			System.out.println("Veritabanı'na bağlanmaya çalışılınıyor");
			
			Class.forName(Main.dbDriver);
			Connection con = DriverManager.getConnection(Main.dbUrl, Main.dbUser, Main.dbPwd);
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
    public static boolean createUnion(long ownerId, String unionName, String pass) {
    	
    	Connection con = connect();
		PreparedStatement ps;

		
		try {
			ps = con.prepareStatement("INSERT INTO UNIONS(OWNERID, UNIONNAME, PASSWORDHASH) VALUES(?, ?, ?)");
			
			ps.setLong(1, ownerId);
			ps.setString(2, unionName);
			ps.setBytes(3, HelperFunctions.hashPassword(pass));
			ps.executeUpdate();
			con.commit();




			con.close();
			return true;
	
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

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


			//PreparedStatement ps2 = con.prepareStatement("SELECT TOP 1 GROUPID FROM GROUPSUNIONS WHERE UNIONNAME = ?");
			//ps2.setString(1, unionName);
			//ResultSet rs = ps2.executeQuery();
			//if (rs.next()) {
			//	long h = rs.getLong("GROUPID");
			//	System.out.println("cvp: " + h);
			//	System.out.println(groupId == h);
			//}

			con.close();
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
			//Connection con2 = connect();
			//PreparedStatement ps2 = con2.prepareStatement("SELECT TOP 1 FILTERANSWER FROM UNIONFILTERS WHERE UNIONNAME = ? AND FILTERQUESTION = ?");
			//ps2.setString(1, unionName);
			//ps2.setString(2, filter);
			//ResultSet rs = ps2.executeQuery();
			//if (rs.next()) {
			//	Clob cl = rs.getClob("FILTERANSWER");
			//	Reader r = cl.getCharacterStream();
			//	StringBuffer buff = new StringBuffer();
			//	int ch;
			//	while ((ch = r.read()) != -1) {
			//		buff.append("" + (char)ch);
			//	}
			//	String st = buff.toString();
			//	System.out.println("cvp: " + st);
			//	
			//} else {
			//	System.out.println("wtf?");
			//}
			//con2.close();
			return true;
	
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return false;

    }
    
    public static DiceStats storeAndGetDiceStats(String emoji, long userId, int score) {
    	//At total you've scored this and that for this emoji
    	//hsql already is unicode encoded so utf-8 emojis can be stored. emojis are 2chars long but some new generation ones may be 3 therefore I'll just have place for 4 on the database
    	
    	Connection con = connect();
		PreparedStatement ps;
		ResultSet rs;
		int attempts;
		float avg;
		try {
			ps = con.prepareStatement("SELECT AVERAGE, ATTEMPTS FROM DICESCORES WHERE EMOJI = ? AND USERID = ?");
			ps.setString(1, emoji);
			ps.setLong(2, userId);
			rs = ps.executeQuery();
			if (rs.next()) {
				attempts = rs.getInt("ATTEMPTS");
				avg = rs.getFloat("AVERAGE");
				avg = ((attempts * avg) + score) / (++attempts);
				

				PreparedStatement ps3 = con.prepareStatement("UPDATE DICESCORES SET AVERAGE = ?, ATTEMPTS = ? WHERE USERID = ? AND EMOJI = ?");


				ps3.setFloat(1, avg);
				ps3.setInt(2, attempts);		
				ps3.setLong(3, userId);
				ps3.setString(4, emoji);
				
				ps3.executeUpdate();
			} else {
				// Row has not been created yet!
				attempts = 1;
				avg = (float)score;

				PreparedStatement ps2 = con.prepareStatement("INSERT INTO DICESCORES(USERID, EMOJI, AVERAGE, ATTEMPTS) VALUES(?, ?, ?, ?)");
				ps2.setLong(1, userId);
				ps2.setString(2, emoji);
				ps2.setFloat(3, avg);
				ps2.setInt(4, attempts);
				ps2.executeUpdate();
			}

			con.commit();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return new DiceStats(attempts, avg);
    }
    
    // Table for unions
    // UNIONS: UnionName OwnerID Passwordhash ///< so if not owner, ask for password
    /*
     
    
    CREATE TABLE PUBLIC.DICESCORES
   	(SCOREID INTEGER NOT NULL IDENTITY,
    USERID BIGINT NOT NULL,
    EMOJI NVARCHAR(4) NOT NULL,
    AVERAGE FLOAT NOT NULL,
    ATTEMPTS INTEGER NOT NULL,
    PRIMARY KEY (SCOREID))
    /// Actually emoji takes up only 2 length but i did 4 anyways
     
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
