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
	private  int[][][] chidren = null;
	private int numChromosome,timeslot = 40,roomCount=0,moduleCount=0,lecturerCount=0;
	private int [] rooms,modules,lecturers,cohorts,startTime,endTime;
	public int [] fitnessValues,hardFitnesses,softFitness,chromosomeIndices;
	public int [] childrenfitnessValues,childrenhardFitnesses,childrensoftFitness;
	private int [] startTimes = read.getStartTimeForSpecialConstraintModules();
	private int [] endTimes = read.getEndTimeForSpecialConstraintModules();
	private int [] days = read.getDaysForSpecialConstraintModules();
	private int [] timeGenes = read.convertDayTimeToTimeGene(days,startTimes);
	private int [] specialRooms = read.getRoomsWithSpecialModuleConstraints();
	private int [] specialModules = read.getModulesWithSpecialConstraints();
	public static int maxH2=0,maxH3=0,maxH4=0,maxH5=0,maxH6=0,maxH7=0,maxH8=0,maxS9=0,maxS10=0,maxS11=5,maxS12=5,maxSoft=0,maxHard=0,maxReward = 0;
	public double percentHard=0,percentSoft=0,percentOverall=0;
	public String message="";
	String [] moduleTypes, roomTypes;
	public Fitness(int[][][] chromosomes,int numChromosome,int roomCount,int timeslots,int [] rooms,int[] modules, String[] moduleTypes, String[] roomTypes ){
		//this.chromosomes = new int[numChromosome][roomCount][timeslots];
		this.chromosomes = chromosomes;
		this.numChromosome=numChromosome;
		this.fitnessValues = new int[numChromosome];
		this.hardFitnesses = new int[numChromosome];
		this.chromosomeIndices = new int[numChromosome];
		this.softFitness = new int[numChromosome];
		this.timeslot= timeslots;
		this.rooms = rooms;
		this.modules=modules;
		this.moduleTypes = moduleTypes;
		this.roomTypes = roomTypes;
		this.moduleCount = this.modules.length;
		this.roomCount= this.rooms.length;
		lecturers = read.getLecturerIds();
		lecturerCount=lecturers.length;
		cohorts = read.getCohortIds();
		this.computeMaximumFitnesses();
	}
	//Compute the fitness of each chromosome in the entire population
	public int[] computeFitnessOfEntirePopulation(){
		long st=0;
		for(int a=0; a<numChromosome;a++){
			st = System.currentTimeMillis();
			//System.out.println("Evaluating the fitness of entire population....,Chromo:" + (a+1));
			fitnessValues[a] = computeOverallFitnessForAChromosome(a);
			//System.out.println("Evaluation of chromosome " + (a+1) + " took: " + (System.currentTimeMillis() - st)/1000 + "Secs to execute");			
		}
		fitnessValues = this.sortFitnessDescending(fitnessValues);
		return fitnessValues;
	}
	/**
	 * Computes the overall fitness for a given Chromosome
	 */
	public int computeOverallFitnessForAChromosome(int chromosome){
		message ="H1: No multiple event at same venue and Time: Never violated due to chromosome representation method";
		chromosomeFitness=0;
		chromosomeFitness =this.getOverallRewardsOnHardConstraints(chromosome);
		chromosomeFitness += this.getOverallRewardsOnSoftConstraints(chromosome);
		message += "<br/><span class=\"success\">Overall fitness for Chromosome " + (chromosome ) + " is: " + chromosomeFitness + " out of " + Fitness.maxReward + "</span>";
		this.percentOverall = (chromosomeFitness *100.0)/this.maxReward;
		message += "<br/><h2><b>Constraint Satisfaction Summary</b></h2><hr>Hard Constraint Satisfaction: " + this.percentHard + "%,";
		message += " Soft Constraint Satisfaction: " + this.percentSoft + "%<br/>";
		message += "Overall Constraints Satisfaction: " + this.percentOverall + "%<hr/>";
		return chromosomeFitness;
	}

	//Compute total hard constraints Rewards
	public int getOverallRewardsOnHardConstraints(int chromosome){
		int hsFitness=0;
		int h8 = this.computeClassHeldInCorrectRoomTypeFitness(chromosome);
		if(h8 > this.maxH8)
			h8=maxH8;
		if(this.maxH8 !=0)
			h8=(h8*100)/this.maxH8;
		
		int h7 =  this.computeClassHeldInCorrectRoomSizeFitness(chromosome);
		if(h7 > this.maxH7)
			h7=maxH7;
		if(this.maxH7 !=0)
			h7=(h7*100)/this.maxH7;
		int h3= this.computeMultipleScheduleForACohort(chromosome);
		if(this.maxH3 !=0)
			h3 = (h3*100)/this.maxH3;
		int h2 = this.computeMultipleScheduleForALecturerAtSameTime(chromosome);
		if(this.maxH2 !=0)
			h2 = (h2*100)/this.maxH2;
		int h4 = this.computePartimeLecturerAvailablityScheduling(chromosome);
		if(this.maxH4 !=0)
			h4 = (h4*100)/this.maxH4;
		int h6 = this.computeSpecialModuleConstraintViolation(chromosome);
		if(this.maxH6 !=0)
			h6 = (h6*100)/this.maxH6;
		if(this.maxH6==0)
			h6=0;
		int h5 = this.computeToVerifyAllModulesWereScheduled(chromosome);
		if(this.maxH5 !=0)
			h5 = (h5*100)/this.maxH5;
		hsFitness = h2 + h3 + h4 + h5+ h6 + h7+h8;
		this.hardFitnesses[chromosome] = hsFitness;
		message  += "<br/>H2:Non-Multiple Scheduling for Lecturer: " + h2 + "% (of " + Fitness.maxH2 + " instances)";
		message += "<br/>H3:Non-Multiple Scheduling for Cohort:" + h3+ "% (of " + Fitness.maxH3 + " instances)";
		message+= "<br/> H4:Part-time Lecturer availability observed:" + h4 + " out of (" + Fitness.maxH4+ " instances)";
		message += "<br/> H5:All modules Scheduled:" + h5 + "%  (of " + Fitness.maxH5+ " instances)";
		message += "<br/> H6:Special Module correctly allocated to preffered room and time:" + h6 + "% (of " + Fitness.maxH6+ " instances)";
		message += "<br/> H7:Classes held in correct room size:" + h7+ "% (of " + Fitness.maxH7 + " instances)";
		message += "<br/>H8: Classes held in correct room type: " + h8 + "% (of " + Fitness.maxH8+ " instances)";
		if(hsFitness == Fitness.maxHard)
			message += "<br/><span class=\"success\">HARD Constraint fitness for Chromosome " + (chromosome ) + " is: " + hsFitness + "% out of " + Fitness.maxHard + " %</span>";
		else
			message += "<br/><span class=\"error\">HARD Constraint fitness for Chromosome " + (chromosome ) + " is: " + hsFitness + " out of " + Fitness.maxHard + "</span>";
		this.percentHard = (hsFitness *100.0)/this.maxHard;
		return hsFitness;
	}
	//Computes all maximum fitnesses immediately the fitness class is instantiated and saves them in static variables
	private void computeMaximumFitnesses(){
		this.maxH2 = this.getMaxH2Reward();
		this.maxH3 = this.getMaxH3Reward();
		this.maxH4 = this.getMaxH4Reward();
		this.maxH5 = this.getMaxH5Reward();
		this.maxH6 = this.getMaxH6Reward();
		this.maxH7 = this.getMaxH7Reward();
		this.maxH8 = this.maxH7;
		this.maxS9 = this.getMaxS9Reward();
		this.maxS10 = this.getMaxS10Reward();
		this.maxSoft = this.getMaximumPossibleSoftConstraintFitnessValue();
		//this.maxHard = this.getMaximumPossibleHardConstraintFitnessValue();
		this.maxHard = this.getMaximumHardConstraintNormalised();
		this.maxReward = this.maxPossibleFitnessValue();
	}
	
	//Get the indices for the chromosomes arranged in descending order of fitness
	public int [] getSortedChromosomeIndices(){
		return this.chromosomeIndices;
	}
	//Compute total soft constraint rewards
	public int getOverallRewardsOnSoftConstraints(int chromosome){
		int scFitness = 0;
		int s12 = this.computeAvoidLaunchTimeEvents(chromosome);
		int s10 = this.computeMoreThan4HoursOfConsecutiveLecturesPerCohort(chromosome);
		int s9 = this.computeMoreThan4HoursOfConsecutiveLecturesPerLecturer(chromosome);
		int s11 = this.computeWednesdayAfternoonEventConstraint(chromosome);
		scFitness = s9 + s10 + s11 + s12;
		this.softFitness[chromosome]=scFitness;
		message += "<br/>S9: Not more than 4-hr consecutive Events for Lecturer:" + s9 + " out of "+ Fitness.maxS9;
		message += "<br/> S10:Not more than 4-hr consecutive Events for a Cohort: " + s10 + " out of " + Fitness.maxS10;
		message += "<br/> S11: No lecture/Lab fixed on Wednesday afternoon: " + s11 + " out of 5";
		message += "<br/> S12: No lecture/Lab during Launch time: " + s12 + " out of 5";
		if(scFitness < this.maxSoft)
			message += "<br/><span class=\"warning\">SOFT Constraint fitness for Chromosome " + (chromosome ) + " is: " + scFitness + " out of " + Fitness.maxSoft + "</span>";
		else
			message += "<br/><span class=\"success\">SOFT Constraint fitness for Chromosome " + (chromosome ) + " is: " + scFitness + " out of " + Fitness.maxSoft + "</span>";
		this.percentSoft = (scFitness *100.0)/this.maxSoft;
		return scFitness;
		
		
	}
	//Compute maximum fitness possible for a chromosome
	public int maxPossibleFitnessValue(){
		int maxFitnessValue=0;
		//maxFitnessValue = this.getMaximumPossibleHardConstraintFitnessValue() ;
		maxFitnessValue = this.getMaximumHardConstraintNormalised();
		maxFitnessValue += this.getMaximumPossibleSoftConstraintFitnessValue();
		return maxFitnessValue;
		
	}
	//Compute maximum fitness value possible for all hard constraints
	public int getMaximumPossibleHardConstraintFitnessValue(){
		int maxFitnessValue=0;
		maxFitnessValue = this.maxH2+ this.maxH3+ this.maxH4 + this.maxH5 ;
		maxFitnessValue += this.maxH6 + this.maxH7 + this.maxH8;
		return maxFitnessValue;	
	}
	//normalise hard constraint maximum to 100%
	public int getMaximumHardConstraintNormalised(){
		int normalised=0;
		if(this.maxH2!=0)
			normalised += Chromosomes.hardWeight;
		if(this.maxH3!=0)
			normalised += Chromosomes.hardWeight;
		if(this.maxH4!=0)
			normalised += Chromosomes.hardWeight;
		if(this.maxH5!=0)
			normalised += Chromosomes.hardWeight;
		if(this.maxH6!=0)
			normalised += Chromosomes.hardWeight;
		if(this.maxH7!=0)
			normalised += Chromosomes.hardWeight;
		if(this.maxH8!=0)
			normalised += Chromosomes.hardWeight;
		return normalised;
		
	}
	//Compute the maximum fitness value possible for all soft constraints
	public int getMaximumPossibleSoftConstraintFitnessValue(){
		int maxFitnessValue=0;
		maxFitnessValue += this.getMaxS9Reward();
		maxFitnessValue += this.getMaxS10Reward() + 10;
		return maxFitnessValue;
	}
	//CONSTRAINT 2: Compute Fitness to check if multiple modules taught by same lecturer are fixed at same time
	public int computeMultipleScheduleForALecturerAtSameTime( int chromosome){
		subfitness=0;
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
		if (partimeLecturersCount==0){
			//System.out.println(" No par")
			return subfitness;//No part time lecture exists for this semester
			
		}
		
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
		
		int specialModuleCount = specialModules.length;
		
		if(specialModuleCount==0)
			return subfitness;
		
		for(int a=0;a<specialModuleCount;a++){
			for(int i=0;i<roomCount;i++){
				for(int j=0;j<timeslot;j++){
					if(chromosomes[chromosome][i][j]==specialModules[a] && j==this.timeGenes[a]-1 && this.specialRooms[a]==this.rooms[i]){
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
		//int [] cohorts = read.getCohortIds();
		int numCohorts = cohorts.length;
		int count=0;
		for(int c =0;c<numCohorts;c++){
			numyears = read.getNumberOfYearsToGraduate(cohorts[c]);
			startingLevel = read.getCohortStartingLevel(cohorts[c]);
			for(int k = startingLevel; k <(startingLevel + numyears); k++){
				for(int i=0;i<timeslot-1;i++){
					isCohortModule = this.cohortHasEventAtGivenTime(chromosome, i, cohorts[c], k);
					isCohortModule2 = this.cohortHasEventAtGivenTime(chromosome, i+1, cohorts[c], k);
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
	
	//S11: Constraint to avoid fixing lectures or labs on Wednesday afternoon
	public int computeWednesdayAfternoonEventConstraint(int chromosome){
		subfitness=0;
		for(int i=19;i<24;i++){
			if(!this.anyEventFixedWithinTimeslot(chromosome, i))
				subfitness+=1;
		}
		return subfitness;
	}
	
	//S12: Constraint to avoid launch time everyday
	public int computeAvoidLaunchTimeEvents(int chromosome){
		subfitness=0;
		for(int i=4;i<37; i=i+8){
			
				if(!this.anyEventFixedWithinTimeslot(chromosome, i))
					subfitness+=1;
			}
			return subfitness;
	}
	//Check if any event at all is scheduled within a particular time slot
	private boolean anyEventFixedWithinTimeslot(int chromosome, int timeslot){
		for(int j=0;j < roomCount;j++){
			if(chromosomes[chromosome][j][timeslot]!=0){
				return true;
			}
		}
		return false;
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
	public boolean isMultipleScheduleForACohort(int chromosome, int timeslot,int cohort,int level){
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
	public void evaluateFitnessOfChildren(int [][][] children){
		//Save statistics for entire generation
		int [][][] saveChromosome=null;
		saveChromosome= this.chromosomes;
		int saveNumChromosome = this.numChromosome;
		this.chromosomes = children;
		this.numChromosome = 2;
		int [] tempfitnessValues,temphardFitnesses,tempsoftFitness;
		tempfitnessValues = this.fitnessValues;
		temphardFitnesses = this.hardFitnesses;
		tempsoftFitness = this.softFitness;
		
		//Compute fitness of children
		this.computeFitnessOfEntirePopulation();
		this.childrenfitnessValues = this.fitnessValues;
		this.childrenhardFitnesses = this.hardFitnesses;
		this.childrensoftFitness = this.softFitness;
		
		//Put back the fitness of parents to old values
		this.chromosomes = saveChromosome;
		this.numChromosome = saveNumChromosome;
		this.fitnessValues = tempfitnessValues;
		this.hardFitnesses = temphardFitnesses;
		this.softFitness = tempsoftFitness;
		
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
		int tolerance = -20; // A tolerance of 10 is when module size is more than room Capacity by only 10 students
		boolean isCorrect = false;
		int roomSize = read.getRoomCapacity(room);
		int moduleSize = read.getModuleSize(module);
		if(moduleSize <= roomSize || (roomSize - moduleSize)  >= tolerance)
			isCorrect=true;
		else
			isCorrect=false;
		
		return isCorrect;
	}
	//Return maximum reward for NOT fixing multiple lectures for a lecturer within time slot
	public int getMaxH2Reward(){
		return timeslot * read.getLecturerCount();
	}
	
	//Returns maximum reward for NOT fixing multiple events for a cohort within a timeslot.
	public int getMaxH3Reward(){
		//int [] cohorts = read.getCohortIds();
		int numCohorts = cohorts.length;
		int maxreward=0,numyears=0,startingLevel=0;
		for(int c =0;c<numCohorts;c++){
			numyears = read.getNumberOfYearsToGraduate(cohorts[c]);
			startingLevel = read.getCohortStartingLevel(cohorts[c]);
			for(int k = startingLevel; k <(startingLevel + numyears); k++){
				maxreward +=1;
			}
		}
		return maxreward = maxreward*timeslot;
	}
	
	//Returns maximum reward for accomoodating time preferences of part time lecturers
	public int getMaxH4Reward(){
		return read.getPartimeLecturerIDs().length;
	}
	
	//Returns maximum reward for ensuring that all lectures and labs are scheduled in the timetable.
	public int getMaxH5Reward(){
		return read.getmoduleCount();
	}
	//Returns maximum reward for events that must hold in specific rooms and time.
	public int getMaxH6Reward(){
		return read.getModulesWithSpecialConstraints().length;
	}
	
	//Returns maximum reward for events that held in correct room size, Same as H8Max
	public int getMaxH7Reward(){
		int max=0;
		for(int a=0;a <timeslot;a++){
			for(int b=0;b <roomCount;b++){
				if(chromosomes[0][b][a]!=0)
					max+=1;
			}
		}
		return max;
	}
	//Returns maximum reward for not more than 4-hour lecturer schedule
	public int getMaxS9Reward(){
		return read.getLecturerCount();
	}
	//Returns maximum reward for not more than 4-hour cohort schedule
	public int getMaxS10Reward(){
		return this.getMaxH3Reward()/timeslot;
	}
	public int[] sortFitnessDescending(int [] fitnessValues){
		int len = fitnessValues.length;
		for( int i=0;i<len;i++)
			this.chromosomeIndices[i]=i;
			
		int temp = 0;
		//Sought according to hard fitness
		for(int i=0;i<len;i++){
			for(int j=0;j<len-1;j++){
				if(this.hardFitnesses[j] <this.hardFitnesses[j+1]){
					//if(fitnessValues[j]<fitnessValues[j+1] ){
						temp=fitnessValues[j];
						fitnessValues[j] = fitnessValues[j+1];
						fitnessValues[j+1]=temp;
						
						temp=this.chromosomeIndices[j];
						this.chromosomeIndices[j]=this.chromosomeIndices[j+1];
						this.chromosomeIndices[j+1]=temp;
						
						temp=this.hardFitnesses[j];
						this.hardFitnesses[j]=this.hardFitnesses[j+1];
						this.hardFitnesses[j+1]=temp;
					//}
				}
			}
		}
		//Sought all feasible solutions according to level of optimisation
		for(int i=0;i<len;i++){
			for(int j=0;j<len-1;j++){
				if(this.hardFitnesses[j] >= this.maxHard){
					if(fitnessValues[j]<fitnessValues[j+1] ){
						temp=fitnessValues[j];
						fitnessValues[j] = fitnessValues[j+1];
						fitnessValues[j+1]=temp;
						
						temp=this.chromosomeIndices[j];
						this.chromosomeIndices[j]=this.chromosomeIndices[j+1];
						this.chromosomeIndices[j+1]=temp;
						
						temp=this.hardFitnesses[j];
						this.hardFitnesses[j]=this.hardFitnesses[j+1];
						this.hardFitnesses[j+1]=temp;
					}
				}
			}
		}
		
		return fitnessValues;
	}
}
