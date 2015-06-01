<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="utpsolver.ReadInputs"%>
 <%@page import="java.util.*" %>
 <%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
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
 	String lecturers = read.displayLecturers();
 	String courses = read.displayCourses();
 
%>
<br/>
<fieldset><legend>Add New Module</legend>
		<form action="/utpsolver/AllocateModuleServlet" method="POST">
			<table width="60%">
				<tr>
					<td>Module:<em>*</em></td>
					<td><select name="module" id="module"><option value="0">Select</option>
				<% out.println(courses); %>
						</select></td>
				</tr>
				
				
				
				
				
				<tr>
				<td><label>Assign To:&nbsp;<em>*</em></label></td>
				<td><select name="lecturer" id="lecturer"><option value="0">Select</option>
				<% out.println(lecturers); %>
						</select></td>
				</tr>
				
				<tr>
					<td> <input type="submit" name="allocate" value="Allocate Module"/></td>
					<td> <input type="reset" /> </td> 

				</tr>
	</table>
	</form>
	</fieldset>
</div>
</div>
</body>
</html>