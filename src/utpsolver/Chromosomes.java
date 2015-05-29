package utpsolver;
import utpsolver.ReadInputs;
/**
 * 
 * @author petereze
 * This class handles the the random initialization of the GA Population
 *
 */
public class Chromosomes {
	public  int[][][] chromosomes = new int[20][][];
	private int timeslot = 40,roomCount=0,moduleCount=0,lecturerCount=0;
	ReadInputs read = new ReadInputs();
	public Chromosomes(){
		roomCount= read.getRoomCount();
		moduleCount=read.getmoduleCount();
		lecturerCount=read.getLecturerCount();
		
	}
	private void initializePopulation(){
		
	}

}
