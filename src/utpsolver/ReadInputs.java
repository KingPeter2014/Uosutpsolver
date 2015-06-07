package utpsolver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.*;

//import com.mysql.fabric.Response;

import utpsolver.DBConnection;
public class ReadInputs {
	private int roomCount=0,lecturerCount=0,moduleCount=0;
	private int roomCapacity=0,studentsInModule=0,studentsInCohort=0;
	private List<Integer> roomids=new ArrayList<Integer>();
	private List<Integer> moduleids=new ArrayList<Integer>();
	private List<Integer> moduleAllocationids=new ArrayList<Integer>();
	private List<Integer> lecturerAllocationids=new ArrayList<Integer>();
	private String dailySchedule="";
	
	int[] idsArray ;
	String message="",roomName,roomType="",rooms="",moduleType="",cohorts="",modules="",
			lecturers="",courseallocations="";
	String dayOfWeek = "";
	DBConnection db = null;
	ResultSet rst = null;
	PreparedStatement pst = null;
	public ReadInputs(){
		db = new DBConnection();
		
	}
	//Get list of all the room ids for generating chromosome
	public int[] getRoomIds(){
		rst = db.executeQuery("SELECT * FROM lecturerooms");
		try {
			while(rst.next()){
				roomids.add(rst.getInt("id"));
			}
			}
			catch (SQLException e) {
				
				e.printStackTrace();
				message+=e.getMessage();
			}
			finally{
				db.closeConnection();
			}
		
		return convertIntegerListToIntegerArray(roomids);
	}
	//Get Lecturer ids from course allocation table
	public int[] getLecturerIdsFromCourseAllocationTable(){
		rst = db.executeQuery("SELECT lecturer_id FROM course_allocations");
		try {
			while(rst.next()){
				lecturerAllocationids.add(rst.getInt("lecturer_id"));
			}
		}
		catch (SQLException e) {	
			e.printStackTrace();
			message+=e.getMessage();
		}
		finally{
					db.closeConnection();
			}
			
		return convertIntegerListToIntegerArray(lecturerAllocationids);
	}
	
		//Get Module/Course ids from course allocation table
	public int[] getModuleIdsFromCourseAllocationTable(){
		rst = db.executeQuery("SELECT course_id FROM course_allocations");
		try {
			while(rst.next()){
				moduleAllocationids.add(rst.getInt("course_id"));
				}
			}
		catch (SQLException e) {				
			e.printStackTrace();
			message+=e.getMessage();
			}
		finally{
				db.closeConnection();
			}
			return convertIntegerListToIntegerArray(moduleAllocationids);
	}
	
	//Converts an integer ArrayList to int Array
	public int[] convertIntegerListToIntegerArray(List<Integer> list){
		idsArray = new int[list.size()];
		Iterator<Integer> iter = list.iterator();
		int i=0;
		while(iter.hasNext()){
			idsArray[i] = Integer.parseInt(iter.next().toString());
			i=i+1;
		}
		
		return  idsArray;
	}
	
	//Get Module ids for generating chromosome
	public int[] getModuleIds(){
		rst = db.executeQuery("SELECT * FROM courses");
		try {
			while(rst.next()){
				moduleids.add(rst.getInt("id"));
			}
			}
			catch (SQLException e) {
				
				e.printStackTrace();
				message+=e.getMessage();
			}
			finally{
				db.closeConnection();
			}
		return convertIntegerListToIntegerArray(moduleids);
	}
	//Returns a list of all the room types available
	public String getRooms(){
		rooms = "<tr>";
		rst = db.executeQuery("SELECT * FROM lecturerooms");
		
		try {
			while(rst.next()){
				//roomTypes.add(rst.getString("roomtype"));
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
	//Gets cohorts for listing, editing and deleting purposes
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
	//Gets modules for listing, editing and deleting purposes
	public String getModules(){
		modules= "";
		ResultSet rst = db.executeQuery("SELECT * FROM courses");
		
		try {
			while(rst.next()){
				
				modules += "<tr><td>" + rst.getInt("id") +"</td><td>" + rst.getString("coursecode") +"(" + rst.getString("coursetype")+ ")</td><td>" + 
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
			try {
				rst.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		
		return modules;
	}
	//Get lecturers for listing and editing purposes
	public String getLecturers(){
		lecturers= "";
		ResultSet rst = db.executeQuery("SELECT * FROM lecturers");
		
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
			try {
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return lecturers;
		
	}
	
	//Return the modules assigned to lecturers
	public String getCourseAllocations(){
		courseallocations="";
		
		//int [] allocatedLecturer = this.getLecturerIdsFromCourseAllocationTable();
		//int	[] allocatedModules = this.getModuleIdsFromCourseAllocationTable();
		//int len = allocatedLecturer.length;
		String query ="SELECT c.coursecode,c.coursetitle,c.coursetype, l.lecturername,ca.id FROM courses c INNER JOIN course_allocations ca on c.id = ca.course_id INNER JOIN lecturers l on ca.lecturer_id = l.id";
		
		rst = db.executeQuery(query);
		try {
			while(rst.next()){
				
				courseallocations += "<tr><td>" + rst.getString("lecturername") +"</td><td>" + rst.getString("coursecode") +"(" + rst.getString("coursetype")+")</td><td>" + 
				rst.getString("coursetitle") +"</td><td>"  +
						 "<td><a href=\"editallocation.jsp?id=" + rst.getInt("id") +"\"> Edit</a>|" 
										+ "<a href=\"delete.jsp?id=" + rst.getInt("id") + "&what=allocation\"> Delete</a></td></tr>";
				
			}
		} catch (SQLException e) {
						e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return courseallocations;
	}
	
	//Gets the names of lecturers to whom a module has been assigned to
	public String getModuleLecturers(int moduleid){
		String lecturers = "";
		String query ="SELECT c.coursecode,c.coursetitle,c.coursetype, l.lecturername,ca.id FROM courses c INNER JOIN course_allocations ca on c.id = ca.course_id AND c.id =" + moduleid+ " INNER JOIN lecturers l on ca.lecturer_id = l.id";
		
		rst = db.executeQuery(query);
		try {
			while(rst.next()){
				lecturers+=rst.getString("lecturername") + ",";
				
			}
		} catch (SQLException e) {
						e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		if(lecturers.equals(""))
			lecturers="NONE";
		return lecturers;
	}

	// Return room code for mapping from genotype to phenotype
	public String getRoomName(int roomid){
		roomName="No Result found";
		rst = db.executeQuery("SELECT code,roomname FROM lecturerooms WHERE id=" + roomid);
		try {
			rst.first();
			roomName= rst.getString("code");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return roomName;
	}
	
	// Return Lecturer Name for mapping from genotype to phenotype
	public String getModuleCode(int moduleid){
		String code="No Result found";
		rst = db.executeQuery("SELECT coursecode FROM courses WHERE id=" + moduleid);
		try {
			rst.first();
			code= rst.getString("coursecode");
		} catch (SQLException e) {
				
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return code;
	}
	
	// Return Module code for mapping from genotype to phenotype
		public String getLecturerName(int lecturerid){
			String lecturerName="No Result found";
			rst = db.executeQuery("SELECT lecturername FROM lecturers WHERE id=" + lecturerid);
			try {
				rst.first();
				lecturerName= rst.getString("lecturername");
			} catch (SQLException e) {
					
				e.printStackTrace();
			}
			finally{
				db.closeConnection();
			}
			return lecturerName;
		}
		
		
	
	//Returns lecture, lab or both depending on the type of room
	public String getRoomType(int roomid){
		rst = db.executeQuery("SELECT roomtype FROM lecturerooms WHERE id=" + roomid);
		try {
			rst.first();
			roomType= rst.getString("roomtype");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return roomType;
	}
	
	//Get the total number of lecture rooms and labs available
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
	
	//Get the number of students that a room can take
	public int getRoomCapacity(int roomid){
		rst = db.executeQuery("SELECT capacity FROM lecturerooms WHERE id=" + roomid);
		try {
			rst.first();
			roomCapacity= rst.getInt("capacity");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		
		return roomCapacity;
	}
	
	//Get the total number of staffs or lecturers
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
	
	//Get the total number of modules in the DB
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
	
	//Return the number of students registered in a module
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
	
	/**Returns if module is lecture or lab based on how it is added to the db.
	 * A module with both lab and lecture hours are added twice, one as lecture and the other as lab
	 * @param moduleid
	 * @return
	 */
	
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
	//Get number of students that registered in a module
	public int getModuleSize(int moduleid){
		rst = db.executeQuery("SELECT numstudents FROM courses WHERE id=" + moduleid);
		int modulesize=0;
		try {
			rst.first();
			modulesize= rst.getInt("numstudents");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return modulesize;
	}
	//Get the number of weekly lecture hours for a module
	public int getLectureHoursPerWeek(int moduleid, String moduleType){
		int lecturehours = 0;
		rst = db.executeQuery("SELECT lecturehours FROM courses WHERE id=" + moduleid);
		try {
			rst.first();
			lecturehours= rst.getInt("lecturehours");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		
		return lecturehours;
	}
	
	//Get the number of weekly lab hours for a module
		public int getLabHoursPerWeek(int moduleid){
			int labhours = 0;
			rst = db.executeQuery("SELECT labhours FROM courses WHERE id=" + moduleid);
			try {
				rst.first();
				labhours= rst.getInt("labhours");
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			finally{
				db.closeConnection();
			}
			
			return labhours;
		}
		
	//Get Courses for display in a select input for course allocation to lecturer
	public String displayCourses(){
		
		modules= "";
		rst = db.executeQuery("SELECT * FROM courses");
		try {
			while(rst.next()){
				modules += "<option value=\"" + rst.getInt("id") +"\">" + rst.getString("coursecode") + "(" + rst.getString("coursetype") + ")" + "</option>";
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
	
	//Transform time genotype to day phenotype
	public String getDayOfWeek(int timeslot){
		dayOfWeek = "INVALID";
		if(timeslot >=1 && timeslot <=8){
			dayOfWeek = "MON";
		}
		else if(timeslot >=9 && timeslot <=16){
			dayOfWeek = "TUE";	
		}
		else if(timeslot >=17 && timeslot <=24){
			dayOfWeek = "WED";	
		}
		else if(timeslot >=25 && timeslot <=32){
			dayOfWeek = "THU";
		}
		else if(timeslot >=33 && timeslot <=40){
			dayOfWeek = "FRI";	
		}
		return dayOfWeek;
	}
	
	//Transform time gene to time Phenotype
	public int getTimeOfDay(int timeslot){
		int time = 9,mod=0;
		mod=timeslot % 8;
		switch(mod){
		case 1: time=9;  break;
		case 2:	time=10; break;
		case 3:	time=11; break;
		case 4:	time=12; break;
		case 5:	time=13; break;
		case 6: time=14; break;
		case 7:	time=15; break;
		case 0: time=16; break;
		}
		return time;
	}
	
	//Transform 24-hour time to 12-hour time
	public String get12HourTime(int timeslot){
		String time = "";
		switch(timeslot){
		case 9:  time="9AM";  break;
		case 10: time="10AM"; break;
		case 11: time="11AM"; break;
		case 12: time="12NOON"; break;
		case 13: time="1PM"; break;
		case 14: time="2PM"; break;
		case 15: time="3PM"; break;
		case 16: time="4PM"; break;
		case 17: time="5PM"; break;
		}
		return time;
	}

	//Returns course code,Room,day,time and lecturer for a lecture or lab
	public String mapEventGeneToPhenotype(int time,int room,int module){
		String phenotype = "";
		phenotype += this.getModuleCode(module) + "(" + this.getModuleType(module) + ")" + "<br/>";
		phenotype+= this.getRoomName(room) + "<br/>";
		//phenotype+= this.getDayOfWeek(time) + "(" + this.get12HourTime(this.getTimeOfDay(time)) + ")<br/>";
		phenotype+= this.getModuleLecturers(module) +"<br/>";
		
		return phenotype;
	}
	//Returns all lecture schedules for a particular day
	public String getDailySchedule(int day, int[][][] population, int chromosome, int[] rooms, int[] modules){
		this.dailySchedule="";
		switch(day){
			case 1: //Monday Timeslots 1-8
				this.dailySchedule="<tr><td><b>MON</b></td>";
				for(int b=0;b <8;b++){
					this.dailySchedule+= "<td>";
					for(int a=0;a <rooms.length;a++){
						if(population[chromosome][a][b]!=0){
							this.dailySchedule+=  this.mapEventGeneToPhenotype(b+1, rooms[a], population[chromosome][a][b]) + "<hr/>";
						}
					}
					this.dailySchedule+= "</td>";
				}
				dailySchedule +="</tr>";
				break;
			case 2: //Tuesday Timeslots 9-16
				this.dailySchedule="<tr><td><b>TUE</b></td>";
				for(int b=8;b <16;b++){
					this.dailySchedule+= "<td>";
					for(int a=0;a <rooms.length;a++){
						if(population[chromosome][a][b]!=0){
							this.dailySchedule+=  this.mapEventGeneToPhenotype(b+1, rooms[a], population[chromosome][a][b]) + "<hr/>";
						}
					}
					this.dailySchedule+= "</td>";
				}
				dailySchedule +="</tr>";
				
				break;
			case 3: //Wednesday Timeslots 17-24
				this.dailySchedule="<tr><td><b>WED</b></td>";
				for(int b=16;b <24;b++){
					this.dailySchedule+= "<td>";
					for(int a=0;a <rooms.length;a++){
						if(population[chromosome][a][b]!=0){
							this.dailySchedule+=  this.mapEventGeneToPhenotype(b+1, rooms[a], population[chromosome][a][b]) + "<hr/>";
						}
					}
					this.dailySchedule+= "</td>";
				}
				dailySchedule +="</tr>";
				
				break;
			case 4: //Thursdays Timeslots 25 - 32
				this.dailySchedule="<tr><td><b>THUR</b></td>";
				for(int b=24;b <32;b++){
					this.dailySchedule+= "<td>";
					for(int a=0;a <rooms.length;a++){
						if(population[chromosome][a][b]!=0){
							this.dailySchedule+=  this.mapEventGeneToPhenotype(b+1, rooms[a], population[chromosome][a][b]) + "<hr/>";
						}
					}
					this.dailySchedule+= "</td>";
				}
				dailySchedule +="</tr>";
				
				break;
			case 5://Friday Timeslots 33-40
				this.dailySchedule="<tr><td><b>FRI</b></td>";
				for(int b=32;b <40;b++){
					this.dailySchedule+= "<td>";
					for(int a=0;a <rooms.length;a++){
						if(population[chromosome][a][b]!=0){
							this.dailySchedule+=  this.mapEventGeneToPhenotype(b+1, rooms[a], population[chromosome][a][b]) + "<hr/>";
						}
					}
					this.dailySchedule+= "</td>";
				}
				dailySchedule +="</tr>";
				
				break;
			
		}
		return this.dailySchedule;
	}
}