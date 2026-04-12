<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Application" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%
    List<Position> approvedMine = (List<Position>) request.getAttribute("myApprovedPositions");
    String selId = (String) request.getAttribute("selectedPositionId");
    List<Application> reviewApps = (List<Application>) request.getAttribute("reviewApplications");
    String ctx = request.getContextPath();
    String[] colors = {"#2563EB", "#7C3AED", "#059669", "#D97706", "#DC2626"};
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Review Applications - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>Review Applications</h2><div class="mt-desc">Evaluate and manage TA applications for your courses</div></div>
</div>
<form method="get" action="<%= ctx %>/mo/review" class="form-group" style="max-width:400px">
  <label class="form-label">Select Position</label>
  <select class="form-select" name="positionId" onchange="this.form.submit()">
    <option value="">-- Choose a position --</option>
    <% for (Position p : approvedMine) { %>
    <option value="<%= JspUtil.esc(p.getId()) %>" <%= p.getId().equals(selId) ? "selected" : "" %>><%= JspUtil.esc(p.getCourseName()) %> (<%= JspUtil.esc(p.getCourseCode()) %>)</option>
    <% } %>
  </select>
</form>
<div id="applicantsList">
  <% if (selId == null || selId.isEmpty()) { %>
  <% } else if (reviewApps == null || reviewApps.isEmpty()) { %>
  <div class="empty-state"><div class="es-icon">&mdash;</div><div class="es-text">No applicants for this position</div></div>
  <% } else {
      int i = 0;
      for (Application a : reviewApps) {
        String bg = colors[i % colors.length];
        i++;
  %>
  <div class="applicant-card">
    <div class="applicant-avatar" style="background:<%= bg %>"><%= JspUtil.initials(a.getTaName()) %></div>
    <div class="applicant-info">
      <div class="ai-name"><%= JspUtil.esc(a.getTaName()) %></div>
      <div class="ai-detail"><%= JspUtil.esc(a.getTaId()) %> · Applied: <%= JspUtil.esc(a.getAppliedDate()) %></div>
    </div>
    <span class="badge <%= JspUtil.badgeClass(a.getStatus()) %>" style="margin-right:8px"><%= JspUtil.esc(a.getStatus()) %></span>
    <% if ("PENDING".equals(a.getStatus())) { %>
    <div class="applicant-actions">
      <form method="post" action="<%= ctx %>/mo/review" style="display:inline-block;max-width:320px">
        <input type="hidden" name="applicationId" value="<%= JspUtil.esc(a.getId()) %>">
        <input type="hidden" name="returnPositionId" value="<%= JspUtil.esc(selId) %>">
        <div class="form-group"><textarea class="form-textarea" name="feedback" placeholder="Feedback" rows="2"></textarea></div>
        <button type="submit" name="status" value="PASSED" class="btn btn-sm btn-success">Accept</button>
        <button type="submit" name="status" value="FAILED" class="btn btn-sm btn-danger">Reject</button>
      </form>
    </div>
    <% } %>
  </div>
  <% } } %>
</div>
</main>
</div>
</body>
</html>
