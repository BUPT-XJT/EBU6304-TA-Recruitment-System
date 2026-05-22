function deadlineBadgeClass(urgency) {
  if (urgency === 'EXPIRED') return 'badge-deadline-expired';
  if (urgency === 'CLOSED') return 'badge-deadline-closed';
  if (urgency === 'DEADLINE_TODAY') return 'badge-deadline-today';
  if (urgency === 'CLOSING_SOON') return 'badge-deadline-soon';
  return '';
}

function renderDeadlineBadge(p) {
  if (!p || !p.deadlineLabel) return '';
  return `<span class="badge ${deadlineBadgeClass(p.deadlineUrgency)}">${escapeHtml(p.deadlineLabel)}</span>`;
}

function formatDaysLeft(days) {
  if (days == null || days === undefined) return '';
  if (days < 0) return 'Deadline passed';
  if (days === 0) return 'Due today';
  if (days === 1) return '1 day left';
  return days + ' days left';
}
