<%@page contentType="text/html; charset=euc-kr" isErrorPage="true" %>
<% response.setStatus(200); %>
<HTML>
    <HEAD><TITLE>�����ͺ��̽� ����</TITLE>
    	<html>
	<head>
	<style>
	@import url(http://fonts.googleapis.com/earlyaccess/jejugothic.css);
body {
font-family: 'Jeju Gothic', serif;
}</style></head></html>
    </HEAD>
    <BODY>
        <H3>�����ͺ��̽� ����</H3>
        ���� �޽���: <%= exception.getMessage() %>
    </BODY>
</HTML>
