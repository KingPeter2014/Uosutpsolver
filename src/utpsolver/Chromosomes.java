package utpsolver;
import java.util.Random;

import utpsolver.ReadInputs;
import utpsolver.Fitness;
/**
 * 
 * @author petereze
 * This class handles the the random initialization of the GA Population
 *
 */
public class Chromosomes {
	public  int numChromosomes = 100, fitness=0;
	public  int[][][] chromosomes = null,bestChromosome,worstChromosome;
	public static int timeslot = 40,roomCount=0,moduleCount=0,lecturerCount=0;
	private String roomType="",overallTimetable="";
	public int [] rooms,modules,cohorts;
	String [] moduleTypes, roomTypes;
	private int freeChromosome=0,freeRoom=-1,freeTimeslot=-1;
	private boolean consecutiveFreeGeneFound=false;
	ReadInputs read = new ReadInputs();
	private boolean exemptLunchTime=false,exemptWednesdayAfternoon=false;
	Fitness fit = null;
	public long startTime = 0,endTime=0;
	
	public Chromosomes(){
		startTime=System.currentTimeMillis();
		lecturerCount=read.getLecturerCount();
		rooms= read.getRoomIds();
		modules = read.getModuleIds();
		cohorts = read.getCohortIds();
		roomCount= rooms.length;
		moduleCount=modules.length;
		moduleTypes = read.getModuleTypeArray();
		roomTypes = read.getRoomTypeArray();
		chromosomes = new int[numChromosomes][roomCount][timeslot];
		this.bestChromosome = new int[1][roomCount][timeslot];
		this.worstChromosome = new int[1][roomCount][timeslot];
		this.initialAllChromosomesToZero();
		this.initializePopulation();
		//initializePopulationWithElitism();
		fit = new Fitness(chromosomes,numChromosomes,roomCount,timeslot,rooms,modules,moduleTypes,roomTypes);
		
	}
	
	public void Chromosomes(boolean useLunchTime,boolean useWedAfternoon){
		lecturerCount=read.getLecturerCount();
		rooms= read.getRoomIds();
		modules = read.getModuleIds();
		roomCount= rooms.length;
		moduleCount=modules.length;
		chromosomes = new int[numChromosomes][roomCount][timeslot];
		this.initialAllChromosomesToZero();
		this.initializePopulationWithElitism();
		
		
	}
	/**
	 * Utilise the room, lecturer and other constraints defined to ensure that the initial population
	 * has satisfied most or all of the hard constraints
	 * Use randomization to allocate remaining events that has no special requirements
	 */
	private void initializePopulation(){
		int time=0,rm=0;
		boolean isLastHour=false, isOccupied=true,inserted=false;
		String moduleType="";
		int modulehours=0,assigned=0;
		for(int i=0; i <numChromosomes;i++){
			for(int d=0; d < moduleCount; d++){
				time = this.generateRandomInteger(timeslot);
				rm =this.generateRandomInteger(roomCount);
				moduleType = read.getModuleType(modules[d]);
				if(moduleType.equals("lab"))
					modulehours = read.getLabHoursPerWeek(modules[d]);
				else
					modulehours = read.getLectureHoursPerWeek(modules[d],"lecture");
				isLastHour = this.isLastDayTimeSlot(time);
				isOccupied = this.isOccupied(i, rm-1, time-1);
				//Handle 1-hour Lecture or Lab genes
				if(!isOccupied && modulehours==1){
					//chromosomes[i][rm-1][time-1]=modules[d];
					inserted = this.insertGene(i, rm-1, time-1, modules[d]);
					
				}
				else if(isOccupied && modulehours==1){
					//Find unoccupied space
					this.findUnoccuppiedGene(i);
					if(freeRoom==-1 &&freeTimeslot==-1){
						System.out.println("Not enough resources to host these lectures");
						//System.exit(0);	
					}
					//chromosomes[i][freeRoom][freeTimeslot]=modules[d];
					inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
				}
				
				//Handle 2-hour Lecture or Lab
				if(modulehours==2){
					this.findConsecutiveUnoccuppiedGene(i);
					if(freeRoom !=-1 && freeTimeslot !=-1 && this.consecutiveFreeGeneFound){
						inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						inserted = this.insertGene(i, freeRoom, freeTimeslot+1, modules[d]);
						this.consecutiveFreeGeneFound=false;
					}
					else{
						//Split into one hour each and insert at different locations in the chromosome
						//Find unoccupied space for first period
						this.findUnoccuppiedGene(i);
						if(freeRoom==-1 &&freeTimeslot==-1){
							System.out.println("Not enough resources to host these lectures");
							System.exit(0);	
						}
						
						inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						
						//Find unoccupied space for second period
						this.findUnoccuppiedGene(i);
						if(freeRoom==-1 &&freeTimeslot==-1){
							System.out.println("Not enough resources to host these lectures");
							System.exit(0);	
						}
						
						inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
					}
					
					
				}
				//Handle 3-hour Lecture or Lab per week
				if(modulehours==3){
					
				}
				
				
				//Handle 4-hour Lecture or Lab per week 
				if(modulehours==4){
					
				}
			}//End modules loop
			
		}//End chromosomes loop	
	}
	//Schedule two hour lecture or lab for part time lecturers
	private void handleSpecificTwoHourModulesScheduling(int chromosome,int module,int startTime,String moduleType){
		if(moduleType.equals("lab"))
			this.findConsecutiveFreeLabRoomsFromStartTime(chromosome, startTime);
		else
			this.findConsecutiveFreeLectureRoomsFromStartTime(chromosome, startTime);
		this.insertGene(chromosome, freeRoom, freeTimeslot, module);
		this.insertGene(chromosome, freeRoom, freeTimeslot+1, module);
		this.consecutiveFreeGeneFound=false;
		
	}
	private void handleRandomTwoHourModulesScheduling(int chromosome,int module,int startTime,String moduleType){
		if(moduleType.equals("lab"))
			this.findConsecutiveFreeLabRoomsFromStartTime(chromosome, startTime);
		else
			this.findConsecutiveFreeLectureRoomsFromStartTime(chromosome, startTime);
		if(this.consecutiveFreeGeneFound){
			this.insertGene(chromosome, freeRoom, freeTimeslot, module);
			this.insertGene(chromosome, freeRoom, freeTimeslot+1, module);
			this.consecutiveFreeGeneFound=false;
		}
		
	}
	//Schedule two hour lecture or lab for general purposes
	private void handleTwoHourModulesScheduling(int chromosome,int module,int startTime,String moduleType){
		if(moduleType.equals("lab"))
			this.findConsecutiveFreeLabRoom(chromosome);
		else
			this.findConsecutiveFreeLectureRooms(chromosome,module);
		this.insertGene(chromosome, freeRoom, freeTimeslot, module);
		this.insertGene(chromosome, freeRoom, freeTimeslot+1, module);
		this.consecutiveFreeGeneFound=false;
			
		}
	//Schedule two hour lecture or lab for general purposes
	private void handleTwoHourUnclashingCohortModulesScheduling(int chromosome,int module,String moduleType, int cohort,int level){
		boolean isOccupied1 = this.isMultipleScheduleForACohort(chromosome, freeTimeslot, cohort, level);
		boolean isOccupied2 = this.isMultipleScheduleForACohort(chromosome, freeTimeslot+1, cohort, level);
		boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(chromosome, this.freeTimeslot, cohort, level);
		boolean isCorrectSize = this.roomSizeMatchedModuleSize(module, freeRoom);
		if(!isOccupied1  && !isOccupied2 && !isMoreThan4CohortHours && isCorrectSize){
			this.insertGene(chromosome, freeRoom, freeTimeslot, module);
			this.insertGene(chromosome, freeRoom, freeTimeslot+1, module);
			
			return;
		}
				
		if(moduleType.equals("lab"))
			this.findConsecutiveFreeCohortLabRooms(chromosome,cohort,level,module);
		else
			this.findConsecutiveFreeCohortLectureRooms(chromosome,cohort,level,module);
		this.insertGene(chromosome, freeRoom, freeTimeslot, module);
		this.insertGene(chromosome, freeRoom, freeTimeslot+1, module);
		this.consecutiveFreeGeneFound=false;

			}

	private void initializePopulationWithElitism(){
		
		//Schedule special modules first to satisfy special room and time constraints (H6)
		this.scheduleSpecialModules();
		//Schedule lectures belonging to partime lecturers(H4)
		this.schedulePartimeLecturerModules();
		
		//Do some random scheduling
		this.scheduleRandomly();
		//Scheduling for cohorts to avoid clashing for each cohort at a given level
		this.scheduleForCohortsWithoutClashing();
		
		//Schedule other remaining modules
		this.scheduleRemainingModules();
		
	}
	//Schedule modules for cohorts without clashing
	private void scheduleForCohortsWithoutClashing(){
		int numyears=0,startingLevel=0,modulehours=0;
		String moduleType="";
		boolean inserted=false;
		int [] cohortsAssignedTo = null;
		int cohortCount = cohorts.length;
		for(int i=0;i<this.numChromosomes;i++){
			for(int d=0; d < moduleCount; d++){
				if(!this.isScheduled(i, this.modules[d])){
					cohortsAssignedTo = read.getModuleCohort(this.modules[d]);
					
					moduleType= this.getModuleType(d);
					if(moduleType.equals("lab")){
						modulehours = read.getLabHoursPerWeek(modules[d]);
						this.findFreeLabRoom(i,modules[d]);
					}
					else{
						modulehours = read.getLectureHoursPerWeek(modules[d],"lecture");
						this.findFreeLectureRoom(i);
					}
					int level = read.getModuleCohortLevel(modules[d], cohortsAssignedTo[0]);
					boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(i, this.freeTimeslot, cohortsAssignedTo[0], level);
					boolean isMultiple = this.isMultipleScheduleForACohort(i, this.freeTimeslot, cohortsAssignedTo[0], level);
					if( modulehours==1 && freeRoom!=-1){
						
						if(!isMultiple & !isMoreThan4CohortHours)
							inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						
					}
					else if( modulehours==2){
						
						this.handleTwoHourUnclashingCohortModulesScheduling(i, modules[d], moduleType,cohortsAssignedTo[0], level);
					}
					
				}
				
			}
			
		}
	}
	//Randomize some schedules to introduce some diversity
	private void scheduleRandomly(){
		int time=0,rm=0;
		boolean isLastHour=false, isOccupied=true,inserted=false,isMultiple=false;
		String moduleType="";
		int modulehours=0,assigned=0;
		int [] cohortsAssignedTo = null;
		
		for(int i=0; i <numChromosomes;i++){
			for(int d=0; d < moduleCount; d++){
				if(!this.isScheduled(i, this.modules[d])){
					time = this.generateRandomInteger(timeslot);
					rm =this.generateRandomInteger(roomCount);
					//moduleType = read.getModuleType(modules[d]);
					moduleType= this.getModuleType(d);
					if(moduleType.equals("lab"))
						modulehours = read.getLabHoursPerWeek(modules[d]);
					else
						modulehours = read.getLectureHoursPerWeek(modules[d],"lecture");
					isLastHour = this.isLastDayTimeSlot(time);
					isOccupied = this.isOccupied(i, rm-1, time-1);
					cohortsAssignedTo = read.getModuleCohort(this.modules[d]);
					int level = read.getModuleCohortLevel(modules[d], cohortsAssignedTo[0]);
					isMultiple = this.isMultipleScheduleForACohort(i, time-1, cohortsAssignedTo[0], level);
					boolean isCorrectRoomSize = this.roomSizeMatchedModuleSize(modules[d], rm-1);
					//Handle 1-hour Lecture or Lab genes
					if(!isOccupied && isCorrectRoomSize && !isMultiple && modulehours==1 && moduleType.equals(this.getRoomType(rm-1))){
						//System.out.println("Randomly Scheduled:" + modules[d] + " in Chromosome " + i);
						inserted = this.insertGene(i, rm-1, time-1, modules[d]);
					}
					
					//Handle 2-hour Lecture or Lab
					if(modulehours==2){
						if(time==Chromosomes.timeslot)
							continue;//As two-hour module cannot start 4pm on Friday, which is timeslot 40
						
						boolean isMultiple2 = this.isMultipleScheduleForACohort(i, time, cohortsAssignedTo[0], level);
						if( !isMultiple &&  isCorrectRoomSize && !isMultiple2 && moduleType.equals(this.getRoomType(rm-1))){
							inserted = this.insertGene(i, rm-1, time-1, modules[d]);
							inserted = this.insertGene(i, rm-1, time, modules[d]);
							//System.out.println("Double Randomly Scheduled:" + modules[d] + " in Chromosome " + i);
						}
						
						
					}
					//Handle 3-hour Lecture or Lab per week
					if(modulehours==3){
						
					}
					
					
					//Handle 4-hour Lecture or Lab per week 
					if(modulehours==4){
						
					}
				}//End if not already scheduled
			}//End modules loop
			
		}//End chromosomes loop	
		
	}
	//Finds all modules with special room and time requirements and allocates them first(H6)
	private void scheduleSpecialModules(){
		int [] specialModules = read.getModulesWithSpecialConstraints();
		int specialModuleCount = specialModules.length;
		
		if(specialModuleCount<=0)
			return;
		int [] startTimes = read.getStartTimeForSpecialConstraintModules();
		int [] endTimes = read.getEndTimeForSpecialConstraintModules();
		int [] days = read.getDaysForSpecialConstraintModules();
		int [] startTimeGenes = read.convertDayTimeToTimeGene(days,startTimes);
		int [] endTimeGenes = read.convertDayTimeToTimeGene(days,endTimes);

		int [] rooms = read.getRoomsWithSpecialModuleConstraints();
		for(int n=0;n <numChromosomes; n++){
			for(int a=0;a<specialModuleCount;a++){
				for(int j=startTimeGenes[a]-1;j<endTimeGenes[a]-1;j++){
					this.insertGene(n, this.getRoomIndex(rooms[a]), j, specialModules[a]);	
				}	
			}
		}
	}
	//Allocate modules belonging to partime lecturers
	private void schedulePartimeLecturerModules(){
		boolean isScheduled=false;
		String moduleType="";
		int modulehours=0;
		int [] partTimeLecturers = read.getPartimeLecturerIDs();
		int partimeLecturersCount = partTimeLecturers.length;
		if (partimeLecturersCount==0)
			return ;//No part time lecture exists for this semester
		
		for(int i=0;i<partimeLecturersCount;i++){
			int [] lecturerModules = read.getLecturerModules(partTimeLecturers[i]);
			if(lecturerModules.length==0){
				continue; //This lecturer has no modules allocated to him/her, so check next lecturer
			}
			boolean isMultiple=false;
			int [] startTimes = read.getStartTimeGenesForPartTimeLecturers(partTimeLecturers[i]);
			int [] endTimes = read.getEndTimeGenesForPartTimeLecturers(partTimeLecturers[i]);
			for(int n=0; n<numChromosomes;n++){
				for(int j=0;j<lecturerModules.length;j++){
					//Check if current module has been allocated in chromosome
					isScheduled = this.isScheduled(n, lecturerModules[j]);
					if(!isScheduled){
						
						moduleType = read.getModuleType(lecturerModules[j]);
						if(moduleType.equals("lab")){
							modulehours = read.getLabHoursPerWeek(lecturerModules[j]);
							for(int a=startTimes[0];a <endTimes[0];a++){
								this.findFreeLecturerLabRoomOnATimeslot(n, partTimeLecturers[i], a-1,lecturerModules[j]);
								if(this.freeRoom!=-1 && this.freeTimeslot!=-1)
									break;
								
							}
							//this.findFreeLabRoom(n);
							
							
						}
						else{
							modulehours = read.getLectureHoursPerWeek(lecturerModules[j],"lecture");
							for(int a=startTimes[0];a <endTimes[0];a++){
								this.findFreeLecturerLectureRoomOnATimeslot(n, partTimeLecturers[i], a-1,lecturerModules[j]);
								if(this.freeRoom!=-1 && this.freeTimeslot!=-1)
									break;
								
							}
							//k
							//this.findFreeLectureRoom(n);
						}
						if( modulehours==1 && freeRoom!=-1){
							//chromosomes[i][rm-1][time-1]=modules[d];
							boolean inserted = this.insertGene(n, freeRoom, this.freeTimeslot, lecturerModules[j]);
							this.freeRoom=-1;this.freeTimeslot=-1;
							
						}
						else if( modulehours==2){
							boolean inserted = this.insertGene(n, freeRoom, this.freeTimeslot, lecturerModules[j]);
							 inserted = this.insertGene(n, freeRoom, this.freeTimeslot+1, lecturerModules[j]);
							 freeRoom=-1;freeTimeslot=-1;							
							//this.handleSpecificTwoHourModulesScheduling(n, lecturerModules[j], this.freeTimeslot, moduleType);
						}

					}
						
				}
			}
			
		}

		
	}
	//Schedule other modules not scheduled by elitism
	private void scheduleRemainingModules(){
		
		boolean isLastHour=false, isOccupied=true,inserted=false;
		int modulehours=0,assigned=0;
		String moduleType="";
		int []cohortsAssignedTo=null;
		for(int i=0; i <numChromosomes;i++){
			for(int d=0; d < moduleCount; d++){
				//check if a module has not been scheduled first before trying to schedule it
				if(!this.isScheduled(i, this.modules[d])){
					cohortsAssignedTo = read.getModuleCohort(this.modules[d]);
					moduleType= this.getModuleType(d);
					
					int level = read.getModuleCohortLevel(modules[d], cohortsAssignedTo[0]);
					if(moduleType.equals("lab")){
						modulehours = read.getLabHoursPerWeek(modules[d]);
						//this.findFreeLabRoom(i);
						this.findFreeCohortLabRoom(i, cohortsAssignedTo[0], level,modules[d]);
						
					}
					else{
						modulehours = read.getLectureHoursPerWeek(modules[d],"lecture");
						//this.findFreeLectureRoom(i);
						this.findFreeCohortLectureRoom(i, cohortsAssignedTo[0], level,modules[d] );

					}
					
					if( modulehours==1 && freeRoom!=-1){
						//chromosomes[i][rm-1][time-1]=modules[d];
						System.out.println("Sing Schedule in Remaining Modules:" + this.modules[d] + " in Chromosome" + i);
						inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						freeRoom=-1;
						freeTimeslot=-1;
						
					}
					else if( modulehours==2){
						//this.handleTwoHourModulesScheduling(i, modules[d], freeTimeslot, moduleType);
						this.handleTwoHourUnclashingCohortModulesScheduling(i, modules[d], moduleType, cohortsAssignedTo[0], level);
					}
					
				}
				
			}
		}
		
		
	}
	//Check if a particular module has been scheduled in a chromosome
	private boolean isScheduled(int chromosome,int module){
		for(int i=0;i<roomCount;i++){
			for(int j=0;j<timeslot;j++){
				if(chromosomes[chromosome][i][j]==module){
					return true;
				}
			}
		}
		return false;
	}
	//Get the index of a room from the chromosome representation array
	int getRoomIndex(int roomid){
		for(int i=0;i<roomCount;i++){
			if(rooms[i]==roomid)
				return i;
		}
		return 0;
	}
	private String getModuleType(int moduleIndex){
		return moduleTypes[moduleIndex];
	}
	private String getRoomType(int roomIndex){
		return roomTypes[roomIndex];
	}
	public String getFitnessOnAContraint(int chromosome){
		fit.computeOverallFitnessForAChromosome(chromosome);
				
		endTime = System.currentTimeMillis();
		return fit.message;
		
	}
	public int getOverallFintnessValue(int chromosome){
		return fit.computeOverallFitnessForAChromosome(chromosome);
	}
	//Evaluate the fitness of entire population
	public int[] evaluatePopulationFitness(){
		return fit.computeFitnessOfEntirePopulation();
	}
	//Look for any free gene space. That is genes whose content is zero. No module allocated to it.
	private void findUnoccuppiedGene(int currentChromosome){
		freeChromosome=currentChromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			for(int a =0;a <this.timeslot ; a++){
				if(chromosomes[currentChromosome][c][a] ==0){
					freeRoom=c;
					freeTimeslot=a;
					return;
				}
			}
					
		}	
	}
	//Find free room of lecture type
	private void findFreeLectureRoom(int currentChromosome){
		freeChromosome=currentChromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lecture")){
				for(int a =0;a <40 ; a++){
					if(chromosomes[currentChromosome][c][a] ==0){
						freeRoom=c;
						freeTimeslot=a;
						return;
					}
				}
			}
					
		}	
	}
	
		
	private void findFreeLabRoom(int chromosome, int module){
		freeChromosome=chromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lab")){
				for(int a =0;a <this.timeslot ; a++){
					boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
					if(chromosomes[chromosome][c][a] ==0 && isCorrect){
						freeRoom=c;
						freeTimeslot=a;
						return;
					}
				}
			}
					
		}	
		
	}
	//Find consecutive timeslots in a laboratory room
	private void findConsecutiveFreeLabRoom(int chromosome){
		freeChromosome=chromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lab")){
				for(int a =0;a <timeslot-1 ; a++){
					if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0){
						freeRoom=c;
						freeTimeslot=a;
						this.consecutiveFreeGeneFound=true;
						return;
					}
				}
			}
					
		}	
		
	}

	//Set free lab room without clash for a cohort
	private void findFreeCohortLabRoom(int chromosome,int cohort,int level, int module){
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=roomCount-1; c >=0; c--){
			if(read.getRoomType(rooms[c]).equals("lab")){
				for(int a =0;a <timeslot-1 ; a++){
					if(chromosomes[chromosome][c][a] ==0){
						boolean isNotMultiple = this.isMultipleScheduleForACohort(chromosome, a, cohort, level);
						boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
						if(!isNotMultiple && isCorrect){
							freeRoom=c;
							freeTimeslot=a;
							return;
						}
					}
				}
			}
						
		}	
		
	}
	//Set free lab room without clash for a lecturer on a single time period 
	private void findFreeLecturerLabRoomOnATimeslot(int chromosome,int lecturer,int time,int module){
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=roomCount-1; c >=0; c--){
			if(read.getRoomType(rooms[c]).equals("lab")){
					if(chromosomes[chromosome][c][time] ==0){
						boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
						boolean isMultiple = this.isMultipleScheduleForALecturer(chromosome, time, lecturer);
						if(!isMultiple && isCorrect){
							freeRoom=c;
							freeTimeslot=time;
							return;
						}
					}
				
			}
						
		}	
		
	}
	//Set free lecture room without clash for a lecturer on a single time period 
		private void findFreeLecturerLectureRoomOnATimeslot(int chromosome,int lecturer,int time,int module){
			freeRoom=-1;
			freeTimeslot=-1;
			for(int c=roomCount-1; c >=0; c--){
				if(read.getRoomType(rooms[c]).equals("lecture")){
						if(chromosomes[chromosome][c][time] ==0){
							boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
							boolean isMultiple = this.isMultipleScheduleForALecturer(chromosome, time, lecturer);
							if(!isMultiple && isCorrect){
								freeRoom=c;
								freeTimeslot=time;
								return;
							}
						}
					
				}
							
			}	
			
		}
	//Set free lecture room without clash for a cohort
		private void findFreeCohortLectureRoom(int chromosome,int cohort,int level,int module){
			freeRoom=-1;
			freeTimeslot=-1;
			for(int c=roomCount-1; c >=0; c--){
				if(read.getRoomType(rooms[c]).equals("lecture")){
					for(int a =0;a <timeslot-1 ; a++){
						if(chromosomes[chromosome][c][a] ==0){
							boolean isNotMultiple = this.isMultipleScheduleForACohort(chromosome, a, cohort, level);
							boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
							if(!isNotMultiple && isCorrect){
								freeRoom=c;
								freeTimeslot=a;
								return;
							}
						}
					}
				}
							
			}	
			
		}
	//Set free lecture room without clash for a cohort
	private void findConsecutiveFreeCohortLectureRooms(int chromosome,int cohort,int level, int module){
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lecture")){
				for(int a =0;a <timeslot-1 ; a++){
					if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0){
						boolean isCorrectSize = this.roomSizeMatchedModuleSize(module, rooms[c]);
						boolean isOccupied1 = this.isMultipleScheduleForACohort(chromosome, a, cohort, level);
						boolean isOccupied2 = this.isMultipleScheduleForACohort(chromosome, a+1, cohort, level);
						if(!isOccupied1  && !isOccupied2 && isCorrectSize){
							freeRoom=c;
							freeTimeslot=a;
							this.consecutiveFreeGeneFound=true;
							return;
						}
					}
				}
			}
						
		}	
	
	}
	//Set free laboratory room without clash for a cohort
	private void findConsecutiveFreeCohortLabRooms(int chromosome,int cohort,int level, int module){
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lab")){
				for(int a =0;a <timeslot-1 ; a++){
					if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0){
						boolean isCorrectSize = this.roomSizeMatchedModuleSize(module, rooms[c]);
						boolean isOccupied1 = this.isMultipleScheduleForACohort(chromosome, a, cohort, level);
						boolean isOccupied2 = this.isMultipleScheduleForACohort(chromosome, a+1, cohort, level);
						if(!isOccupied1  && !isOccupied2 && isCorrectSize){
							freeRoom=c;
							freeTimeslot=a;
							this.consecutiveFreeGeneFound=true;
							return;
						}
					}
				}
			}
						
		}	
	
	}


	//Find consecutive timeslots in a laboratory room
	private void findConsecutiveFreeLectureRooms(int chromosome,int module){
		freeChromosome=chromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lab")){
				for(int a =0;a <timeslot-1 ; a++){
					boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
					if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0 && isCorrect){
						freeRoom=c;
						freeTimeslot=a;
						this.consecutiveFreeGeneFound=true;
						return;
					}
				}
			}
						
		}	
			
		}
	//Find consecutive timeslots in a lecture room from a start time
	private void findConsecutiveFreeLectureRoomsFromStartTime(int chromosome, int startTime){
		freeChromosome=chromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lecture")){
				for(int a =startTime;a <timeslot-1 ; a++){
					if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0 && a >=startTime){
						freeRoom=c;
						freeTimeslot=a;
						this.consecutiveFreeGeneFound=true;
						return;
					}
				}
			}
							
		}	
				
	}
	
	//Find consecutive timeslots in a lab room from a start time
		private void findConsecutiveFreeLabRoomsFromStartTime(int chromosome, int startTime){
			freeChromosome=chromosome;
			freeRoom=-1;
			freeTimeslot=-1;
			this.consecutiveFreeGeneFound=false;
			for(int c=0; c < roomCount; c++){
				if(read.getRoomType(rooms[c]).equals("lab")){
					for(int a =0;a <timeslot-1 ; a++){
						if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0 && a >=startTime){
							freeRoom=c;
							freeTimeslot=a;
							this.consecutiveFreeGeneFound=true;
							return;
						}
					}
				}
								
			}	
					
		}
	
	
	private void findConsecutiveUnoccuppiedGene(int currentChromosome){
		freeChromosome=currentChromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		consecutiveFreeGeneFound=false;
		int time = this.generateRandomInteger(timeslot);
		int rm =this.generateRandomInteger(roomCount);
		boolean isLast=true,isoccupied=true;
		isLast = this.isLastDayTimeSlot(time);
		isoccupied = this.isOccupied(currentChromosome, rm-1, time-1);
		if(!isLast && !isoccupied){
			isoccupied=true;
			isoccupied = this.isOccupied(currentChromosome, rm-1, time);
			if(!isoccupied){
				freeChromosome=currentChromosome;
				freeRoom=rm-1;
				freeTimeslot=time-1;
				consecutiveFreeGeneFound=true;
				return;
			}
		}
		
		for(int c=0; c < roomCount; c++){
			for(int a =0;a <40 ; a++){
				if(chromosomes[currentChromosome][c][a] ==0){
					isLast=this.isLastDayTimeSlot(a+1);
					if(!isLast){
						//If not last, check if next is occupied
						isoccupied = this.isOccupied(currentChromosome, c, a+1);
						if(!isoccupied){
							freeChromosome=currentChromosome;
							freeRoom=c;
							freeTimeslot=a;
							consecutiveFreeGeneFound=true;
						}
						
					}
										
				}
				else
					continue;

			}
					
		}	
	}
	
	//check for two consecutive free time
	//Inserts a module gene in a corresponding time and room locus
	private boolean insertGene(int chromosomeIndex, int roomIndex, int timeslot, int module){
		boolean result=false;
		if(chromosomes[chromosomeIndex][roomIndex][timeslot] ==0){
			chromosomes[chromosomeIndex][roomIndex][timeslot]=module;
			result= true;
		}
		
				return result;
	}
	
	//Check if last time slot of the day
	private boolean isLastDayTimeSlot(int timeslot){
		if(timeslot==40 ||timeslot==32||timeslot==24||timeslot==16||timeslot==8)
			return true;
		else 
			return false;
	}
	
	//Check if a room and time (Event) is occupied already
	private boolean isOccupied(int chromosome, int room, int time){
		boolean occupied = true;
		if(chromosomes[chromosome][room][time] ==0)
			occupied=false;
		
		return occupied;
	}
	
	//Checks the number of hours a lecture or lab has already been given in a week
	private int getNumberOfHoursAlreadyAllocated(int module, int chromosome){
		int numTimes=0;
		for(int i=0; i <numChromosomes;i++){
			for(int c=0; c < roomCount; c++){
				for(int a =0;a <40 ; a++){
					if(chromosomes[chromosome][c][a] ==module)
						numTimes +=1;
				}
					
				}
			}
			
		
		return numTimes;
	}
	private void initialAllChromosomesToZero(){
		for(int i=0; i <numChromosomes;i++){
			for(int c=0; c < roomCount; c++){
				for(int a =0;a <40 ; a++){
					
					chromosomes[i][c][a]=0;
					
				}
				
			}
		}
		
	}
		
	public void evaluateChildren(int [][][]children){
		fit.evaluateFitnessOfChildren(children);
		
	}
	public int[] getOveralFitnessOfChildren(){
		return fit.childrenfitnessValues;
	}
	public int[] getHardFitnessOfChildren(){
		return fit.childrenhardFitnesses;
	}
	public int[] getSoftFitnessOfChildren(){
		return fit.childrensoftFitness;
	}
	public String getCurrentFitnessMessage(){
		return fit.message;
	}
	public int generateRandomInteger(int maxNumber){
		
		// nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    //int randomNum = rand.nextInt((max - min) + 1) + min;

		int rand=0;
		 Random rn= new Random();
		 rand = rn.nextInt((maxNumber-1)  + 1) +1;
		return rand;
	}
	
	//Display the best individual candidate after the the termination of GA
	public String displayGeneratedTimetable(int chromosome){
		overallTimetable="";
		for(int a = 1;a<=5;a++)
			overallTimetable += read.getDailySchedule(a, chromosomes, chromosome, rooms, modules);
		
		return overallTimetable;
		
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
					isCohortEvent = read.confirmCohortEventAtALevelOfStudy(chromosomes[chromosome][i][timeslot], cohort,level);
					if(isCohortEvent)
						countSchedules +=1;
				}
			}
			if(countSchedules <=0)
				isMultiple = false;
			return isMultiple;
			
		}
		//Check if there already A SCHEDULE FOR A LECTURER IN A TIME SLOT
		private boolean isMultipleScheduleForALecturer(int chromosome, int time, int lecturer){
			boolean isMultiple = true,isLecturerEvent=false;
			int countSchedules=0;
			for(int i=0;i< roomCount;i++){
				if(chromosomes[chromosome][i][time]!=0){
					isLecturerEvent = read.confirmEventBelongsToLecturer(chromosomes[chromosome][i][time], lecturer);
					if(isLecturerEvent)
						countSchedules +=1;
				}
			}
			if(countSchedules <=0)
				isMultiple = false;
			return isMultiple;
			

		}
	//Check if a module is being scheduled in correct room size or not
	private boolean roomSizeMatchedModuleSize(int module,int room){
		int roomSize = read.getRoomCapacity(room);
		int moduleSize = read.getModuleSize(module);
		int tolerance = -10; // A tolerance of 10 is when module size is more than room Capacity by only 10 students
		boolean isMatched = false;
			
		if(moduleSize <= roomSize || (roomSize - moduleSize)  >= tolerance)
			isMatched=true;
		else
			isMatched=false;
			
			return isMatched;
	}
	//Check if a cohort has more than four hours of lecture per day
	private boolean isMoreThan4HoursOfConsecutiveLecturesPerCohort(int chromosome,int currentTime, int cohort,int level){
			boolean isCohortModule=false,isCohortModule2=false,found=false;
			int count=0;			
			for(int i=0;i<currentTime;i++){
				isCohortModule = this.cohortHasEventAtGivenTime(chromosome, i, cohort, level);
				isCohortModule2 = this.cohortHasEventAtGivenTime(chromosome, i+1, cohort, level);
				if(isCohortModule && isCohortModule2)
					count+=1;
				else
					count=0;
						
				if(count==4)
						return	found=true;
								
				//reset counter before checking the next day
				if(i==7||i==15||i==23||i==31)
					count=0;
						
			}
					
			return found;
	}
	//Check if a cohort has an event at a particular time in any of the rooms
	private boolean cohortHasEventAtGivenTime(int chromosome, int time,int cohort, int level){
		boolean hasLecture=false;
		for(int k=0;k<roomCount;k++){

			hasLecture=read.confirmCohortEventAtALevelOfStudy(chromosomes[chromosome][k][time], cohort, level);
			if(hasLecture)
				return true;
		}
			
		return hasLecture;
		}
		public int [] getSortedChromosomeIndices(){
			return fit.getSortedChromosomeIndices();
		}
	public int getMaxFitnessReward(){
		return Fitness.maxReward;
	}
	public int getMaxHardConstraintReward(){
		return  Fitness.maxHard;
	}
	public int getMaxSoftConstraintReward(){
		return Fitness.maxSoft;
	}
	//This method replaces a chromosome with a better chromosome after a Crossover and a mutation
	public void replaceChromosome(int replaceThis, int withThis,int [][][] fromThis){
		for(int i=0;i<this.roomCount;i++){
			for(int j=0;j<this.timeslot;j++){
				this.chromosomes[replaceThis][i][j]=fromThis[withThis][i][j];
			}
		}
		
	}
	//Return overall rewards on hard constraints
	public int getOverallHardConstraintRewards( int chromosome){
		return fit.getOverallRewardsOnHardConstraints(chromosome);
	}
	//Return overall rewards on hard constraints
	public int getOverallSoftConstraintRewards( int chromosome){
		return fit.getOverallRewardsOnSoftConstraints(chromosome);
	}
}
