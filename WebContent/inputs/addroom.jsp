<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Add Room</title>
</head>
<body>
<div id="maincontent">
	<fieldset><legend>Add New Room</legend>
		<form action="/utpsolver/AddRoomServlet" method="POST">
			<table>
				<tr>
					<td>Name<em>*</em></td>
					<td><input type="text" name="roomname" id="roomname" placeholder="Room name or unique identifier"/></td>
				</tr>
				<tr>
					<td>Description</td>
					<td><input type="text" name="description" id="description" placeholder="Better elaborate name"/></td>
				</tr>
				
				<tr>
				<td style="width:200px"><label>Type&nbsp;<em>*</em></label></td>
				<td><select name="type" id="type"><option value="0">...</option><option value="lecture">Lecture Only</option><option value="lab">Laboratory Only</option><option value="both">Both</optio></select></td>
				</tr>
				
				<tr>
					<td>Capacity<em>*</em></td>
					<td><input type="number" name="capacity" id="capacity" value = "0" placeholder="How many students can it take?"/></td>
				</tr>
				<!--  
					<tr>
						<td>Longitude</td>
						<td><input type="decimal" name="longitude" id="longitude"  value="0.0" placeholder="Longitude if known"/></td>
					</tr>
				
					<tr>
						<td>Latitude</td>
						<td><input type="text" name="latitude" id="latitude"  value="0.0" placeholder="Latitude if knwon"/></td>
					</tr>
				-->
					<tr>
						<td> <input type="submit" name="addroom" value="Create Room"/></td>
						<td> <input type="reset" /> </td> 
	
					</tr>
				
	</table>
	</form>
	</fieldset>
	</div>
</body>
</html>