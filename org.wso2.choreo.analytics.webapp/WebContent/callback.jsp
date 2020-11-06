<%@page import="com.choroe.analytics.portal.TokenInfo" %>
<%@page import="com.choroe.analytics.portal.TokenRetrieve" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <%
        boolean isLoginError = false;
        String code = request.getParameter("code");
        if (code != null) {
            TokenInfo info = new TokenRetrieve().getToken(code);
            for (Cookie c : info.getCookies()) {
                response.addCookie(c);
            }
    %>
    <script type="text/javascript">
        localStorage.setItem("user", "<% out.print(info.getUser()); %>");
        location.href = '/choreo-analytics/portal/';
    </script>
    <%
        } else {
            isLoginError = true;
        }
    %>
    
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<%
    if (isLoginError) {
%>
<h1>Error occurred while login</h1>
<%
    }
%>
</body>
</html>
