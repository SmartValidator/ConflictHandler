package com;

import java.util.ArrayList;
import java.util.List;

public class ConflictResolver extends ConflictHandler{
	
	private ConflictGetter conflictGetter;
	
	public ConflictResolver(String db, String user, String password){
		conflictGetter = new ConflictGetter(db, user, password);
		this.start();
	}
	
	public void start(){
        
		conflictGetter.start();
		roas = new ArrayList<>();
		conflicts = new ArrayList<>();
		roas = conflictGetter.getRoas();
		conflicts = conflictGetter.getConflicts();
		
//		System.out.println("\nLoaded conflicts: " + conflicts.size());
		
		System.out.println("\nHandling conflicts, please stand by...");
        long start = System.currentTimeMillis();
		
		this.removeValid();
		
		System.out.println("Invalid conflicts:" + conflicts.size());
		
		this.ignoreAll();
		
		System.out.println("Ignored all");
		
        long end = System.currentTimeMillis();
        long diff = end - start;
        System.out.println("\nFinished. Handling took " + (diff / 1000)  + "." + (diff % 1000) + " s.");
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
	
	private void ignoreAll(){
		for(int i = 0; i < roas.size(); i++){
			roas.get(i).setFiltered(false);
		}
	}

}
