package main.java.org.smartvalidator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector extends ConflictClasses {
	
    private Connection connection;
	
    public DatabaseConnector(){
    	String database = System.getProperty("database");
    	String user = System.getProperty("user");
    	String password = System.getProperty("password");
    	
        System.out.println("-------- PostgreSQL "
                + "JDBC Connection ------------");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;
        }
        System.out.println("PostgreSQL JDBC Driver Registered!");
        connection = null;
        try {
            connection = DriverManager.getConnection(database, user, password);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console.\n");
            e.printStackTrace();
            return;
        }
        if (connection != null) {
            System.out.println("You made it, take control over your database now!");
        } else {
            System.out.println("\nFailed to make connection!");
        }
    }
    
    public void loadAnnouncements(){
    	this.announcements = new ArrayList<>();
    	try{
    		Statement stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT * FROM announcements ORDER BY id");
    		List<Announcement> announcements = new ArrayList<>();
    		Announcement announcement;
    		
    		while(rs.next()){
    			int id = rs.getInt(1);
    			long asn = rs.getLong(2);
    			String prefix = rs.getString(3);
    			Timestamp created_at = rs.getTimestamp(4);
    			Timestamp updated_at = rs.getTimestamp(5);
    			announcement = new Announcement(id,asn, prefix, created_at, updated_at);
    			announcements.add(announcement);
    			if(announcements.size() % 100000 == 0){
    				System.out.println("Loaded " + announcements.size() + " announcements.");
    			}
    		}
    		
    		rs = stmt.executeQuery("SELECT * FROM verified_announcements ORDER BY id");
    		VerifiedAnnouncement verifiedAnnouncement;
    		
    		while(rs.next()){
    			int id = rs.getInt(1);
    			int announcement_id = rs.getInt(2);
    			Timestamp created_at = rs.getTimestamp(3);
    			Timestamp updated_at = rs.getTimestamp(4);
    			announcement = announcements.get(announcement_id - 1);
    			verifiedAnnouncement = new VerifiedAnnouncement(id, announcement, created_at, updated_at);
    			this.announcements.add(verifiedAnnouncement);
    			if(this.announcements.size() % 100000 == 0){
    				System.out.println("Loaded " + this.announcements.size() + " verified announcements.");
    			}
    		}
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    }
    
    public void loadRoas(){
    	roas = new ArrayList<>();
    	try{
    		Statement stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT * FROM validated_roas ORDER BY id");
    		Roa roa;
    		
    		while(rs.next()){
    			int id = rs.getInt(1);
    			Boolean filtered = rs.getBoolean(5);
    			Boolean whitelisted = rs.getBoolean(6);
    			roa = new Roa(id, rs.getLong(2), rs.getString(3), rs.getInt(4), 
    					filtered, whitelisted, rs.getInt(7), rs.getTimestamp(8), rs.getTimestamp(9));
    			roas.add(roa);
    			if(roas.size() % 10000 == 0){
    				System.out.println("Loaded " + roas.size() + " ROAs.");
    			}
    		}
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    }
    
    public void loadConflicts(){
    	conflicts = new ArrayList<>();
    	try{
    		Statement stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT * FROM validated_roas_verified_announcements");
    		rs.next();
    		
    		int previousAnnouncement_id;
    		int announcement_id = rs.getInt(2);
    		VerifiedAnnouncement announcement = announcements.get(announcement_id - 1);
    		
    		int roa_id = rs.getInt(3);
    		Roa roa = roas.get(roa_id - 1);
    		int route_validity = rs.getInt(4);
    		Timestamp created_at = rs.getTimestamp(5);
			Timestamp updated_at = rs.getTimestamp(6);
    		RoaEntry roaEntry = new RoaEntry(roa, route_validity, created_at, updated_at);
    		
    		Conflict conflict = new Conflict(announcement, roaEntry);
    		
    		while(rs.next()){
    			previousAnnouncement_id = announcement_id;
    			announcement_id = rs.getInt(2);
    			announcement = announcements.get(announcement_id - 1);
        		
        		roa_id = rs.getInt(3);
        		roa = roas.get(roa_id - 1);
        		route_validity = rs.getInt(4);
        		created_at = rs.getTimestamp(5);
    			updated_at = rs.getTimestamp(6);
        		roaEntry = new RoaEntry(roa, route_validity, created_at, updated_at);
    			if(announcement_id == previousAnnouncement_id){
    				conflict.addRoa(roaEntry);
    			}else{
    				conflicts.add(conflict);
    				conflict = new Conflict(announcement, roaEntry);
    			}
    			if(conflicts.size() % 10000 == 0){
    				System.out.println("Loaded " + conflicts.size() + " conflicts.");
    			}
    		}
    		if(!conflicts.contains(conflict)){
    			conflicts.add(conflict);
    		}
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    }
    
    public void pushRoas(){
    	try{
    		Statement stmt = connection.createStatement();
    		for(int i = 0; i < roas.size(); i++){
    			
    		}
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    }
    
}
