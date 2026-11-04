. (Join-Path $PSScriptRoot 'environment.ps1')
Set-Location (Join-Path $projectRoot 'frontend')
& npm.cmd run build
if ($LASTEXITCODE) {throw 'Falha no build Angular.'}
Set-Location (Join-Path $projectRoot 'backend')
& mvn.cmd "-Dmaven.repo.local=$mavenRepository" -o test
if ($LASTEXITCODE) {throw 'Falha nos testes Java.'}
