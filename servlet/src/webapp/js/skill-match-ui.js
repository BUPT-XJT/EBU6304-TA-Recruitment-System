function skillMatchBadgeClass(score) {
  if (score >= 80) return 'badge-success';
  if (score >= 50) return 'badge-warning';
  return 'badge-danger';
}

/** Rule-based skill match from API (skillMatch object). */
function renderSkillMatch(sm, compact) {
  if (!sm || sm.matchScore == null) return '';
  const score = sm.matchScore;
  const badge = `<span class="badge ${skillMatchBadgeClass(score)}">${score}% match</span>`;
  if (compact) {
    return `<div class="skill-match-row">${badge}</div>`;
  }
  const matched = (sm.matchedSkills || []);
  const missing = (sm.missingSkills || []);
  let tags = '';
  if (matched.length) {
    tags += '<div class="skill-match-label">Matched</div><div class="skill-match-tags">'
      + matched.map(s => `<span class="tag tag-match">${escapeHtml(s)}</span>`).join(' ')
      + '</div>';
  }
  if (missing.length) {
    tags += '<div class="skill-match-label">Missing</div><div class="skill-match-tags">'
      + missing.map(s => `<span class="tag tag-missing">${escapeHtml(s)}</span>`).join(' ')
      + '</div>';
  }
  const summary = sm.summary ? `<div class="skill-match-summary">${escapeHtml(sm.summary)}</div>` : '';
  return `<div class="skill-match-block">${badge}${summary}${tags}</div>`;
}
