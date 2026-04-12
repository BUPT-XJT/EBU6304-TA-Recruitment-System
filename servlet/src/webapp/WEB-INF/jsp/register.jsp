<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%
    String err = (String) request.getAttribute("error");
    String newId = (String) request.getAttribute("newUserId");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Register - BUPT TA Recruitment System</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="login-layout">
  <div class="login-left">
    <h2>Join as a Teaching Assistant</h2>
    <p>Register to start browsing and applying for TA positions across various departments.</p>
    <div class="ll-features">
      <div class="ll-feat"><div class="llf-icon" aria-hidden="true"></div><span>Open to all eligible students</span></div>
      <div class="ll-feat"><div class="llf-icon" aria-hidden="true"></div><span>Multiple positions available</span></div>
      <div class="ll-feat"><div class="llf-icon" aria-hidden="true"></div><span>Build your academic career</span></div>
    </div>
  </div>
  <div class="login-right">
    <div class="login-card">
    <h3>Create Account</h3>
    <div class="lr-sub">Register as a Teaching Assistant candidate</div>
    <% if (newId != null) { %>
    <div class="note-box" style="margin-bottom:16px;border-left:4px solid var(--success)">
      Registration successful! Your ID is <strong><%= JspUtil.esc(newId) %></strong>.
      <a href="<%= ctx %>/login">Sign in</a>
    </div>
    <% } %>
    <% if (err != null) { %>
    <div class="note-box" style="margin-bottom:16px;border-left:4px solid var(--danger)"><%= JspUtil.esc(err) %></div>
    <% } %>
    <% if (newId == null) { %>
    <form method="post" action="<%= ctx %>/register" autocomplete="off">
      <div class="form-group">
        <label class="form-label">Full Name</label>
        <input class="form-input" type="text" name="name" placeholder="Enter your full name" required>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label class="form-label">Email</label>
          <input class="form-input" type="email" name="email" placeholder="your@email.com" required>
        </div>
        <div class="form-group">
          <label class="form-label">Phone</label>
          <input class="form-input" type="text" name="phone" placeholder="Phone number">
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">Programme</label>
        <input class="form-input" type="text" name="programme" placeholder="e.g. Computer Science">
      </div>
      <div class="form-row">
        <div class="form-group">
          <label class="form-label">Password</label>
          <input class="form-input" type="password" name="password" placeholder="Create password" required>
        </div>
        <div class="form-group">
          <label class="form-label">Confirm Password</label>
          <input class="form-input" type="password" name="confirm" placeholder="Confirm password" required>
        </div>
      </div>
      <button type="submit" class="btn btn-primary btn-lg w-full" style="justify-content:center">Create Account</button>
    </form>
    <% } %>
    <div class="login-footer">
      Already have an account? <a href="<%= ctx %>/login">Sign In</a>
    </div>
    </div>
  </div>
</div>
</body>
</html>
