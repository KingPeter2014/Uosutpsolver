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
</div>

<fieldset><legend>Randomly Generated Timetable</legend>

<% 
	ReadInputs read = new ReadInputs();
	Chromosomes cr = new Chromosomes();
	String test = "<br/><b>Fitness Unit Tests:</b><hr/>" + cr.getFitnessOnAContraint(1);
	//int[] allFitness = cr.evaluatePopulationFitness();
	
	out.println(test);
	String test1 = "Generated Timetable:<br/><table border=\"1\"> <tr> <th>Day/Time</th>"+
	"<th>9 - 9.50am</th><th>10 - 10.50am</th><th>11 - 11.50am</th><th>12 - 12.50pm</th><th>1 - 1.50pm</th>" +
	"<th>2 - 2.50pm</th><th>3 - 3.50pm</th><th>4 - 5pm</th></tr>"
	+ cr.displayGeneratedTimetable(1) + "</table>";
	out.println("<br/>" + test1);

	
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
	/*
	out.println("<br/><hr/>Chromosome Structure[19]<hr/>");
	for(int b=0; b< Chromosomes.roomCount; b++){
		for(int a =0; a < timeslots; a++)
			out.println(chromo[19][b][a] + "&nbsp&nbsp&nbsp");
		out.println("<br/>");
		
	}
	
	
	
	for(i=0;i<cr.numChromosomes;i++){
		out.println("Fitness of chromosome" + (i+1) + ":" +allFitness[i] + "<br/>");
		
	}
	*/
	long a = cr.startTime/1000;
	long b = cr.endTime/1000;
	long runningTime = b-a;
	out.println("<b><h2>It took approximately "+ runningTime + " seconds to run this GA</h2></b>");
	
%>
</fieldset>
</div>
</body>
</html>