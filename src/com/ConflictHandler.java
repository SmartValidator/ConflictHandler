package com;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConflictHandler {
	
	protected List<VerifiedAnnouncement> announcements;
    protected List<Roa> roas;
    protected List<Conflict> conflicts;
    
    public class Conflict {
    	private VerifiedAnnouncement announcement;
    	private List<RoaEntry> roas;
    	
    	public Conflict(VerifiedAnnouncement announcement, List<RoaEntry> roas){
    		this.announcement = announcement;
    		this.roas = roas;
    	}
    	
    	public Conflict(VerifiedAnnouncement announcement, RoaEntry roaEntry){
    		this.announcement = announcement;
    		this.roas = new ArrayList<>();
    		this.roas.add(roaEntry);
    	}
    	
    	public VerifiedAnnouncement getAnnouncement(){
    		return this.announcement;
    	}
    	
    	public void addRoa(RoaEntry roaEntry){
    		this.roas.add(roaEntry);
    	}
    	
    	public List<RoaEntry> getRoas(){
    		return this.roas;
    	}
    	
    	public int[] getRoaIds(){
    		int[] roaIds = new int[roas.size()];
    		for(int i = 0; i < roas.size(); i++){
    			roaIds[i] = roas.get(i).getRoaId();
    		}
    		return roaIds;
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
    	
//    	public Roa getRoa(){
//    		return this.roa;
//    	}
    	
    	public int getRoaId(){
    		return roa.getId();
    	}
    	
    	public int getValidity(){
    		return this.route_validity;
    	}
    }
    
    public class Roa {
    	private int id;
    	private long asn;
    	private String prefix;
    	private int max_length;
    	private Boolean filtered;
    	private Boolean whitelisted;
    	private int trust_anchor_id;
    	private Timestamp created_at;
    	private Timestamp updated_at;
    	
    	public Roa(int id, long asn, String prefix, int max_length, 
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
    	
    	@Override
    	public String toString(){
    		return this.id + "\t" + this.asn + "\t" + this.prefix + "\t" + this.max_length + "\t" + this.filtered + "\t" +
    	this.whitelisted + "\t" + this.trust_anchor_id + "\t" + this.created_at + "\t" + this.updated_at;
    	}
    	
    	public int getId(){
    		return id;
    	}
    	
    	public void setFiltered(Boolean filtered){
    		this.filtered = filtered;
    	}
    	
    	public Boolean getWhitelisted(){
    		return this.whitelisted;
    	}
    	
    	public void setFilteredWhitelistFalse(){
    		this.filtered = false;
    		this.whitelisted = false;
    	}
    }
    
    public class VerifiedAnnouncement {
    	private int id;
    	private Announcement announcement;
    	private Timestamp created_at;
    	private Timestamp updated_at;
    	
    	public VerifiedAnnouncement(int id, Announcement announcement, Timestamp created_at, Timestamp updated_at){
    		this.id = id;
    		this.announcement = announcement;
    		this.created_at = created_at;
    		this.updated_at = updated_at;
    	}
    	
    	@Override
    	public String toString(){
    		return this.id + "\t" + this.announcement.toString() + "\t" + this.created_at + "\t" + this.updated_at; 
    	}
    	
    	public int getId(){
    		return this.id;
    	}
    	
    	public long getAsn(){
    		return this.announcement.getAsn();
    	}
    	
    	public String getPrefix(){
    		return this.announcement.getPrefix();
    	}
    	
    	public Timestamp getCreated_at(){
    		return announcement.getCreated_at();
    	}
    }
    
    public class Announcement {
    	private int id;
    	private long asn;
    	private String prefix;
    	private Timestamp created_at;
    	private Timestamp updated_at;
    	
    	public Announcement(int id, long asn, String prefix, Timestamp created_at, Timestamp updated_at){
    		this.id = id;
    		this.asn = asn;
    		this.prefix = prefix;
    		this.created_at = created_at;
    		this.updated_at = updated_at;
    	}
    	
    	@Override
    	public String toString(){
    		return this.id + "\t" + this.asn + "\t" + this.prefix + "\t" + this.created_at + "\t" + this.updated_at;
    	}
    	
    	public int getId(){
    		return this.id;
    	}
    	
    	public long getAsn(){
    		return this.asn;
    	}
    	
    	public String getPrefix(){
    		return this.prefix;
    	}
    	
    	public Timestamp getCreated_at(){
    		return created_at;
    	}
    }
    
}
