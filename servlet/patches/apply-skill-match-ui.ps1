# Run from servlet/ after closing IDE tabs that lock webapp HTML files.
$webapp = Join-Path $PSScriptRoot "..\src\webapp"
@(
  @("ta-positions.html", "ta\positions.html"),
  @("ta-saved.html", "ta\saved.html"),
  @("mo-review.html", "mo\review.html"),
  @("ta-dashboard.html", "ta\dashboard.html")
) | ForEach-Object {
  Copy-Item -Force (Join-Path $PSScriptRoot $_[0]) (Join-Path $webapp $_[1])
}
Write-Host "Applied patch HTML files to src/webapp"
