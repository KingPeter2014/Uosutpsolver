package utpsolver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModuleCohortServlet extends HttpServlet {
	UploadInputs upload = null;
	String message="";
	int cohort=0,module=0,level=0;
	public ModuleCohortServlet() {
		super();
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Prepare HTML page for output
		PrintWriter out;
		res.setContentType("text/html");
		out = res.getWriter();
				message = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></head><body><div id=\"maincontent\">";
		out.println(message);
		
		//Retrieve form parameters for course allocation
		module = Integer.parseInt(req.getParameter("module"));
		if(module==0){
			message+="Please, Select the module you want to allocate";
			out.println(message);
			return;
		}
		cohort = Integer.parseInt(req.getParameter("cohort"));
		if(cohort==0){
			message+="Please, Select the Cohort to whom the course will be assign ";
			out.println(message);
			return;
		}
		
		level = Integer.parseInt(req.getParameter("level"));
		if(level==0){
			message+="Please, Select the level of study";
			out.println(message);
			return;
		}

		upload = new UploadInputs();
		int count = 0;
		count = upload.assignModuleToCohort(module, cohort,level);
		//out.println(message);
		if(count >0)
			out.println("Module successfully assigned to Cohort");
		else
			out.println("Module NOT successfully asigned. An error occured.");
		
	}


}
