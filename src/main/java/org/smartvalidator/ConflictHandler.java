package main.java.org.smartvalidator;

import java.util.ArrayList;

public class ConflictHandler extends ConflictClasses {
	
	private DatabaseConnector databaseConnector;
	private HeuristicApplier heuristicApplier;
	
	public ConflictHandler(){
		announcements = new ArrayList<>();
		roas = new ArrayList<>();
		conflicts = new ArrayList<>();
		databaseConnector = new DatabaseConnector();
		load(true, true, true);
		
		heuristicApplier = new HeuristicApplier(announcements, roas, conflicts);
        heuristicApplier.removeValid();
        heuristicApplier.setFilteredWhitelistFalse();
        
		System.out.println("Invalid conflicts: " + conflicts.size());
	}
	
	private void load(Boolean loadAnnouncements, Boolean loadRoas, Boolean loadConflicts){
		System.out.println("Loading conflicts, please stand by...\n");
        long start = System.currentTimeMillis();
		
		if(loadAnnouncements){
			databaseConnector.loadAnnouncements();
			announcements = databaseConnector.announcements;
		}
		if(loadRoas){
			databaseConnector.loadRoas();
			roas = databaseConnector.roas;

		}
		if(loadConflicts){
			databaseConnector.loadConflicts();
			conflicts = databaseConnector.conflicts;
		}
		long end = System.currentTimeMillis();
        long diff = end - start;
        System.out.println("Loaded ROAs: " + roas.size() + ", Loaded conflicts: " + conflicts.size() + 
        		". Loading took " + (diff / 1000)  + "." + (diff % 1000) + " s.\n");
	}

}
