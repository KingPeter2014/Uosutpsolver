<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="utpsolver.ReadInputs"%>
 <%@page import="java.util.*" %>
 <%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Existing Constraints</title>
</head>
<body>
<% 
	ReadInputs read = new ReadInputs();
	String availabilities = read.getPartimeLecturerAvailabilities();
	String special = read.getSpecialConstraintSettings();

%>
<div>
	<fieldset><legend>Part time Lecturer Availabilities</legend>
	<table width="60%"><tr><th>S/N</th><th>Lecturer</th><th>Day</th><th>Start Time</th><th>End Time</th></tr>
	<% out.print(availabilities); %>
	</table>
	</fieldset>
</div>

<div>
<fieldset><legend>Special Room Requirements</legend>
	<table width="60%"><tr><th>S/N</th><th>Module</th><th>Room</th><th>Day</th><th>Start Time</th><th>End Time</th></tr>
	<% out.print(special); %>
	</table>
	</fieldset>

</div>
</body>
</html>