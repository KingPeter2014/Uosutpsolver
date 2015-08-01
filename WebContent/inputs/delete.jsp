<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Delete Object</title>
</head>
<body>
	Delete not allowed from the application version 1. To be considered for version 2.
	<%
		String id = request.getParameter("id");
		String what = request.getParameter("what");
		out.println(" <hr/>You want to delete " + what + " with id of " + id);
	
	%>
</body>
</html>