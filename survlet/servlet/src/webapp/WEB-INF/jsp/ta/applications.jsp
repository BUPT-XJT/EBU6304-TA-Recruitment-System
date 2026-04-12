<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Application" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%
    List<Application> apps = (List<Application>) request.getAttribute("applications");
    String st = (String) request.getAttribute("filterStatus");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>My Applications - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>My Applications</h2><div class="mt-desc">Track the status of your TA applications</div></div>
</div>
<div class="tabs">
  <a class="tab<%= "ALL".equals(st)?" active":"" %>" href="<%= ctx %>/ta/applications?status=ALL">All</a>
  <a class="tab<%= "PENDING".equals(st)?" active":"" %>" href="<%= ctx %>/ta/applications?status=PENDING">Pending</a>
  <a class="tab<%= "PASSED".equals(st)?" active":"" %>" href="<%= ctx %>/ta/applications?status=PASSED">Passed</a>
  <a class="tab<%= "FAILED".equals(st)?" active":"" %>" href="<%= ctx %>/ta/applications?status=FAILED">Failed</a>
</div>
<div class="table-wrapper">
  <table>
    <thead><tr><th>Position</th><th>Course</th><th>Applied Date</th><th>Status</th><th>Feedback</th></tr></thead>
    <tbody>
      <% if (apps.isEmpty()) { %>
      <tr><td colspan="5" style="text-align:center;padding:40px;color:var(--gray-400)">No applications found</td></tr>
      <% } else for (Application a : apps) { %>
      <tr>
        <td><strong><%= JspUtil.esc(a.getPositionTitle()) %></strong></td>
        <td><%= JspUtil.esc(a.getCourseCode()) %></td>
        <td><%= JspUtil.esc(a.getAppliedDate()) %></td>
        <td><span class="badge <%= JspUtil.badgeClass(a.getStatus()) %>"><%= JspUtil.esc(a.getStatus()) %></span></td>
        <td style="font-size:12px;color:var(--gray-500)"><%= JspUtil.esc(a.getFeedback()) %></td>
      </tr>
      <% } %>
    </tbody>
  </table>
</div>
</main>
</div>
</body>
</html>
