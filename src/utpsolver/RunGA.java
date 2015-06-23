package utpsolver;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import utpsolver.ReadInputs;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utpsolver.DBConnection;
import utpsolver.Crossover;

public class RunGA extends HttpServlet {
	//The index of the best and worst chromosome is maintained here
	private int bestChromosome=0,worstChromosome=0;
	ReadInputs read = new ReadInputs();
	Crossover cover;
	//The current parents and Children in the current generation are maintained here
	private int parent1=0,parent2=0,child1=0,child2=0;
	
	//The maximum number of rooms, modules, cohort and lecturers respectively
	private int roomCount =0, moduleCount=0,cohortCount=0,lecturerCount=0;
	
	private int currentGeneration=0, MaximumGeneration = 450;
	private String message="";
	Chromosomes cr = new Chromosomes();
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
		
		
		String test = "<br/><b>Fitness Unit Tests:</b><hr/>" + cr.getFitnessOnAContraint(0);
		out.println(test);
		String test1 = "Generated Timetable:<br/><table border=\"1\"> <tr> <th>Day/Time</th>"+
				"<th>9 - 9.50am</th><th>10 - 10.50am</th><th>11 - 11.50am</th><th>12 - 12.50pm</th><th>1 - 1.50pm</th>" +
				"<th>2 - 2.50pm</th><th>3 - 3.50pm</th><th>4 - 5pm</th></tr>"
				+ cr.displayGeneratedTimetable(0) + "</table>";
				out.println("<br/>" + test1);
		this.disPlayChromosome(0);
		this.disPlayChromosome(1);
		this.doCrossover();
		String child1 = cover.printChildren();
		out.println("<br/>" + child1);
		long a = cr.startTime/1000;
		long b = cr.endTime/1000;
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
		 
		 cover = new Crossover(0,1,cr.chromosomes,cr.numChromosomes,cr.timeslot,cr.rooms,cr.modules,cr.moduleTypes,cr.roomTypes);
		
	}
	public void displayGeneralTimetable(int chromosome){
		String test1 = "Generated Timetable:<br/><table border=\"1\"> <tr> <th>Day/Time</th>"+
				"<th>9 - 9.50am</th><th>10 - 10.50am</th><th>11 - 11.50am</th><th>12 - 12.50pm</th><th>1 - 1.50pm</th>" +
				"<th>2 - 2.50pm</th><th>3 - 3.50pm</th><th>4 - 5pm</th></tr>"
				+ cr.displayGeneratedTimetable(chromosome-1) + "</table>";
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
	

}
