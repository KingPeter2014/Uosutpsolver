package utpsolver;
import java.util.Random;

import utpsolver.ReadInputs;
/**
 * 
 * @author petereze
 * This class handles the the random initialization of the GA Population
 *
 */
public class Chromosomes {
	private int numChromosomes = 20;
	public  int[][][] chromosomes = null;
	public static int timeslot = 40,roomCount=0,moduleCount=0,lecturerCount=0;
	private int [] rooms,modules;
	
	ReadInputs read = new ReadInputs();
	public Chromosomes(){
		lecturerCount=read.getLecturerCount();
		rooms= read.getRoomIds();
		modules = read.getModuleIds();
		roomCount= rooms.length;
		moduleCount=modules.length;
		chromosomes = new int[numChromosomes][roomCount][timeslot];
		this.initialAllChromosomesToZero();
		this.initializePopulation();

		
	}
	private void initializePopulation(){
		int module = 0;
		for(int i=0; i <numChromosomes;i++){
			for(int c=0; c < roomCount; c++){
				for(int a =0;a <40 ; a++){
					module = this.generateRandomInteger(moduleCount);
					chromosomes[i][c][a]=modules[module-1];
					
				}
				
			}
		}
		
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

}
