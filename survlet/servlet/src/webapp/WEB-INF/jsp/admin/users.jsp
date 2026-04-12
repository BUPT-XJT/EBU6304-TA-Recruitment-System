<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.ta.web.User" %>
<%@ page import="com.bupt.ta.web.jsp.JspUtil" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="java.util.List" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    String roleF = (String) request.getAttribute("userFilterRole");
    String q = (String) request.getAttribute("userFilterQ");
    if (q == null) q = "";
    String qe = URLEncoder.encode(q, StandardCharsets.UTF_8);
    String ctx = request.getContextPath();
    String[] colors = {"#2563EB", "#7C3AED", "#059669", "#D97706", "#DC2626"};
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Manage Users - BUPT TA Recruitment</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
<link rel="stylesheet" href="<%= ctx %>/css/polish-overrides.css">
</head>
<body>
<div class="app-layout">
<%@ include file="../includes/sidebar.jspf" %>
<main class="main-content">
<%@ include file="../includes/flash.jspf" %>
<div class="main-top">
  <div><h2>Manage Users</h2><div class="mt-desc">View and manage all system users</div></div>
</div>
<div class="tabs">
  <a class="tab<%= "ALL".equals(roleF)?" active":"" %>" href="<%= ctx %>/admin/users?role=ALL&q=<%= qe %>">All Users</a>
  <a class="tab<%= "TA".equals(roleF)?" active":"" %>" href="<%= ctx %>/admin/users?role=TA&q=<%= qe %>">TAs</a>
  <a class="tab<%= "MO".equals(roleF)?" active":"" %>" href="<%= ctx %>/admin/users?role=MO&q=<%= qe %>">Module Owners</a>
  <a class="tab<%= "ADMIN".equals(roleF)?" active":"" %>" href="<%= ctx %>/admin/users?role=ADMIN&q=<%= qe %>">Admins</a>
</div>
<form method="get" action="<%= ctx %>/admin/users" class="search-bar">
  <input type="hidden" name="role" value="<%= JspUtil.esc(roleF) %>">
  <input class="search-input" type="search" name="q" value="<%= JspUtil.esc(q) %>" placeholder="Search users by name, ID, or email...">
  <button type="submit" class="btn btn-secondary">Search</button>
</form>
<div class="table-wrapper">
  <table>
    <thead><tr><th>User</th><th>ID</th><th>Email</th><th>Role</th><th>Programme</th><th>Skills</th></tr></thead>
    <tbody>
      <% if (users.isEmpty()) { %>
      <tr><td colspan="6" style="text-align:center;padding:40px;color:var(--gray-400)">No users found</td></tr>
      <% } else {
        int i = 0;
        for (User u : users) {
          String bg = colors[i % colors.length];
          i++;
          String[] skills = u.getSkills() != null ? u.getSkills().split(";") : new String[0];
      %>
      <tr>
        <td>
          <div style="display:flex;align-items:center;gap:10px">
            <div class="sidebar-avatar" style="width:32px;height:32px;font-size:12px;background:<%= bg %>"><%= JspUtil.initials(u.getName()) %></div>
            <strong><%= JspUtil.esc(u.getName()) %></strong>
          </div>
        </td>
        <td><%= JspUtil.esc(u.getId()) %></td>
        <td><%= JspUtil.esc(u.getEmail()) %></td>
        <td><span class="badge <%= JspUtil.roleBadgeClass(u.getRole()) %>"><%= JspUtil.esc(u.getRole()) %></span></td>
        <td><%= u.getProgramme() == null || u.getProgramme().isEmpty() ? "-" : JspUtil.esc(u.getProgramme()) %></td>
        <td>
          <% int shown = 0;
          for (String sk : skills) {
            if (sk == null || sk.trim().isEmpty()) continue;
            if (shown >= 3) break;
            shown++; %>
          <span class="tag"><%= JspUtil.esc(sk.trim()) %></span>
          <% }
          if (shown == 0) { %>-<% } %>
        </td>
      </tr>
      <% } } %>
    </tbody>
  </table>
</div>
</main>
</div>
</body>
</html>
