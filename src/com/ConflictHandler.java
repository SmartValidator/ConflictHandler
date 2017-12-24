package com;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConflictHandler {
	
    protected List<Roa> roas;
    protected List<Conflict> conflicts;
    
    public class Conflict {
    	private int announcement_id;
    	private List<RoaEntry> roas;
    	
    	public Conflict(int announcement_id, List<RoaEntry> roas){
    		this.announcement_id = announcement_id;
    		this.roas = roas;
    	}
    	
    	public Conflict(int announcement_id, RoaEntry roaEntry){
    		this.announcement_id = announcement_id;
    		this.roas = new ArrayList<>();
    		this.roas.add(roaEntry);
    	}
    	
    	public void addRoa(RoaEntry roaEntry){
    		this.roas.add(roaEntry);
    	}
    	
    	public List<RoaEntry> getRoas(){
    		return this.roas;
    	}
    }
    
    public class RoaEntry {
    	private Roa roa;
    	private int route_validity;
    	private Timestamp created_at;
    	private Timestamp updated_at;
    	
    	public RoaEntry(Roa roa, int route_validity, Timestamp created_at, Timestamp updated_at){
    		this.roa = roa;
    		this.route_validity = route_validity;
    		this.created_at = created_at;
    		this.updated_at = updated_at;
    	}
    	
    	public RoaEntry(int id, int asn, String prefix, int max_length, 
    			Boolean filtered, Boolean whitelisted, int trust_anchor_id, Timestamp roa_created_at, Timestamp roa_updated_at, 
    			int route_validity, Timestamp created_at, Timestamp updated_at){
    		this.roa = new Roa(id, asn, prefix, max_length, filtered, whitelisted, trust_anchor_id, roa_created_at, roa_updated_at);
    		this.route_validity = route_validity;
    		this.created_at = created_at;
    		this.updated_at = updated_at;
    	}
    	
    	public Roa getRoa(){
    		return this.roa;
    	}
    	
    	public int getValidity(){
    		return this.route_validity;
    	}
    }
    
    public class Roa {
    	private int id;
    	private int asn;
    	private String prefix;
    	private int max_length;
    	private Boolean filtered;
    	private Boolean whitelisted;
    	private int trust_anchor_id;
    	private Timestamp created_at;
    	private Timestamp updated_at;
    	
    	public Roa(int id, int asn, String prefix, int max_length, 
    			Boolean filtered, Boolean whitelisted, int trust_anchor_id, Timestamp created_at, Timestamp updated_at){
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
    	
    	public void setFiltered(Boolean filtered){
    		this.filtered = filtered;
    	}
    	
    	public Boolean getWhitelisted(){
    		return this.whitelisted;
    	}
    }

    public List<Roa> getRoas(){
    	return this.roas;
    }
    
    public List<Conflict> getConflicts(){
    	return this.conflicts;
    }

	public void setRoas(List<Roa> roas) {
		this.roas = roas;
	}

	public void setConflicts(List<Conflict> conflicts) {
		this.conflicts = conflicts;
	}
    
}
