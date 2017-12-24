package com;

import java.sql.*;
import java.util.*;

public class ConflictGetter extends ConflictHandler{
	
    private Connection connection;
	
    public ConflictGetter(String db, String user, String password){
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
            connection = DriverManager.getConnection(
                    db, user,
                    password);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console.\n");
            e.printStackTrace();
            return;
        }
        if (connection != null) {
            System.out.println("You made it, take control over your database now!");
//            this.start();
        } else {
            System.out.println("\nFailed to make connection!");
        }
    }
    
 // Start the Conflict Getter
    public void start(){
    	System.out.println("\nLoading conflicts, please stand by...");
        long start = System.currentTimeMillis();
        
    	roas = new ArrayList<>();
    	conflicts = new ArrayList<>();
    	loadRoas();
    	loadConflicts();
    	
    	System.out.println("Loaded ROAs: " + roas.size() + "\nLoaded conflicts: " + conflicts.size());
        long end = System.currentTimeMillis();
        long diff = end - start;
        System.out.println("\nFinished. Loading took " + (diff / 1000)  + "." + (diff % 1000) + " s.");
    }
    
    private void loadRoas(){
    	try{
    		Statement stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT * FROM validated_roas");
    		Roa roa;
    		
    		while(rs.next()){
    			roa = new Roa(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4), 
    					rs.getBoolean(5), rs.getBoolean(6), rs.getInt(7), rs.getTimestamp(8), rs.getTimestamp(9));
    			roas.add(roa);
    			if(roas.size() % 10000 == 0){
    				System.out.println("Loaded " + roas.size() + " ROAs.");
    			}
    		}
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    }
    
    private void loadConflicts(){
    	try{
    		Statement stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT * FROM validated_roas_verified_announcements");
    		
    		rs.next();
    		int previousAnnouncement_id;
    		int announcement_id = rs.getInt(2);
    		int roa_id = rs.getInt(3);
    		Roa roa = roas.get(roa_id - 1);
    		RoaEntry roaEntry = new RoaEntry(roa, rs.getInt(4), rs.getTimestamp(5), rs.getTimestamp(6));
    		
    		Conflict conflict = new Conflict(announcement_id, roaEntry);
    		
    		while(rs.next()){
    			previousAnnouncement_id = announcement_id;
    			announcement_id = rs.getInt(2);
    			roaEntry = new RoaEntry(roa, rs.getInt(4), rs.getTimestamp(5), rs.getTimestamp(6));
    			if(announcement_id == previousAnnouncement_id){
    				conflict.addRoa(roaEntry);
    			}else{
    				conflicts.add(conflict);
    				conflict = new Conflict(announcement_id, roaEntry);
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
    
}
