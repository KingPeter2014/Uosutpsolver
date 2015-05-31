package utpsolver;

import java.sql.SQLException;

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
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return message;
		
	}

}
