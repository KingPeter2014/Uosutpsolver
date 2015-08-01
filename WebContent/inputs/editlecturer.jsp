<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Edit Lecturer</title>
</head>
<body>

<%
	String lecturerid = request.getParameter("id");
 	out.println("You want to edit lecturer of ID " + lecturerid);
%>
</body>
</html>