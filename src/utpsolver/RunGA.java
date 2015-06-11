package utpsolver;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utpsolver.DBConnection;

public class RunGA extends HttpServlet {
	//The index of the best and worst chromosome is maintained here
	private int bestChromosome=0,worstChromosome=0;
	
	//The current parents and Children in the current generation are maintained here
	private int parent1=0,parent2=0,child1=0,child2=0;
	
	//The maximum number of rooms, modules, cohort and lecturers respectively
	private int roomCount =0, moduleCount=0,cohortCount=0,lecturerCount=0;
	
	private int currentGeneration=0, MaximumGeneration = 450;
	private String message="";
	
	private List<String> cohortTimetable,lecturerTimetable,roomTimetable;
	
	public RunGA(){
		super();
		
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Prepare HTML page for output
		PrintWriter out;
		res.setContentType("text/html");
		out = res.getWriter();
		ServletContext context = req.getServletContext();
		
		message = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></head><body><div id=\"maincontent\">";
		out.println(message);
		//Process constraint variables set from constraints page and call the elitism Chromosome constructor
		
	}
	
	public void displayTimetable(){
		
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
