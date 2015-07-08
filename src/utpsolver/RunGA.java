package utpsolver;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import utpsolver.ReadInputs;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utpsolver.DBConnection;
import utpsolver.Crossover;
import utpsolver.Chromosomes;

public class RunGA extends HttpServlet {
	//The index of the best and worst chromosome is maintained here
	private int bestChromosome=0,worstChromosome=0;
	private int betterChild=0;
	ReadInputs read = new ReadInputs();
	Crossover cover;
	//The current parents and Children in the current generation are maintained here
	private int parent1=0,parent2=0;
	private int [][][] children;
	private int[] hardFitness,softFitness,overallFitness;
	
	//The maximum number of rooms, modules, cohort and lecturers respectively
	private int roomCount =0, moduleCount=0,cohortCount=0,lecturerCount=0;
	int[] allFitness,sortedIndices;
	private int currentGeneration=0, MaximumGeneration = 500;
	private int maxreward=0,maxHard=0,maxSoft=0;
	private String message="";
	private Chromosomes cr = null;
	PrintWriter out;
	
	private List<String> cohortTimetable,lecturerTimetable,roomTimetable;
	
	public RunGA(){
		super();
		
		
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Prepare HTML page for output
		res.setContentType("text/html");
		out = res.getWriter();
		message = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></head><body><div id=\"maincontent\">";
		out.println(message);
		//Process constraint variables set from constraints page and call the elitism Chromosome constructor
		cr = new Chromosomes();
		if(cr.status==0){
			out.println("No moddule to Schedule or an error occured");
			return;
		}
		this.maxreward = cr.getMaxFitnessReward();
		this.maxHard=cr.getMaxHardConstraintReward();
		this.maxSoft = cr.getMaxSoftConstraintReward();
		allFitness = cr.evaluatePopulationFitness();
		sortedIndices = cr.getSortedChromosomeIndices();
		this.bestChromosome = sortedIndices[0];
		this.worstChromosome = sortedIndices[cr.numChromosomes-1];
		
		//RUN GA IN THIS LOOP
		for(int i=0; i<this.MaximumGeneration;i++){
			//If overall fitness of best chromosome is same as maximum fitness discontinue generations
			
			if(allFitness[this.bestChromosome] >=this.maxreward){
				out.println("Jumping out of loop");
				break;
			}
			this.currentGeneration = i;
			this.doCrossover();
			this.getFitnessOfChildren();
			//Get the overall hard constraint reward on the worst chromosome
			int hardFitness = cr.getOverallHardConstraintRewards(this.worstChromosome);
			//Get overall fitness of best child after crossover
			int overallFitnessOfBestChild = this.overallFitness[this.betterChild-1];
			out.println("<br/>Best Chromosome fitness : " + cr.getOverallFintnessValue(this.bestChromosome) +", BestChild Fitness: " + overallFitnessOfBestChild + " in generation:" + this.currentGeneration);
			//if(overallFitnessOfBestChild > allFitness[this.worstChromosome] && this.hardFitness[this.betterChild-1] >=hardFitness){
				//cr.replaceChromosome(this.worstChromosome, this.betterChild-1, this.children);
			if(overallFitnessOfBestChild > allFitness[this.worstChromosome]){
				cr.replaceChromosome(this.worstChromosome, this.betterChild-1, this.children);
				
				// Evaluate Entire chromosome only after replacement
				allFitness = cr.evaluatePopulationFitness();
				sortedIndices = cr.getSortedChromosomeIndices();
				this.bestChromosome = sortedIndices[0];
				this.worstChromosome = sortedIndices[cr.numChromosomes-1];
			}
			
		}
		String test = "<br/><button id=\"best\"><b>Click to Display/Hide the Fitness of Best Chromosome</button><hr/>" + cr.getFitnessOnAContraint(this.bestChromosome);
		out.println(test);
		String test1 = "Generated Timetable:<br/><table border=\"1\"> <tr> <th>Day/Time</th>"+
				"<th>9 - 9.50am</th><th>10 - 10.50am</th><th>11 - 11.50am</th><th>12 - 12.50pm</th><th>1 - 1.50pm</th>" +
				"<th>2 - 2.50pm</th><th>3 - 3.50pm</th><th>4 - 5pm</th></tr>"
				+ cr.displayGeneratedTimetable(this.bestChromosome) + "</table>";
				out.println("<br/>" + test1);
		//this.disPlayChromosome(this.bestChromosome);
		//this.disPlayChromosome(parent2);
		//String child1 = cover.printChildren();
		//out.println("<hr/>After evaluating children: <br/>" +cr.getCurrentFitnessMessage());
		
		//out.println("<br/>" + child1);
		out.println(cr.displayCohortTimetables(this.bestChromosome));
		long a = cr.startTime/1000;
		long b = System.currentTimeMillis()/1000;
		long runningTime = b-a;
		out.println("<b><h2>It took approximately "+ runningTime + " seconds to run this GA</h2></b>");

		
	}
	public void disPlayChromosome(int chromosome){

		int timeslots = 40;
		int [][][] chromo = cr.chromosomes;
		
		out.println("<br/><hr/>Chromosome Structure[" + chromosome + "]<hr/>");
		for(int b=0; b< Chromosomes.roomCount; b++){
			for(int a =0; a < timeslots; a++)
				out.println(chromo[chromosome][b][a] + "&nbsp&nbsp&nbsp");
			out.println("<br/>");
			
		}
		
	}
	public void doCrossover(){
		 children = new int[2][cr.roomCount][cr.timeslot];
		 int p1 = this.generateRandomInteger(cr.numChromosomes-1);
		 int p2 = this.generateRandomInteger(cr.numChromosomes-cr.numChromosomes/2);
		 this.parent1 = this.doTournamentSelection(p1, p2);
		 p1 = this.generateRandomInteger(cr.numChromosomes-1);
		 p2 = this.generateRandomInteger(cr.numChromosomes-cr.numChromosomes/2);
		 this.parent2 = this.doTournamentSelection(p1, p2);
		 //Call Crossover and mutation operators
		 cover = new Crossover(parent1,parent2,cr.chromosomes,cr.numChromosomes,cr.timeslot,cr.rooms,cr.modules,cr.moduleTypes,cr.roomTypes);
		 children[0]= cover.getFirstChild();
		 children[1]= cover.getSecondChild();
		 cr.evaluateChildren(children);
		
	}
	public void displayGeneralTimetable(int chromosome){
		String test1 = "Generated Timetable:<br/><table border=\"1\"> <tr> <th>Day/Time</th>"+
				"<th>9 - 9.50am</th><th>10 - 10.50am</th><th>11 - 11.50am</th><th>12 - 12.50pm</th><th>1 - 1.50pm</th>" +
				"<th>2 - 2.50pm</th><th>3 - 3.50pm</th><th>4 - 5pm</th></tr>"
				+ cr.displayGeneratedTimetable(chromosome) + "</table>";
				out.println("<br/>" + test1);
		
	}
	public List<String> getCohortTimetable(int cohortid){
		
		return cohortTimetable;
		
	}
	
public List<String> getLecturerTimetable(int lecturerid){
		
		return lecturerTimetable;
		
	}

public List<String> getRoomTimetable(int cohortid){
	
	return roomTimetable;
	
}
private void getFitnessOfChildren(){
	this.hardFitness = cr.getHardFitnessOfChildren();
	this.softFitness = cr.getOveralFitnessOfChildren();
	this.overallFitness = cr.getOveralFitnessOfChildren();
	if(this.hardFitness[0]>=this.hardFitness[1] )
		this.betterChild=1;
	else
		this.betterChild=2;
	//out.println("<br/>Best child is :" + this.betterChild);
	//out.println("<br/>Hard fitness of child 1 is  :" + this.hardFitness[0] + " out of " + this.maxHard);
	
	//out.println("<br/>Overall fitness of best child is  :" + this.overallFitness[this.betterChild-1]);
}
//Mutation by swaping modules in a given room between two timeslots
private void MutateChromosome(int[][][] chromosome,int chromoIndex){
	int t1 = this.generateRandomInteger(cr.timeslot-1);
	int t2 = this.generateRandomInteger(cr.timeslot-1);
	int room = this.generateRandomInteger(cr.roomCount-1);
	int temp = chromosome[chromoIndex][room][t1];
	chromosome[chromoIndex][room][t1]=chromosome[chromoIndex][room][t2];
	chromosome[chromoIndex][room][t2]=temp;
			
}
private int generateRandomInteger(int maxNumber){
	
	// nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    //int randomNum = rand.nextInt((max - min) + 1) + min;

	int rand=0;
	 Random rn= new Random();
	 rand = rn.nextInt((maxNumber)  + 1) ;
	return rand;
}
//Tournament Selection of Parents for crossing
private int doTournamentSelection(int p1,int p2){
	double random = Math.random();
	if(this.allFitness[p1]>this.allFitness[p2] && random <=0.7)
		return this.sortedIndices[p1];
	else
		return this.sortedIndices[p2];
		
}


}
