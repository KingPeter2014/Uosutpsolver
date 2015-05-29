package utpsolver;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import com.mysql.fabric.Response;

import utpsolver.DBConnection;
public class ReadInputs {
	private int roomCount=0,lecturerCount=0,moduleCount=0;
	private int roomCapacity=0,studentsInModule=0,studentsInCohort=0;
	private List<String> roomnames=new ArrayList<String>(),roomTypes=new ArrayList<String>();
	String roomName,roomType,rooms="";
	DBConnection db = null;
	ResultSet rst = null;
	PreparedStatement pst = null;
	public ReadInputs(){
		db = new DBConnection();
		
	}
	public List<String> getRoomNames(){
		
		rst = db.executeQuery("SELECT code,roomname FROM lecturerooms");
		roomnames.add("Existing rooms");
		db.closeConnection();
		return roomnames;
	}
	//Returns a list of all the room types available
	public String getRooms(){
		rooms = "<tr>";
		rst = db.executeQuery("SELECT * FROM lecturerooms");
		roomTypes.add("Existing Room Types");
		try {
			while(rst.next()){
				roomTypes.add(rst.getString("roomtype"));
				rooms += "<td>" + rst.getInt("id") +"</td><td>" + rst.getString("code") + "</td><td>" + 
						rst.getString("roomtype") + "</td><td>" + rst.getInt("capacity") + "</td><td>"
						+ "<a href=\"editroom.jsp?id=" +rst.getInt("id") + "\"> Edit</a>|" 
						+ "<a href=\"deleteroom.jsp?id=" +rst.getInt("id") + "\"> Delete</a></td></tr>"
						;
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
			roomTypes.add(e.getMessage());
		}
		finally{
			db.closeConnection();
		}
		return rooms;
		
	}
	
	public String getRoomName(int roomid){
		roomName="No Result found";
		rst = db.executeQuery("SELECT code,roomname FROM lecturerooms WHERE id=" + roomid);
		try {
			roomName= rst.getString("code");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return roomName;
	}
	
	public String getRoomType(int roomid){
		
		return roomType;
	}
	
	public int getRoomCount(){
		
		return roomCount;
	}
	
	public int getRoomCapacity(int roomid){
		
		return roomCapacity;
	}
	
	public int getLecturerCount(){
		
		return lecturerCount;
	}
	public int getmoduleCount(){
		
		return moduleCount;
	}
}
