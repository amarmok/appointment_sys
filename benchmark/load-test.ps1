param(
    [int]$Requests = 100,
    [string]$Token
)

if ([string]::IsNullOrWhiteSpace($Token)) {
    Write-Host "Token is required. Use -Token <JWT>."
    exit 1
}

[System.Net.ServicePointManager]::ServerCertificateValidationCallback = { $true }

$uri = "https://localhost:8443/api/services"
$headers = @{ Authorization = "Bearer $Token" }
$times = @()

for ($i = 0; $i -lt $Requests; $i++) {
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    try {
        Invoke-RestMethod -Uri $uri -Headers $headers -Method Get | Out-Null
    } catch {
        Write-Host "Request failed: $($_.Exception.Message)"
    }
    $sw.Stop()
    $times += $sw.Elapsed.TotalMilliseconds
}

$avg = ($times | Measure-Object -Average).Average
Write-Host ("Average latency: {0:N2} ms over {1} requests" -f $avg, $Requests)
