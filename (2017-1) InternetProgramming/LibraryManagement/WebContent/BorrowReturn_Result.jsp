<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR" errorPage="DBError.jsp"%>

<%
	Cookie cookie = new Cookie("TITLE", "");
	cookie.setMaxAge(0);
	response.addCookie(cookie);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link href="Style.css" type="text/css" rel="stylesheet">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>
<body>
	<h1>완료되었습니다.</h1>
	<a href=Main.jsp>메인으로 돌아가기</a>
</body>
</html>