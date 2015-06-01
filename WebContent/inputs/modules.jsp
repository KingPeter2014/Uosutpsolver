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
<title>Modules/Courses</title>
</head>
<body>
<div class="container">
		<div id="menubar">
			<li> <a href="../index.jsp">Home</a></li> 
			<li><a href="addmodule.jsp">New Module</a></li>
			<li><a href="courseallocation.jsp"> Module Allocation</a></li>
			
		</div>
<% 
	ReadInputs read = new ReadInputs();
 	String modules = read.getModules();
 
%>
<br/>
<fieldset><legend>Listing Modules</legend>
	<table width="100%"> 
		<tr>
			<th> id</th><th>Course Code</th><th>Title</th><th>Level of Study</th><th>Actions</th>
		</tr>
		<% 
			
			out.println(modules);
		%>

	</table>
</fieldset>
</div>
</body>
</html>