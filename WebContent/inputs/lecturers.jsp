<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@page import="utpsolver.ReadInputs"%>
 <%@page import="java.util.*" %>
 <%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="../style.css">
<title>Lecturers</title>
</head>
<body>
<div class="container">
		<div id="menubar">
			<li> <a href="../index.jsp">Home</a></li> 
			<li><a href="addlecturer.jsp"> Add Lecturer</a></li>
			<li><a href="allocatemodule.jsp"> Module Allocation</a></li>
			
		</div>
<div>
<% 
	ReadInputs read = new ReadInputs();
 	String rooms = read.getLecturers();
 
%>
<br/>
<fieldset><legend>Listing Rooms</legend>
	<table width="100%"> 
		<tr>
			<th> id</th><th>Name</th><th>Type</th><th>Department</th><th>Actions</th>
		</tr>
		<% 
			
			out.println(rooms);
		%>

	</table>
</fieldset>
</div>
</div>
</body>
</html>