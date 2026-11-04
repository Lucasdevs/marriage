param([switch]$Postgres)
. (Join-Path $PSScriptRoot 'environment.ps1')
Set-Location (Join-Path $projectRoot 'backend')
$arguments=@("-Dmaven.repo.local=$mavenRepository",'spring-boot:run')
if ($Postgres) {$arguments+='-Dspring-boot.run.profiles=postgres'}
& mvn.cmd @arguments
