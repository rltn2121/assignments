<%@page contentType="text/html; charset=euc-kr" isErrorPage="true" %>
<% response.setStatus(200); %>
<HTML>
    <HEAD><TITLE>데이터베이스 에러</TITLE>
    	<html>
	<head>
	<style>
	@import url(http://fonts.googleapis.com/earlyaccess/jejugothic.css);
body {
font-family: 'Jeju Gothic', serif;
}</style></head></html>
    </HEAD>
    <BODY>
        <H3>데이터베이스 에러</H3>
        에러 메시지: <%= exception.getMessage() %>
    </BODY>
</HTML>
