package utpsolver;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

import com.mysql.jdbc.Driver;

public class AddCohortServlet extends HttpServlet {
	private String message="",cohortname="";
	private int numstudents=0,level=0;
	public AddCohortServlet() {
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
		cohortname = req.getParameter("cname");
		numstudents = Integer.parseInt(req.getParameter("numstudent"));
		level = Integer.parseInt(req.getParameter("level"));
		if(cohortname.equals("")){
			message+="Every Cohort must have a name";
			out.println(message);
			return;
		}
		message = "";
	}

}
