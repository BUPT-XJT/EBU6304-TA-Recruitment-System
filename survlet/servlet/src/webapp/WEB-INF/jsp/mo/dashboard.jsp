<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Application" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.User" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%
    User u = (User) request.getAttribute("currentUser");
    List<Position> positions = (List<Position>) request.getAttribute("positions");
    List<Application> allApps = (List<Application>) request.getAttribute("allApplications");
    String ctx = request.getContextPath();
    int approved = 0, pending = 0, totalApps = 0;
    for (Position p : positions) {
        if ("APPROVED".equals(p.getStatus())) approved++;
        if ("PENDING".equals(p.getStatus())) pending++;
    }
    for (Application a : allApps) {
        for (Position p : positions) {
            if (p.getId().equals(a.getPositionId())) { totalApps++; break; }
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>MO Dashboard - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div>
    <h2>Welcome, <%= JspUtil.esc(u.getName()) %>!</h2>
    <div class="mt-desc">Manage your TA positions and applications</div>
  </div>
  <a href="<%= ctx %>/mo/publish" class="btn btn-primary">+ New Position</a>
</div>
<div class="stat-grid">
  <div class="stat-card"><div class="sc-icon">P</div><div class="sc-label">Total Positions</div><div class="sc-value"><%= positions.size() %></div></div>
  <div class="stat-card"><div class="sc-icon">V</div><div class="sc-label">Approved</div><div class="sc-value"><%= approved %></div></div>
  <div class="stat-card"><div class="sc-icon">Q</div><div class="sc-label">Pending Approval</div><div class="sc-value"><%= pending %></div></div>
  <div class="stat-card"><div class="sc-icon">U</div><div class="sc-label">Total Applicants</div><div class="sc-value"><%= totalApps %></div></div>
</div>
<div class="card">
  <div class="card-title">My Published Positions</div>
  <div class="table-wrapper" style="margin-top:12px;box-shadow:none;border:none">
    <table>
      <thead><tr><th>Course</th><th>Code</th><th>Positions</th><th>Deadline</th><th>Status</th><th>Applicants</th></tr></thead>
      <tbody>
        <% if (positions.isEmpty()) { %>
        <tr><td colspan="6" style="text-align:center;padding:40px;color:var(--gray-400)">No positions published yet</td></tr>
        <% } else for (Position p : positions) {
            int ac = 0;
            for (Application a : allApps) if (a.getPositionId().equals(p.getId())) ac++;
        %>
        <tr>
          <td><strong><%= JspUtil.esc(p.getCourseName()) %></strong></td>
          <td><%= JspUtil.esc(p.getCourseCode()) %></td>
          <td><%= p.getNumPositions() %></td>
          <td><%= JspUtil.esc(p.getDeadline()) %></td>
          <td><span class="badge <%= JspUtil.badgeClass(p.getStatus()) %>"><%= JspUtil.esc(p.getStatus()) %></span></td>
          <td><%= ac %></td>
        </tr>
        <% } %>
      </tbody>
    </table>
  </div>
</div>
</main>
</div>
</body>
</html>
