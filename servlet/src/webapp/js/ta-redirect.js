(function () {
  const path = location.pathname.replace(/\/+$/, '');
  if (path.endsWith('/ta/dashboard.html')) {
    location.replace('/ta/dash.html' + location.search + location.hash);
  } else if (path.endsWith('/ta/positions.html')) {
    location.replace('/ta/browse.html' + location.search + location.hash);
  }
})();
