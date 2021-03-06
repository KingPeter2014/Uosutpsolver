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
	public static int hardWeight =100,softWeight=20;
	public  int[][][] chromosomes = null,bestChromosome,worstChromosome;
	public static int timeslot = 40,roomCount=0,moduleCount=0,lecturerCount=0;
	private String roomType="",overallTimetable="";
	public int [] rooms,modules,cohorts;
	String [] moduleTypes, roomTypes;
	public int status=1;
	private int freeChromosome=0,freeRoom=-1,freeTimeslot=-1;
	private boolean consecutiveFreeGeneFound=false;
	ReadInputs read = new ReadInputs();
	private boolean exemptLunchTime=false,exemptWednesdayAfternoon=false;
	Fitness fit = null;
	public long startTime = 0,endTime=0,initEnd=0;
	private int algorithmVersion=0;
	private Random rn = new Random(System.currentTimeMillis());
	public Chromosomes(){
		startTime=System.currentTimeMillis();
		lecturerCount=read.getLecturerCount();
		rooms= read.getRoomIds();
		modules = read.getModuleIds();
		if(modules.length==0){
			System.out.println("No module to schedule");
			status=0;
			return;
		}
		cohorts = read.getCohortIds();
		roomCount= rooms.length;
		moduleCount=modules.length;
		moduleTypes = read.getModuleTypeArray();
		roomTypes = read.getRoomTypeArray();
		chromosomes = new int[numChromosomes][roomCount][timeslot];
		this.bestChromosome = new int[1][roomCount][timeslot];
		this.worstChromosome = new int[1][roomCount][timeslot];
		this.initialAllChromosomesToZero();
		//this.nonConstructivePopulationInitialisation();
		this.constructivePopulationInitialisation();
		initEnd = System.currentTimeMillis();
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
		this.constructivePopulationInitialisation();
		
		
	}
	/**
	 * Utilise the room, lecturer and other constraints defined to ensure that the initial population
	 * has satisfied most or all of the hard constraints
	 * Use randomization to allocate remaining events that has no special requirements
	 */
	private void nonConstructivePopulationInitialisation(){
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
				if(!isOccupied && (modulehours==1 ||modulehours==3)){
					//chromosomes[i][rm-1][time-1]=modules[d];
					inserted = this.insertGene(i, rm-1, time-1, modules[d]);
					
				}
				else if(isOccupied && (modulehours==1 ||modulehours==3)){
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
				if(modulehours==2||modulehours==3||modulehours==4){
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
				//Handle 4-hour Lecture or Lab per week by scheduling the remaining 2 hours
				if(modulehours==4){
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
				
				
				
			}//End modules loop
			
		}//End chromosomes loop	
	}
	//Schedule two hour lecture or lab for part time lecturers
	private void scheduleLevelOneDistributively(){
		boolean isLastHour=false, isOccupied=true,inserted=false;
		String moduleType="";
		int levelOffered=0,count=4;
		int modulehours=0,assigned=0;
		int [] cohortsAssignedTo = null;
		for(int i=0; i <numChromosomes;i++){
			for(int d=0; d < moduleCount; d++){
				levelOffered= read.getFirstLevelOffered(modules[d]);
				if( levelOffered==1 && !this.isScheduled(i, this.modules[d])){
					cohortsAssignedTo = read.getModuleCohort(this.modules[d]);
					
					moduleType = read.getModuleType(modules[d]);
					if(moduleType.equals("lab"))
						modulehours = read.getLabHoursPerWeek(modules[d]);
					else
						modulehours = read.getLectureHoursPerWeek(modules[d],"lecture");
					if(cohortsAssignedTo.length==0)
						continue ;
					
					if(modulehours==2){
						int day=0;
						switch(count){
						case 1: day=0;break;//for monday
						case 2: day=8;break;//for Tuesday
						case 3: day=16;break;//for Wednesday
						case 4: day=24;break;// for thursday
						case 5: day=32;break;// for Friday
						default: day=5;
						}
						
						if(moduleType.equals("lab"))
							this.findConsecutiveFreeLabRoomsFromStartTime(i, day,modules[d]);
						else
							this.findConsecutiveFreeLectureRoomsFromStartTime(i, day,modules[d]);
						if(this.freeTimeslot==-1){
							System.out.println(" No free Distributive room for module on  day" + day);
							//continue;
						}
						boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(i, this.freeTimeslot, cohortsAssignedTo,modules[d]);
						boolean isMoreThan4CohortHours2 = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(i, this.freeTimeslot+1, cohortsAssignedTo,modules[d]);
						boolean isMultiple = false;//this.isMultipleScheduleForACohort(i, this.freeTimeslot, cohortsAssignedTo[0], level);
						//isMultiple = this.isClashingForAnycohortInModule(cohortsAssignedTo, i, this.freeTimeslot, modules[d]);
						//boolean isMultiple2 = this.isClashingForAnycohortInModule(cohortsAssignedTo, i, this.freeTimeslot+1, modules[d]);
						if(!isMoreThan4CohortHours && !isMoreThan4CohortHours2){
							this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
							this.insertGene(i, freeRoom, freeTimeslot+1, modules[d]);
							//System.out.println(" Scheduled Distinctively module " + modules[d] + " in chromosome " + i);
							this.consecutiveFreeGeneFound=false;
						}
					}
				}
				count=count+1;
				if(count>5) count=2;
			}
			
		}
		
		
	}
	
	//Schedule two hour lecture or lab for general purposes
	private void handleTwoHourUnclashingCohortModulesScheduling(int chromosome,int module,String moduleType, int[] cohort){
		
				
		if(moduleType.equals("lab"))
			this.findConsecutiveFreeCohortLabRooms(chromosome,cohort,module);
		else
			this.findConsecutiveFreeCohortLectureRooms(chromosome,cohort,module);
		
		this.insertGene(chromosome, freeRoom, freeTimeslot, module);
		this.insertGene(chromosome, freeRoom, freeTimeslot+1, module);
		this.consecutiveFreeGeneFound=false;

			}

	private void constructivePopulationInitialisation(){
		
		//Schedule special modules first to satisfy special room and time constraints (H6)
		this.scheduleSpecialModules();
		//Schedule lectures belonging to partime lecturers(H4)
		this.schedulePartimeLecturerModules();
		
		//Schedule Level One distributively across the week
		this.scheduleLevelOneDistributively();
				
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
						this.findFreeLectureRoom(i,modules[d]);
					}
					if(cohortsAssignedTo.length==0)
						continue ;
					boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(i, this.freeTimeslot, cohortsAssignedTo,modules[d]);
					boolean isMultiple = false;//this.isMultipleScheduleForACohort(i, this.freeTimeslot, cohortsAssignedTo[0], level);
					isMultiple = this.isClashingForAnycohortInModule(cohortsAssignedTo, i, this.freeTimeslot, modules[d]);
					
					if( (modulehours==1 || modulehours==3) && freeRoom!=-1 && !isMultiple && !isMoreThan4CohortHours){
							inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						
					}
					if( modulehours==2|| modulehours==3|| modulehours==4){
						//System.out.println(" Calling two hour cohort schedule");
						
						this.handleTwoHourUnclashingCohortModulesScheduling(i, modules[d], moduleType,cohortsAssignedTo);
					}
					if(modulehours==4){
						this.handleTwoHourUnclashingCohortModulesScheduling(i, modules[d], moduleType,cohortsAssignedTo);
						
					}
					
				}
				
			}
			
		}
	}
	//Randomize some schedules to introduce some diversity
	private void scheduleRandomly(){
		int time=0,rm=0,cohortsInModule=0;
		boolean isLastHour=false, isOccupied=true,inserted=false,isMultiple=false,isMultipleForLecturer=true;
		String moduleType="";
		int modulehours=0,assigned=0;
		
		int [] cohortsAssignedTo = null,lecturersAllocatedTo;
		for(int i=0; i <numChromosomes;i++){
			for(int d=0; d < moduleCount; d++){
				//select a module at random
				int rd= this.generateRandomInteger(moduleCount)-1;
				
				if(this.isScheduled(i, this.modules[rd]))
					continue;//Continue with next module if current one is already scheduled
				//Generate random time and room
				time = this.generateRandomInteger(timeslot);
				rm =this.generateRandomInteger(roomCount);
				//Get module type
				moduleType = read.getModuleType(modules[rd]);
				if(!moduleType.equals(this.getRoomType(rm-1))){
					//System.out.println(" Module type does not match room type. Go next..");
					continue;
				}
				
				//If module type matches, then get lecture or lab duration
				if(moduleType.equals("lab"))
					modulehours = read.getLabHoursPerWeek(modules[rd]);
				else
					modulehours = read.getLectureHoursPerWeek(modules[rd],"lecture");
				isLastHour = this.isLastDayTimeSlot(time);
				if(isLastHour && modulehours >1){
					continue;
				}
				
				//Check if generated time and room is already occupied
				isOccupied = this.isOccupied(i, rm-1, time-1);
				if(isOccupied){
					//System.out.println("The random time and space is already occupied. Go next..");
					continue;
				}
				
				//Get cohorts that offer current module
				cohortsAssignedTo = read.getModuleCohort(this.modules[rd]);
				//Get lecturers that teach current module
				lecturersAllocatedTo=read.getModuleLecturersList(modules[rd]);
				if(cohortsAssignedTo.length==0)
					continue;
				//Check if any of the lecturers have lecturer already at this period
				isMultipleForLecturer = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, i, time-1);
				if(isMultipleForLecturer){
					//System.out.println("One of the Lecturers already have fixed lecture at this moment. Go next..");
					continue;
				}
				isMultiple= this.isClashingForAnycohortInModule(cohortsAssignedTo, i, time-1, modules[rd]);
				
				//Check if correct room size was chosen
				boolean isCorrectRoomSize = this.roomSizeMatchedModuleSize(modules[rd], rm-1);
				
				//Check if more than four consecutive hours of lecture has been fixed for a cohort
				boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(i, time-1, cohortsAssignedTo,modules[rd]);
				if(isMoreThan4CohortHours){
					//System.out.println(" More than four cohort hours detected in Schedule randomly");
				}
				
				//Handle 1-hour Lecture or Lab genes
				if((modulehours==1 ||modulehours==3) && isCorrectRoomSize && !isMultiple && !isMultipleForLecturer && !isMoreThan4CohortHours){
					//System.out.println("Single Randomly Scheduled:" + modules[d] + " in Chromosome " + i);
					inserted = this.insertGene(i, rm-1, time-1, modules[rd]);
				}
				//Handle 2-hour Lecture or Lab
				if(modulehours==2||modulehours==3||modulehours==4){
					if(time==Chromosomes.timeslot)
						continue;//As two-hour module cannot start 4pm on Friday, which is timeslot 40
					//Check if any of the lecturers have lecturer already at second period
					boolean isMultipleForLecturer2 = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, i, time);
					boolean isMoreThan4Hours2 = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(i, time, cohortsAssignedTo, modules[d]);
					boolean isMultiple2= this.isClashingForAnycohortInModule(cohortsAssignedTo, i, time, modules[rd]);
					
					if(isCorrectRoomSize && !isMultiple && !isMultipleForLecturer&& !isMultiple2 && !isMultipleForLecturer2 && !isMoreThan4CohortHours && !isMoreThan4Hours2){
					inserted = this.insertGene(i, rm-1, time-1, modules[rd]);
					inserted = this.insertGene(i, rm-1, time, modules[rd]);
					//System.out.println(" Double Randomly Scheduled");
					}
				}
				
				
				//Handle 4-hour Lecture or Lab per week 
				if(modulehours==4){
					if(time==Chromosomes.timeslot)
						continue;//As two-hour module cannot start 4pm on Friday, which is timeslot 40
					//Check if any of the lecturers have lecturer already at second period
					boolean isMultipleForLecturer2 = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, i, time);
					
					boolean isMultiple2= this.isClashingForAnycohortInModule(cohortsAssignedTo, i, time, modules[rd]);
					
					if(isCorrectRoomSize && !isMultiple && !isMultipleForLecturer&& !isMultiple2 && !isMultipleForLecturer2 && !isMoreThan4CohortHours){
					inserted = this.insertGene(i, rm-1, time-1, modules[rd]);
					inserted = this.insertGene(i, rm-1, time, modules[rd]);
					//System.out.println(" Double Randomly Scheduled");
					}
					
				}

				
				
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
			int numstarts = startTimes.length;
			if(numstarts==0)
				continue;
			for(int n=0; n<numChromosomes;n++){
				for(int j=0;j<lecturerModules.length;j++){
					//Check if current module has been allocated in chromosome
					isScheduled = this.isScheduled(n, lecturerModules[j]);
					if(!isScheduled){
						
						moduleType = read.getModuleType(lecturerModules[j]);
						if(moduleType.equals("lab")){
							modulehours = read.getLabHoursPerWeek(lecturerModules[j]);
							for(int b=0;b<numstarts;b++){
								for(int a=startTimes[b];a <endTimes[b];a+=2){
									
									this.findFreeLecturerLabRoomOnATimeslot(n, partTimeLecturers[i], a-1,lecturerModules[j]);
									if(this.freeRoom!=-1 && this.freeTimeslot!=-1)
										break;
									
								}
							}
							//this.findFreeLabRoom(n);
							
							
						}
						else{
							modulehours = read.getLectureHoursPerWeek(lecturerModules[j],"lecture");
							for(int b=0;b<numstarts;b++){
								for(int a=startTimes[b];a <endTimes[b];a+=3){
									this.findFreeLecturerLectureRoomOnATimeslot(n, partTimeLecturers[i], a-1,lecturerModules[j]);
									if(this.freeRoom!=-1 && this.freeTimeslot!=-1)
										break;
									
								}
							}
							//this.findFreeLectureRoom(n);
						}
						if(( modulehours==1||modulehours==3) && freeRoom!=-1 &&this.freeTimeslot!=-1){
							//chromosomes[i][rm-1][time-1]=modules[d];
							boolean inserted = this.insertGene(n, freeRoom, this.freeTimeslot, lecturerModules[j]);
							this.freeRoom=-1;this.freeTimeslot=-1;
							
						}
						if( modulehours==2||modulehours==3||modulehours==4){
							boolean inserted = this.insertGene(n, freeRoom, this.freeTimeslot, lecturerModules[j]);
							 inserted = this.insertGene(n, freeRoom, this.freeTimeslot+1, lecturerModules[j]);
							 freeRoom=-1;freeTimeslot=-1;							
							//this.handleSpecificTwoHourModulesScheduling(n, lecturerModules[j], this.freeTimeslot, moduleType);
						}
						if(modulehours==4){
							if(moduleType.equals("lab")){
								modulehours = read.getLabHoursPerWeek(lecturerModules[j]);
								for(int b=0;b<numstarts;b++){
									for(int a=startTimes[b];a <endTimes[b];a+=2){
										
										this.findFreeLecturerLabRoomOnATimeslot(n, partTimeLecturers[i], a-1,lecturerModules[j]);
										if(this.freeRoom!=-1 && this.freeTimeslot!=-1)
											break;
										
									}
								}
															}
							else{
								modulehours = read.getLectureHoursPerWeek(lecturerModules[j],"lecture");
								for(int b=0;b<numstarts;b++){
									for(int a=startTimes[b];a <endTimes[b];a+=3){
										this.findFreeLecturerLectureRoomOnATimeslot(n, partTimeLecturers[i], a-1,lecturerModules[j]);
										if(this.freeRoom!=-1 && this.freeTimeslot!=-1)
											break;
										
									}
								}
							}
							boolean inserted = this.insertGene(n, freeRoom, this.freeTimeslot, lecturerModules[j]);
							 inserted = this.insertGene(n, freeRoom, this.freeTimeslot+1, lecturerModules[j]);
							 freeRoom=-1;freeTimeslot=-1;							
							
						}//End of module hours = 4

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
					if(cohortsAssignedTo.length<=0)
						continue;//This module has not been assigned to a cohort
					//int level = read.getModuleCohortLevel(modules[d], cohortsAssignedTo[0]);
					if(moduleType.equals("lab")){
						modulehours = read.getLabHoursPerWeek(modules[d]);
						//this.findFreeLabRoom(i);
						this.findFreeCohortLabRoom(i, cohortsAssignedTo,modules[d]);
						
					}
					else{
						modulehours = read.getLectureHoursPerWeek(modules[d],"lecture");
						//this.findFreeLectureRoom(i);
						//System.out.println(" LENGTH:"+ cohortsAssignedTo.length);
						this.findFreeCohortLectureRoom(i, cohortsAssignedTo,modules[d] );

					}
					boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(i, freeTimeslot, cohortsAssignedTo,modules[d]);
					if(isMoreThan4CohortHours){
						//System.out.println(" More than four cohort hours detected in Schedule Remaining modules");
					}
					boolean isClashing = this.isClashingForAnycohortInModule(cohortsAssignedTo, i, freeTimeslot, modules[d]);
					if((modulehours==1 ||modulehours==3) && freeRoom!=-1 && !isClashing && !isMoreThan4CohortHours){
						//chromosomes[i][rm-1][time-1]=modules[d];
						//System.out.println("Single Schedule in Remaining Modules:" + this.modules[d] + " in Chromosome" + i);
						inserted = this.insertGene(i, freeRoom, freeTimeslot, modules[d]);
						freeRoom=-1;
						freeTimeslot=-1;
						
					}
					else if( modulehours==2||modulehours==3||modulehours==4){
						
						//this.handleTwoHourModulesScheduling(i, modules[d], freeTimeslot, moduleType);
						this.handleTwoHourUnclashingCohortModulesScheduling(i, modules[d], moduleType, cohortsAssignedTo);
					}
					
				}
				
			}
		}
		
		
	}
	
	//Check if module clashes for any lecturer in a module about to be scheduled
	private boolean isClashingForAnyLecturerInModule(int[] lecturers,int chromosome, int time){
		int count=0;
		boolean isMultipleForLecturer=true;
		//Check clashing for lecturer
		for(int ic=0;ic<lecturers.length;ic++){//Check clashing per cohort In module
			isMultipleForLecturer = this.isMultipleScheduleForALecturer(chromosome, time, lecturers[ic]);
			if(isMultipleForLecturer){
				count+=1;
			}
			
		}
		if(count <= 0)
			isMultipleForLecturer=false;
		else 
			isMultipleForLecturer=true;
		return isMultipleForLecturer;
		
	}
	//Check if module clashes for any cohort that offers a module about to be scheduled
	private boolean isClashingForAnycohortInModule(int[] cohorts,int chromosome, int time, int module){
		int count=0;
		boolean isMultiple=true;
		int cohortsInModule=cohorts.length;
		//System.out.println(" There are " + cohortsInModule + " cohorts offering module " + module );
		
		for(int i=0;i<cohortsInModule; i++){//Check clashing per cohort In module
			int level = read.getModuleCohortLevel(module, cohorts[i]);
			isMultiple = this.isMultipleScheduleForACohort(chromosome, time, cohorts[i], level);
			if(isMultiple){
				count+=1;
				//System.out.println("Clashing for cohort found while scheduling " + module );
				return true;
			}
				
		}
		if(count > 0)
			isMultiple=true;
		else
			isMultiple=false;
		
		return isMultiple;
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
		if(fit!=null)
			fit.computeOverallFitnessForAChromosome(chromosome);
		else return "No resources to run GA";
				
		endTime = System.currentTimeMillis();
		return fit.message;
		
	}
	public int getOverallFintnessValue(int chromosome){
		return fit.computeOverallFitnessForAChromosome(chromosome);
	}
	//Evaluate the fitness of entire population
	public int[] evaluatePopulationFitness(){
		int [] zero = {0};
		if(fit!=null)
			return fit.computeFitnessOfEntirePopulation();
		else
			return zero;
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
	private void findFreeLectureRoom(int currentChromosome, int module){
		freeChromosome=currentChromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lecture")){
				for(int a =0;a <this.timeslot ; a++){
					boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
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
	
	//Set free lab room without clash for a cohort
	private void findFreeCohortLabRoom(int chromosome,int[] cohorts, int module){
		freeRoom=-1;
		freeTimeslot=-1;
		int level=0;
		boolean isMultiple=true;
		int cohortsInModule= cohorts.length;
		for(int c=roomCount-1; c >=0; c--){
			if(this.getRoomType(c).equals("lab")){
				for(int a =0;a <timeslot-1 ; a++){
					if(chromosomes[chromosome][c][a] ==0){
						for(int i=0;i<cohortsInModule;i++){//Check clashing per cohort In module
							level = read.getModuleCohortLevel(module, cohorts[i]);
							isMultiple = this.isMultipleScheduleForACohort(i, a, cohorts[i], level);
							if(isMultiple)
								break;
						}
						boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
						if(!isMultiple && isCorrect){
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
						int cohort[] = read.getModuleCohort(module);
						boolean isClashing = this.isClashingForAnycohortInModule(cohort, chromosome, time, module);
						boolean isClashing2 = this.isClashingForAnycohortInModule(cohort, chromosome, time, module);
						boolean isMultiple = this.isMultipleScheduleForALecturer(chromosome, time, lecturer);
						boolean isOccupied = this.isOccupied(chromosome, this.getRoomIndex(rooms[c]), time);
						if(!isMultiple && isCorrect && !isOccupied && !isClashing && !isClashing2){
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
							int cohort[] = read.getModuleCohort(module);
							boolean isClashing = this.isClashingForAnycohortInModule(cohort, chromosome, time, module);
							boolean isClashing2 = this.isClashingForAnycohortInModule(cohort, chromosome, time+1, module);
							boolean isMultiple = this.isMultipleScheduleForALecturer(chromosome, time, lecturer);
							boolean isOccupied = this.isOccupied(chromosome, this.getRoomIndex(rooms[c]), time);
							if(!isMultiple && isCorrect && !isOccupied && !isClashing && !isClashing2){
								freeRoom=c;
								freeTimeslot=time;
								return;
							}
						}
					
				}
							
			}	
			
		}
	//Set free lecture room without clash for a cohort
		private void findFreeCohortLectureRoom(int chromosome,int[] cohorts,int module){
			freeRoom=-1;
			freeTimeslot=-1;
			int level=0;
			boolean isMultiple =true,isMultipleForLecturer=false;
			int cohortsInModule=cohorts.length;
			if(cohortsInModule <1)
				return;
			
			for(int c=roomCount-1; c >=0; c--){
				if(read.getRoomType(rooms[c]).equals("lecture")){
					for(int a =0;a <timeslot-1 ; a++){
						if(chromosomes[chromosome][c][a] ==0){
							for(int ic=0;ic<cohortsInModule;ic++){//Check clashing per cohort In module
								
								level = read.getModuleCohortLevel(module, cohorts[ic]);
								isMultiple = this.isMultipleScheduleForACohort(chromosome, a, cohorts[ic], level);
								if(isMultiple)
									break;
							}
							boolean isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
							if(!isMultiple && isCorrect){
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
	private void findConsecutiveFreeCohortLectureRooms(int chromosome,int[] cohort, int module){
		freeRoom=-1;
		freeTimeslot=-1;
		boolean isMultiple1=true,isMultiple2=true,lectMultiple,lectMultiple2;
		int level=0,cohortsInModule = cohorts.length;
		int []lecturersAllocatedTo;
		if(this.algorithmVersion==0){
			for(int c=0; c < roomCount; c++){
				if(read.getRoomType(rooms[c]).equals("lecture")){
					for(int a =0;a <timeslot-1 ; a++){
						if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0){
							boolean isCorrectSize = this.roomSizeMatchedModuleSize(module, rooms[c]);
							isMultiple1 = this.isClashingForAnycohortInModule(cohort, chromosome, a, module);
							isMultiple2= this.isClashingForAnycohortInModule(cohort, chromosome, a+1, module);
						
							boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(chromosome, a, cohort,module);
							if(isMoreThan4CohortHours){
								//System.out.println(" More than four cohort hours detected in findConsecFreeCohortLecture");
							}
							lecturersAllocatedTo=read.getModuleLecturersList(module);
							lectMultiple = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, chromosome, a);
							lectMultiple2 = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, chromosome, a+1);
							
							if(!isMultiple1  && !isMultiple2 && !lectMultiple && !lectMultiple2 && isCorrectSize){
								freeRoom=c;
								freeTimeslot=a;
								this.consecutiveFreeGeneFound=true;
								//System.out.println(" Version 1:Consecutive cohort lecture not more than four hours");
								return;
								
							}
						}
					}
				}
							
			}
			this.algorithmVersion=1;
		}else{
			//Version 2
			for(int c=0; c < roomCount; c++){
				if(read.getRoomType(rooms[c]).equals("lecture")){
					for(int a =timeslot-1;a >15 ; a--){
						if(chromosomes[chromosome][c][a-1] ==0 && chromosomes[chromosome][c][a] ==0){
							boolean isCorrectSize = this.roomSizeMatchedModuleSize(module, rooms[c]);
							isMultiple1 = this.isClashingForAnycohortInModule(cohort, chromosome, a-1, module);
							isMultiple2= this.isClashingForAnycohortInModule(cohort, chromosome, a, module);
						
							boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(chromosome, a-1, cohort,module);
							if(isMoreThan4CohortHours){
								//System.out.println(" More than four cohort hours detected in findConsecFreeCohortLecture");
							}
							lecturersAllocatedTo=read.getModuleLecturersList(module);
							lectMultiple = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, chromosome, a-1);
							lectMultiple2 = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, chromosome, a);
							
							if(!isMultiple1  && !isMultiple2 && !lectMultiple && !lectMultiple2 && isCorrectSize){
								freeRoom=c;
								freeTimeslot=a-1;
								//System.out.println(" Version 2:Consecutive cohort lecture not more than four hours");
								this.consecutiveFreeGeneFound=true;
								return;
							}
						}
					}
				}
							
			}
			this.algorithmVersion=0;
		}//End Algorithm version 2
	
	}
	//Set free laboratory room without clash for a cohort
	private void findConsecutiveFreeCohortLabRooms(int chromosome,int[] cohorts, int module){
		freeRoom=-1;
		freeTimeslot=-1;
		boolean isMultiple1=true,isMultiple2=true,lectMultiple,lectMultiple2;
		int [] lecturersAllocatedTo=null;
		int level=0,cohortsInModule = cohorts.length;
		if(this.algorithmVersion==0){
			for(int c=0; c < roomCount; c++){
				if(read.getRoomType(rooms[c]).equals("lab")){
					for(int a=this.timeslot-1 ;a >0 ; a--){
						if(chromosomes[chromosome][c][a-1] ==0 && chromosomes[chromosome][c][a] ==0){
							boolean isCorrectSize = this.roomSizeMatchedModuleSize(module, rooms[c]);
							isMultiple1 = this.isClashingForAnycohortInModule(cohorts, chromosome, a-1, module);
							isMultiple2= this.isClashingForAnycohortInModule(cohorts, chromosome, a, module);
						/**	for(int i=0;i<cohortsInModule;i++){//Check clashing per cohort In module
								level = read.getModuleCohortLevel(module, cohorts[i]);
								isMultiple1 = this.isMultipleScheduleForACohort(i, a, cohorts[i], level);
								isMultiple2 = this.isMultipleScheduleForACohort(i, a+1, cohorts[i], level);
								if(isMultiple1 || isMultiple2 )
									break;
							}**/
							
							lecturersAllocatedTo=read.getModuleLecturersList(module);
							lectMultiple = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, chromosome, a-1);
							lectMultiple2 = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, chromosome, a);
							boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(chromosome, a-1, cohorts,module);
							if(isMoreThan4CohortHours){
								//System.out.println(" More than four cohort hours detected in findConsecFreeCohortLab");
							}
							if(!isMultiple1  && !isMultiple2 && !lectMultiple && !lectMultiple2 && isCorrectSize){
								freeRoom=c;
								freeTimeslot=a-1;
								//System.out.println(" Found in findConsecFreeCohortLab in Chromosome " + chromosome);
								
								this.consecutiveFreeGeneFound=true;
								return;
							}
						}
					}
				}
							
			}
			this.algorithmVersion=1;
		}else{
		
		//Version 2
			for(int c=0; c < roomCount; c++){
				if(read.getRoomType(rooms[c]).equals("lab")){
					for(int a=0 ;a <this.timeslot-1 ; a++){
						if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0){
							boolean isCorrectSize = this.roomSizeMatchedModuleSize(module, rooms[c]);
							isMultiple1 = this.isClashingForAnycohortInModule(cohorts, chromosome, a, module);
							isMultiple2= this.isClashingForAnycohortInModule(cohorts, chromosome, a+1, module);
						/**	for(int i=0;i<cohortsInModule;i++){//Check clashing per cohort In module
								level = read.getModuleCohortLevel(module, cohorts[i]);
								isMultiple1 = this.isMultipleScheduleForACohort(i, a, cohorts[i], level);
								isMultiple2 = this.isMultipleScheduleForACohort(i, a+1, cohorts[i], level);
								if(isMultiple1 || isMultiple2 )
									break;
							}**/
							
							lecturersAllocatedTo=read.getModuleLecturersList(module);
							lectMultiple = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, chromosome, a);
							lectMultiple2 = this.isClashingForAnyLecturerInModule(lecturersAllocatedTo, chromosome, a+1);
							boolean isMoreThan4CohortHours = this.isMoreThan4HoursOfConsecutiveLecturesPerCohort(chromosome, a, cohorts,module);
							if(isMoreThan4CohortHours){
								//System.out.println(" More than four cohort hours detected in findConsecFreeCohortLab");
							}
							if(!isMultiple1  && !isMultiple2 && !lectMultiple && !lectMultiple2 && isCorrectSize){
								freeRoom=c;
								freeTimeslot=a;
								
								//System.out.println(" Found in findConsecFreeCohortLab in Chromosome " + chromosome);
								
								this.consecutiveFreeGeneFound=true;
								return;
							}
						}
					}
				}
							
			}
			this.algorithmVersion=0;
		}//End of algorithm version 2
	
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
	private void findConsecutiveFreeLectureRoomsFromStartTime(int chromosome, int startTime,int module){
		freeChromosome=chromosome;
		freeRoom=-1;
		freeTimeslot=-1;
		boolean isCorrect=false,isClashing,isClashing2;
		for(int c=0; c < roomCount; c++){
			if(read.getRoomType(rooms[c]).equals("lecture")){
				isCorrect = this.roomSizeMatchedModuleSize(module, rooms[c]);
				int [] cohortsAssignedTo = read.getModuleCohort(module);
				isClashing = this.isClashingForAnycohortInModule(cohortsAssignedTo, chromosome, startTime, module);
				isClashing2 = this.isClashingForAnycohortInModule(cohortsAssignedTo, chromosome, startTime+1, module);
				for(int a =startTime;a <startTime+6 ; a++){
					if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0 && a >=startTime && isCorrect && !isClashing && !isClashing2){
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
		private void findConsecutiveFreeLabRoomsFromStartTime(int chromosome, int startTime,int module){
			freeChromosome=chromosome;
			freeRoom=-1;
			freeTimeslot=-1;
			this.consecutiveFreeGeneFound=false;
			boolean isCorrectRoomSize=false,isClashing,isClashing2;
			for(int c=0; c < roomCount; c++){
				if(read.getRoomType(rooms[c]).equals("lab")){
					isCorrectRoomSize = this.roomSizeMatchedModuleSize(module, rooms[c]);
					int [] cohortsAssignedTo = read.getModuleCohort(module);
					isClashing = this.isClashingForAnycohortInModule(cohortsAssignedTo, chromosome, startTime, module);
					isClashing2 = this.isClashingForAnycohortInModule(cohortsAssignedTo, chromosome, startTime+1, module);
					for(int a =startTime;a <startTime+6 ; a++){
						if(chromosomes[chromosome][c][a] ==0 && chromosomes[chromosome][c][a+1] ==0 && a >=startTime && isCorrectRoomSize && !isClashing && !isClashing2){
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
		if(chromosomeIndex <0 ||roomIndex <0 || timeslot<0)
			return false;
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
		 //Random rn= new Random();
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
	//Display Timetable for all cohorts and levels
	public String displayCohortTimetables(int chromosome){
		int numyears=0,startingLevel=0,modulehours=0;
		String moduleType="",timetableOwner="";
		String cohortTimetables="";
		boolean inserted=false;
		int [] cohortsAssignedTo = null;
		for(int i=0;i<cohorts.length;i++){
			startingLevel = read.getCohortStartingLevel(cohorts[i]);
			String cohortname =  read.getCohortTitle(cohorts[i]);
			numyears = read.getNumberOfYearsToGraduate(cohorts[i]);
			for(int j=startingLevel;j<(startingLevel + numyears);j++){
				timetableOwner=	"<br/><b>TIMETABLE FOR: "	+ cohortname + ", Level " + j + "</b><br/><hr/>";		
				cohortTimetables += timetableOwner + read.getCohortScheduleByLevel(cohorts[i], j, chromosomes, chromosome);
			}
			
		}
		return cohortTimetables;
	}
	//Checks if an event has been scheduled more than once for a Cohort at a given level of level of study
		public boolean isMultipleScheduleForACohort(int chromosome, int timeslot,int cohort,int level){
			//For each cohort, get the starting level and number of years
			boolean isMultiple = true, isCohortEvent=false;
			int countSchedules=0;
			if(timeslot >= this.timeslot)
				return true;
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
			else 
				isMultiple=true;
			return isMultiple;
			
		}
		//Check if there is already A SCHEDULE FOR A LECTURER IN A TIME SLOT
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
			else
				isMultiple=true;
			return isMultiple;
			

		}
	//Check if a module is being scheduled in correct room size or not
	private boolean roomSizeMatchedModuleSize(int module,int room){
		int roomSize = read.getRoomCapacity(room);
		int moduleSize = read.getModuleSize(module);
		int tolerance = -20; // A tolerance of 10 is when module size is more than room Capacity by only 10 students
		boolean isMatched = false;
			
		if(moduleSize <= roomSize || (roomSize - moduleSize)  >= tolerance)
			isMatched=true;
		else
			isMatched=false;
			
			return isMatched;
	}
	//Check if a cohort has more than four hours of lecture per day
	private boolean isMoreThan4HoursOfConsecutiveLecturesPerCohort(int chromosome,int currentTime, int[] cohort,int module){
			boolean isCohortModule=false,isCohortModule2=false,found=false;
			int count=0;	
			
			for(int i=0;i<currentTime;i++){
				for(int j=0;j < cohort.length;j++){
					int level = read.getModuleCohortLevel(module, cohort[j]);
					isCohortModule = this.cohortHasEventAtGivenTime(chromosome, i, cohort[j], level);
					isCohortModule2 = this.cohortHasEventAtGivenTime(chromosome, i+1, cohort[j], level);
					if(isCohortModule && isCohortModule2)
						count+=1;
					else
						count=0;
							
					if(count==4){
						//System.out.println(" More than four consecutive hours detected in chromosome " + chromosome);
						
						return	found=true;
					}
									
					//reset counter before checking the next day
					if(i==7||i==15||i==23||i==31)
						count=0;
				}
						
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
			int [] zero = {0};
			if(fit!=null)
				return fit.getSortedChromosomeIndices();
			else return zero;
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
	public int[] getHardFitnesses(){
		int [] zero = {0};
		if(fit !=null)
			return fit.hardFitnesses;
		else return zero;
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
