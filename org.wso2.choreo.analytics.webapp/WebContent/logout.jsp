<%@ page import="com.choroe.analytics.portal.Utils" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Logout</title>
    <%
        String idToken = Utils.getIdToken(request.getCookies());
        String site = Utils.getLogOutUrl(idToken);
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", site);
    %>
</head>
<body>

</body>
</html>
