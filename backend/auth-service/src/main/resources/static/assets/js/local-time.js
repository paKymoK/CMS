// Times arrive from the server as UTC (<time datetime="...Z">); show them in the viewer's own zone.
// Without JavaScript the UTC text the server wrote stays, and it says UTC.
document.querySelectorAll('time.ts[datetime]').forEach(function (el) {
  var d = new Date(el.getAttribute('datetime'));
  if (isNaN(d)) return;
  var dateOnly = el.hasAttribute('data-date-only');
  var opts = dateOnly
    ? { day: '2-digit', month: 'short', year: 'numeric' }
    : { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' };
  el.title = el.textContent;
  el.textContent = d.toLocaleString(undefined, opts);
});
