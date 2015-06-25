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
	private int parent1=0,parent2=5;
	private int [][][] children;
	private int[] hardFitness,softFitness,overallFitness;
	
	//The maximum number of rooms, modules, cohort and lecturers respectively
	private int roomCount =0, moduleCount=0,cohortCount=0,lecturerCount=0;
	
	private int currentGeneration=0, MaximumGeneration = 450;
	private String message="";
	Chromosomes cr = null;
	PrintWriter out;
	
	private List<String> cohortTimetable,lecturerTimetable,roomTimetable;
	
	public RunGA(){
		super();
		cr = new Chromosomes();
		
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Prepare HTML page for output
		
		res.setContentType("text/html");
		out = res.getWriter();
		
		
		message = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></head><body><div id=\"maincontent\">";
		out.println(message);
		//Process constraint variables set from constraints page and call the elitism Chromosome constructor
		
		
		this.doCrossover();
		String test = "<br/><b>Fitness Unit Tests:</b><hr/>" + cr.getFitnessOnAContraint(parent1);
		out.println(test);
		String test1 = "Generated Timetable:<br/><table border=\"1\"> <tr> <th>Day/Time</th>"+
				"<th>9 - 9.50am</th><th>10 - 10.50am</th><th>11 - 11.50am</th><th>12 - 12.50pm</th><th>1 - 1.50pm</th>" +
				"<th>2 - 2.50pm</th><th>3 - 3.50pm</th><th>4 - 5pm</th></tr>"
				+ cr.displayGeneratedTimetable(parent1) + "</table>";
				out.println("<br/>" + test1);
		this.disPlayChromosome(parent1);
		this.disPlayChromosome(parent2);
		String child1 = cover.printChildren();
		out.println("<hr/>After evaluating children: <br/>" +cr.getCurrentFitnessMessage());
		this.getFitnessOfChildren();
		out.println("<br/>" + child1);
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
		 int parent1 = this.generateRandomInteger(cr.numChromosomes-1);
		 int parent2 = this.generateRandomInteger(cr.numChromosomes-1);
		 this.parent1=parent1;this.parent2=parent2;
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


}
