package utpsolver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddRoomServlet extends HttpServlet {
	private String message="",roomName="",description="", type="";
	private int capacity=0;
	private double latitude=0.0, longitude=0.0;
	UploadInputs upload = null;
	public AddRoomServlet() {
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
		roomName = req.getParameter("roomname");
		if(roomName.equals("")){
			message+="Every Room must have a name";
			out.println(message);
			return;
		}
		
		description = req.getParameter("description");
		type = req.getParameter("type");

		capacity = Integer.parseInt(req.getParameter("capacity"));
		if(capacity <= 0){
			message+="The capacity of a room cannot be less than 1";
			out.println(message);
			return;
		}
		longitude = Double.parseDouble(req.getParameter("longitude"));
		latitude = Double.parseDouble(req.getParameter("latitude"));
		upload = new UploadInputs();
		message =  upload.addRoom(roomName, description, type, capacity, latitude, longitude);
		out.println(message);
	}
	
	
}
