# Script to create dummy users and reviews

# Function to check if a service is ready
function Test-ServiceReady {
    param (
        [string]$uri
    )
    try {
        Invoke-RestMethod -Uri $uri -Method GET -ErrorAction SilentlyContinue
        return $true
    } catch {
        return $false
    }
}

# Wait for services to be ready
Write-Host "Setting up port-forwarding for API Gateway..."
$apiGatewayJob = Start-Job -ScriptBlock { kubectl port-forward service/api-gateway 8090:8080 }

Write-Host "Waiting for API Gateway to be ready..."
Start-Sleep -Seconds 10

# Create dummy users
Write-Host "Creating dummy users..."
$user1 = @{
    username='testuser1'
    email='test1@example.com'
    password='password123'
    preferredGenres=@('Action', 'Adventure', 'Science Fiction')
} | ConvertTo-Json

$user2 = @{
    username='testuser2'
    email='test2@example.com'
    password='password123'
    preferredGenres=@('Drama', 'Romance', 'Comedy')
} | ConvertTo-Json

$user3 = @{
    username='testuser3'
    email='test3@example.com'
    password='password123'
    preferredGenres=@('Horror', 'Thriller', 'Mystery')
} | ConvertTo-Json

try {
    # Register users
    Write-Host "Registering users..."
    Invoke-RestMethod -Uri 'http://localhost:8090/api/auth/register' -Method POST -Body $user1 -ContentType 'application/json'
    Invoke-RestMethod -Uri 'http://localhost:8090/api/auth/register' -Method POST -Body $user2 -ContentType 'application/json'
    Invoke-RestMethod -Uri 'http://localhost:8090/api/auth/register' -Method POST -Body $user3 -ContentType 'application/json'
    
    # Login to get tokens
    Write-Host "Logging in to get tokens..."
    $loginInfo1 = @{ username='testuser1'; password='password123' } | ConvertTo-Json
    $loginInfo2 = @{ username='testuser2'; password='password123' } | ConvertTo-Json
    $loginInfo3 = @{ username='testuser3'; password='password123' } | ConvertTo-Json
    
    $token1 = (Invoke-RestMethod -Uri 'http://localhost:8090/api/auth/login' -Method POST -Body $loginInfo1 -ContentType 'application/json').token
    $token2 = (Invoke-RestMethod -Uri 'http://localhost:8090/api/auth/login' -Method POST -Body $loginInfo2 -ContentType 'application/json').token
    $token3 = (Invoke-RestMethod -Uri 'http://localhost:8090/api/auth/login' -Method POST -Body $loginInfo3 -ContentType 'application/json').token
    
    # Get user profiles to get user IDs
    Write-Host "Getting user profiles..."
    $user1Info = Invoke-RestMethod -Uri 'http://localhost:8090/api/users/profile' -Method GET -Headers @{Authorization="Bearer $token1"}
    $user2Info = Invoke-RestMethod -Uri 'http://localhost:8090/api/users/profile' -Method GET -Headers @{Authorization="Bearer $token2"}
    $user3Info = Invoke-RestMethod -Uri 'http://localhost:8090/api/users/profile' -Method GET -Headers @{Authorization="Bearer $token3"}
    
    # Get movies list
    Write-Host "Getting movie list..."
    $movies = Invoke-RestMethod -Uri 'http://localhost:8090/api/movies' -Method GET
    
    # Create reviews for each user
    Write-Host "Creating reviews for users..."
    $comments = @(
        'Amazing movie! Highly recommended.',
        'Great storyline and characters.',
        'Visually stunning experience.',
        'Good movie but the ending could be better.',
        'Average film, nothing special.',
        'Interesting concept but poor execution.',
        'An absolute masterpiece.',
        'Compelling characters and interesting plot.',
        'The cinematography was breathtaking.',
        'Wasn''t very impressed with this one.',
        'Could have been better.'
    )
    
    foreach ($movie in $movies | Select-Object -First 15) {
        # Generate random ratings and comments for each user
        $rating1 = Get-Random -Minimum 3 -Maximum 6
        $rating2 = Get-Random -Minimum 2 -Maximum 6
        $rating3 = Get-Random -Minimum 1 -Maximum 6
        
        $comment1 = $comments[(Get-Random -Minimum 0 -Maximum $comments.Length)]
        $comment2 = $comments[(Get-Random -Minimum 0 -Maximum $comments.Length)]
        $comment3 = $comments[(Get-Random -Minimum 0 -Maximum $comments.Length)]
        
        # Create reviews
        $review1 = @{
            movieId=$movie.id
            userId=$user1Info.id
            username='testuser1'
            rating=$rating1
            comment="$($movie.title): $comment1"
        } | ConvertTo-Json
        
        $review2 = @{
            movieId=$movie.id
            userId=$user2Info.id
            username='testuser2'
            rating=$rating2
            comment="$($movie.title): $comment2"
        } | ConvertTo-Json
        
        $review3 = @{
            movieId=$movie.id
            userId=$user3Info.id
            username='testuser3'
            rating=$rating3
            comment="$($movie.title): $comment3"
        } | ConvertTo-Json
        
        try {
            Invoke-RestMethod -Uri 'http://localhost:8090/api/reviews' -Method POST -Body $review1 -ContentType 'application/json' -ErrorAction SilentlyContinue
            Invoke-RestMethod -Uri 'http://localhost:8090/api/reviews' -Method POST -Body $review2 -ContentType 'application/json' -ErrorAction SilentlyContinue
            Invoke-RestMethod -Uri 'http://localhost:8090/api/reviews' -Method POST -Body $review3 -ContentType 'application/json' -ErrorAction SilentlyContinue
            Write-Host "Created reviews for movie: $($movie.title)"
        } catch {
            Write-Host "Error creating reviews for movie: $($movie.title): $_"
        }
        
        # Pause slightly between requests to prevent overloading
        Start-Sleep -Milliseconds 500
    }
    
    Write-Host "User and review creation completed!"
}
catch {
    Write-Host "Error in script: $_"
}
finally {
    # Stop port-forwarding job
    if ($apiGatewayJob) {
        Stop-Job -Job $apiGatewayJob
        Remove-Job -Job $apiGatewayJob -Force
    }
} 