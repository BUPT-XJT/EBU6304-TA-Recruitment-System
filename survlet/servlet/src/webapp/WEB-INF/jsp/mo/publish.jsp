<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Publish Position - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>Publish New Position</h2><div class="mt-desc">Create a new TA position for your course</div></div>
</div>
<div class="card" style="max-width:720px">
  <form method="post" action="<%= ctx %>/mo/publish">
    <div class="form-row">
      <div class="form-group"><label class="form-label">Course Name</label><input class="form-input" name="courseName" placeholder="e.g. Software Engineering" required></div>
      <div class="form-group"><label class="form-label">Course Code</label><input class="form-input" name="courseCode" placeholder="e.g. EBU6304" required></div>
    </div>
    <div class="form-row">
      <div class="form-group"><label class="form-label">Department</label><input class="form-input" name="department" placeholder="e.g. International School" required></div>
      <div class="form-group"><label class="form-label">Number of Positions</label><input class="form-input" type="number" name="numPositions" min="1" value="1" required></div>
    </div>
    <div class="form-row">
      <div class="form-group"><label class="form-label">Hours per Week</label><input class="form-input" type="number" name="hoursPerWeek" min="0" value="6" required></div>
      <div class="form-group"><label class="form-label">Pay Rate</label><input class="form-input" name="payRate" placeholder="e.g. 50/hr" required></div>
    </div>
    <div class="form-group"><label class="form-label">Application Deadline</label><input class="form-input" type="date" name="deadline" required></div>
    <div class="form-group"><label class="form-label">Required Skills (semicolon separated)</label><input class="form-input" name="requiredSkills" placeholder="e.g. Java;Agile;Testing"></div>
    <div class="form-group"><label class="form-label">Duties (one per line)</label><textarea class="form-textarea" name="duties" rows="4" placeholder="Assist in lab sessions&#10;Grade assignments"></textarea></div>
    <div class="note-box">After submission, the position will require admin approval before it becomes visible to TAs.</div>
    <div class="modal-actions" style="margin-top:20px">
      <a href="<%= ctx %>/mo/dashboard" class="btn btn-secondary">Cancel</a>
      <button type="submit" class="btn btn-primary">Publish Position</button>
    </div>
  </form>
</div>
</main>
</div>
</body>
</html>
