<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="utpsolver.ReadInputs"%>
 <%@page import="java.util.*" %>
 <%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="style.css">
<title>GA Constraints</title>
</head>
<body>
<%
	session.setAttribute("lecturerConstraint", "");
	session.setAttribute("moduleConstraint", "");
	session.setAttribute("roomConstraint", "");
	ReadInputs read = new ReadInputs();
	String allocations = read.getCourseAllocations();
	String lecturers = read.displayLecturers();
	String courses = read.displayCourses();
	String rooms = read.displayRooms();

%>
<div class="maincontainer">
	<div id="menubar">
			<li> <a href="index.jsp">Home</a></li> 
			
	</div>
	<br/>
	<div class="left">
	<fieldset><legend>Part-Time Lecturer Constraints</legend>
	
	<form action="/utpsolver/LecturerAvailabilityServlet" method="post">
	<table>
	<tr>
				<td><label>Lecturer:&nbsp;<em>*</em></label></td>
				<td><select name="lecturer" id="lecturer"><option value="0">Select</option>
				<% out.println(lecturers); %>
					</select> is available</td>
						<td>from <select name="from">
						<%for(int a=9;a <=17;a++ ){
							out.println("<option value=\"" + a +  "\">" +a +"</option>");
							}
							%>
					</select> to
					 <select name="to">
						<%for(int a=10;a <=17;a++ ){
							out.println("<option value=\"" + a +  "\">" +a +"</option>");
							}
							%>
					</select>
					ON
					<select name="days">
						<option value="1"> Mondays</option>
						<option value="2"> Tuesdays</option>
						<option value="3"> Wednesdays</option>
						<option value="4"> Thursdays</option>
						<option value="5"> Fridays</option>
					</select>
				</td>
			</tr>
			<tr><td colspan="2"><input type="submit" name="lecturerconstraint" value="Submit Availability"></td></tr>
	</table>
	</form>
	</fieldset>
	</div>
	
	<div class="right">
	<fieldset><legend>Generate Timetable
	</legend> 
	
	<form action="/utpsolver/RunGA" method="post">
	<!--  	<input type="checkbox" name="excludelunchtime"> Exclude Lunch time (Between 1 - 2pm daily)<br/>
		<input type="checkbox" name="excludewednesdaynoon"> Exclude Wednesday afternoon(From 12:noon) <br/>
		-->
		<input type="Submit" value="Generate Timetable" name="runGA" id="runGA">
	</form>
	
	</fieldset>
	</div>
	
	<div class="left">
	<fieldset><legend>Special Module Constraints</legend>
	<form action="/utpsolver/SpecialModuleConstraint" method="post">
	<table>
		<tr>
				<td><label>Module:&nbsp;<em>*</em></label></td>
				<td><select name="module" id="module"><option value="0">Select</option>
				<% out.println(courses); %>
			</select>MUST TAKE PLACE IN
		</td>
		<td><label>Room:&nbsp;<em>*</em></label></td>
				<td><select name="room" id="room"><option value="0">Select</option>
				<% out.println(rooms); %>
			</select>		</td>
			<td>from <select name="from">
						<%for(int a=9;a <=17;a++ ){
							out.println("<option value=\"" + a +  "\">" +a +"</option>");
							}
							%>
					</select> to
					 <select name="to">
						<%for(int a=10;a <=17;a++ ){
							out.println("<option value=\"" + a +  "\">" +a +"</option>");
							}
							%>
					</select>
					ON
					<select name="days">
						<option value="1"> Mondays</option>
						<option value="2"> Tuesdays</option>
						<option value="3"> Wednesdays</option>
						<option value="4"> Thursdays</option>
						<option value="5"> Fridays</option>
					</select>
				</td>
		
		</tr>
		<tr><td colspan="3"><input type="submit" name="moduleconstraint" value="Submit Special module requirement"></td></tr>
		</table>
		</form>
	</fieldset>
	</div>
	<div class="right">
			
	</div>
</div>
</body>
</html>