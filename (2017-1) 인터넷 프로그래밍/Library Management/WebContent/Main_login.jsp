<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR" errorPage="DBError.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="Style.css" type="text/css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>���� ���� �� ����Ʈ</title>
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
			throw new Exception("���̵� �Ǵ� ��й�ȣ�� ��ġ���� �ʽ��ϴ�.<br>");
		}
		
	%>
	<h1>

		<%=id%>��, ȯ���մϴ�! <a href=Main.jsp>�α׾ƿ�</a>
	</h1>
<div>
	<table align=center>

		<tr>
			<td>�˻��� �����͸� �Է��ϼ���</td>
		</tr>

		<tr>

			<td>
			
				<form action=BookSearch_by_DB.jsp>
			����	<input type=radio name= TYPE value= title >
			 ����<input type=radio name= TYPE value= writer >
					<input type=text name = DATA > <input type=submit value="�˻�">
				</form>
			</td>
		</tr>
	</table>

</div>
</body>
</html>

