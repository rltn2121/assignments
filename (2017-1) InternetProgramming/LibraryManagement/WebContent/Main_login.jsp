<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR" errorPage="DBError.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="Style.css" type="text/css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>도서 관리 웹 사이트</title>
</head>
<body>
	<%
		String id = request.getParameter("ID");
		String pw = request.getParameter("PW");
		String DBid = (String) request.getAttribute("DBID");
		String DBpw = (String) request.getAttribute("DBPW");
		Cookie cookie = null;
		if (id.equals(DBid) && pw.equals(DBpw)) {
		} else {
			throw new Exception("아이디 또는 비밀번호가 일치하지 않습니다.<br>");
		}
		
	%>
	<h1>

		<%=id%>님, 환영합니다! <a href=Main.jsp>로그아웃</a>
	</h1>
<div>
	<table align=center>

		<tr>
			<td>검색할 데이터를 입력하세요</td>
		</tr>

		<tr>

			<td>
			
				<form action=BookSearch_by_DB.jsp>
			제목	<input type=radio name= TYPE value= title >
			 저자<input type=radio name= TYPE value= writer >
					<input type=text name = DATA > <input type=submit value="검색">
				</form>
			</td>
		</tr>
	</table>

</div>
</body>
</html>

