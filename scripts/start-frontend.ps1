. (Join-Path $PSScriptRoot 'environment.ps1')
Set-Location (Join-Path $projectRoot 'frontend')
if (!(Test-Path node_modules)) { & npm.cmd ci --cache (Join-Path $toolRoot 'npm-cache'); if ($LASTEXITCODE) {throw 'Falha ao instalar dependências.'} }
& npm.cmd start -- --port 4201
