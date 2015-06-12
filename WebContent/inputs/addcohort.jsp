<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Add new Cohort</title>
</head>
<body>

<div id="maincontent">
	<fieldset><legend>Add New Room</legend>
		<form action="/utpsolver/AddCohortServlet" method="POST">
			<table width="60%">
				<tr>
					<td>Cohort Name<em>*</em></td>
					<td><input type="text" name="cname" id="cname" placeholder=" Eg. B.Sc Computer Science"/></td>
				</tr>
				
				
				<tr>
				<td><label>Cohort Name&nbsp;<em>*</em></label></td>
				<td><select name="cohortname" id="cohortname"><option value="0">...</option><option value="bsccsc">Bsc. Computer Science</option><option value="bengcsc">B.Eng Computer Science</option><option value="both">Both</optio></select></td>
				</tr>
				
				<tr>
					<td>Number of Students<em>*</em></td>
					<td><input type="number" name="numstudent" id="numstudent" min="1" max="1000"  placeholder="How many students can it take?"/></td>
				</tr>
				<tr>
					<td>Starting Level of Study</td>
					<td><input type="number" name="level" id="level" min="1" max="10" placeholder="Eg. 2"/></td>
				</tr>
				<tr>
					<td>Number of Years</td>
					<td><input type="number" name="numyrs" id="numyrs" min="1" max="10" placeholder="Eg. 2"/></td>
				</tr>
				
				<tr>
					<td> <input type="submit" name="addcohort" value="Create Cohort"/></td>
					<td> <input type="reset" /> </td> 

				</tr>
	</table>
	</form>
	</fieldset>
	</div>
</body>
</html>