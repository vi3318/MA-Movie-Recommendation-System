# Test API Endpoints

$apiGatewayUrl = "http://localhost:8080"
$resultsFile = "api-test-results.md"

# Initialize results file with header
@"
# API Endpoints Test Results
Test Date: $(Get-Date)

"@ | Out-File -FilePath $resultsFile

# Function to test an endpoint
function Test-Endpoint {
    param (
        [string]$Method,
        [string]$Endpoint,
        [string]$Description,
        [string]$RequestBody = "",
        [string]$ContentType = "application/json"
    )

    $url = "$apiGatewayUrl$Endpoint"

    Write-Host "Testing $Method $url - $Description"

    try {
        $params = @{
            Method = $Method
            Uri = $url
        }

        if ($RequestBody -ne "") {
            $params.Body = $RequestBody
            $params.ContentType = $ContentType
        }

        $response = Invoke-WebRequest @params
        $status = "✅ Pass (Status: $($response.StatusCode))"
        
        # Extract response content
        $content = $response.Content
        if ($content.Length -gt 500) {
            $content = $content.Substring(0, 500) + "... (truncated)"
        }
    }
    catch {
        $status = "❌ Fail (Error: $($_.Exception.Message))"
        $content = $_.Exception.Response
    }

    # Append to results file
    @"
## $Method $Endpoint
- Description: $Description
- Status: $status
- Response: 
```
$content
```

"@ | Out-File -FilePath $resultsFile -Append

    return $status
}

# Test auth endpoint to get token
$loginBody = '{ "username": "testuser1", "password": "password" }'
$loginStatus = Test-Endpoint -Method "POST" -Endpoint "/api/auth/login" -Description "Login user" -RequestBody $loginBody

# Extract token if login succeeded
$token = ""
if ($loginStatus -like "*Pass*") {
    try {
        $loginResponse = Invoke-WebRequest -Method POST -Uri "$apiGatewayUrl/api/auth/login" -Body $loginBody -ContentType "application/json"
        $token = ($loginResponse.Content | ConvertFrom-Json).token
        Write-Host "Authentication token obtained"

        # Append to results file
        @"
## Authentication
- Token obtained successfully
- Will use for authorized requests

"@ | Out-File -FilePath $resultsFile -Append
    }
    catch {
        Write-Host "Failed to extract token: $_"
    }
}

# Test Movie Service endpoints
Test-Endpoint -Method "GET" -Endpoint "/api/movies" -Description "Get all movies"
Test-Endpoint -Method "GET" -Endpoint "/api/movies/67f7d63f9329fd8539a02292" -Description "Get movie by ID (3 Idiots)"
Test-Endpoint -Method "GET" -Endpoint "/api/movies/search?title=Dangar" -Description "Search movies by title"

# Test Review Service endpoints
Test-Endpoint -Method "GET" -Endpoint "/api/reviews" -Description "Get all reviews"
Test-Endpoint -Method "GET" -Endpoint "/api/reviews/movie/67f7d63f9329fd8539a02292" -Description "Get reviews for movie (3 Idiots)"
Test-Endpoint -Method "GET" -Endpoint "/api/reviews/user/67f7cde591b50e155f293879" -Description "Get reviews by user (testuser1)"
Test-Endpoint -Method "GET" -Endpoint "/api/reviews/movie/67f7d63f9329fd8539a02292/average-rating" -Description "Get average rating for movie (3 Idiots)"

# Test Recommendation Service endpoints with authorization if token is available
if ($token -ne "") {
    $authHeader = @{
        "Authorization" = "Bearer $token"
    }

    # Test user profile with authorization
    try {
        $response = Invoke-WebRequest -Method GET -Uri "$apiGatewayUrl/api/users/profile" -Headers $authHeader
        $status = "✅ Pass (Status: $($response.StatusCode))"
        $content = $response.Content
        if ($content.Length -gt 500) {
            $content = $content.Substring(0, 500) + "... (truncated)"
        }
    }
    catch {
        $status = "❌ Fail (Error: $($_.Exception.Message))"
        $content = $_.Exception.Response
    }

    # Append to results file
    @"
## GET /api/users/profile
- Description: Get user profile (Authorized)
- Status: $status
- Response: 
```
$content
```

"@ | Out-File -FilePath $resultsFile -Append
}

# Test recommendation endpoints for testuser1
Test-Endpoint -Method "GET" -Endpoint "/api/recommendations/users/67f7cde591b50e155f293879" -Description "Get recommendations for testuser1"
Test-Endpoint -Method "GET" -Endpoint "/api/recommendations/users/67f7cde591b50e155f293879/genre" -Description "Get genre-based recommendations for testuser1"
Test-Endpoint -Method "GET" -Endpoint "/api/recommendations/test-communication/67f7cde591b50e155f293879" -Description "Test inter-service communication"

Write-Host "Testing completed. Results saved to $resultsFile" 