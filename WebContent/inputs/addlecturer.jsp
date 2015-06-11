<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="../style.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Lecturers</title>
</head>
<body>
<div id="maincontent">
	<fieldset><legend>Add New Module</legend>
		<form action="/utpsolver/AddLecturerServlet" method="POST">
			<table width="60%">
				<tr>
					<td>Lecturer Name<em>*</em></td>
					<td><input type="text" name="name" id="name" placeholder=" Eg. D.C Jones"/></td>
				</tr>
				
				
				<tr>
				<td><label>Employment Status&nbsp;<em>*</em></label></td>
				<td><select name="status" id="status"><option value="0">Select</option><option value="fulltime">Full Time</option><option value="partime">Part Time</option></select></td>
				</tr>
				
				
				
				<tr>
				<td><label>Home Department&nbsp;<em>*</em></label></td>
				<td><select name="department" id="department"><option value="0">Select</option><option value="dcs">Computer Science</option>
						<option value="iph">Physics</option>
						<option value="maths">Mathematics</option>
						<option value="mee">Mechanical</option>
						<option value="aero">Aerospace Engineering</option>
						<option value="acse">Automatic Control Systems</option>
						<option value="psch">Psychology</option>
						<option value="eee">Electrical/Electronic Engineering</option>
						<option value="faculty">Engineering Faculty</option>
						<option value="management">Management School</option>
						<option value="English">English Language Support</option>
						<option value="others">Other</option>
						</select></td>
				</tr>
				
				<tr>
					<td> <input type="submit" name="addlecturer" value="Add new Lecturer"/></td>
					<td> <input type="reset" /> </td> 

				</tr>
	</table>
	</form>
	</fieldset>
	</div>
</body>
</html>