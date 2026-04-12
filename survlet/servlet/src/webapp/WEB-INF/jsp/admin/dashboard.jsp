<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Application" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    Map<String, Object> stats = (Map<String, Object>) request.getAttribute("stats");
    List<Position> pending = (List<Position>) request.getAttribute("pendingPositions");
    List<Application> recent = (List<Application>) request.getAttribute("recentApplications");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin Dashboard - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>Admin Dashboard</h2><div class="mt-desc">System overview and management</div></div>
</div>
<div class="stat-grid">
  <div class="stat-card"><div class="sc-icon">P</div><div class="sc-label">Total Positions</div><div class="sc-value"><%= stats.get("totalPositions") %></div></div>
  <div class="stat-card"><div class="sc-icon">A</div><div class="sc-label">Applications</div><div class="sc-value"><%= stats.get("totalApplications") %></div></div>
  <div class="stat-card"><div class="sc-icon">T</div><div class="sc-label">Registered TAs</div><div class="sc-value"><%= stats.get("totalTAs") %></div></div>
  <div class="stat-card"><div class="sc-icon">U</div><div class="sc-label">Total Users</div><div class="sc-value"><%= stats.get("totalUsers") %></div></div>
</div>
<div class="grid-2">
  <div class="card">
    <div class="card-title">Positions Pending Approval</div>
    <div style="margin-top:12px">
      <% if (pending.isEmpty()) { %>
      <div style="padding:20px;text-align:center;color:var(--gray-400)">No pending positions</div>
      <% } else {
        int n = Math.min(5, pending.size());
        for (int i = 0; i < n; i++) {
          Position p = pending.get(i);
      %>
      <div style="display:flex;justify-content:space-between;align-items:center;padding:10px 0;border-bottom:1px solid var(--gray-100)">
        <div>
          <div style="font-size:13px;font-weight:600;color:var(--gray-700)"><%= JspUtil.esc(p.getCourseName()) %></div>
          <div style="font-size:11px;color:var(--gray-400)"><%= JspUtil.esc(p.getCourseCode()) %> · by <%= JspUtil.esc(p.getMoName()) %></div>
        </div>
        <a href="<%= ctx %>/admin/approve" class="btn btn-sm btn-outline">Review</a>
      </div>
      <% } } %>
    </div>
  </div>
  <div class="card">
    <div class="card-title">Recent Applications</div>
    <div style="margin-top:12px">
      <% if (recent.isEmpty()) { %>
      <div style="padding:20px;text-align:center;color:var(--gray-400)">No applications</div>
      <% } else {
        int from = Math.max(0, recent.size() - 5);
        for (int i = recent.size() - 1; i >= from; i--) {
          Application a = recent.get(i);
      %>
      <div style="display:flex;justify-content:space-between;align-items:center;padding:10px 0;border-bottom:1px solid var(--gray-100)">
        <div>
          <div style="font-size:13px;font-weight:600;color:var(--gray-700)"><%= JspUtil.esc(a.getTaName()) %></div>
          <div style="font-size:11px;color:var(--gray-400)"><%= JspUtil.esc(a.getPositionTitle()) %> · <%= JspUtil.esc(a.getAppliedDate()) %></div>
        </div>
        <span class="badge <%= JspUtil.badgeClass(a.getStatus()) %>"><%= JspUtil.esc(a.getStatus()) %></span>
      </div>
      <% } } %>
    </div>
  </div>
</div>
</main>
</div>
</body>
</html>
