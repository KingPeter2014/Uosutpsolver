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
		message=query;
		
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

}
