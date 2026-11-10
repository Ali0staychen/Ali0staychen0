$src = 'C:\Users\ali\Documents\Craft Anything\base\checks\app-screenshot.png'
$out = $args[0]
if (-not $out) { $out = 'C:\Users\ali\Documents\Craft Anything\base\checks\app-screenshot.png' }
$dir = Split-Path -Parent $out
if ($dir) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
Copy-Item -Force $src $out
exit 0
