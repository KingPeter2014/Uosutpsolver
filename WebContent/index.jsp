<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="utpsolver.ReadInputs"%>
    <%@page import="utpsolver.Chromosomes"%>
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
	Chromosomes cr = new Chromosomes();
	String test = "Fitness test for Room Size:" + cr.getFitnessOnAContraint(1);
	out.println(test);
	
	int timeslots = 40;
	int [][][] chromo = cr.chromosomes;
	int[] moduleIdsArray = read.getModuleIds();
	int numModules = moduleIdsArray.length;
	int i=0;
	
	
	out.println("<br/><hr/>Chromosome Structure[1]<hr/>");
	for(int b=0; b< Chromosomes.roomCount; b++){
		for(int a =0; a < timeslots; a++)
			out.println(chromo[1][b][a] + "&nbsp&nbsp&nbsp");
		out.println("<br/>");
		
	}
	out.println("<br/><hr/>Chromosome Structure[19]<hr/>");
	for(int b=0; b< Chromosomes.roomCount; b++){
		for(int a =0; a < timeslots; a++)
			out.println(chromo[19][b][a] + "&nbsp&nbsp&nbsp");
		out.println("<br/>");
		
	}
	
	
%>
</fieldset>
</div>
</body>
</html>