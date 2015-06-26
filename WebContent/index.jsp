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
<br/>
<fieldset><legend>Randomly Generated Timetable</legend>

<% 
	long start = System.currentTimeMillis()/1000;

	ReadInputs read = new ReadInputs();
	Chromosomes cr = new Chromosomes();
	
	int[] allFitness = cr.evaluatePopulationFitness();
	int[] sortedIndices = cr.getSortedChromosomeIndices();
	String test = "<br/><b>Fitness of Best Chromosome["+(sortedIndices[0] +1)+"]:</b><hr/>" + cr.getFitnessOnAContraint(sortedIndices[0]);
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
	
	/**
	out.println("<br/><hr/>Best Chromosome Structure[" + (sortedIndices[0]+1)+ "]<hr/>");
	for(int b=0; b< Chromosomes.roomCount; b++){
		for(int a =0; a < timeslots; a++)
			out.println(chromo[sortedIndices[0]][b][a] + "&nbsp&nbsp&nbsp");
		out.println("<br/>");
		
	}
	
	out.println("<br/><hr/>Worst Chromosome Structure["+(sortedIndices[cr.numChromosomes-1]+1)+"]<hr/>");
	for(int b=0; b< Chromosomes.roomCount; b++){
		for(int a =0; a < timeslots; a++)
			out.println(chromo[sortedIndices[cr.numChromosomes-1]][b][a] + "&nbsp&nbsp&nbsp");
		out.println("<br/>");
		
	}
	
	**/
	
	for(i=0;i<cr.numChromosomes;i++){
		out.println("Fitness of chromosome:  " + (sortedIndices[i] + 1) + " is " +allFitness[i] + "<br/>");
		
	}
	
	
	long b = System.currentTimeMillis()/1000;
	long runningTime = b-start;
	out.println("<b><h2>It took approximately "+ runningTime + " second(s) to run this GA</h2></b>");
	
%>
</fieldset>
</div>
</body>
</html>