<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="utpsolver.ReadInputs"%>
 <%@page import="java.util.*" %>
 <%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="../style.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Course Allocation</title>
</head>
<body>
<div class="container">
		<div id="menubar">
			<li> <a href="../index.jsp">Home</a></li> 
			
		</div>
<div>
<% 
	ReadInputs read = new ReadInputs();
 	String cohorts = read.displayCohorts();
 	String courses = read.displayCourses();
 	String moduleCohorts = read.getModuleCohorts();
 
%>
<br/>
<fieldset><legend>Assign Module to Cohort</legend>
		<form action="/utpsolver/ModuleCohortServlet" method="POST">
			<table width="100%">
				<tr>
					<td>Module:<em>*</em></td>
					<td><select name="module" id="module"><option value="0">Select</option>
				<% out.println(courses); %>
						</select></td>
				</tr>
				
				
				<tr>
				<td><label>Assign To:&nbsp;<em>*</em></label></td>
				<td><select name="cohort" id="cohort"><option value="0">Select</option>
				<% out.println(cohorts); %>
						</select></td>
				</tr>
				<tr>
					<td>Level of Study</td>
					<td><input type="number" name="level" id="level" min="1" max="10" placeholder="Eg. 2"/></td>
				</tr>
				<tr>
					<td> <input type="submit" name="allocate" value="Assign to Cohort"/></td>
					<td> <input type="reset" /> </td> 

				</tr>
	</table>
	</form>
	</fieldset><br/>
	<fieldset><legend>Module-Cohort Assignments</legend>
	<table width="70%">
	<tr>
		<th>ID</th><th>Cohort</th><th>Module Code</th><th> Module Title</th><th>Level of Study</th><th>Actions</th>
	</tr>
	<% out.println(moduleCohorts); %>
	</table>
	
	</fieldset>
</div>
</div>
</body>
</html>