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
	private List<Integer>  lecturerids=new ArrayList<Integer>();
	private List<Integer> moduleids=new ArrayList<Integer>();
	private List<Integer> moduleAllocationids=new ArrayList<Integer>();
	private List<Integer> lecturerAllocationids=new ArrayList<Integer>();
	private String dailySchedule="";
	
	int[] idsArray,lecturerIds ;
	String message="",roomName,roomType="",rooms="",moduleType="",cohorts="",modules="",
			lecturers="",courseallocations="";
	//Equivalence of Database data
	public static int [][] cohortDB=null;//nx3,0=cohortid,1=start_level,2=number of years
	public static int [][] modulesDB = null;//nx3,0=moduleid,1= numstudents,2=lab/lecturehours
	public static int [][] courseAllocationsDB = null;//nx2,0=lecturerid,1=courseid
	public static int [][] lectureroomsDB = null;//nx2,0=roomid,1=roomcapacity
	public static String [] roomTypes,moduleTypes;
	public static int [][] lecturerAvailabilitiesDB = null;//nx4,0=lecturerid,1=day,2=startime,3=endtime
	public static int [][] modulesInCohort = null;//nx3,0=cohortid,1=courseid,2=level
	public static int [][] specialmoduleConstraints;//nx5,0=moduleid,1=roomid,2=day,3=startime,4=endtime
	String dayOfWeek = "";
	DBConnection db = null;
	ResultSet rst = null;
	PreparedStatement pst = null;
	
	public ReadInputs(){
		db = new DBConnection();
		
		//Initialise all arrays that will be used to cache the DB while algorithm runs.
		roomCount = this.getRoomCount();
		lectureroomsDB = new int[roomCount][2];
		ReadInputs.roomTypes = this.getRoomTypesArray();
		moduleTypes = this.getModuleTypesArray();
		modulesDB = new int[moduleTypes.length][3];
		int [] c = this.getAllCohortIds();
		cohortDB = new int[c.length][3];
		int [] d = this.getModuleIdsFromCourseAllocationTable();
		courseAllocationsDB = new int[d.length][2];
		lecturerAvailabilitiesDB = new int[this.getLecturerAvailabilityCount()][4];
		modulesInCohort= new int[this.getModulesInCohortCount()][3];
		int [] specialStart = this.getDaysForSpecialConstraintModules();
		specialmoduleConstraints = new int[specialStart.length][5];
		
		//Read Database Tables into the initialised arrays
		this.getCohortTableFromDB();
		this.getCourseTableFromDB();
		this.getCourseAllocationTableFromDB();
		this.getLectureRoomsTableFromDB();
		this.getAllLecturerIds();
		this.getPartTimeLecturerAvailabilitiesTableFromDB();
		this.getModulesInCohortsTableFromDB();
		this.getSpecialModuleConstraintsTableFromDB();
	}
	
	//Read Course allocation table to courseAllocationsDB Array
	private void getLectureRoomsTableFromDB(){
		rst = db.executeQuery("SELECT * FROM lecturerooms");
		int count=0;
		try {
			while(rst.next()){
				lectureroomsDB[count][0]=rst.getInt("id");
				lectureroomsDB[count][1]=rst.getInt("capacity");
				count+=1;
			}
		}
		catch (SQLException e) {	
			e.printStackTrace();
			message+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
	}
	//Read Part time lecturer availability table to lecturerAvailabilitiesDB Array
	private void getPartTimeLecturerAvailabilitiesTableFromDB(){
		rst = db.executeQuery("SELECT * FROM lecturer_availabilites");
		int count=0;
		try {
			while(rst.next()){
				lecturerAvailabilitiesDB[count][0]=rst.getInt("lecturer_id");
				lecturerAvailabilitiesDB[count][1]=rst.getInt("day");
				lecturerAvailabilitiesDB[count][2]=rst.getInt("start_time");
				lecturerAvailabilitiesDB[count][3]=rst.getInt("end_time");
				count+=1;
			}
		}
		catch (SQLException e) {	
			e.printStackTrace();
			message+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
	}
	//Reads special module constraints from DB into array
	private void getSpecialModuleConstraintsTableFromDB(){
		rst = db.executeQuery("SELECT * FROM special_module_constraints");
		int count=0;
		try {
			while(rst.next()){
				specialmoduleConstraints[count][0]=rst.getInt("module_id");
				specialmoduleConstraints[count][1]=rst.getInt("room_id");
				specialmoduleConstraints[count][2]=rst.getInt("day");
				specialmoduleConstraints[count][3]=rst.getInt("start_time");
				specialmoduleConstraints[count][4]=rst.getInt("end_time");
				count+=1;
			}
		}
		catch (SQLException e) {	
			e.printStackTrace();
			message+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
	}
	//Read Modules in Cohorts table to modulesInCohort Array
	private void getModulesInCohortsTableFromDB(){
		rst = db.executeQuery("SELECT * FROM modules_in_cohort");
		int count=0;
		try {
			while(rst.next()){
				modulesInCohort[count][0]=rst.getInt("cohort_id");
				modulesInCohort[count][1]=rst.getInt("course_id");
				modulesInCohort[count][2]=rst.getInt("level");
				count+=1;
			}
		}
		catch (SQLException e) {	
			e.printStackTrace();
			message+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
	}
	//Read lecturerooms table to courseAllocationsDB Array
	private void getCourseAllocationTableFromDB(){
		rst = db.executeQuery("SELECT * FROM course_allocations");
		int count=0;
		try {
			while(rst.next()){
				courseAllocationsDB[count][0]=rst.getInt("lecturer_id");
				courseAllocationsDB[count][1]=rst.getInt("course_id");
				count+=1;
			}
		}
		catch (SQLException e) {	
			e.printStackTrace();
			message+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
	}
	//Read Courses/Modules table to modulesDB Array
	private void getCourseTableFromDB(){
		rst = db.executeQuery("SELECT * FROM courses");
		int count=0;
		try {
			while(rst.next()){
				modulesDB[count][0]=rst.getInt("id");
				modulesDB[count][1]=rst.getInt("numstudents");
				if(rst.getString("coursetype").equals("lecture"))
				modulesDB[count][2]=rst.getInt("lecturehours");
				else
					modulesDB[count][2]=rst.getInt("labhours");
				count+=1;
			}
		}
		catch (SQLException e) {	
			e.printStackTrace();
			message+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
	}
	//Read CourseAllocations table to cohortDB Array
	private void getCohortTableFromDB(){
		rst = db.executeQuery("SELECT * FROM cohorts");
		int count=0;
		try {
			while(rst.next()){
				cohortDB[count][0]=rst.getInt("id");
				cohortDB[count][1]=rst.getInt("level_of_study");
				cohortDB[count][2]=rst.getInt("number_of_years");
				count+=1;
			}
		}
		catch (SQLException e) {	
			e.printStackTrace();
			message+=e.getMessage();
		}
		finally{
			db.closeConnection();
		}
	}
	//Get list of all the room ids for generating chromosome
	public int[] getRoomIds(){
		int l =this.lectureroomsDB.length;
		int [] rmids = new int[l];
		for(int i=0;i <l;i++){
			rmids[i] = lectureroomsDB[i][0];
		}
		return rmids;
	}
	
	//Get lecturer ids from lecturers' Table
	public int[] getLecturerIds(){
		return this.lecturerIds;
	}
	//Get lecturer ids from lecturers' Table
	private void getAllLecturerIds(){
		List<Integer>  lecturerids=new ArrayList<Integer>();
		rst = db.executeQuery("SELECT * FROM lecturers");
		try {
			while(rst.next()){
				lecturerids.add(rst.getInt("id"));
			}
			}
			catch (SQLException e) {
				
				e.printStackTrace();
				message+=e.getMessage();
			}
			finally{
				db.closeConnection();
			}
		
		this.lecturerIds= convertIntegerListToIntegerArray(lecturerids);
	}
	//Returns a string array of the module types corresponding to each module
	public String[] getModuleTypeArray(){
		return ReadInputs.moduleTypes;
	}
	private String[] getModuleTypesArray(){
		List<String>  moduleTypeArray = new ArrayList<String>();
		rst = db.executeQuery("SELECT coursetype FROM courses");
		try {
			while(rst.next()){
				moduleTypeArray.add(rst.getString("coursetype"));
			}
			}
			catch (SQLException e) {
				
				e.printStackTrace();
				message+=e.getMessage();
			}
			finally{
				db.closeConnection();
			}
		
		this.moduleTypes= convertStringListToStringArray(moduleTypeArray);
		return this.moduleTypes;
	}
	//Returns a string array of room types corresponding to each room
	private String[] getRoomTypesArray(){
		List<String>  roomTypeArray = new ArrayList<String>();
		rst = db.executeQuery("SELECT roomtype FROM lecturerooms");
		try {
			while(rst.next()){
				roomTypeArray.add(rst.getString("roomtype"));
			}
			}
			catch (SQLException e) {
				
				e.printStackTrace();
				message+=e.getMessage();
			}
			finally{
				db.closeConnection();
			}
			
			ReadInputs.roomTypes= convertStringListToStringArray(roomTypeArray);
			return ReadInputs.roomTypes;
		}
	public String[] getRoomTypeArray(){
				return ReadInputs.roomTypes;
	}
	//Confirm if an event belongs to a lecturer
	public boolean confirmEventBelongsToLecturer(int event, int lecturer){
		boolean belongsToLecturer = false;
		/*
		rst = db.executeQuery("SELECT * FROM course_allocations WHERE course_id=" + event + " AND lecturer_id=" + lecturer);
		try {
				if(rst.first())	
					belongsToLecturer= true;
			
			
			}
			catch (SQLException e) {
				
				e.printStackTrace();
				message+=e.getMessage();
			}
			finally{
				db.closeConnection();
			}
		*/
		int l = ReadInputs.courseAllocationsDB.length;
		for(int i=0;i<l;i++){
			if(ReadInputs.courseAllocationsDB[i][0]==lecturer && ReadInputs.courseAllocationsDB[i][1]==event){
				belongsToLecturer= true;
				return belongsToLecturer;
			}

		}
		return belongsToLecturer;
	}
	
	//Confirm if an event belongs to a Cohort of a particular level of study
	public boolean confirmCohortEventAtALevelOfStudy(int module , int cohort, int level){
		boolean belongsToCohort = false;
		/*
		rst = db.executeQuery("SELECT * FROM modules_in_cohort WHERE course_id=" + module + " AND cohort_id=" + cohort + " AND level="+level);
		
		try {
				if(rst.first())	
					belongsToCohort= true;
			}
			catch (SQLException e) {
				
				e.printStackTrace();
				message+=e.getMessage();
			}
			finally{
				db.closeConnection();
			}
			*/
		int l = ReadInputs.modulesInCohort.length;
		for(int i=0;i<l;i++){
			if(ReadInputs.modulesInCohort[i][0]==cohort && ReadInputs.modulesInCohort[i][1]==module && ReadInputs.modulesInCohort[i][2]==level){
				belongsToCohort= true;
				return belongsToCohort;
			}

		}

		return belongsToCohort;
		
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
	//Converts an integer ArrayList to int Array
	public String[] convertStringListToStringArray(List<String> list){
		String[] stringArray = new String[list.size()];
		Iterator<String> iter = list.iterator();
		int i=0;
		while(iter.hasNext()){
			stringArray[i] = iter.next().toString();
			i=i+1;
		}
			
		return  stringArray;
	}
	//Get ids of part time lecturers only
	public int[] getPartimeLecturerIDs(){
		List<Integer> partimeids=new ArrayList<Integer>();
		rst = db.executeQuery("SELECT id FROM lecturers WHERE lecturer_type='partime'");
		try {
			while(rst.next()){
				partimeids.add(rst.getInt("id"));
				}
			}
		catch (SQLException e) {				
			e.printStackTrace();
			message+=e.getMessage();
			}
		finally{
				db.closeConnection();
			}
			return convertIntegerListToIntegerArray(partimeids);
	}
	// Get ids of all modules allocated to a particular lecturer
	public int [] getLecturerModules(int lecturer){
		List<Integer> lecturerModules=new ArrayList<Integer>();
		/*
		rst = db.executeQuery("SELECT course_id FROM course_allocations WHERE lecturer_id=" + lecturer);
		try {
			while(rst.next()){
				lecturerModules.add(rst.getInt("course_id"));
				}
			}
		catch (SQLException e) {				
			e.printStackTrace();
			message+=e.getMessage();
			}
		finally{
				db.closeConnection();
			}
		*/
		int l = ReadInputs.courseAllocationsDB.length;
		for(int i=0;i<l;i++){
			if( ReadInputs.courseAllocationsDB[i][0]==lecturer){
				lecturerModules.add(ReadInputs.courseAllocationsDB[i][1]);
				
			}

		}
			return convertIntegerListToIntegerArray(lecturerModules);
		
	}
	//Get start times for part time lecturers
	public int[] partimeLecturerStartTimes(int lecturer){
		List<Integer> startTimes=new ArrayList<Integer>();
		/*
		rst = db.executeQuery("SELECT start_time FROM lecturer_availabilites WHERE lecturer_id=" + lecturer);
		try {
			while(rst.next()){
				startTimes.add(rst.getInt("start_time"));
				}
			}
		catch (SQLException e) {				
			e.printStackTrace();
			message+=e.getMessage();
			}
		finally{
				db.closeConnection();
			}
		*/
		int l = ReadInputs.lecturerAvailabilitiesDB.length;
		for(int i=0;i<l;i++){
			if( ReadInputs.lecturerAvailabilitiesDB[i][0]==lecturer){
				startTimes.add(ReadInputs.lecturerAvailabilitiesDB[i][2]);
				
			}

		}
			return convertIntegerListToIntegerArray(startTimes);
		
	}
	//Get the total number of lecture rooms and labs available
	private int getLecturerAvailabilityCount(){
			int numava=0;
			rst = db.executeQuery("SELECT count(id) AS numavailable FROM lecturer_availabilites");
			try {
				rst.first();
				numava= rst.getInt("numavailable");
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			finally{
				db.closeConnection();
			}
			
			return numava;
		}
		
	//Get End times for part time lecturers
	public int[] partimeLecturerEndTimes(int lecturer){
		List<Integer> endTimes=new ArrayList<Integer>();
		/**
		rst = db.executeQuery("SELECT end_time FROM lecturer_availabilites WHERE lecturer_id=" + lecturer);
		try {
			while(rst.next()){
				endTimes.add(rst.getInt("end_time"));
				}
			}
		catch (SQLException e) {				
			e.printStackTrace();
			message+=e.getMessage();
			}
		finally{
			db.closeConnection();
		}
		**/
		int l = ReadInputs.lecturerAvailabilitiesDB.length;
		for(int i=0;i<l;i++){
			if( ReadInputs.lecturerAvailabilitiesDB[i][0]==lecturer){
				endTimes.add(ReadInputs.lecturerAvailabilitiesDB[i][3]);
				
			}

		}
		
		return convertIntegerListToIntegerArray(endTimes);
			
	}
	//Get corresponding availablibilty days for part time lecturers
	public int[] partimeLecturerDays(int lecturer){
			List<Integer> days=new ArrayList<Integer>();
			/**
			rst = db.executeQuery("SELECT day FROM lecturer_availabilites WHERE lecturer_id=" + lecturer);
			try {
				while(rst.next()){
					days.add(rst.getInt("day"));
					}
				}
			catch (SQLException e) {				
				e.printStackTrace();
				message+=e.getMessage();
				}
			finally{
				db.closeConnection();
			}
			**/
			int l = ReadInputs.lecturerAvailabilitiesDB.length;
			for(int i=0;i<l;i++){
				if( ReadInputs.lecturerAvailabilitiesDB[i][0]==lecturer){
					days.add(ReadInputs.lecturerAvailabilitiesDB[i][1]);
					
				}

			}
			return convertIntegerListToIntegerArray(days);
				
		}
		
	//Get Module ids for generating chromosome
	public int[] getModuleIds(){
		/*
		List<Integer> moduleids=new ArrayList<Integer>();
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
		*/
		int l =ReadInputs.modulesDB.length;
		int [] courseids = new int[l];
		for(int i=0;i <l;i++){
			courseids[i] = modulesDB[i][0];
		}
		return courseids;
		
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
	//Get array of the IDs of Cohorts registered in the system
	//Get array of the IDs of Cohorts registered in the system
	private int[] getAllCohortIds(){
			
			List<Integer> cohortids=new ArrayList<Integer>();
			rst = db.executeQuery("SELECT * FROM cohorts");
			try {
				while(rst.next()){
					cohortids.add(rst.getInt("id"));
				}
				}
				catch (SQLException e) {
					
					e.printStackTrace();
					message+=e.getMessage();
				}
				finally{
					db.closeConnection();
				}
			
			return convertIntegerListToIntegerArray(cohortids);
		}
	//Get Cohort ids from local array Table
	public int[] getCohortIds(){
		
		int l =ReadInputs.cohortDB.length;
		int [] cohortids = new int[l];
		for(int i=0;i <l;i++){
			cohortids[i] = cohortDB[i][0];
		}
		return cohortids;

	}
	//Get the total number of moules in cohort asignnments in the DB
	private int getModulesInCohortCount(){
		int modeincohort=0;
		rst = db.executeQuery("SELECT count(id) AS numodules FROM modules_in_cohort");
		try {
			rst.first();
			modeincohort= rst.getInt("numodules");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return modeincohort;
	}

	//Get the starting level for a cohort
	public int getCohortStartingLevel(int cohortid){
		int start=0;
		/*
		rst = db.executeQuery("SELECT level_of_study FROM cohorts WHERE id=" + cohortid);
		try {
			rst.first();
			start= rst.getInt("level_of_study");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		*/
		int l =ReadInputs.cohortDB.length;
		int [] cohortids = new int[l];
		for(int i=0;i <l;i++){
			if(cohortDB[i][0]==cohortid){
				start = cohortDB[i][1];
				return start;
			}
		}
		return start;
	}
	//Get the number of years to finish a degree for a Cohort
	public int getNumberOfYearsToGraduate(int cohortid){
		int numyrs=0;
		int l =ReadInputs.cohortDB.length;
		int [] cohortids = new int[l];
		for(int i=0;i <l;i++){
			if(cohortDB[i][0]==cohortid){
				numyrs = cohortDB[i][2];
				return numyrs;
			}
		}
		return numyrs;
		
	}
	//Gets cohorts for listing, editing and deleting purposes
	public String getCohorts(){
		cohorts = "";
		
		rst = db.executeQuery("SELECT * FROM cohorts");
		
		try {
			while(rst.next()){
				
				cohorts += "<tr><td>" + rst.getInt("id") +"</td><td>" + rst.getString("cohortname") + "</td><td>" + rst.getString("numstudents")
						+ "</td><td>" +rst.getInt("level_of_study") + "</td><td>" +  rst.getInt("number_of_years")+ "</td>"
						+ "<td><a href=\"editcohort.jsp?id=" +rst.getInt("id") + "\"> Edit</a>|" 
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
	
	//Gets the names of Cohorts to which a module has been assigned
	public String getModuleCohorts(){
			String lecturers = "";
			String query ="SELECT c.coursecode,c.coursetitle,c.coursetype,c.level, ch.cohortname,ca.id FROM courses c INNER JOIN modules_in_cohort ca on c.id = ca.course_id INNER JOIN cohorts ch on ca.cohort_id = ch.id";
			
			rst = db.executeQuery(query);
			try {
				while(rst.next()){
					
					lecturers += "<tr><td>" + rst.getInt("id") +  "</td><td>" + rst.getString("cohortname") +"</td><td>" + rst.getString("coursecode") +"(" + rst.getString("coursetype")+")</td><td>" + 
					rst.getString("coursetitle") +"</td><td>" + rst.getInt("level")+ "</td>"+
							 "<td><a href=\"editmodulecohort.jsp?id=" + rst.getInt("id") +"\"> Edit</a>|" 
											+ "<a href=\"delete.jsp?id=" + rst.getInt("id") + "&what=modulecohort\"> Delete</a></td></tr>";
					
				}
			} catch (SQLException e) {
							e.printStackTrace();
			}
			finally{
				db.closeConnection();
			}			if(lecturers.equals(""))
				lecturers="<tr><td colspan=\"4\"NONE</td></tr>";
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
	//Returns the ids of the cohorts to which a module belongs
	public int[] getModuleCohort(int moduleid){
		List<Integer> cohortids=new ArrayList<Integer>();
		/**
		rst = db.executeQuery("SELECT cohort_id FROM modules_in_cohort WHERE course_id=" + moduleid);
		
		try {
			while(rst.next()){
				cohortids.add(rst.getInt("cohort_id"));
			}
			}
			catch (SQLException e) {
				
				e.printStackTrace();
				message+=e.getMessage();
			}
			finally{
				db.closeConnection();
			}
			**/
		int l =ReadInputs.modulesInCohort.length;
		
		for(int i=0;i <l;i++){
			if(modulesInCohort[i][1]==moduleid){
				cohortids.add(modulesInCohort[i][0]);
				
			}
		}
		return convertIntegerListToIntegerArray(cohortids);

		
	}
	
	//Returns the level  in which a module is offered
		public int getModuleCohortLevel(int moduleid,int cohortid){
			int level=0;
			/*
			rst = db.executeQuery("SELECT level FROM modules_in_cohort WHERE course_id=" + moduleid + " AND cohort_id=" + cohortid);
			
			try {
				rst.first();
					level=rst.getInt("level");
				
				}
				catch (SQLException e) {
					
					e.printStackTrace();
					message+=e.getMessage();
				}
				finally{
					db.closeConnection();
				}
				*/
			int l =ReadInputs.modulesInCohort.length;
			
			for(int i=0;i <l;i++){
				if(modulesInCohort[i][1]==moduleid && modulesInCohort[i][0]==cohortid){
					return modulesInCohort[i][2];//Returns level a module is studied by a cohort
				}
			}
			return level;

			
		}
	// Returns Name of Lecturer for mapping from genotype to phenotype
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
		String roomType="";
		/*
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
		*/
		for(int i=0;i<this.roomCount;i++){
			if(this.lectureroomsDB[i][0]==roomid)
			return	roomType= ReadInputs.roomTypes[i];
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
		/*
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
		*/
		for(int i=0;i<this.roomCount;i++){
			if(this.lectureroomsDB[i][0]==roomid)
			return	roomCapacity= lectureroomsDB[i][1];
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
		/*
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
		*/
		for(int i=0;i<this.moduleCount;i++){
			if(this.modulesDB[i][0]==moduleid)
			return	studentsInModule= modulesDB[i][1];
		}
		return studentsInModule;
	}
	
	/**Returns if module is lecture or lab based on how it is added to the db.
	 * A module with both lab and lecture hours are added twice, one as lecture and the other as lab
	 * @param moduleid
	 * @return
	 */
	
	public String getModuleType(int moduleid){
		moduleType="";
		//CONFIRM THIS ALGORITHM FROM TIMETABLE OUTPUT
		/**
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
		**/
		for(int i=0;i<this.modulesDB.length;i++){
			if(this.modulesDB[i][0]==moduleid)
			return	moduleType= ReadInputs.moduleTypes[i];
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
	
	//Get Courses for display in a select input for course allocation to lecturer
	public String displayRooms(){
			
			String rooms= "";
			rst = db.executeQuery("SELECT * FROM lecturerooms");
			try {
				while(rst.next()){
					rooms += "<option value=\"" + rst.getInt("id") +"\">" + rst.getString("roomname") + "(" + rst.getString("code") + ")" + "</option>";
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
	
	//Converts start times of partime lecturers to timeslots between 1 and 40
	public int[] getStartTimeGenesForPartTimeLecturers(int lecturer){
		int count=1;
		int [] startTimes = this.partimeLecturerStartTimes(lecturer);
		count= startTimes.length;
		int [] start = new int[count];
		int [] days = this.partimeLecturerDays(lecturer);
		for(int i=0;i<count;i++){
			start[i] = this.convertDayTimeToTimeGene(days[i], startTimes[i]);
		}
		return start;
		
	}
	//Converts End times of partime lecturers to timeslots between 1 and 40
	public int[] getEndTimeGenesForPartTimeLecturers(int lecturer){
		int count=1;
		int [] endTimes = this.partimeLecturerEndTimes(lecturer);
		count= endTimes.length;
		int [] endTime = new int[count];
		int [] days = this.partimeLecturerDays(lecturer);
		for(int i=0;i<count;i++){
			endTime[i] = this.convertDayTimeToTimeGene(days[i], endTimes[i]);
		}
		return endTime;		
	}
	public int[] convertDayTimeToTimeGene(int [] days,int []time){
		int count= days.length;
		int [] timeGene = new int[count];
		for(int i=0;i<count;i++){
			timeGene[i] = this.convertDayTimeToTimeGene(days[i], time[i]);
		}
	
		return timeGene;
	}
	//Returns an array of the start times for modules that must be held in a specific time and venue
	public int[] getStartTimeForSpecialConstraintModules(){
		List<Integer> startTimes=new ArrayList<Integer>();
		rst = db.executeQuery("SELECT * FROM special_module_constraints");
		try {
			while(rst.next()){
				startTimes.add(rst.getInt("start_time"));
				}
			}
		catch (SQLException e) {				
			e.printStackTrace();
			message+=e.getMessage();
			}
		finally{
				db.closeConnection();
			}
			return convertIntegerListToIntegerArray(startTimes);
		
	}
	
	//Returns an array of the end times for modules that must be held in a specific time and venue
	public int[] getEndTimeForSpecialConstraintModules(){
		List<Integer> endTimes=new ArrayList<Integer>();
		rst = db.executeQuery("SELECT * FROM special_module_constraints");
		try {
			while(rst.next()){
				endTimes.add(rst.getInt("end_time"));
				}
			}
		catch (SQLException e) {				
			e.printStackTrace();
			message+=e.getMessage();
			}
		finally{
				db.closeConnection();
			}
			return convertIntegerListToIntegerArray(endTimes);
	}
	
	//Returns an array of the end times for modules that must be held in a specific time and venue
	public int[] getDaysForSpecialConstraintModules(){
		List<Integer> day=new ArrayList<Integer>();
		rst = db.executeQuery("SELECT * FROM special_module_constraints");
		try {
			while(rst.next()){
				day.add(rst.getInt("day"));
				}
			}
		catch (SQLException e) {				
			e.printStackTrace();
			message+=e.getMessage();
			}
		finally{
				db.closeConnection();
			}
			return convertIntegerListToIntegerArray(day);
		}

	//Get all modules with special constraint requirements
	public int [] getModulesWithSpecialConstraints(){
		List<Integer> moduleids=new ArrayList<Integer>();
		rst = db.executeQuery("SELECT * FROM special_module_constraints");
		try {
			while(rst.next()){
				moduleids.add(rst.getInt("module_id"));
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
	//Get all rooms allocated to corresponding modules with special room constraints
	public int [] getRoomsWithSpecialModuleConstraints(){
			List<Integer> roomids=new ArrayList<Integer>();
			rst = db.executeQuery("SELECT * FROM special_module_constraints");
			try {
				while(rst.next()){
					roomids.add(rst.getInt("room_id"));
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
	//Given day and time, return an integer between 1 and 40 to indicate time index on a Chromosome
	private int convertDayTimeToTimeGene(int day, int time){
		int timeslot=0;
			switch(day){
				case 1:
					
					switch(time){
					case 9:
						timeslot=1;break;
					case 10:
						timeslot=2;break;
					case 11:
						timeslot=3;break;
					case 12:
						timeslot=4;break;
					case 13:
						timeslot = 5;break;
					case 14:
						timeslot=6;break;
					case 15:
						timeslot=7;break;
					case 16:
						timeslot=8;break;
					case 17:
						timeslot=8;break;
					}
					break;
					
				case 2:
					switch(time){
					case 9:
						timeslot=9;break;
					case 10:
						timeslot=10;break;
					case 11:
						timeslot=11;break;
					case 12:
						timeslot=12;break;
					case 13:
						timeslot = 13;break;
					case 14:
						timeslot=14;break;
					case 15:
						timeslot=15;break;
					case 16:
						timeslot=16;break;
					case 17:
						timeslot=16;break;
					}
					break;
					
				case 3:
					switch(time){
					case 9:
						timeslot=17;break;
					case 10:
						timeslot=18;break;
					case 11:
						timeslot=19;break;
					case 12:
						timeslot=20;break;
					case 13:
						timeslot = 21;break;
					case 14:
						timeslot=22;break;
					case 15:
						timeslot=23;break;
					case 16:
						timeslot=24;break;
					case 17:
						timeslot=24;break;
					}
					break;
					
				case 4:
					switch(time){
					case 9:
						timeslot=25;break;
					case 10:
						timeslot=26;break;
					case 11:
						timeslot=27;break;
					case 12:
						timeslot=28;break;
					case 13:
						timeslot = 29;break;
					case 14:
						timeslot=30;break;
					case 15:
						timeslot=31;break;
					case 16:
						timeslot=32;break;
					case 17:
						timeslot=32;break;
					}
					break;
					
				case 5:
					switch(time){
					case 9:
						timeslot=33;break;
					case 10:
						timeslot=34;break;
					case 11:
						timeslot=35;break;
					case 12:
						timeslot=36;break;
					case 13:
						timeslot = 37;break;
					case 14:
						timeslot=38;break;
					case 15:
						timeslot=39;break;
					case 16:
						timeslot=40;break;
					case 17:
						timeslot=40;break;
					}
					break;
				default:	
			}
		
		return timeslot;
		
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