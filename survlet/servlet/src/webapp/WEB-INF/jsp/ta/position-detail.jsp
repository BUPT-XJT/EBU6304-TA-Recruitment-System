<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%
    Position p = (Position) request.getAttribute("position");
    boolean applied = Boolean.TRUE.equals(request.getAttribute("applied"));
    String ctx = request.getContextPath();
    String[] skills = p.getRequiredSkills() != null ? p.getRequiredSkills().split(";") : new String[0];
    String[] duties = p.getDuties() != null ? p.getDuties().split("\n") : new String[0];
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><%= JspUtil.esc(p.getCourseName()) %> - BUPT TA</title>
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
    <a href="<%= ctx %>/ta/positions" class="btn btn-secondary btn-sm" style="margin-bottom:8px">&larr; Back</a>
    <h2><%= JspUtil.esc(p.getCourseName()) %></h2>
    <div class="mt-desc"><%= JspUtil.esc(p.getCourseCode()) %> · <%= JspUtil.esc(p.getDepartment()) %> · Posted by <%= JspUtil.esc(p.getMoName()) %></div>
  </div>
</div>
<div class="card" style="max-width:720px">
  <div class="grid-2" style="margin-bottom:16px">
    <div><strong style="font-size:12px;color:var(--gray-500)">Positions</strong><div style="font-size:15px;font-weight:700"><%= p.getNumPositions() %></div></div>
    <div><strong style="font-size:12px;color:var(--gray-500)">Hours/Week</strong><div style="font-size:15px;font-weight:700"><%= p.getHoursPerWeek() %></div></div>
    <div><strong style="font-size:12px;color:var(--gray-500)">Pay Rate</strong><div style="font-size:15px;font-weight:700"><%= JspUtil.esc(p.getPayRate()) %></div></div>
    <div><strong style="font-size:12px;color:var(--gray-500)">Deadline</strong><div style="font-size:15px;font-weight:700"><%= JspUtil.esc(p.getDeadline()) %></div></div>
  </div>
  <div style="margin-bottom:16px"><strong style="font-size:12px;color:var(--gray-500)">Required Skills</strong>
    <div style="margin-top:6px">
      <% for (String sk : skills) { if (sk == null || sk.isEmpty()) continue; %>
      <span class="tag"><%= JspUtil.esc(sk.trim()) %></span>
      <% } %>
    </div>
  </div>
  <div style="margin-bottom:20px"><strong style="font-size:12px;color:var(--gray-500)">Duties</strong>
    <ul style="margin-top:6px;padding-left:20px;font-size:13px;color:var(--gray-600)">
      <% for (String d : duties) { if (d == null || d.trim().isEmpty()) continue; %>
      <li><%= JspUtil.esc(d.trim()) %></li>
      <% } %>
    </ul>
  </div>
  <% if (applied) { %>
  <span class="badge badge-info" style="padding:8px 18px;font-size:13px">Already Applied</span>
  <% } else { %>
  <form method="post" action="<%= ctx %>/ta/apply">
    <input type="hidden" name="positionId" value="<%= JspUtil.esc(p.getId()) %>">
    <button type="submit" class="btn btn-primary">Apply Now</button>
  </form>
  <% } %>
</div>
</main>
</div>
</body>
</html>
