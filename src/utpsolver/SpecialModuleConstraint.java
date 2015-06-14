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
 * Saves the special module constraints where a module must hold in a specific room at a specific time and day of the week
 */
public class SpecialModuleConstraint extends HttpServlet {
	private String message="";
	private int module=0,room=0,start_time=0,end_time=0,day=0;

	/**
	 * 
	 */
	public SpecialModuleConstraint() {
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
		module = Integer.parseInt(req.getParameter("module"));
		if(module==0){
			message+="Please Select the module with special constraint";
			out.println(message);
			return;
		}
		
		room = Integer.parseInt(req.getParameter("room"));
		if(room==0){
			message+="Please Select the room in which the module must take place in";
			out.println(message);
			return;
		}
		//INSERT THE CONSTRAINTS INTO SPECIAL MODULE CONSTRAINTS
		
	}

}
