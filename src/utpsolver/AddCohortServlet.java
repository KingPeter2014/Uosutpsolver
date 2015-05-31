package utpsolver;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import utpsolver.UploadInputs;



public class AddCohortServlet extends HttpServlet {
	private String message="",cohortname="";
	private int numstudents=0,level=0;
	UploadInputs upload = null;
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
		if(cohortname.equals("")){
			message+="Every Cohort must have a name";
			out.println(message);
			return;
		}
		String ns = req.getParameter("numstudent");
		if(ns!=null && !ns.equals("")){
			numstudents = Integer.parseInt(ns);
		}
		else
		{
			message+="Please, enter value for number of students in this Cohort";
			out.println(message);
			return;
		}
		String l = req.getParameter("level");
		if(l!=null && !l.equals("")){
			level = Integer.parseInt(l);
		}
		else
		{
			message+="Please, enter the level of study for this Cohort";
			out.println(message);
			return;
		}		
		message = "";
		upload = new UploadInputs();
		String addcohort = upload.addCohort(cohortname, numstudents, level);
		message += addcohort;
		out.println(message);
	}

}
