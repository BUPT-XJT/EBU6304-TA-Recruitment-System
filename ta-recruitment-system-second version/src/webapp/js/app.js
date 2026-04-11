const API = '/api';

async function api(path, options = {}) {
  const resp = await fetch(API + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  });
  const data = await resp.json();
  if (!resp.ok) throw new Error(data.error || 'Request failed');
  return data;
}

function getUser() {
  const u = sessionStorage.getItem('user');
  return u ? JSON.parse(u) : null;
}

function setUser(u) {
  sessionStorage.setItem('user', JSON.stringify(u));
}

function clearUser() {
  sessionStorage.removeItem('user');
}

function requireLogin(role) {
  const user = getUser();
  if (!user) { location.href = '/index.html'; return null; }
  if (role && user.role !== role) { location.href = '/index.html'; return null; }
  return user;
}

function logout() {
  api('/logout', { method: 'POST' }).catch(() => {});
  clearUser();
  location.href = '/index.html';
}

function toast(msg, type = 'success') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const el = document.createElement('div');
  el.className = 'toast toast-' + type;
  el.textContent = msg;
  container.appendChild(el);
  setTimeout(() => el.remove(), 3000);
}

function badgeClass(status) {
  const map = {
    'APPROVED': 'badge-success', 'PASSED': 'badge-success',
    'PENDING': 'badge-warning',
    'REJECTED': 'badge-danger', 'FAILED': 'badge-danger',
    'CLOSED': 'badge-gray'
  };
  return map[status] || 'badge-info';
}

function initials(name) {
  if (!name) return '?';
  const parts = name.trim().split(/\s+/);
  if (parts.length >= 2) return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  return name.substring(0, 2).toUpperCase();
}

function renderSidebar(role, activePage) {
  const user = getUser();
  const name = user ? user.name : '';
  const avatarColors = ['#2563EB', '#7C3AED', '#059669', '#D97706', '#DC2626'];
  const color = avatarColors[name.length % avatarColors.length];

  const menus = {
    TA: [
      { section: 'Main' },
      { icon: '📊', label: 'Dashboard', href: '/ta/dashboard.html', id: 'dashboard' },
      { icon: '👤', label: 'My Profile', href: '/ta/profile.html', id: 'profile' },
      { section: 'Recruitment' },
      { icon: '💼', label: 'Browse Positions', href: '/ta/positions.html', id: 'positions' },
      { icon: '📋', label: 'My Applications', href: '/ta/applications.html', id: 'applications' },
    ],
    MO: [
      { section: 'Main' },
      { icon: '📊', label: 'Dashboard', href: '/mo/dashboard.html', id: 'dashboard' },
      { section: 'Management' },
      { icon: '📝', label: 'Publish Position', href: '/mo/publish.html', id: 'publish' },
      { icon: '👥', label: 'Review Applications', href: '/mo/review.html', id: 'review' },
      { icon: '📄', label: 'Offer Letters', href: '/mo/offers.html', id: 'offers' },
    ],
    ADMIN: [
      { section: 'Main' },
      { icon: '📊', label: 'Dashboard', href: '/admin/dashboard.html', id: 'dashboard' },
      { section: 'Management' },
      { icon: '✅', label: 'Approve Positions', href: '/admin/approve.html', id: 'approve' },
      { icon: '📈', label: 'Statistics', href: '/admin/stats.html', id: 'stats' },
      { icon: '⏱', label: 'TA Workload', href: '/admin/workload.html', id: 'workload' },
      { icon: '👥', label: 'Manage Users', href: '/admin/users.html', id: 'users' },
    ]
  };

  let html = `
    <a class="sidebar-brand" href="/${role.toLowerCase()}/dashboard.html">
      <div class="sb-icon">TA</div>
      <span>TA Recruitment</span>
    </a>`;

  const items = menus[role] || [];
  for (const item of items) {
    if (item.section) {
      html += `<div class="sidebar-section">${item.section}</div>`;
    } else {
      const cls = item.id === activePage ? 'sidebar-item active' : 'sidebar-item';
      html += `<a class="${cls}" href="${item.href}">
        <span class="si-icon">${item.icon}</span>${item.label}</a>`;
    }
  }

  html += `
    <div class="sidebar-user">
      <div class="sidebar-avatar" style="background:${color}">${initials(name)}</div>
      <div>
        <div style="font-weight:600;font-size:13px">${name}</div>
        <div style="font-size:11px;color:rgba(255,255,255,0.4)">${role}</div>
      </div>
      <button class="sidebar-logout" onclick="logout()" title="Logout">⏻</button>
    </div>`;

  document.querySelector('.sidebar').innerHTML = html;
}

function formatDate(dateStr) {
  if (!dateStr) return '-';
  return dateStr;
}

function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
