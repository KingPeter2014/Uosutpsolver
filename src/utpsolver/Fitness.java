package utpsolver;
import utpsolver.ReadInputs;
/**
 * The fitness class computes overall fitness for a chromosome. It uses ONEMAX() function and tries to maximise the 
 * number of 1s returned. IF a constraint is satisfied, a 1 is added to the fitness value. Hence, a chromosome with 
 * greater value of fitness is a better solution than the others. 
 * 
 * @author petereze
 *
 */

public class Fitness{
	private int subfitness = 0,chromosomeFitness=0;
	private ReadInputs read = new ReadInputs();
	private  int[][][] chromosomes = null;
	private int numChromosome,timeslot = 40,roomCount=0,moduleCount=0,lecturerCount=0;
	private int [] rooms,modules,lecturers,cohorts,startTime,endTime,days;
	public Fitness(int[][][] chromosomes,int numChromosome,int roomCount,int timeslots,int [] rooms,int[] modules ){
		//this.chromosomes = new int[numChromosome][roomCount][timeslots];
		this.chromosomes = chromosomes;
		this.numChromosome=numChromosome;
		this.timeslot= timeslots;
		this.rooms = rooms;
		this.modules=modules;
		this.moduleCount = this.modules.length;
		this.roomCount= this.rooms.length;
		
	}
	/**
	 * Computes the overall fitness for a given Chromosome
	 */
	public int computeOverallFitnessForAChromosome(int chromosome){
		chromosomeFitness=0;
		chromosomeFitness += this.computeClassHeldInCorrectRoomTypeFitness(chromosome);
		
		
		return chromosomeFitness;
	}
	//CONSTRAINT 2: Compute Fitness to check if multiple modules taught by same lecturer are fixed at same time
	public int computeMultipleScheduleForALecturerAtSameTime( int chromosome){
		subfitness=0;
		lecturers = read.getLecturerIds();
		lecturerCount=lecturers.length;
		for(int i=0;i<lecturerCount;i++){
			for(int j=0;j<timeslot;j++){
					if(!checkMultipleScheduling(chromosome,j,lecturers[i]))
						subfitness+=1;
				
			}
		}
		return subfitness;
	}
	
	//CONSTRAINT 3: Compute Fitness to Check if multiple modules belonging to same cohort are fixed at same time
	public int computeMultipleScheduleForACohort(int chromosome){
		subfitness = 0;
		int numyears=0,startingLevel=0;
		cohorts = read.getCohortIds();
		int cohortCount = cohorts.length;
		for(int i=0;i <cohortCount;i++){
			numyears = read.getNumberOfYearsToGraduate(cohorts[i]);
			startingLevel = read.getCohortStartingLevel(cohorts[i]);
			for(int j=0; j <timeslot;j++){
				for(int k = startingLevel; k <(startingLevel + numyears); k++){
					if(!isMultipleScheduleForACohort(chromosome, j,cohorts[i],k))
						subfitness +=1;
				}
			}
		}
		
		return subfitness;
	}
	
	//CONSTRAINT 4: Compute the fitness to check scheduling lecture for part time lecturer only during his or her available time
	public int computePartimeLecturerAvailablityScheduling(int chromosome){
		subfitness  = 0;
		int count=0;
		int [] partTimeLecturers = read.getPartimeLecturerIDs();
		int partimeLecturersCount = partTimeLecturers.length;
		if (partimeLecturersCount==0)
			return subfitness;//No part time lecture exists for this semester
		
		for(int i=0;i<partimeLecturersCount;i++){
			int [] lecturerModules = read.getLecturerModules(partTimeLecturers[i]);
			if(lecturerModules.length==0){
				subfitness +=1;//A reward is given for a part-time lecturer with no module assigned for the semester
				continue;
			}
			int [] startTimes = read.getStartTimeGenesForPartTimeLecturers(partTimeLecturers[i]);
			int [] endTimes = read.getEndTimeGenesForPartTimeLecturers(partTimeLecturers[i]);
			for(int j=0;j<lecturerModules.length;j++){
				if(this.moduleScheduledWithinLecturerAvailableTime(chromosome, lecturerModules[j], startTimes, endTimes))
					count+=1;	
			}
			
			if(count==lecturerModules.length)
				subfitness+=1;
			count=0;
		}
		return subfitness;
	}
	/**
	 * This method picks a particular module belonging to a part-time lecturer and determines if all of its schedule
	 * is within the available time provided by the lecturer. It checks both lecture and lab classes
	 * @param chromosome - The particular chromosome under consideration
	 * @param module - module being checked within the chromosome
	 * @param startTimes - Array of Start times that the lecturer is available for  days of week
	 * @param endTimes - Array of End times that the lecturer is available for  days of week
	 * @return - Returns true if module was scheduled with a part-time lecturer's available time
	 */
	private boolean moduleScheduledWithinLecturerAvailableTime(int chromosome, int module,int[] startTimes,int[] endTimes){
		boolean isWithinTime=false;
		 int matched=0;
		for(int i=0;i<roomCount;i++){
			for(int j=0;j<timeslot;j++){
				if(chromosomes[chromosome][i][j]==module){
					for(int k =0;k <startTimes.length;k++){
						for(int l=startTimes[k];l<=endTimes[k];l++){
							if(j==(l-1)){//We need to subtract 1 from l as it takes values from 1 to 40 instead of 0-39
								matched +=1;
							}
						}
					}
					
				}
			}
		}
		if(matched >0)
			isWithinTime=true;
		return isWithinTime;
	}
	//CONSTRAINT 5: Ensure all modules taught for the semester are scheduled in the timetable
	public int computeToVerifyAllModulesWereScheduled(int chromosome){
		subfitness=0;
		int [] modules = read.getModuleIds();
		int numModules = modules.length;
		int count=0;
		for(int a=0;a<numModules;a++){
			for(int i=0;i<roomCount;i++){
				for(int j=0;j<timeslot;j++){
					if(chromosomes[chromosome][i][j]==modules[a]){
						count+=1;
					}
				}
				
			}
			if(count>0)
				subfitness+=1;
			count=0;//Reset counter for the next module to be checked
		}
		return subfitness;
		
	}
	//CONSTRAINT 6: Accommodate lectures and labs that must hold at a specific time and venue within the week
	public int computeSpecialModuleConstraintViolation(int chromosome){
		subfitness=0;
		int count=0;
		int [] specialModules = read.getModulesWithSpecialConstraints();
		int specialModuleCount = specialModules.length;
		int [] startTimes = read.getStartTimeForSpecialConstraintModules();
		int [] endTimes = read.getEndTimeForSpecialConstraintModules();
		int [] days = read.getDaysForSpecialConstraintModules();
		int [] timeGenes = read.convertDayTimeToTimeGene(days,startTimes);
		int [] rooms = read.getRoomsWithSpecialModuleConstraints();
		if(specialModuleCount==0)
			return subfitness;
		
		for(int a=0;a<specialModuleCount;a++){
			for(int i=0;i<roomCount;i++){
				for(int j=0;j<timeslot;j++){
					if(chromosomes[chromosome][i][j]==specialModules[a] && j==timeGenes[a]-1 && rooms[a]==this.rooms[i]){
						subfitness+=1;
					}
				}
			
			}
		}
		return subfitness;
	}
	
	//CONSTRAINT 7: Computes fitness to Check if all the classes are held in correct Size for this individual chromosome
	public int computeClassHeldInCorrectRoomSizeFitness(int chromosome){
		subfitness = 0;
		boolean iscorrect = false;			
		for(int c=0; c < roomCount; c++){
			for(int a =0;a <timeslot ; a++){
				if(chromosomes[chromosome][c][a]!=0){
					iscorrect = this.checkIfClassHeldInCorrectRoomSize(chromosome, rooms[c], chromosomes[chromosome][c][a]);
					if(iscorrect)
						subfitness+=1;
				}		
			}	
		}
		return subfitness;		
	}
		
	//CONSTRAINT 8:Computes fitness to Check if all the classes are held in correct room type for this individual chromosome
	public int computeClassHeldInCorrectRoomTypeFitness(int chromosome){
		subfitness = 0;
		boolean iscorrect = false;	
		for(int c=0; c < roomCount; c++){
			for(int a =0;a <timeslot ; a++){
				if(chromosomes[chromosome][c][a]!=0){
					iscorrect = this.checkIfClassIsHeldInCorrectRoomType(chromosome, rooms[c], chromosomes[chromosome][c][a]);
					if(iscorrect)
						subfitness+=1;
				}		
			}	
		}
			
		return subfitness;		
	}
	//CONSTRAINT 9: More than 4 hours of consecutive lectures per lecturer
	public int computeMoreThan4HoursOfConsecutiveLecturesPerLecturer(int chromosome){
		subfitness=0;
		boolean isLecturerModule=false,isLecturerModule2=false,found=false;
		int [] lecturers = read.getLecturerIds();
		int numLecturers = lecturers.length;
		int count=0;
		for(int b=0;b <numLecturers;b++){
			for(int i=0;i<timeslot-1;i++){
				isLecturerModule = this.lecturerHasEventAtGivenTime(chromosome,i, lecturers[b]);
				isLecturerModule2 = this.lecturerHasEventAtGivenTime(chromosome,i+1, lecturers[b]);
				if(isLecturerModule && isLecturerModule2)
					count+=1;
				else
					count=0;
				
				if(count==4)
					found=true;
						
						//reset counter before checking the next day
						if(i==7||i==15||i==23||i==31)
							count=0;
					
			}
			if(!found)
				subfitness+=1;//give a reward if not up to five consecutive events found
			//Reset parameters before checking for the next lecturers
			count=0;
			found=false;
		}
		return subfitness;
		
	}
	//CONSTRAINT 10: More than 4 hours of consecutive lectures per Cohort
	public int computeMoreThan4HoursOfConsecutiveLecturesPerCohort(int chromosome){
		subfitness=0;
		int numyears=0,startingLevel=0;
		boolean isCohortModule=false,isCohortModule2=false,found=false;
		int [] cohorts = read.getCohortIds();
		int numCohorts = cohorts.length;
		int count=0;
		for(int c =0;c<numCohorts;c++){
			numyears = read.getNumberOfYearsToGraduate(cohorts[c]);
			startingLevel = read.getCohortStartingLevel(cohorts[c]);
			for(int k = startingLevel; k <(startingLevel + numyears); k++){
				for(int i=0;i<timeslot-1;i++){
					isCohortModule = this.cohortHasEventAtGivenTime(chromosome, i, cohorts[c], k);
					isCohortModule2 = this.cohortHasEventAtGivenTime(chromosome, i+1, c, k);
					if(isCohortModule && isCohortModule2)
						count+=1;
					else
						count=0;
					
					if(count==4)
						found=true;
							
					//reset counter before checking the next day
					if(i==7||i==15||i==23||i==31)
						count=0;
					
				}
				if(!found)
					subfitness+=1;//give a reward if not up to five consecutive events found
				//Reset parameters before checking for the next lecturers
				count=0;
				found=false;
				
			}	
			
		}
		return subfitness;
	}
	//Check if a lecturer has an event at a particular time in any of the rooms
	private boolean lecturerHasEventAtGivenTime(int chromosome, int time,int lecturer){
		boolean hasLecture=false;
		for(int k=0;k<roomCount;k++){
			hasLecture = this.checkLecturerEvent(chromosomes[chromosome][k][time], lecturer);
			if(hasLecture)
				return true;
		}
		return hasLecture;
	}
	//Check if a cohort has an event at a particular time in any of the rooms
		private boolean cohortHasEventAtGivenTime(int chromosome, int time,int cohort, int level){
			boolean hasLecture=false;
			for(int k=0;k<roomCount;k++){
				hasLecture = this.checkCohortEvent(chromosomes[chromosome][k][time] , cohort,level);
				if(hasLecture)
					return true;
			}
			return hasLecture;
		}
	//Checks if an event has been scheduled more than once for a lecturer in different room for an individual chromosome on a given timeslot
	private boolean checkMultipleScheduling(int chromosome,int timeslot, int lecturer){
		boolean isMultiple = true, isLecturerEvent=false;
		int countSchedules=0;
		for(int i=0;i< roomCount;i++){
			if(chromosomes[chromosome][i][timeslot]!=0){
				isLecturerEvent = checkLecturerEvent(chromosomes[chromosome][i][timeslot], lecturer);
				if(isLecturerEvent)
					countSchedules +=1;
			}
		}
		if(countSchedules <=1)
			isMultiple = false;
		return isMultiple;
		
	}
	//Checks if an event has been scheduled more than once for a Cohort at a given level of level of study
	private boolean isMultipleScheduleForACohort(int chromosome, int timeslot,int cohort,int level){
		//For each cohort, get the starting level and number of years
		boolean isMultiple = true, isCohortEvent=false;
		int countSchedules=0;
		//For each level, check if there is clash of lecture
		//For each cohort, for each timeslot and for each level, if there is no clash return a value

		for(int i=0;i< roomCount;i++){
			if(chromosomes[chromosome][i][timeslot]!=0){
				isCohortEvent = checkCohortEvent(chromosomes[chromosome][i][timeslot], cohort,level);
				if(isCohortEvent)
					countSchedules +=1;
			}
		}
		if(countSchedules <=1)
			isMultiple = false;
		return isMultiple;
		
	}
	
	//Check if a particular event belongs to a lecturer
	private boolean checkLecturerEvent(int event, int lecturer){
		return read.confirmEventBelongsToLecturer(event, lecturer);
		
	}
	//Check if a particular event belongs to a Cohort of a particular Level
	private boolean checkCohortEvent(int module , int cohort, int level){
		
		return read.confirmCohortEventAtALevelOfStudy(module, cohort, level);
	}
	
	//Check if class held in appropriate room type for a gene event in a chromosome
	private boolean checkIfClassIsHeldInCorrectRoomType(int chromosome,int room, int module){
		boolean isCorrect = false;
		String roomType = read.getRoomType(room);
		String moduleType = read.getModuleType(module);
		
		if(roomType.equals(moduleType)){
			isCorrect = true;
		}
		else{
			isCorrect = false;
		}
		return isCorrect;
		
	}
	
	//Check if class held in appropriate room Size for a gene event in a chromosome
	private boolean checkIfClassHeldInCorrectRoomSize(int chromosome,int room,int module){
		int tolerance = -10; // A tolerance of 10 is when module size is more than room Capacity by only 10 students
		boolean isCorrect = false;
		int roomSize = read.getRoomCapacity(room);
		int moduleSize = read.getModuleSize(module);
		if(moduleSize <= roomSize || (roomSize - moduleSize)  >= tolerance)
			isCorrect=true;
		else
			isCorrect=false;
		
		return isCorrect;
	}

}
