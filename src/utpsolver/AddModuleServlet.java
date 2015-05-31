package utpsolver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddModuleServlet extends HttpServlet{
	private String message="",code="",title="",category="",department="";
	private int lecturehour=0, labhour=0,level=0,numstudents=0;
	public AddModuleServlet(){
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
	}

}
