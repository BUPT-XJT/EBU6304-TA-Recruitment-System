<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%
    List<Position> positions = (List<Position>) request.getAttribute("positions");
    String tab = (String) request.getAttribute("approveTab");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Approve Positions - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>Approve Positions</h2><div class="mt-desc">Review and approve position requests from Module Owners</div></div>
</div>
<div class="tabs">
  <a class="tab<%= "PENDING".equals(tab)?" active":"" %>" href="<%= ctx %>/admin/approve?tab=PENDING">Pending</a>
  <a class="tab<%= "APPROVED".equals(tab)?" active":"" %>" href="<%= ctx %>/admin/approve?tab=APPROVED">Approved</a>
  <a class="tab<%= "REJECTED".equals(tab)?" active":"" %>" href="<%= ctx %>/admin/approve?tab=REJECTED">Rejected</a>
  <a class="tab<%= "ALL".equals(tab)?" active":"" %>" href="<%= ctx %>/admin/approve?tab=ALL">All</a>
</div>
<div class="table-wrapper">
  <table>
    <thead><tr><th>Course</th><th>Code</th><th>Posted By</th><th>Positions</th><th>Deadline</th><th>Status</th><th>Actions</th></tr></thead>
    <tbody>
      <% if (positions.isEmpty()) { %>
      <tr><td colspan="7" style="text-align:center;padding:40px;color:var(--gray-400)">No positions found</td></tr>
      <% } else for (Position p : positions) { %>
      <tr>
        <td><strong><%= JspUtil.esc(p.getCourseName()) %></strong></td>
        <td><%= JspUtil.esc(p.getCourseCode()) %></td>
        <td><%= JspUtil.esc(p.getMoName()) %></td>
        <td><%= p.getNumPositions() %></td>
        <td><%= JspUtil.esc(p.getDeadline()) %></td>
        <td><span class="badge <%= JspUtil.badgeClass(p.getStatus()) %>"><%= JspUtil.esc(p.getStatus()) %></span></td>
        <td>
          <% if ("PENDING".equals(p.getStatus())) { %>
          <form method="post" action="<%= ctx %>/admin/approve" style="display:inline">
            <input type="hidden" name="positionId" value="<%= JspUtil.esc(p.getId()) %>">
            <input type="hidden" name="returnTab" value="<%= JspUtil.esc(tab) %>">
            <button type="submit" name="status" value="APPROVED" class="btn btn-sm btn-success">Approve</button>
            <button type="submit" name="status" value="REJECTED" class="btn btn-sm btn-danger">Reject</button>
          </form>
          <% } else { %>-<% } %>
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
