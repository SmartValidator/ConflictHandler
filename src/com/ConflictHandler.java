package com;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.json.*;

public class ConflictHandler {

    private Connection connection;
    private List<Roa> validatedRoas;
//    private List<VerifiedAnnouncement> verifiedAnnouncements;
    private List<VerifiedAnnouncement> conflicts;
    
    public class VerifiedAnnouncement{
    	private int id;
    	private int announcement_id;
    	private int validated_roa_id;
    	private String route_validity;
    	private Timestamp created_at;
    	private Timestamp updated_at;
    	
    	public VerifiedAnnouncement(int id, int announcement_id, int validated_roa_id,
    			String route_validity, Timestamp created_at, Timestamp updated_at){
    		this.id = id;
    		this.announcement_id = announcement_id;
    		this.validated_roa_id = validated_roa_id;
    		this.route_validity = route_validity;
    		this.created_at = created_at;
    		this.updated_at = updated_at;
    	}
    	
    	public String print(){
    		return this.id + "  " + this.announcement_id + "  " + this.validated_roa_id + "  "
    				+this.route_validity + "  " + this.created_at + "  " + this.updated_at;
    	}
    }

    public class Roa{
        private int id;
        private int asn;
        private String prefix;
        private int max_length;
        private boolean filtered;
        private boolean whitelisted;
        private int trust_anchor_id;
        private Timestamp created_at;
        private Timestamp updated_at;

        public Roa(int id, int asn, String prefix, int max_length,
        		boolean filtered, boolean whitelisted, int trust_anchor_id,
        		Timestamp created_at, Timestamp updated_at){
            this.id = id;
            this.asn = asn;
            this.prefix = prefix;
            this.max_length = max_length;
            this.filtered = filtered;
            this.whitelisted = whitelisted;
            this.trust_anchor_id = trust_anchor_id;
            this.created_at = created_at;
            this.updated_at = updated_at;
        }
        
        public String print(){
        	return this.id + "  " + this.asn + "  " + this.prefix + "  " + this.max_length + "  "
        			+ this.filtered + "  " + this.whitelisted + "  " + this.trust_anchor_id + "  "
        			+ this.created_at + "  " + this.updated_at;
        }
    }

    public ConflictHandler(String db, String user, String password){
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
                    "jdbc:postgresql://smart-validator.net/smart_validator_test_4", user,
                    password);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console.\n");
            e.printStackTrace();
            return;
        }
        if (connection != null) {
            System.out.println("You made it, take control over your database now!");
            System.out.println("Handling conflicts, please stand by...\n");
            long start = System.currentTimeMillis();
            this.runConflictHandler();
            long end = System.currentTimeMillis();
            long diff = end - start;
            System.out.println("Finished. Handling took " + (diff / 1000)  + "." + (diff % 1000) + " s.");
        } else {
            System.out.println("Failed to make connection!");
        }
    }
    
    public ConflictHandler(String announcementsJsonFile, String roasJsonFile){
    	System.out.println("Handling conflicts.");
    	this.loadJsonFiles(announcementsJsonFile, roasJsonFile);
    	System.out.println("Done handling.");
    }
    
    private void loadConflicts(){
    	try{
    		Statement stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT * FROM verified_announcements "
    				+ "WHERE route_validity IN (0, 1, 2, 3, 4)");
    		
    		while(rs.next()){
    			conflicts.add(new VerifiedAnnouncement(rs.getInt(1), rs.getInt(2),
    					rs.getInt(3), rs.getString(4), rs.getTimestamp(5), rs.getTimestamp(6)));
    		}
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    }
    
    private void loadRoas(){
    	try{
    		Statement stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT * FROM validated_roas");
    		
    		while(rs.next()){
    			// Add entry to the validatedRoas table
    			validatedRoas.add(new Roa(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4),
    					rs.getBoolean(5), rs.getBoolean(6), rs.getInt(7), rs.getTimestamp(8), rs.getTimestamp(9)));
    		}
    	}catch(Exception e){
    			System.out.println(e.getMessage());
    	}
    }
    
    private void loadJsonFiles(String announcementsJsonFile, String roasJsonFile){
    	Path announcementsPath = Paths.get(announcementsJsonFile);
    	try (BufferedReader reader = Files.newBufferedReader(announcementsPath)) {
    		JSONTokener tokener = new JSONTokener(reader);
    		JSONArray jsonAnns = (JSONArray) tokener.nextValue();
    		
    		for(int i = 0; i < jsonAnns.length(); i++){
    			JSONObject jsonAnn = jsonAnns.getJSONObject(i);
    			JSONObject ann = jsonAnn.getJSONObject("Annoucment");
    			
    			JSONArray roas = jsonAnn.getJSONArray("OverlapRoas");
    			Timestamp creation = Timestamp.valueOf(jsonAnn.getString("creation"));
    	    	Timestamp last_modified = Timestamp.valueOf(jsonAnn.getString("last_modified"));
    			
//    			private int id;
//    	    	private int announcement_id;
//    	    	private int validated_roa_id;
//    	    	private String route_validity;
//    	    	private Timestamp created_at;
//    	    	private Timestamp updated_at;
    			
    	    }
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
    	Path roasPath = Paths.get(roasJsonFile);
    	try (BufferedReader reader = Files.newBufferedReader(roasPath)) {
    	    JSONTokener tokener = new JSONTokener(reader);
    	    JSONArray jsonRoas = (JSONArray) tokener.nextValue();
    	    
    	    for(int i = 0; i < jsonRoas.length(); i++){
    	    	JSONObject jsonRoa = jsonRoas.getJSONObject(i);
    	    	String asnString = jsonRoa.getString("Asn");
    	    	int asn = Integer.parseInt(asnString.substring(2, asnString.length()));
    	    	String prefix = jsonRoa.getString("Prefix");
    	    	int maxLength;
    	    	try{
    	    		maxLength = jsonRoa.getInt("MaxLength");
    	    	} catch (Exception e) {
    	    		maxLength = 0;
    	    	}
//    	    	String trustAnchor = jsonRoa.getString("TrustAnchor");
    	    	Timestamp creation = Timestamp.valueOf(jsonRoa.getString("creation"));
    	    	Timestamp last_modified = Timestamp.valueOf(jsonRoa.getString("last_modified"));
    	    	Boolean filtered = jsonRoa.getBoolean("filtered");
    	    	Boolean whitelist = jsonRoa.getBoolean("whitelist");
    	    	Roa roaEntry = new Roa(i, asn, prefix, maxLength, filtered, whitelist,
    	    			0, creation, last_modified);
    	    }
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
    }
    
    private void printAnnouncementTable(List<VerifiedAnnouncement> table){
    	for(int i = 0; i < table.size(); i++){
    		if(i % 1000 == 0){
    			System.out.println(table.get(i).print());
    		}
    	}
    }
    
    private void printRoaTable(){
    	for(int i = 0; i < validatedRoas.size(); i++){
    		Roa roa = validatedRoas.get(i);
    		System.out.println(roa.print());
    	}
    }
    
    private void getMetadata(){
        try {
            // Get the database metadata
            DatabaseMetaData dbmd = connection.getMetaData();

            // Specify the type of object; in this case we want tables
            String[] types = {"TABLE"};
            ResultSet dbrs = dbmd.getTables(null, null, "%", types);

            while (dbrs.next()) {
                String tableName = dbrs.getString(3);
                String tableCatalog = dbrs.getString(1);
                String tableSchema = dbrs.getString(2);
                System.out.println("Table : " + tableName + "\nCatalog : " + tableCatalog + "\nSchema : " + tableSchema);
                
                // Get the metadata of ROA table and verified announcement table
                if(tableName.equals("validated_roas") || tableName.equals("verified_announcements")){
	                Statement stmt = connection.createStatement();
	                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
	                ResultSetMetaData rsmd = rs.getMetaData();
	                int columnCount = rsmd.getColumnCount();
	                for(int i = 1; i < columnCount + 1; i++){
	                	String columnName = rsmd.getColumnName(i);
	                	String columnType = rsmd.getColumnTypeName(i);
	                	System.out.println(columnName + " : " + columnType);
	                }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void runConflictHandler(){
    	this.getMetadata();
//    	verifiedAnnouncements = new ArrayList<>();
    	conflicts = new ArrayList<>();
    	validatedRoas = new ArrayList<>();
//    	this.getAnnouncements();
//    	this.getConflicts();
//    	this.getRoas();
//    	this.printAnnouncementTable(conflicts);
//    	this.printRoaTable();
    }
    
}

