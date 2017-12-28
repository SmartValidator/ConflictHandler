package org.smartvalidator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConflictResolver extends ConflictHandler{
	
	private ConflictGetter databaseConnector;
	private static final long DAY = 86400000;
	
	public ConflictResolver(String db, String user, String password){
		databaseConnector = new ConflictGetter(db, user, password);
	}
	
	// Options for testing purposes.
	public void load(Boolean loadAnnouncements, Boolean loadRoas, Boolean loadConflicts){
		System.out.println("Loading conflicts, please stand by...\n");
        long start = System.currentTimeMillis();
		
		if(loadAnnouncements){		
			announcements = new ArrayList<>();
			databaseConnector.loadAnnouncements();
			announcements = databaseConnector.announcements;
		}
		if(loadRoas){
			roas = new ArrayList<>();
			databaseConnector.loadRoas();
			roas = databaseConnector.roas;
		}
		if(loadConflicts){
			conflicts = new ArrayList<>();
			databaseConnector.loadConflicts();
			conflicts = databaseConnector.conflicts;
		}
		long end = System.currentTimeMillis();
        long diff = end - start;
        System.out.println("Loaded ROAs: " + roas.size() + ", Loaded conflicts: " + conflicts.size() + 
        		". Loading took " + (diff / 1000)  + "." + (diff % 1000) + " s.\n");
		
		this.removeValid();
		this.setFilteredWhitelistFalse();
		System.out.println("Invalid conflicts: " + conflicts.size());
	}
	
	public void handleConflicts(String heuristics, int days){
		System.out.println("Resolving conflicts, please stand by...\n");
        long start = System.currentTimeMillis();
        
        switch(heuristics){
        	case "i": ignoreAll();
        			System.out.println("Ignore all.");
        			break;
        	case "f": filterAfterTime(days);
        			System.out.println("Filter all.");
        			break;
        	case "w": whitelistAfterTime(days);
        			System.out.println("Whitelist all.");
        			break;
        	default: break;
        }
        System.out.println("ROAs: " + roas.size());
        
        long end = System.currentTimeMillis();
        long diff = end - start;
        System.out.println("Invalid conflicts: " + conflicts.size() + ", ROAs: " + roas.size() + 
        		". Resolving took " + (diff / 1000)  + "." + (diff % 1000) + " s.");
	}
	
	public void push(){
		databaseConnector.pushRoas();
	}
	
	private void removeValid(){
    	for(int i = 0; i < conflicts.size(); i++){
    		for(int j = 0; j < conflicts.get(i).getRoas().size(); j++){
    			// If the ROAs field contains a valid ROA, the conflict is removed from the list.
    			if(conflicts.get(i).getRoas().get(j).getValidity() == 0){
    				conflicts.remove(i);
    				break;
    			}
    		}
    	}
    }
	
	private void setFilteredWhitelistFalse(){
		for(int i = 0; i < roas.size(); i++){
			roas.get(i).setFilteredWhitelistFalse();
		}
	}
	
	// Ignore all conflicts, i.e. remove whitelist ROAs and do not filter ROAs.
	private void ignoreAll(){
		int i = 0;
		while(i < roas.size()){
			if(roas.get(i).getWhitelisted() == true){
				roas.remove(i);
			}else{
				roas.get(i).setFiltered(false);
				i++;
			}
		}
	}
	
	private void filterAfterTime(int days){
		long now = System.currentTimeMillis();
		for(int i = 0; i < conflicts.size(); i++){
			VerifiedAnnouncement announcement = conflicts.get(i).getAnnouncement();
			if((now - announcement.getCreated_at().getTime()) / DAY > days){
				for(int j = 0; j < conflicts.get(i).getRoaIds().length; j++){
					int roaId = conflicts.get(i).getRoaIds()[j];
					roas.get(roaId - 1).setFiltered(true);
				}
			}
		}
	}
	
	private void whitelistAfterTime(int days){
		long now = System.currentTimeMillis();
		for(int i = 0; i < conflicts.size(); i++){
			VerifiedAnnouncement announcement = conflicts.get(i).getAnnouncement();
			if((now - announcement.getCreated_at().getTime()) / DAY > days){
				roas.add(new Roa(roas.size(), announcement.getAsn(), announcement.getPrefix(), 0, false, true, 0, null, null));
			}
		}
	}

}
