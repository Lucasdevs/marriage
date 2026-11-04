$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$toolRoot = Join-Path $projectRoot '.tools'
$fallback = Join-Path $env:USERPROFILE 'Documents/Tcc/.tools'
foreach ($root in @($toolRoot, $fallback)) {
 if (Test-Path $root) {
  $javaTool = Get-ChildItem $root -Directory -Filter 'jdk-*' | Select-Object -First 1
  $nodeTool = Get-ChildItem $root -Directory -Filter 'node-*-win-x64' | Select-Object -First 1
  $mavenTool = Get-ChildItem $root -Directory -Filter 'apache-maven-*' | Select-Object -First 1
  if ($javaTool) { $env:JAVA_HOME=$javaTool.FullName; $env:PATH=(Join-Path $javaTool.FullName 'bin')+';'+$env:PATH }
  if ($nodeTool) { $env:PATH=$nodeTool.FullName+';'+$env:PATH }
  if ($mavenTool) { $env:PATH=(Join-Path $mavenTool.FullName 'bin')+';'+$env:PATH }
 }
}
$mavenRepository=Join-Path $toolRoot 'm2'
