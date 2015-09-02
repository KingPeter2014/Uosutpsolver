<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="../style.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Add Module</title>
</head>
<body>
<div id="maincontent">
	<fieldset><legend>Add New Module</legend>
		<form action="/utpsolver/AddModuleServlet" method="POST">
			<table width="60%">
				<tr>
					<td>Module Code<em>*</em></td>
					<td><input type="text" name="code" id="code" placeholder=" Eg. COM2008"/></td>
				</tr>
				<tr>
					<td>Module Title<em>*</em></td>
					<td><input type="text" name="title" id="title" placeholder="Eg. Web Intelligence"/></td>
				</tr>
				
				<tr>
				<td><label>Module Category&nbsp;<em>*</em></label></td>
				<td><select name="category" id="category"><option value="0">Select</option><option value="core">Core Module</option><option value="elective">Elective</option><option value="both">Both</optio></select></td>
				</tr>
				<tr>
				<td><label>Type&nbsp;<em>*</em></label></td>
				<td><select name="type" id="type"><option value="0">Select</option><option value="lecture">Lectures Only</option><option value="lab">Laboratory Only</option><option value="both">Both</optio></select></td>
				</tr>
				<tr>
				<tr>
					<td>Lecture Hours per week<em>*</em></td>
					<td><input type="number" name="lecturehour" id="lecturehour" value="1" min="0" max="10"  placeholder=""/></td>
				</tr>
				<tr>
					<td>Laboratory Hours per week</td>
					<td><input type="number" name="labhour" id="labhour" value="0" min="0" max="10"  placeholder=""/></td>
				</tr>
				
				
				<tr>
					<td>Number of Students<em>*</em></td>
					<td><input type="number" name="numstudents" id="numstudent" value = "40" min="1" max="1000"  placeholder="How many students can it take?"/></td>
				</tr>
				<tr>
					<td>Level Offered</td>
					<td><input type="number" name="level" id="level" min="1" max="10" placeholder="Eg. 2"/></td>
				</tr>
				
				<tr>
				<td><label>Host Department&nbsp;<em>*</em></label></td>
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
					<td> <input type="submit" name="addmodule" value="Add new Module"/></td>
					<td> <input type="reset" /> </td> 

				</tr>
	</table>
	</form>
	</fieldset>
	</div>
</body>
</html>