<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.User" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    List<Map<String, Object>> rows = (List<Map<String, Object>>) request.getAttribute("workloadRows");
    int maxHours = (Integer) request.getAttribute("maxHours");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>TA Workload - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>TA Workload</h2><div class="mt-desc">Monitor teaching assistant workload distribution</div></div>
</div>
<div class="table-wrapper">
  <table>
    <thead><tr><th>TA Name</th><th>ID</th><th>Programme</th><th>Assigned Positions</th><th>Total Hours/Week</th><th>Workload</th></tr></thead>
    <tbody>
      <% if (rows.isEmpty()) { %>
      <tr><td colspan="6" style="text-align:center;padding:40px;color:var(--gray-400)">No TAs registered</td></tr>
      <% } else for (Map<String, Object> row : rows) {
          User ta = (User) row.get("ta");
          int ac = (Integer) row.get("assignedCount");
          int th = (Integer) row.get("totalHours");
          double pct = (Double) row.get("pct");
          String barColor = (String) row.get("barColor");
      %>
      <tr>
        <td><strong><%= JspUtil.esc(ta.getName()) %></strong></td>
        <td><%= JspUtil.esc(ta.getId()) %></td>
        <td><%= ta.getProgramme() == null || ta.getProgramme().isEmpty() ? "-" : JspUtil.esc(ta.getProgramme()) %></td>
        <td><%= ac %></td>
        <td><%= th %>h</td>
        <td style="min-width:120px">
          <div class="progress-bar"><div class="progress-fill" style="width:<%= pct %>%;background:<%= barColor %>"></div></div>
          <div style="font-size:10px;color:var(--gray-400);margin-top:2px"><%= th %>/<%= maxHours %>h</div>
        </td>
      </tr>
      <% } %>
    </tbody>
  </table>
</div>
</main>
</div>
</body>
</html>
