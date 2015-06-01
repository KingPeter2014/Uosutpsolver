package utpsolver;

import java.io.IOException;
import java.io.PrintWriter;
import utpsolver.UploadInputs;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AllocateModuleServlet extends HttpServlet {
	UploadInputs upload = null;
	String message="";
	int lecturer=0,module=0;
	public AllocateModuleServlet() {
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
		lecturer = Integer.parseInt(req.getParameter("lecturer"));
		if(lecturer==0){
			message+="Please, Select the Lecturer to whom the course will be allocated";
			out.println(message);
			return;
		}
		
		upload = new UploadInputs();
		int count = upload.courseAllocation(module, lecturer);
		if(count >0)
			out.println("Module successfully allocated to lecturer");
		else
			out.println("Module NOT successfully allocated. An error occured.");
		
	}

}
