<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="utpsolver.ReadInputs"%>
    <%@page import="utpsolver.Chromosomes"%>
 <%@page import="java.util.*" %>
 <%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
#div1, #div2
{float:left; width:100px; height:35px; margin:10px;padding:10px;border:1px solid #aaaaaa;}
</style>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

<script>

$(document).ready(function(){
    $("#best").click(function(){
        $("#statistics").toggle("slow");
        
    });
    
    $("#chromo").click(function(){
       
        $("#allchromosomes").toggle("slow");
    });
});
var dragData = null; 
function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
	ev.dataTransfer.effectAllowed = 'move';
    ev.dataTransfer.setData("text", ev.target.id);
    dragData = ev.target.id;
    /*
    dragData = ev.dataTransfer.getData("text");
	ev.dataTransfer.effectAllowed = 'move';
	ev.dataTransfer.setData('text/html', this.innerHTML);
	*/
}

function drop(ev) {
    ev.preventDefault();
    if (ev.stopPropagation) {
		ev.stopPropagation();
	}
   // dragData.innerHTML = this.innerHTML;
    var data = ev.dataTransfer.getData("text");
    dragData.text = data;
    ev.target.appendChild(document.getElementById(data));
  
    
	//if (dragData != this) {
	//	dragData.innerHTML = this.innerHTML;
	//	this.innerHTML = ev.dataTransfer.getData('text/html');
	//} 
	//return false;

	
}	
	
</script>
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
			<li> <a href="inputs/cohorts.jsp"> Cohort</a> </li>
			<!--  <li> <a href="settings.jsp"> GA Settings</a> </li>-->
			<li> <a href="constraints.jsp"> Special Constraints</a> </li>
		</div>
<div>
</div>
<br/>
<fieldset><legend>UTPSolver</legend>
<div id="div1" ondrop="drop(event)" ondragover="allowDrop(event)">
  <span draggable="true" ondragstart="drag(event)" id="drag1" width="88" height="31">Drag me</span>
</div>

<div id="div2" ondrop="drop(event)" ondragover="allowDrop(event)"></div>


<!-- 
<h1> Usage Instructions</h1>
The usage of each of the tabs above for running this University Timetabling problem solver (UTPSolver) is described below:
<ol>
	<li><b>Home</b> - This keeps you on this home page</li>
	<li><b>Rooms</b> - This enables one to add rooms, their types and capacity</li>
	<li><b>Lecturers</b> - This enables one to add lecturers, indicate if they are fulltime or partime as well as 
	allocate a module to them.</li>
	<li><b>Modules</b> - This enables one to add module code, lecture and lab hours, number of students that enroll 
	in it and then allocate the module to a lecturer too.</li>
	<li><b>Cohort</b> - This enables one to add cohort, the starting level for the cohort, the number of 
	years to graduate and also the modules to be taken by this cohort at each level of study.</li>
	<li><b>Special Constraints</b> - This is where one can specify the times of the week that 
	partime lecturers are available. It also enables the timetable officer to specify 
	modules that must take place in a particular room and time. After adding this constraints one can click 
	<b>"Generate timetable" </b> on that page to run the program to generate timetable based on previously
	 entered data.</li>
</ol>
 

<h2><b>IMPORTANT:<br/>
Add rooms, modules, lecturers and cohorts before assigning modules to cohorts or 
allocating modules to lecturers. These set up must be completed before executing the UTPSolver.</b></h2><br/>
 -->
<form action="constraints.jsp" method="post">
	
		<input type="Submit" value="Execute UTPSolver" name="runGA" id="runGA">
	</form>
	<table><tr><td id="a" draggable="true">A</td><td>B</td><td id="c" draggable="true">C</td></tr></table>

<% 

	long start = System.currentTimeMillis()/1000;
	ReadInputs read = new ReadInputs();
	Chromosomes cr = new Chromosomes();
	if(cr==null)
		return;
	
	int[] allFitness = cr.evaluatePopulationFitness();
	int[] sortedIndices = cr.getSortedChromosomeIndices();
	String test = "<br/><button id=\"best\"><b>Click to Display/Hide the Fitness of Best Chromosome</button>["+(sortedIndices[0] +1)+"]:</b><hr/><div id=\"statistics\">" + cr.getFitnessOnAContraint(sortedIndices[0]) + "</div>";
	out.println(test);
	String test1 = "Generated Timetable:<br/><table border=\"1\"> <tr> <th>Day/Time</th>"+
	"<th>9 - 9.50am</th><th>10 - 10.50am</th><th>11 - 11.50am</th><th>12 - 12.50pm</th><th>1 - 1.50pm</th>" +
	"<th>2 - 2.50pm</th><th>3 - 3.50pm</th><th>4 - 5pm</th></tr>"
	+ cr.displayGeneratedTimetable(sortedIndices[0]) + "</table>";
	out.println("<br/>" + test1);


	int timeslots = 40;
	int [][][] chromo = cr.chromosomes;
	int[] moduleIdsArray = read.getModuleIds();
	int numModules = moduleIdsArray.length;
	int i=0; 
	int [] hard = cr.getHardFitnesses();
	out.println("<button id=\"chromo\"> Hide/Show all Chromosome Fitness</button><div id=\"allchromosomes\">");
	for(i=0;i<cr.numChromosomes;i++){
		out.println("Hard Fitness of chromosome:  " + (sortedIndices[i] + 1) + " is " +hard[i] + "<br/>");
		
	}
	out.println("</div>");
	
	long b = System.currentTimeMillis()/1000;
	long runningTime = b-start;
	out.println("<b><h2>It took approximately "+ runningTime + " second(s) to run this GA</h2></b>");
	

	
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
	
%>
<!--  <a rel='nofollow' href='http://www.qrcode-generator.de' border='0' style='cursor:default'><img src='https://chart.googleapis.com/chart?cht=qr&chl=www.kingrock.com.ng&chs=180x180&choe=UTF-8&chld=L|2' alt=''></a> -->
</fieldset><br/>
<center><span class="footer"> <b> 2015 Masters of Advanced Software Engineering Dissertation Project by: Eze Peter U.; Supervised by: Dr. D.C. Walker</b></span></center>
</div>
</body>
</html>