# Native verification and artifact hash for build 0.32.
# Usage: powershell -ExecutionPolicy Bypass -File scripts/verify_032.ps1
# Never touches worlds under MahouTsukai-Fabric/run/server.

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$project = Join-Path $root 'MahouTsukai-Fabric'
$env:JAVA_HOME = Join-Path $root 'tools\jdk\jdk-25.0.4.1+1'
if (!(Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) { throw "Project JDK not found at $env:JAVA_HOME" }

$progressionMarker = Join-Path $root 'analysis\phase026-progression-server.passed'
$economyMarker = Join-Path $project 'run\economy-checks\economy-checks.passed'

function Invoke-Gradle([string[]]$arguments) {
    & (Join-Path $project 'gradlew.bat') --offline @arguments
    if ($LASTEXITCODE -ne 0) { throw "Gradle failed: gradlew $($arguments -join ' ')" }
}

function Assert-FreshMarker([string]$path, [datetime]$since, [string]$label) {
    if (!(Test-Path $path)) { throw "$label marker missing: $path" }
    if ((Get-Item $path).LastWriteTime -lt $since) { throw "$label marker is stale: $path" }
}

Push-Location $project
try {
    Write-Host '== compile =='
    Invoke-Gradle @('compileJava', 'compileClientJava', 'selfCheckClasses')

    Write-Host '== progression checks =='
    $stamp = Get-Date
    Invoke-Gradle @('runProgressionChecks')
    Assert-FreshMarker $progressionMarker $stamp 'Progression'

    Write-Host '== economy checks =='
    $stamp = Get-Date
    Invoke-Gradle @('runEconomyChecks')
    Assert-FreshMarker $economyMarker $stamp 'Economy'

    Write-Host '== build =='
    Invoke-Gradle @('build')

    $jar = Get-ChildItem (Join-Path $project 'build\libs') -Filter '*0.32-incomplete.jar' |
        Where-Object { $_.Name -notmatch 'sources' } | Select-Object -First 1
    if ($null -eq $jar) { throw 'Built JAR not found in build/libs' }
    $hash = (Get-FileHash -Algorithm SHA256 $jar.FullName).Hash.ToLower()
    Write-Host ''
    Write-Host "JAR:     $($jar.FullName)"
    Write-Host "Size:    $($jar.Length) bytes"
    Write-Host "SHA-256: $hash"

    $sums = Join-Path $root 'artifacts\releases\v1.36.36-fabric.0.32\SHA256SUMS.txt'
    if (Test-Path $sums) {
        if (Select-String -Path $sums -SimpleMatch $hash) {
            Write-Host 'Release copy SHA256SUMS matches the fresh build.'
        } else {
            Write-Warning 'Release SHA256SUMS does not match the fresh build; refresh the artifact copy before sharing.'
        }
    }
} finally {
    Pop-Location
}
