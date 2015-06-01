package utpsolver;

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
	private List<String> roomTypes=new ArrayList<String>();
	String roomName,roomType,rooms="",moduleType="",cohorts="",modules="",lecturers="";
	DBConnection db = null;
	ResultSet rst = null;
	PreparedStatement pst = null;
	public ReadInputs(){
		db = new DBConnection();
		
	}
	
	//Returns a list of all the room types available
	public String getRooms(){
		rooms = "<tr>";
		rst = db.executeQuery("SELECT * FROM lecturerooms");
		
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
			rooms+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		return rooms;
		
	}
	
	public String getCohorts(){
		cohorts = "";
		rst = db.executeQuery("SELECT * FROM cohorts");
		
		try {
			while(rst.next()){
				
				cohorts += "<tr><td>" + rst.getInt("id") +"</td><td>" + rst.getString("cohortname") + "</td><td>" + 
						rst.getString("numstudents") + "</td><td>" + rst.getInt("level_of_study") + "</td><td>"
						+ "<a href=\"editcohort.jsp?id=" +rst.getInt("id") + "\"> Edit</a>|" 
						+ "<a href=\"delete.jsp?id=" +rst.getInt("id") + "&what=cohort\"> Delete</a></td></tr>"
						;
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
			cohorts+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		return cohorts;
	}
	public String getModules(){
		modules= "";
		rst = db.executeQuery("SELECT * FROM courses");
		
		try {
			while(rst.next()){
				
				modules += "<tr><td>" + rst.getInt("id") +"</td><td>" + rst.getString("coursecode") + "</td><td>" + 
						rst.getString("coursetitle") + "</td><td>" + rst.getInt("level") + "</td><td>"
						+ "<a href=\"editmodule.jsp?id=" +rst.getInt("id") + "\"> Edit</a>|" 
						+ "<a href=\"delete.jsp?id=" +rst.getInt("id") + "&what=module\"> Delete</a></td></tr>"
						;
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
			modules+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		
		return modules;
		
	}
	
	public String getLecturers(){
		lecturers= "";
		rst = db.executeQuery("SELECT * FROM lecturers");
		
		try {
			while(rst.next()){
				
				lecturers += "<tr><td>" + rst.getInt("id") +"</td><td>" + rst.getString("lecturername") + "</td><td>" + 
						rst.getString("lecturer_type") + "</td><td>" + rst.getString("department") + "</td><td>"
						+ "<a href=\"editlecturer.jsp?id=" +rst.getInt("id") + "\"> Edit</a>|" 
						+ "<a href=\"delete.jsp?id=" +rst.getInt("id") + "&what=lecturer\"> Delete</a></td></tr>"
						;
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
			lecturers+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		
		return lecturers;
		
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
		
		rst = db.executeQuery("SELECT count(id) AS numrooms FROM lecturerooms");
		try {
			rst.first();
			roomCount= rst.getInt("numrooms");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		
		return roomCount;
	}
	
	public int getRoomCapacity(int roomid){
		
		rst = db.executeQuery("SELECT capacity FROM lecturerooms WHERE id=" + roomid);
		try {
			rst.first();
			roomCapacity= rst.getInt("numrooms");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		
		return roomCapacity;
	}
	
	public int getLecturerCount(){
		rst = db.executeQuery("SELECT count(id) AS numlecturers FROM lecturers");
		try {
			rst.first();
			lecturerCount= rst.getInt("numlecturers");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return lecturerCount;
	}
	public int getmoduleCount(){
		rst = db.executeQuery("SELECT count(id) AS numodules FROM courses");
		try {
			rst.first();
			moduleCount= rst.getInt("numodules");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return moduleCount;
	}
	
	public int getStudentsInModule(int moduleid){
		rst = db.executeQuery("SELECT numstudents FROM courses WHERE id=" + moduleid);
		try {
			rst.first();
			studentsInModule= rst.getInt("numstudents");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return studentsInModule;
	}
	public String getModuleType(int moduleid){
		rst = db.executeQuery("SELECT coursetype FROM courses WHERE id=" + moduleid);
		try {
			rst.first();
			moduleType= rst.getString("coursetype");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return moduleType;
	}
	//Get Courses for display in a select input for course allocation to lecturer
	public String displayCourses(){
		
		modules= "";
		rst = db.executeQuery("SELECT * FROM courses");
		try {
			while(rst.next()){
				modules += "<option value=\"" + rst.getInt("id") +"\">" + rst.getString("coursecode") + "</option>";
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
			modules+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}

		
		return modules;
		
	}
	
	//Get Lecturers to whom Courses could be assigned
	public String displayLecturers(){
		lecturers = "";
		rst = db.executeQuery("SELECT * FROM lecturers");
		
		try {
			while(rst.next()){
				lecturers += "<option value=\"" + rst.getInt("id") +"\">" + rst.getString("lecturername") + "</option>";
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
			lecturers+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		
		return lecturers;
	}
	//Get Cohorts to which Courses could be assigned to
	public String displayCohorts(){
		lecturers = "";
		rst = db.executeQuery("SELECT * FROM cohorts");
		
		try {
			while(rst.next()){
				lecturers += "<option value=\"" + rst.getInt("id") +"\">" + rst.getString("cohortname") + "</option>";
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
			lecturers+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		
		return lecturers;
	}
}
