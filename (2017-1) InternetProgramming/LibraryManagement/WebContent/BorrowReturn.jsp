<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR" errorPage="DBError.jsp"%>

<%@page import="java.sql.*"%>

<%
	Connection conn = null;
	Statement stmt = null;
	try {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/booklist", "root", "1234");

		if (conn == null)
			throw new Exception("데이터베이스에 연결할 수 없습니다.<br>");
		stmt = conn.createStatement();

		Cookie cookies[] = request.getCookies();
		String title = request.getParameter("TITLE");
		String state = request.getParameter("STATE");
		String command = null;
		if (state.equals("O")) {
			command = String.format("update goodsinfo set " + "available := %d where title = '%s';", 0, title);
		} else
			command = String.format("update goodsinfo set " + "available := %d where title = '%s';", 1, title);
		int rowNum = stmt.executeUpdate(command);
		if (rowNum < 1) {
			throw new Exception(state + "\n" + title);
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

	response.sendRedirect("BorrowReturn_Result.jsp");
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

<%!private String getCookieValue(Cookie[] cookies, String name) {
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}%>

