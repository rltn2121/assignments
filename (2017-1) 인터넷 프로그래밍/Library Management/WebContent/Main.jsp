<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR" errorPage="DBError.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	Cookie cookie = new Cookie("ID", null);
	cookie.setMaxAge(0);
	response.addCookie(cookie);
%>
<html>
<head>
<meta charset="EUC-KR">
<title>���� ���� �� ����Ʈ</title>
<head>
 <link href="Style.css" type="text/css" rel="stylesheet">

</head>
<body>
	<h1>�������� ���� ������</h1>
	<div><table align = center>
		<form action=Login_judge.jsp method=POST>
			<tr>
				<td>���̵�</td>
				<td><input type=text name=ID placeholder=ID></td>
			</tr>
			<tr>
				<td>��й�ȣ</td>
				<td><input type=password name=PW placeholder=********></td>
			</tr>

			<tr>
				<td><input type=submit name=LOGIN value=�α���></td>
		</form>
		<td><form action=Subscription.html method=POST>
				<input type=submit value=ȸ������ background = red>
			</form></td>
		</tr>
	</table></div>
</body>
</html>