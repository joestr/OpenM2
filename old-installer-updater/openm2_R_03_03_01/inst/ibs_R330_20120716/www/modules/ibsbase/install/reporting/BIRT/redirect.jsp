<%
// Retrieve the request encoding
String encoding = request.getCharacterEncoding ();
if (encoding == null)
{
	// Set the default encoding
	encoding = "UTF-8";
} // if

// Retrieve the request parameters
String queryStr = (request.getParameter("queryStr") != null) ?
	new String(request.getParameter("queryStr").getBytes(), encoding) : "";
String queryName = (request.getParameter("queryName") != null) ?
	new String(request.getParameter("queryName").getBytes(), encoding) : "";
String outputFormat = (request.getParameter("outputFormat") != null) ?
	new String(request.getParameter("outputFormat").getBytes(), encoding) : "";

String paramJDBCDriverClass = (request.getParameter("paramJDBCDriverClass") != null) ?
	new String(request.getParameter("paramJDBCDriverClass").getBytes(), encoding) : "";
String paramJDBCDriverUrl = (request.getParameter("paramJDBCDriverUrl") != null) ?
	new String(request.getParameter("paramJDBCDriverUrl").getBytes(), encoding) : "";
String paramJDBCUsername = (request.getParameter("paramJDBCUsername") != null) ?
	new String(request.getParameter("paramJDBCUsername").getBytes(), encoding) : "";
String paramJDBCPassword = (request.getParameter("paramJDBCPassword") != null) ?
	new String(request.getParameter("paramJDBCPassword").getBytes(), encoding) : "";
String paramJDBCJNDIUrl = (request.getParameter("paramJDBCJNDIUrl") != null) ?
	new String(request.getParameter("paramJDBCJNDIUrl").getBytes(), encoding) : "";
	
String paramLocale = (request.getParameter("paramLocale") != null) ? request.getParameter("paramLocale") : "";

session.setAttribute ("paramSqlQuery", queryStr);

/* Setting JDBC Driver Parameter from Application */
session.setAttribute ("paramJDBCDriverClass", paramJDBCDriverClass);
session.setAttribute ("paramJDBCDriverUrl", paramJDBCDriverUrl);
session.setAttribute ("paramJDBCUsername", paramJDBCUsername);
session.setAttribute ("paramJDBCPassword", paramJDBCPassword);
session.setAttribute ("paramJDBCJNDIUrl", paramJDBCJNDIUrl);

/*
 * Configure the JDBC Connection properties manually
 *
 * Deactivated because DB connetion parameter should be set via the application
 *
session.setAttribute ("paramJDBCDriverClass", "com.inet.tds.TdsDriver");
session.setAttribute ("paramJDBCDriverUrl", "jdbc:inetdae7a:tribb:1434");
session.setAttribute ("paramJDBCUsername", "sa");
session.setAttribute ("paramJDBCPassword", "");
session.setAttribute ("paramJDBCJNDIUrl", "");
*/

/* Run the report in frameset
String forwardStr = "frameset?__report=" +
    request.getParameter("queryName") +
    ".rptdesign&__overwrite=true";
*/

// Run the report as PDF
String forwardStr =
    "run?" +
    "__report=" + queryName + ".rptdesign" +
    "&__overwrite=true" +
    "&__locale=" + paramLocale +
    "&__format=" + outputFormat;

forwardStr = response.encodeRedirectURL (forwardStr);
%>
<B>queryStr: </B>
<PRE><%= session.getAttribute("paramSqlQuery") %></PRE>
<BR/><BR/>
<B>queryName: </B><CODE><%= queryName %></CODE>
<BR/><BR/>
<B>outputFormat: </B><CODE><%= outputFormat %></CODE>
<BR/><BR/>
<B>paramJDBCDriverClass: </B><CODE><%= paramJDBCDriverClass %></CODE>
<BR/>
<B>paramJDBCDriverUrl: </B><CODE><%= paramJDBCDriverUrl %></CODE>
<BR/>
<B>paramJDBCUsername: </B><CODE><%= paramJDBCUsername %></CODE>
<BR/>
<B>paramJDBCPassword: </B><CODE><%= paramJDBCPassword %></CODE>
<BR/>
<B>paramJDBCJNDIUrl: </B><CODE><%= paramJDBCJNDIUrl %></CODE>
<BR/><BR/>
<B>forwardStr: </B><CODE><%= forwardStr %></CODE>
<%
  response.sendRedirect (forwardStr);
%>
