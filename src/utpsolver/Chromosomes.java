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
	public int numChromosomes = 20, fitness=0;
	public  int[][][] chromosomes = null;
	public static int timeslot = 40,roomCount=0,moduleCount=0,lecturerCount=0;
	private String roomType="",overallTimetable="";
	private int [] rooms,modules;
	String [] moduleTypes, roomTypes;
	private int freeChromosome=0,freeRoom=0,freeTimeslot=0;
	private boolean consecutiveFreeGeneFound=false;
	ReadInputs read = new ReadInputs();
	private boolean exemptLunchTime=false,exemptWednesdayAfternoon=false;
	Fitness fit = null;
	public long startTime = System.currentTimeMillis(),endTime=0;
	
	public Chromosomes(){
		lecturerCount=read.getLecturerCount();
		rooms= read.getRoomIds();
		modules = read.getModuleIds();
		roomCount= rooms.length;
		moduleCount=modules.length;
		moduleTypes = read.getModuleTypeArray();
		roomTypes = read.getRoomTypeArray();
		chromosomes = new int[numChromosomes][roomCount][timeslot];
		this.initialAllChromosomesToZero();
		//this.initializePopulation();
		initializePopulationWithElitism();
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
					if(freeRoom==0 &&freeTimeslot==0){
						System.out.println("Not enough resources to host these lectures");
						System.exit(0);	
					}
					//chromosomes[i][freeRoom][freeTimeslot]=modules[d];
					inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
				}
				
				//Handle 2-hour Lecture or Lab
				if(modulehours==2){
					this.findConsecutiveUnoccuppiedGene(i);
					if(freeRoom !=0 && freeTimeslot !=0 && this.consecutiveFreeGeneFound){
						inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						inserted = this.insertGene(i, freeRoom, freeTimeslot+1, modules[d]);
						this.consecutiveFreeGeneFound=false;
					}
					else{
						//Split into one hour each and insert at different locations in the chromosome
						//Find unoccupied space for first period
						this.findUnoccuppiedGene(i);
						if(freeRoom==0 &&freeTimeslot==0){
							System.out.println("Not enough resources to host these lectures");
							System.exit(0);	
						}
						
						inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						
						//Find unoccupied space for second period
						this.findUnoccuppiedGene(i);
						if(freeRoom==0 &&freeTimeslot==0){
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
			this.findConsecutiveFreeLectureRooms(chromosome);
		this.insertGene(chromosome, freeRoom, freeTimeslot, module);
		this.insertGene(chromosome, freeRoom, freeTimeslot+1, module);
		this.consecutiveFreeGeneFound=false;
			
		}

	private void initializePopulationWithElitism(){
		int time=0,rm=0;
		boolean isLastHour=false, isOccupied=true,inserted=false;
		String moduleType="";
		int modulehours=0,assigned=0;
		//Schedule special modules first to satisfy special room and time constraints (H6)
		this.scheduleSpecialModules();
		//Schedule lectures belonging to partime lecturers
		this.schedulePartimeLecturerModules();
		//Do some random scheduling
		this.scheduleRandomly();
		
		//Schedule other remaining modules
		this.scheduleRemainingModules();
		
	}
	//Randomize some schedules to introduce some diversity
	private void scheduleRandomly(){
		int time=0,rm=0;
		boolean isLastHour=false, isOccupied=true,inserted=false;
		String moduleType="";
		int modulehours=0,assigned=0;
		
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
					//Handle 1-hour Lecture or Lab genes
					if(!isOccupied && modulehours==1 && moduleType.equals(this.getRoomType(rm-1))){
						
						inserted = this.insertGene(i, rm-1, time-1, modules[d]);
						
					}
					else if(isOccupied && modulehours==1){
						//Find unoccupied space
						this.findUnoccuppiedGene(i);
						if(freeRoom==0 &&freeTimeslot==0){
							System.out.println("Not enough resources to host these lectures");
							System.exit(0);	
						}
						
					}
					
					//Handle 2-hour Lecture or Lab
					if(modulehours==2){
						this.handleRandomTwoHourModulesScheduling(i, modules[d], time-1, moduleType);
						
						
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
							this.findFreeLabRoom(n);
						}
						else{
							modulehours = read.getLectureHoursPerWeek(lecturerModules[j],"lecture");
							this.findFreeLectureRoom(n);
						}
						if( modulehours==1 && freeRoom!=0){
							//chromosomes[i][rm-1][time-1]=modules[d];
							boolean inserted = this.insertGene(n, freeRoom, startTimes[0]-1, lecturerModules[j]);
							
						}
						else if( modulehours==2){
							this.handleSpecificTwoHourModulesScheduling(n, lecturerModules[j], startTimes[0]-1, moduleType);
						}

					}
						
				}
			}
			
		}

		
	}
	//Schedule other modules not scheduled by elitism
	private void scheduleRemainingModules(){
		int time=0,rm=0;
		boolean isLastHour=false, isOccupied=true,inserted=false;
		int modulehours=0,assigned=0;
		String moduleType="";
		for(int i=0; i <numChromosomes;i++){
			for(int d=0; d < moduleCount; d++){
				//check if a module has not been scheduled first before trying to schedule it
				if(!this.isScheduled(i, this.modules[d])){
					moduleType= this.getModuleType(d);
					if(moduleType.equals("lab")){
						modulehours = read.getLabHoursPerWeek(modules[d]);
						this.findFreeLabRoom(i);
					}
					else{
						modulehours = read.getLectureHoursPerWeek(modules[d],"lecture");
						this.findFreeLectureRoom(i);
					}
					if( modulehours==1 && freeRoom!=0){
						//chromosomes[i][rm-1][time-1]=modules[d];
						inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						
					}
					else if( modulehours==2){
						this.handleTwoHourModulesScheduling(i, modules[d], freeTimeslot, moduleType);
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
		String message ="H1: No multiple event at same venue and Time: Never violated due to chromosome representation method";
		message  += "<br/>H2:Non-Multiple Scheduling for Lecturer: " + fit.computeMultipleScheduleForALecturerAtSameTime(chromosome) + " out of " +fit.getMaxH2Reward();
		message += "<br/>H3:Non-Multiple Scheduling for Cohort:" + fit.computeMultipleScheduleForACohort(chromosome)+ " out of " +fit.getMaxH3Reward();
		message+= "<br/> H4:Part-time Lecturer availability observed:" + fit.computePartimeLecturerAvailablityScheduling(chromosome) + " out of " + fit.getMaxH4Reward();
		message += "<br/> H5:All modules Scheduled:" + fit.computeToVerifyAllModulesWereScheduled(chromosome)+ " out of " + fit.getMaxH5Reward();
		message += "<br/> H6:Special Module correctly allocated to preffered room and time:" + fit.computeSpecialModuleConstraintViolation(chromosome)+ " out of " +fit.getMaxH6Reward();
		message += "<br/> H7:Classes held in correct room size:" + fit.computeClassHeldInCorrectRoomSizeFitness(chromosome)+ " out of " +fit.getMaxH7Reward();
		message += "<br/>H8: Classes held in correct room type: " + fit.computeClassHeldInCorrectRoomTypeFitness(chromosome)+ " out of " +fit.getMaxH7Reward();
		message += "<br/>S9: Not more than 4-hr consecutive Events for Lecturer:" + fit.computeMoreThan4HoursOfConsecutiveLecturesPerLecturer(chromosome)+ " out of "+fit.getMaxS9Reward();
		message += "<br/> S10:Not more than 4-hr consecutive Events for a Cohort: " + fit.computeMoreThan4HoursOfConsecutiveLecturesPerCohort(chromosome)+ " out of " + fit.getMaxS10Reward();
		message += "<br/> S11: No lecture/Lab fixed on Wednesday afternoon: " + fit.computeWednesdayAfternoonEventConstraint(chromosome)+ " out of 5";
		message += "<br/> S12: No lecture/Lab during Launch time: " + fit.computeAvoidLaunchTimeEvents(chromosome) + " out of 5";
		endTime = System.currentTimeMillis();
		message += "<br/><span class=\"success\">Overall fitness for Chromosome " + (chromosome +1) + " is: " + fit.computeOverallFitnessForAChromosome(chromosome) + " out of " + fit.maxPossibleFitnessValue() + "</span>";
		return message;
		
	}
	//Evaluate the fitness of entire population
	public int[] evaluatePopulationFitness(){
		return fit.computeFitnessOfEntirePopulation();
	}
	//Look for any free gene space. That is genes whose content is zero. No module allocated to it.
	private void findUnoccuppiedGene(int currentChromosome){
		freeChromosome=currentChromosome;
		freeRoom=0;
		freeTimeslot=0;
		for(int c=0; c < roomCount; c++){
			for(int a =0;a <40 ; a++){
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
		freeRoom=0;
		freeTimeslot=0;
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
	private void findFreeLabRoom(int chromosome){
		freeChromosome=chromosome;
		freeRoom=0;
		freeTimeslot=0;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lab")){
				for(int a =0;a <40 ; a++){
					if(chromosomes[chromosome][c][a] ==0){
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
		freeRoom=0;
		freeTimeslot=0;
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
	
	//Find consecutive timeslots in a laboratory room
	private void findConsecutiveFreeLectureRooms(int chromosome){
		freeChromosome=chromosome;
		freeRoom=0;
		freeTimeslot=0;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lecture")){
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
	//Find consecutive timeslots in a lecture room from a start time
	private void findConsecutiveFreeLectureRoomsFromStartTime(int chromosome, int startTime){
		freeChromosome=chromosome;
		freeRoom=0;
		freeTimeslot=0;
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
			freeRoom=0;
			freeTimeslot=0;
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
		freeRoom=0;
		freeTimeslot=0;
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
		for(int a = 1;a<=5;a++)
			overallTimetable += read.getDailySchedule(a, chromosomes, chromosome, rooms, modules);
		
		return overallTimetable;
		
	}
}
