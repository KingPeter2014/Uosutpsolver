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
	//String count = "No of lecturers:" +read.getRoomName(1);
	List<Integer> roomids=new ArrayList<Integer>();
	List<Integer> moduleids=new ArrayList<Integer>();
	
	moduleids = read.getModuleIds();
	int numModules = read.getmoduleCount();
	Iterator iter = moduleids.iterator();
	int[] moduleIdsArray = new int[moduleids.size()];
	int i=0;
	while(iter.hasNext()){
		moduleIdsArray[i] = Integer.parseInt(iter.next().toString());
		out.println(moduleIdsArray[i] + ",");
		i=i+1;
	}
	
	//out.println(count);

%>
</fieldset>
</div>
</body>
</html>