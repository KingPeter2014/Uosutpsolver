/**
 * 
 */
package utpsolver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author petereze
 *
 */
public class LecturerAvailabilityServlet extends HttpServlet {

	/**
	 * 
	 */
	private String message="";
	UploadInputs upload = null;
	private int lecturerid=0,startime=0,endtime=0,day=0;
	public LecturerAvailabilityServlet() {
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
		lecturerid = Integer.parseInt(req.getParameter("lecturer"));
		if(lecturerid==0){
			message+="Please Select the part time lecturer you want";
			out.println(message);
			return;
		}
		String ns = req.getParameter("from");
		if(ns!=null && !ns.equals("")){
			startime = Integer.parseInt(ns);
		}
		else
		{
			message+="Please, Select the starting time for availability";
			out.println(message);
			return;
		}
		endtime = Integer.parseInt(req.getParameter("to"));
		if(endtime < startime){
			message+="Please, the starting time for availability cannot be more than the ending time.";
			out.println(message);
			return;

		}
		day = Integer.parseInt(req.getParameter("days"));
		upload = new UploadInputs();
		int count = upload.registerLecturerAvailability(lecturerid, startime, endtime, day);
		if(count >0){
			out.println("Lecturer availability successfully recorded.");
		}
		else{
			out.println("Error: Could not register Lecturer availability.");
			
		}
	}

}
