package utpsolver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddModuleServlet extends HttpServlet{
	private String message="",code="",title="",category="",type="",department="";
	private int lecturehour=0, labhour=0,level=0,numstudents=0;
	UploadInputs upload = null;
	public AddModuleServlet(){
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
		code = req.getParameter("code");
		if(code.equals("")){
			message+="Please, enter the module code";
			out.println(message);
			return;
		}
		title= req.getParameter("title");
		
		category = req.getParameter("category");
		if (category.equals("0")){
			message+="Please select if the module is core or elective or both";
			out.println(message);
			return;
			
		}
		
		type = req.getParameter("type");
		if (type.equals("0")){
			message+="Please, Select if this module is lecture only, lab only or both";
			out.println(message);
			return;
			
		}
		lecturehour = Integer.parseInt(req.getParameter("lecturehour"));
		labhour = Integer.parseInt(req.getParameter("labhour"));
		numstudents = Integer.parseInt(req.getParameter("numstudents"));
		
		//Ensure that both lecture hour and laboratory hours are provided for a lab and lecture module
		if(type.equals("both")){
			if(labhour <=0){
				message+="Please, enter the number of hours for Laboratory component";
				out.println(message);
				return;
				
			}
			if(lecturehour <=0){
				message+="Please, enter the number of hours for Lecture component";
				out.println(message);
				return;
				
			}
		}
		if(type.equals("lab") && labhour <= 0){
			message+="Laboratory hours cannot be zero for a lab-only module";
			out.println(message);
			return;
			
		}
		//Validate lecture hours for a lecture only module
		if(type.equals("lecture") && lecturehour <= 0){
			message+="Lecture hours cannot be zero for a lecture-only module";
			out.println(message);
			return;
			
		}
		String l = req.getParameter("level");
		if(l!=null && !l.equals("")){
			level = Integer.parseInt(l);
		}
		else
		{
			message+="Please, enter the level this course is offered";
			out.println(message);
			return;
		}	
		department =  req.getParameter("department");
		if (department.equals("0")){
			message+="Please select a valid host department for this module";
			out.println(message);
			return;
			
		}
		upload = new UploadInputs();
		message = upload.addModule(code, title, category,type, lecturehour, labhour, numstudents, level, department);
		out.println(message);
		
	}

}
