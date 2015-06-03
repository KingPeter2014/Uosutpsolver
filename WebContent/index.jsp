<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="utpsolver.ReadInputs"%>
 <%@page import="java.util.*" %>
 <%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>UoSTimetabler</title>
<link rel="stylesheet" type="text/css" href="style.css">
 
  <meta http-equiv="keywords" content="GA,UTP">
  <meta http-equiv="description" content="University Timetabling Problem Solver">
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<body>
<div class="container">
		<div id="menubar"> 
			<li><a href="index.jsp"> Home</a></li>
			<li> <a href="inputs/rooms.jsp"> Rooms</a></li>
			<li> <a href="inputs/lecturers.jsp">Lecturers</a></li>
			<li> <a href="inputs/modules.jsp"> Modules</a> </li>
			<li> <a href="inputs/classes.jsp"> Cohort</a> </li>
			<li> <a href="settings.jsp"> GA Settings</a> </li>
			<li> <a href="constraints.jsp"> Constraints</a> </li>
		</div>
<div>
<button>Generate Timetable</button>
</div>

<fieldset><legend>Run GA</legend>
<button>Generate Timetable</button>
<% 
ReadInputs read = new ReadInputs();
	String test = "Time Conversions:" +read.get12HourTime(15);
	out.println(test + ",");
	int timeslots = 40;
	int [] roomids=read.getRoomIds();
	int[] moduleIdsArray = read.getModuleIds();
	int numModules = moduleIdsArray.length;
	int i=0;
	out.println("<br/>Module IDs:");
	while(i <numModules){
		
		out.println(moduleIdsArray[i] + ",");
		
		i=i+1;
	}
		out.println("<br/>Rooms IDs:");
	i=0;
	int numRooms = roomids.length;
	while(i <numRooms){
		
		out.println(roomids[i] + ",");
		
		i=i+1;
	}
	out.println("<br/><hr/>Chromosome Structure<hr/>Time slots:=>");
	for(int a =1; a <= timeslots; a++){
		out.println(a + "&nbsp&nbsp");
		
	}
	out.println("<br/>Room IDs:<br/>");
	for(int b=0; b <numRooms;b++){
		out.println("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp" + roomids[b] + "<br/>");

	}

%>
</fieldset>
</div>
</body>
</html>