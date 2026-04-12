<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.User" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%
    User u = (User) request.getAttribute("profileUser");
    String ctx = request.getContextPath();
    String skillsComma = (u.getSkills() == null || u.getSkills().isEmpty()) ? "" : u.getSkills().replace(";", ", ");
    String[] skillParts = u.getSkills() != null ? u.getSkills().split(";") : new String[0];
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>My Profile - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>My Profile</h2><div class="mt-desc">Manage your personal information</div></div>
</div>
<div class="grid-2">
  <div class="card">
    <div class="card-title" style="margin-bottom:16px">Personal Information</div>
    <form method="post" action="<%= ctx %>/ta/profile">
      <input type="hidden" name="formAction" value="save">
      <div class="form-row">
        <div class="form-group"><label class="form-label">Full Name</label><input class="form-input" name="name" value="<%= JspUtil.esc(u.getName()) %>"></div>
        <div class="form-group"><label class="form-label">Student ID</label><input class="form-input" value="<%= JspUtil.esc(u.getId()) %>" disabled></div>
      </div>
      <div class="form-row">
        <div class="form-group"><label class="form-label">Email</label><input class="form-input" value="<%= JspUtil.esc(u.getEmail()) %>" disabled></div>
        <div class="form-group"><label class="form-label">Phone</label><input class="form-input" name="phone" value="<%= JspUtil.esc(u.getPhone()) %>"></div>
      </div>
      <div class="form-row">
        <div class="form-group"><label class="form-label">Programme</label><input class="form-input" name="programme" value="<%= JspUtil.esc(u.getProgramme()) %>"></div>
        <div class="form-group"><label class="form-label">Year of Study</label>
          <select class="form-select" name="yearOfStudy">
            <option value="">Select</option>
            <% String y = u.getYearOfStudy() != null ? u.getYearOfStudy() : "";
            String[] opts = {"Year 1", "Year 2", "Year 3", "Year 4", "Postgraduate"};
            for (String o : opts) { %>
            <option value="<%= JspUtil.esc(o) %>" <%= o.equals(y) ? "selected" : "" %>><%= JspUtil.esc(o) %></option>
            <% } %>
          </select>
        </div>
      </div>
      <div class="form-row">
        <div class="form-group"><label class="form-label">University</label><input class="form-input" name="university" value="<%= JspUtil.esc(u.getUniversity()) %>"></div>
        <div class="form-group"><label class="form-label">GPA</label><input class="form-input" name="gpa" value="<%= JspUtil.esc(u.getGpa()) %>" placeholder="e.g. 3.7"></div>
      </div>
      <div class="form-group"><label class="form-label">Skills (comma separated)</label><input class="form-input" name="skills" value="<%= JspUtil.esc(skillsComma) %>" placeholder="Java, Python, SQL"></div>
      <div class="form-group"><label class="form-label">Experience</label><textarea class="form-textarea" name="experience" placeholder="Describe your relevant experience"><%= JspUtil.esc(u.getExperience()) %></textarea></div>
      <button type="submit" class="btn btn-primary">Save Changes</button>
    </form>
  </div>
  <div>
    <div class="card" style="margin-bottom:16px">
      <div class="card-title" style="margin-bottom:16px">Change Password</div>
      <form method="post" action="<%= ctx %>/ta/profile">
        <input type="hidden" name="formAction" value="password">
        <div class="form-group"><label class="form-label">Current Password</label><input class="form-input" type="password" name="oldPassword" required></div>
        <div class="form-group"><label class="form-label">New Password</label><input class="form-input" type="password" name="newPassword" required></div>
        <div class="form-group"><label class="form-label">Confirm New Password</label><input class="form-input" type="password" name="confirmPassword" required></div>
        <button type="submit" class="btn btn-secondary">Update Password</button>
      </form>
    </div>
    <div class="card">
      <div class="card-title">Profile Summary</div>
      <div style="margin-top:12px;font-size:13px;color:var(--gray-500)">
        <div style="text-align:center;margin-bottom:16px">
          <div class="sidebar-avatar" style="width:64px;height:64px;font-size:24px;margin:0 auto 8px;background:var(--primary)"><%= JspUtil.initials(u.getName()) %></div>
          <div style="font-weight:700;color:var(--gray-800);font-size:15px"><%= JspUtil.esc(u.getName()) %></div>
          <div style="color:var(--gray-400);font-size:12px"><%= JspUtil.esc(u.getId()) %> · <%= u.getProgramme() == null || u.getProgramme().isEmpty() ? "No programme" : JspUtil.esc(u.getProgramme()) %></div>
        </div>
        <div style="border-top:1px solid var(--gray-100);padding-top:12px">
          <div style="margin-bottom:8px"><strong>University:</strong> <%= u.getUniversity() == null || u.getUniversity().isEmpty() ? "-" : JspUtil.esc(u.getUniversity()) %></div>
          <div style="margin-bottom:8px"><strong>GPA:</strong> <%= u.getGpa() == null || u.getGpa().isEmpty() ? "-" : JspUtil.esc(u.getGpa()) %></div>
          <div style="margin-bottom:8px"><strong>Skills:</strong></div>
          <div>
            <% boolean any = false;
            for (String s : skillParts) {
              if (s != null && !s.trim().isEmpty()) { any = true; %>
            <span class="tag"><%= JspUtil.esc(s.trim()) %></span>
            <% } }
            if (!any) { %>-<% } %>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</main>
</div>
</body>
</html>
