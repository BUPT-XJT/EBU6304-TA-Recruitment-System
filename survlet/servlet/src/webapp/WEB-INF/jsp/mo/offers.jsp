<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.Application" %>
<%@ page import="com.bupt.ta.web.Position" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.util.List" %>
<%
    List<Position> approvedMine = (List<Position>) request.getAttribute("myApprovedPositions");
    String selId = (String) request.getAttribute("selectedOfferPositionId");
    Position pos = (Position) request.getAttribute("offerPosition");
    List<Application> passed = (List<Application>) request.getAttribute("offerApplications");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Offer Letters - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>Offer Letters</h2><div class="mt-desc">Preview offer letters for accepted TAs</div></div>
</div>
<form method="get" action="<%= ctx %>/mo/offers" class="form-group" style="max-width:400px">
  <label class="form-label">Select Position</label>
  <select class="form-select" name="positionId" onchange="this.form.submit()">
    <option value="">-- Choose a position --</option>
    <% for (Position p : approvedMine) { %>
    <option value="<%= JspUtil.esc(p.getId()) %>" <%= p.getId().equals(selId) ? "selected" : "" %>><%= JspUtil.esc(p.getCourseName()) %> (<%= JspUtil.esc(p.getCourseCode()) %>)</option>
    <% } %>
  </select>
</form>
<div id="offersArea">
  <% if (pos == null || passed == null) { %>
  <% } else if (passed.isEmpty()) { %>
  <div class="empty-state"><div class="es-icon">&mdash;</div><div class="es-text">No accepted applicants for this position yet</div></div>
  <% } else for (Application a : passed) { %>
  <div class="offer-preview" style="margin-bottom:24px">
    <div class="op-header">
      <div style="font-size:12px;color:var(--gray-400);margin-bottom:4px">Beijing University of Posts and Telecommunications</div>
      <h3>Teaching Assistant Offer Letter</h3>
    </div>
    <div class="op-body">
      <p>Dear <span class="op-field"><%= JspUtil.esc(a.getTaName()) %></span>,</p>
      <p style="margin-top:12px">We are pleased to offer you the position of <strong>Teaching Assistant</strong> for the course:</p>
      <p style="margin:12px 0"><span class="op-field"><%= JspUtil.esc(pos.getCourseName()) %></span> (<span class="op-field"><%= JspUtil.esc(pos.getCourseCode()) %></span>)</p>
      <p><strong>Details:</strong></p>
      <ul style="padding-left:20px;margin:8px 0">
        <li>Department: <%= JspUtil.esc(pos.getDepartment()) %></li>
        <li>Hours per Week: <%= pos.getHoursPerWeek() %></li>
        <li>Pay Rate: <%= JspUtil.esc(pos.getPayRate()) %></li>
        <li>Module Owner: <%= JspUtil.esc(pos.getMoName()) %></li>
      </ul>
      <p style="margin-top:12px">Please confirm your acceptance by responding to this offer within 7 days.</p>
      <p style="margin-top:16px">Best regards,<br><strong><%= JspUtil.esc(pos.getMoName()) %></strong><br><%= JspUtil.esc(pos.getDepartment()) %></p>
    </div>
  </div>
  <% } %>
</div>
</main>
</div>
</body>
</html>
