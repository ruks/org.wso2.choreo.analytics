<%@ page import="com.choroe.analytics.portal.Utils" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%
	for (Cookie c : Utils.getLogoutCookies()) {
		response.addCookie(c);
	}
%>
	<script type="text/javascript">
		localStorage.removeItem("user");
		location.href ='/choreo-analytics/index.jsp';
	</script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

</body>
</html>
