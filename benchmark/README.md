# Benchmark

This folder contains a lightweight PowerShell load test you can run against
`https://localhost:8443`.

## Prereqs
- App running with HTTPS enabled.
- A valid JWT for a user with access to `/api/services` (any authenticated user).

## Run
```powershell
.\benchmark\load-test.ps1 -Requests 100 -Token "<JWT>"
```

## What It Does
The script performs repeated GET requests to `/api/services`, records latency,
and prints the average response time. Use the output as basic stress-test
evidence.
