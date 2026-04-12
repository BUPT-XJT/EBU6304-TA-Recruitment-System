<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%
    String err = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login - BUPT TA Recruitment System</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="login-layout">
  <div class="login-left">
    <h2>BUPT TA Recruitment System</h2>
    <p>A comprehensive platform for managing Teaching Assistant recruitment at Beijing University of Posts and Telecommunications.</p>
    <p style="margin-top:12px;font-size:13px;opacity:.9"><a href="<%= ctx %>/jsp/open-positions.jsp">Server-side JSP: approved positions list</a></p>
    <div class="ll-features">
      <div class="ll-feat"><div class="llf-icon" aria-hidden="true"></div><span>Browse and apply for TA positions</span></div>
      <div class="ll-feat"><div class="llf-icon" aria-hidden="true"></div><span>Track application status in real-time</span></div>
      <div class="ll-feat"><div class="llf-icon" aria-hidden="true"></div><span>Streamlined review and approval process</span></div>
    </div>
  </div>
  <div class="login-right">
    <div class="login-card">
    <h3>Welcome Back</h3>
    <div class="lr-sub">Sign in to your account to continue</div>
    <% if (err != null) { %>
    <div class="note-box" style="margin-bottom:16px;border-left:4px solid var(--danger)"><%= JspUtil.esc(err) %></div>
    <% } %>
    <form method="post" action="<%= ctx %>/login" autocomplete="off">
      <div class="form-group">
        <label class="form-label">User ID</label>
        <input class="form-input" type="text" name="userId" id="loginUserId" placeholder="e.g. TA001" required autocomplete="off">
      </div>
      <div class="form-group">
        <label class="form-label">Password</label>
        <input class="form-input" type="password" name="password" id="loginPassword" placeholder="Enter your password" required autocomplete="off">
      </div>
      <button type="submit" class="btn btn-primary btn-lg w-full" style="justify-content:center">Sign In</button>
    </form>
    <div class="login-footer">
      Don't have an account? <a href="<%= ctx %>/register">Register as TA</a>
    </div>
    <div class="note-box" style="margin-top:24px">
      <strong>Demo Accounts:</strong><br>
      TA: TA001 / 123 &nbsp;|&nbsp; MO: MO001 / 123 &nbsp;|&nbsp; Admin: ADMIN001 / 123
    </div>
    </div>
  </div>
</div>
<script>
(function () {
  var p = new URLSearchParams(location.search);
  if (p.get('logout') === '1') {
    var u = document.getElementById('loginUserId');
    var pw = document.getElementById('loginPassword');
    if (u) { u.value = ''; }
    if (pw) { pw.value = ''; }
    history.replaceState(null, '', '<%= ctx %>/login');
  }
})();
</script>
</body>
</html>
