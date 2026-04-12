<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Application" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.User" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%
    User u = (User) request.getAttribute("currentUser");
    List<Application> apps = (List<Application>) request.getAttribute("applications");
    List<Position> positions = (List<Position>) request.getAttribute("approvedPositions");
    String ctx = request.getContextPath();
    int pending = 0, passed = 0, failed = 0;
    for (Application a : apps) {
        if ("PENDING".equals(a.getStatus())) pending++;
        if ("PASSED".equals(a.getStatus())) passed++;
        if ("FAILED".equals(a.getStatus())) failed++;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>TA Dashboard - BUPT TA Recruitment</title>
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
    <div class="mt-desc">Overview of your TA activities</div>
  </div>
</div>
<div class="stat-grid">
  <div class="stat-card"><div class="sc-icon">A</div><div class="sc-label">Total Applications</div><div class="sc-value"><%= apps.size() %></div></div>
  <div class="stat-card"><div class="sc-icon">Q</div><div class="sc-label">Pending</div><div class="sc-value"><%= pending %></div></div>
  <div class="stat-card"><div class="sc-icon">S</div><div class="sc-label">Passed</div><div class="sc-value"><%= passed %></div></div>
  <div class="stat-card"><div class="sc-icon">O</div><div class="sc-label">Open Positions</div><div class="sc-value"><%= positions.size() %></div></div>
</div>
<div class="grid-2">
  <div class="card">
    <div class="card-title">Recent Applications</div>
    <div style="margin-top:12px">
      <% if (apps.isEmpty()) { %>
      <div class="empty-state"><div class="es-icon">&mdash;</div><div class="es-text">No applications yet</div></div>
      <% } else {
        int n = Math.min(3, apps.size());
        for (int i = apps.size() - 1; i >= apps.size() - n && i >= 0; i--) {
          Application a = apps.get(i); %>
      <div style="display:flex;justify-content:space-between;align-items:center;padding:10px 0;border-bottom:1px solid var(--gray-100)">
        <div>
          <div style="font-size:13px;font-weight:600;color:var(--gray-700)"><%= JspUtil.esc(a.getPositionTitle()) %></div>
          <div style="font-size:11px;color:var(--gray-400)"><%= JspUtil.esc(a.getCourseCode()) %> · <%= JspUtil.esc(a.getAppliedDate()) %></div>
        </div>
        <span class="badge <%= JspUtil.badgeClass(a.getStatus()) %>"><%= JspUtil.esc(a.getStatus()) %></span>
      </div>
      <% } } %>
    </div>
  </div>
  <div class="card">
    <div class="card-title">Recommended Positions</div>
    <div style="margin-top:12px">
      <% if (positions.isEmpty()) { %>
      <div class="empty-state"><div class="es-icon">&mdash;</div><div class="es-text">No positions available</div></div>
      <% } else {
        int max = Math.min(3, positions.size());
        for (int i = 0; i < max; i++) {
          Position p = positions.get(i); %>
      <div style="padding:10px 0;border-bottom:1px solid var(--gray-100)">
        <div style="font-size:13px;font-weight:600;color:var(--gray-700)"><%= JspUtil.esc(p.getCourseName()) %></div>
        <div style="font-size:11px;color:var(--gray-400)"><%= JspUtil.esc(p.getCourseCode()) %> · <%= JspUtil.esc(p.getDepartment()) %> · Deadline: <%= JspUtil.esc(p.getDeadline()) %></div>
      </div>
      <% } } %>
    </div>
  </div>
</div>
</main>
</div>
</body>
</html>
