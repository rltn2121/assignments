<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR" errorPage = "DBError.jsp"%>

<%@page import="java.sql.*"%>

<%
	Connection conn = null;
	Statement stmt = null;
	try {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userlist", "root", "1234");

		if (conn == null)
			throw new Exception("데이터베이스에 연결할 수 없습니다.<br>");
		stmt = conn.createStatement();
		String id = request.getParameter("ID");
		String pw = request.getParameter("PW");
		ResultSet rs = stmt.executeQuery("select id, pw from userinfo where id =" + "\"" + id+"\";");
		if (rs.next()) {
			String DBid = rs.getString("id");
			String DBpw = rs.getString("pw");
			request.setAttribute("DBID", DBid);
			request.setAttribute("DBPW", DBpw);
		}
	} finally {
		try {
			stmt.close();
		} catch (Exception ignored) {

		}
		try {
			conn.close();
		} catch (Exception ignored) {

		}
	}
	
	RequestDispatcher dispatcher = request.getRequestDispatcher("Main_login.jsp");
	dispatcher.forward(request, response);
%>
<%!private String toUnicode(String str) {
		try {
			byte[] b = str.getBytes("ISO-8859-1");
			return new String(b);
		} catch (java.io.UnsupportedEncodingException uee) {
			System.out.println(uee.getMessage());
			return null;
		}
	}%>
	
