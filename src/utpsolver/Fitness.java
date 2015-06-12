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
	private int [] rooms,modules,lecturers;
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
	//Computes fitness to Check if all the classes are held in correct room type for this individual chromosome
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
	
	//Computes fitness to Check if all the classes are held in correct Size for this individual chromosome
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
	
	//Check if a particular event belongs to a lecturer
	private boolean checkLecturerEvent(int event, int lecturer){
		return read.confirmEventBelongsToLecturer(event, lecturer);
		
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
