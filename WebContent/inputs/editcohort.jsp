<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Edit Cohort</title>
</head>
<body>
<%
	String cohortid = request.getParameter("id");
 	out.println("You want to edit Cohort of ID " + cohortid);
%>
</body>
</html>