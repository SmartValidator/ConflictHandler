package com;

import java.util.ArrayList;
import java.util.List;

public class ConflictResolver extends ConflictHandler{
	
	private ConflictGetter conflictGetter;
	
	public ConflictResolver(String db, String user, String password){
		conflictGetter = new ConflictGetter(db, user, password);
	}
	
	// Options for testing purposes.
	public void start(Boolean loadAnnouncements, Boolean loadRoas, Boolean loadConflicts){
		conflictGetter.start(loadAnnouncements, loadRoas, loadConflicts);
		
		if(loadAnnouncements){		
			announcements = new ArrayList<>();
			announcements = conflictGetter.announcements;
		}
		if(loadRoas){
			roas = new ArrayList<>();
			roas = conflictGetter.roas;
		}
		if(loadConflicts){
			conflicts = new ArrayList<>();
			conflicts = conflictGetter.conflicts;
		}
		
		this.removeValid();
		this.setFilteredWhitelistFalse();
		System.out.println("Invalid conflicts: " + conflicts.size() + ", ROAs: " + roas.size());
	}
	
	public void handleConflicts(String heuristics){
		System.out.println("Resolving conflicts, please stand by...\n");
        long start = System.currentTimeMillis();
        
        switch(heuristics){
        	case "i": ignoreAll();
        			System.out.println("Ignore all.");
        			break;
        	case "f": filterAll();
        			System.out.println("Filter all.");
        			break;
        	case "w": whitelistAll();
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
	
	private void filterAll(){
		for(int i = 0; i < roas.size(); i++){
			roas.get(i).setFiltered(true);
		}
	}
	
	private void whitelistAll(){
		for(int i = 0; i < conflicts.size(); i++){
			VerifiedAnnouncement announcement = conflicts.get(i).getAnnouncement();
			roas.add(new Roa(roas.size(), announcement.getAsn(), announcement.getPrefix(), 0, false, true, 0, null, null));
		}
	}

}
