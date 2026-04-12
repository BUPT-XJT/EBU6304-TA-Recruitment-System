<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%
    List<Position> positions = (List<Position>) request.getAttribute("positions");
    Set<String> appliedIds = (Set<String>) request.getAttribute("appliedIds");
    List<String> departments = (List<String>) request.getAttribute("departments");
    String fq = (String) request.getAttribute("filterQ");
    String fd = (String) request.getAttribute("filterDept");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Browse Positions - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>Browse Positions</h2><div class="mt-desc">Find and apply for TA positions</div></div>
</div>
<form method="get" action="<%= ctx %>/ta/positions" class="search-bar">
  <input class="search-input" type="search" name="q" value="<%= JspUtil.esc(fq) %>" placeholder="Search by course name, code, or skills...">
  <select class="form-select" name="dept" style="width:200px">
    <option value="">All Departments</option>
    <% for (String d : departments) { %>
    <option value="<%= JspUtil.esc(d) %>" <%= d.equals(fd) ? "selected" : "" %>><%= JspUtil.esc(d) %></option>
    <% } %>
  </select>
  <button type="submit" class="btn btn-secondary">Filter</button>
</form>
<div class="job-grid">
  <% if (positions.isEmpty()) { %>
  <div class="empty-state" style="grid-column:1/-1"><div class="es-icon">&mdash;</div><div class="es-text">No positions found</div></div>
  <% } else for (Position p : positions) {
      boolean applied = appliedIds.contains(p.getId());
      String[] skills = p.getRequiredSkills() != null ? p.getRequiredSkills().split(";") : new String[0];
  %>
  <div class="job-card">
    <div class="jc-top">
      <div>
        <div class="jc-title"><%= JspUtil.esc(p.getCourseName()) %></div>
        <div class="jc-dept"><%= JspUtil.esc(p.getCourseCode()) %> · <%= JspUtil.esc(p.getDepartment()) %></div>
      </div>
      <span class="badge badge-success">Open</span>
    </div>
    <div class="jc-info">
      <span><%= p.getNumPositions() %> positions</span>
      <span><%= p.getHoursPerWeek() %> h/week</span>
      <span><%= JspUtil.esc(p.getPayRate()) %></span>
    </div>
    <div class="jc-tags">
      <% for (String sk : skills) { if (sk == null || sk.isEmpty()) continue; %>
      <span class="tag"><%= JspUtil.esc(sk.trim()) %></span>
      <% } %>
    </div>
    <div class="jc-bottom">
      <span class="jc-deadline">Deadline: <%= JspUtil.esc(p.getDeadline()) %></span>
      <% if (applied) { %><span class="badge badge-info">Applied</span>
      <% } else { %><a class="btn btn-primary btn-sm" href="<%= ctx %>/ta/position?id=<%= JspUtil.esc(p.getId()) %>">View &amp; Apply</a><% } %>
    </div>
  </div>
  <% } %>
</div>
</main>
</div>
</body>
</html>
