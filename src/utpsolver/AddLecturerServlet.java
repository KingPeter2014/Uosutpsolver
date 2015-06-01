package utpsolver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddLecturerServlet extends HttpServlet {
	private String message="",name="",status="",department="";
	UploadInputs upload = null;
	public AddLecturerServlet(){
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
		
		//Retrieve form parameters to create a lectuer
		name = req.getParameter("name");
		if(name.equals("")){
			message+="Please, enter the name of the Lecturer";
			out.println(message);
			return;
		}
		status= req.getParameter("status");
		department= req.getParameter("department");
		upload = new UploadInputs();
		
		//Create a new lecturer
		message = upload.addLecturer(name, status, department);
		out.println(message);
	}

}
