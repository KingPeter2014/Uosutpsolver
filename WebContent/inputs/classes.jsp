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
<title>Class groups/Cohort</title>
</head>
<body>
<div class="container">
		<div id="menubar">
			<li> <a href="classes.jsp">Home</a></li> 
			<li><a href="addcohort.jsp"> Add Cohort</a></li>
			
		</div>
<div>
<% 
	ReadInputs read = new ReadInputs();
 	String cohorts = read.getCohorts();
 
%>
<fieldset><legend>Listing Cohorts</legend>
	<table width="100%"> 
		<tr>
			<th> id</th><th>Cohort Name</th><th>Number of Students</th><th>Level of Study</th><th>Actions</th>
		</tr>
		<% 
			
			out.println(cohorts);
		%>

	</table>
</fieldset>
</div>
</div>
</body>
</html>