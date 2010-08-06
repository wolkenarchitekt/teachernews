<%
  // Perform redirect according to request parameter for Non-JSF-requests.
  // Security restriction errors (see web.xml) redirect to index.jsp, appending a 
  // GET-parameter login (login=login or login=error). Parameter gets forwarded to 
  // the JSF page for handling in JSF.
	String loginParam = request.getParameter("login");
	if (loginParam == null)
		response.sendRedirect("/teachernews/home.faces");
	else {
		response.sendRedirect("/teachernews/login.faces?login=" + loginParam);
	}
%>
