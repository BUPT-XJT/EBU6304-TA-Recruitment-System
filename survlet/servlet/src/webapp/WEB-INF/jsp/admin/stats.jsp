<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Application" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    Map<String, Object> stats = (Map<String, Object>) request.getAttribute("stats");
    List<Position> positions = (List<Position>) request.getAttribute("positions");
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    String ctx = request.getContextPath();
    int pApp = (Integer) stats.get("pendingApplications");
    int pPass = (Integer) stats.get("passedApplications");
    int pFail = (Integer) stats.get("failedApplications");
    int maxA = Math.max(1, Math.max(pApp, Math.max(pPass, pFail)));
    int ap = (Integer) stats.get("approvedPositions");
    int pp = (Integer) stats.get("pendingPositions");
    int tp = (Integer) stats.get("totalPositions");
    int other = Math.max(0, tp - ap - pp);
    int maxP = Math.max(1, Math.max(ap, Math.max(pp, other)));
    int maxPer = 1;
    for (Position p : positions) {
        int c = 0;
        for (Application a : applications) if (a.getPositionId().equals(p.getId())) c++;
        if (c > maxPer) maxPer = c;
    }
    int maxPx = 120;
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Statistics - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>System Statistics</h2><div class="mt-desc">Overview of recruitment data and trends</div></div>
</div>
<div class="stat-grid">
  <div class="stat-card"><div class="sc-icon">P</div><div class="sc-label">Total Positions</div><div class="sc-value"><%= stats.get("totalPositions") %></div></div>
  <div class="stat-card"><div class="sc-icon">A</div><div class="sc-label">Total Applications</div><div class="sc-value"><%= stats.get("totalApplications") %></div></div>
  <div class="stat-card"><div class="sc-icon">T</div><div class="sc-label">Total TAs</div><div class="sc-value"><%= stats.get("totalTAs") %></div></div>
  <div class="stat-card"><div class="sc-icon">M</div><div class="sc-label">Module Owners</div><div class="sc-value"><%= stats.get("totalMOs") %></div></div>
</div>
<div class="grid-2">
  <div class="card">
    <div class="card-title" style="margin-bottom:16px">Application Status Distribution</div>
    <div class="chart-placeholder" id="appChart">
      <% int h1 = Math.max(pApp > 0 ? 4 : 0, pApp * maxPx / maxA); %>
      <div class="chart-bar" style="height:<%= h1 %>px;background:var(--warning)"><span class="cb-val"><%= pApp %></span><span class="cb-label">Pending</span></div>
      <% int h2 = Math.max(pPass > 0 ? 4 : 0, pPass * maxPx / maxA); %>
      <div class="chart-bar" style="height:<%= h2 %>px;background:var(--success)"><span class="cb-val"><%= pPass %></span><span class="cb-label">Passed</span></div>
      <% int h3 = Math.max(pFail > 0 ? 4 : 0, pFail * maxPx / maxA); %>
      <div class="chart-bar" style="height:<%= h3 %>px;background:var(--danger)"><span class="cb-val"><%= pFail %></span><span class="cb-label">Failed</span></div>
    </div>
  </div>
  <div class="card">
    <div class="card-title" style="margin-bottom:16px">Position Status Distribution</div>
    <div class="chart-placeholder" id="posChart">
      <% int x1 = Math.max(ap > 0 ? 4 : 0, ap * maxPx / maxP); %>
      <div class="chart-bar" style="height:<%= x1 %>px;background:var(--success)"><span class="cb-val"><%= ap %></span><span class="cb-label">Approved</span></div>
      <% int x2 = Math.max(pp > 0 ? 4 : 0, pp * maxPx / maxP); %>
      <div class="chart-bar" style="height:<%= x2 %>px;background:var(--warning)"><span class="cb-val"><%= pp %></span><span class="cb-label">Pending</span></div>
      <% int x3 = Math.max(other > 0 ? 4 : 0, other * maxPx / maxP); %>
      <div class="chart-bar" style="height:<%= x3 %>px;background:var(--gray-400)"><span class="cb-val"><%= other %></span><span class="cb-label">Other</span></div>
    </div>
  </div>
</div>
<div class="card mt-20">
  <div class="card-title" style="margin-bottom:16px">Applications per Position</div>
  <div class="chart-placeholder" id="perPosChart" style="height:220px;flex-wrap:wrap;align-content:flex-end">
    <% for (Position p : positions) {
        int c = 0;
        for (Application a : applications) if (a.getPositionId().equals(p.getId())) c++;
        int bh = Math.max(c > 0 ? 4 : 0, c * maxPx / maxPer);
    %>
    <div class="chart-bar" style="height:<%= bh %>px;background:var(--primary);min-width:32px;width:32px"><span class="cb-val"><%= c %></span><span class="cb-label"><%= JspUtil.esc(p.getCourseCode()) %></span></div>
    <% } %>
  </div>
</div>
</main>
</div>
</body>
</html>
