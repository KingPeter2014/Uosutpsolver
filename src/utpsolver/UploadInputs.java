package utpsolver;



public class UploadInputs extends ReadInputs {
	private String message="";
	public UploadInputs(){
		super();
	}
	public String addCohort(String name, int numstudents, int level){
		message = "Has not updated anything yet";
		String query = "INSERT INTO cohorts(cohortname,numstudents,level_of_study) VALUES('" + name + "'," + numstudents +"," + level+")";
		message=query;
		try {
			db.updateQuery(query);
			message = "Class group, " + name + ", successfully added";
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		finally{
			db.closeConnection();
		}
		return message;
		
	}
	//Add new room
	public String addRoom(String name, String description,  String type,int capacity, double latitude,double longitude){
		message = "";
		String query = "INSERT INTO lecturerooms(code,roomname,roomtype,capacity,latitude"
				+ ",longitude) VALUES('" + name + "','" + description + "','" + type +"',"  + capacity + "," + latitude + "," + longitude +")";
		message=query;
		//return message;
		
		try {
			int count=db.updateQuery(query);
			if(count>0){
				message = "Room:, " + name + ", successfully added";
			}
		} catch (Exception e) {
			
			message += "Error: " + e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		return message;	
		
	}
	
	
	//Add module to db repeating it for lab and lecture components respectively
	public String addModule(String code, String title, String category,String type, int lecturehour, int labhour,
			int numstudents, int level, String department){
		String query = "INSERT INTO courses(coursecode,coursetitle,coursetype,numstudents,lecturehours"
				+ ",labhours,level,category,homedepartment) VALUES('" + code + "','" + title + "','" + 
				type +"'," + numstudents + "," + lecturehour + "," + labhour + "," + level+",'"+
				category +"','" + department +"')";
		if(type.equals("lecture") || type.equals("lab")){
			
			//message=query;
			try {
			int count =	db.updateQuery(query);
			if (count >0){
				message = "Module, " + code + ", successfully added";
			}
			else{
				message = "Unable to add new module";
				
			}
			} catch (Exception e) {
				
				message += e.getMessage();
			}
			finally{
				db.closeConnection();
			}
			return message;
		}
		//If type=both, insert lecture as one module, then lab as another 
		type="lecture";
		String query1 = "INSERT INTO courses(coursecode,coursetitle,coursetype,numstudents,lecturehours"
				+ ",labhours,level,category,homedepartment) VALUES('" + code + "','" + title + "','" + 
				type +"'," + numstudents + "," + lecturehour + "," + labhour + "," + level+",'"+
				category +"','" + department +"')";
		
		//message=query;
		try {
			db.updateQuery(query1);
			message += "Module, " + code + ", (Lecture) successfully added.</br>";
			
			type="lab";
			String query2 = "INSERT INTO courses(coursecode,coursetitle,coursetype,numstudents,lecturehours"
					+ ",labhours,level,category,homedepartment) VALUES('" + code + "','" + title + "','" + 
					type +"'," + numstudents + "," + lecturehour + "," + labhour + "," + level+",'"+
					category +"','" + department +"')";
			int count = db.updateQuery(query2);
			if(count >0)
				message += "Module, " + code + ", (Laboratory) successfully added";
			else{
				message= "Could not save the lab component of this course";
			}
		} catch (Exception e) {
			
			message += e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		
				
		return message;
	}
	
	//Add a new Lecturer
	
	public String addLecturer(String name, String status,  String department){
		message = "Has not updated anything yet";
		String query = "INSERT INTO lecturers(lecturername,lecturer_type,department) VALUES('" + name + "','" + status +"','" + department+"')";
		//message=query;
		
		try {
			int count=db.updateQuery(query);
			if(count>0){
				message = "Lecturer:, " + name + ", successfully added";
			}
		} catch (Exception e) {
			
			message += e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		return message;	
	}
	
	//Allocate a course to a lecturer
	public int courseAllocation(int module,int lecturer){
		int count=0;
		
		String query = "INSERT INTO course_allocations(lecturer_id,course_id) VALUES(" + lecturer + "," + module + ")";
		message=query;
		try {
			count=db.updateQuery(query);
			message = count + "";
		} catch (Exception e) {
			
			message += e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		return count;
	}
	
	//Assign a module to a cohort. The module becomes a core module for the cohort
	public int assignModuleToCohort(int module, int cohort, int level){
		int count =0;
		String query = "INSERT INTO modules_in_cohort(cohort_id,course_id,level) VALUES(" + cohort + "," + module + "," + level +  ")";
		//message=query;
		try {
			count=db.updateQuery(query);
			message = count + "";
		} catch (Exception e) {
			
			message += e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		
		return count;
	}
	//Register the days and times that a lecturer (Part time) is available over the week
	public int registerLecturerAvailability(int lecturer,int from, int to,int day){
		int count =0;
		String query = "INSERT INTO lecturer_availabilites(lecturer_id,day,start_time,end_time) VALUES(" + lecturer + "," + day + "," + from + "," + to+ ")";
		//message=query;
		try {
			count=db.updateQuery(query);
			message = count + "";
		} catch (Exception e) {
			
			message += e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		
		return count;
		
	}
	//Register special rooms where some special modules must take place
	public int registerSpecialModuleConstraint(int module,int room){
		int count =0;
		String query = "INSERT INTO special_module_constraints(module_id,room_id) VALUES(" + module + "," + room + ")";
		try {
			count=db.updateQuery(query);
			message = count + "";
		} catch (Exception e) {
				
			message += e.getMessage();
		}
		finally{
			db.closeConnection();
		}
		return count;
			
	}	

}
