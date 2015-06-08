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
	private int numChromosomes = 50, fitness=0;
	public  int[][][] chromosomes = null;
	public static int timeslot = 40,roomCount=0,moduleCount=0,lecturerCount=0;
	private String roomType="",overallTimetable="";
	private int [] rooms,modules;
	private int freeChromosome=0,freeRoom=0,freeTimeslot=0;
	private boolean consecutiveFreeGeneFound=false;
	ReadInputs read = new ReadInputs();
	Fitness fit = null;
	
	public Chromosomes(){
		lecturerCount=read.getLecturerCount();
		rooms= read.getRoomIds();
		modules = read.getModuleIds();
		roomCount= rooms.length;
		moduleCount=modules.length;
		chromosomes = new int[numChromosomes][roomCount][timeslot];
		this.initialAllChromosomesToZero();
		this.initializePopulation();
		 fit = new Fitness(chromosomes,numChromosomes,roomCount,timeslot,rooms,modules);
		
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
	
	private void initializePopulationWithElitism(){
		
	}
	public int getFitnessOnAContraint(int chromosome){
		this.fitness = fit.computeClassHeldInCorrectRoomSizeFitness(chromosome);
		return this.fitness;
		
	}
	//Look for any free gene space. That is genes whose content is zero. No module allocated to it.
	private void findUnoccuppiedGene(int currentChromosome){
		freeChromosome=currentChromosome;
		freeRoom=0;
		freeTimeslot=0;
		for(int c=0; c < roomCount; c++){
			for(int a =0;a <40 ; a++){
				if(chromosomes[currentChromosome][c][a] ==0){
					freeChromosome=currentChromosome;
					freeRoom=c;
					freeTimeslot=a;
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
