# Create Test Data for Movie Recommendation System
# This script adds dummy movies, users, and reviews to test the recommendation functionality

$API_BASE = "http://localhost:8090"
$DIRECT_RECOMMENDATION_API = "http://localhost:8084"

# Helper function for API calls
function Invoke-ApiCall {
    param (
        [string]$Method,
        [string]$Endpoint,
        [object]$Body = $null,
        [string]$BaseUrl = $API_BASE
    )
    
    $uri = "$BaseUrl$Endpoint"
    $headers = @{
        "Content-Type" = "application/json"
    }
    
    Write-Host "Making $Method request to $uri"
    
    try {
        if ($Body) {
            $bodyJson = $Body | ConvertTo-Json -Depth 10
            $response = Invoke-RestMethod -Method $Method -Uri $uri -Headers $headers -Body $bodyJson -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Method $Method -Uri $uri -Headers $headers -ErrorAction Stop
        }
        return $response
    }
    catch {
        Write-Host "Error: $_" -ForegroundColor Red
        if ($_.Exception.Response) {
            try {
                $responseBody = $_.Exception.Response.GetResponseStream()
                $reader = New-Object System.IO.StreamReader($responseBody)
                $responseContent = $reader.ReadToEnd()
                Write-Host "Response content: $responseContent" -ForegroundColor Yellow
            }
            catch {
                Write-Host "Could not read response content: $_" -ForegroundColor Red
            }
        }
        return $null
    }
}

# 1. Create Users
Write-Host "Creating test users..." -ForegroundColor Green

$user1 = @{
    username = "testuser1"
    email = "testuser1@example.com"
    password = "password123"
    preferredGenres = @("Action", "Science Fiction", "Adventure")
}

$user2 = @{
    username = "testuser2"
    email = "testuser2@example.com"
    password = "password123"
    preferredGenres = @("Comedy", "Romance", "Drama")
}

$createdUser1 = Invoke-ApiCall -Method "POST" -Endpoint "/api/users" -Body $user1
$createdUser2 = Invoke-ApiCall -Method "POST" -Endpoint "/api/users" -Body $user2

$testUserId1 = if ($createdUser1 -and $createdUser1.id) { $createdUser1.id } else { "1" }
$testUserId2 = if ($createdUser2 -and $createdUser2.id) { $createdUser2.id } else { "2" }

Write-Host "Created/Using users:" -ForegroundColor Cyan
Write-Host "User 1 ID: $testUserId1"
Write-Host "User 2 ID: $testUserId2"

# 2. Create Movies
Write-Host "`nCreating test movies..." -ForegroundColor Green

$movies = @(
    @{
        title = "The Matrix"
        director = "The Wachowskis"
        releaseYear = 1999
        genres = @("Science Fiction", "Action", "Adventure")
        description = "A computer hacker learns about the true nature of reality"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg"
    },
    @{
        title = "Inception"
        director = "Christopher Nolan"
        releaseYear = 2010
        genres = @("Science Fiction", "Action", "Thriller")
        description = "A thief who steals corporate secrets through dream-sharing technology"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_.jpg"
    },
    @{
        title = "The Avengers"
        director = "Joss Whedon"
        releaseYear = 2012
        genres = @("Action", "Adventure", "Science Fiction")
        description = "Earth's mightiest heroes must come together to stop an alien invasion"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BNDYxNjQyMjAtNTdiOS00NGYwLWFmNTAtNThmYjU5ZGI2YTI1XkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg"
    },
    @{
        title = "The Dark Knight"
        director = "Christopher Nolan"
        releaseYear = 2008
        genres = @("Action", "Crime", "Drama", "Thriller")
        description = "Batman faces his greatest challenge yet: the Joker"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg"
    },
    @{
        title = "Titanic"
        director = "James Cameron"
        releaseYear = 1997
        genres = @("Romance", "Drama", "Disaster")
        description = "A young aristocrat falls in love with a kind but poor artist aboard the RMS Titanic"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMDdmZGU3NDQtY2E5My00ZTliLWIzOTUtMTY4ZGI1YjdiNjk3XkEyXkFqcGdeQXVyNTA4NzY1MzY@._V1_.jpg"
    },
    @{
        title = "The Notebook"
        director = "Nick Cassavetes"
        releaseYear = 2004
        genres = @("Romance", "Drama")
        description = "A poor young man falls in love with a rich young woman"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMTk3OTM5Njg5M15BMl5BanBnXkFtZTYwMzA0ODI3._V1_.jpg"
    },
    @{
        title = "The Lord of the Rings: The Fellowship of the Ring"
        director = "Peter Jackson"
        releaseYear = 2001
        genres = @("Adventure", "Fantasy", "Action")
        description = "A hobbit embarks on a quest to destroy a powerful ring"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BN2EyZjM3NzUtNWUzMi00MTgxLWI0NTctMzY4M2VlOTdjZWRiXkEyXkFqcGdeQXVyNDUzOTQ5MjY@._V1_.jpg"
    },
    @{
        title = "Forrest Gump"
        director = "Robert Zemeckis"
        releaseYear = 1994
        genres = @("Drama", "Romance", "Comedy")
        description = "The life journey of a man with low IQ who accomplishes great things"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_.jpg"
    },
    @{
        title = "The Shawshank Redemption"
        director = "Frank Darabont"
        releaseYear = 1994
        genres = @("Drama", "Crime")
        description = "A banker is sentenced to life in Shawshank State Penitentiary for a crime he didn't commit"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BNDE3ODcxYzMtY2YzZC00NmNlLWJiNDMtZDViZWM2MzIxZDYwXkEyXkFqcGdeQXVyNjAwNDUxODI@._V1_.jpg"
    },
    @{
        title = "Pulp Fiction"
        director = "Quentin Tarantino"
        releaseYear = 1994
        genres = @("Crime", "Drama")
        description = "The lives of two mob hitmen, a boxer, a gangster and his wife intertwine"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg"
    },
    @{
        title = "The Grand Budapest Hotel"
        director = "Wes Anderson"
        releaseYear = 2014
        genres = @("Comedy", "Drama", "Adventure")
        description = "A concierge at a famous European hotel teams up with a lobby boy to prove his innocence"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMzM5NjUxOTEyMl5BMl5BanBnXkFtZTgwNjEyMDM0MDE@._V1_.jpg"
    },
    @{
        title = "La La Land"
        director = "Damien Chazelle"
        releaseYear = 2016
        genres = @("Romance", "Drama", "Musical")
        description = "A jazz pianist and an aspiring actress fall in love in Los Angeles"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMzUzNDM2NzM2MV5BMl5BanBnXkFtZTgwNTM3NTg4OTE@._V1_.jpg"
    },
    @{
        title = "Interstellar"
        director = "Christopher Nolan"
        releaseYear = 2014
        genres = @("Science Fiction", "Adventure", "Drama")
        description = "A team of explorers travel through a wormhole in space to ensure humanity's survival"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg"
    },
    @{
        title = "The Godfather"
        director = "Francis Ford Coppola"
        releaseYear = 1972
        genres = @("Crime", "Drama")
        description = "The aging patriarch of an organized crime dynasty transfers control to his son"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg"
    },
    @{
        title = "Jurassic Park"
        director = "Steven Spielberg"
        releaseYear = 1993
        genres = @("Science Fiction", "Adventure", "Action")
        description = "A theme park with cloned dinosaurs experiences a catastrophic shutdown"
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMjM2MDgxMDg0Nl5BMl5BanBnXkFtZTgwNTM2OTM5NDE@._V1_.jpg"
    }
)

$createdMovies = @()
foreach ($movie in $movies) {
    $createdMovie = Invoke-ApiCall -Method "POST" -Endpoint "/api/movies" -Body $movie
    if ($createdMovie) {
        $createdMovies += $createdMovie
        Write-Host "Created movie: $($movie.title) with ID: $($createdMovie.id)"
    }
}

# Use movie IDs from the response or hardcode them if creation failed
if ($createdMovies.Count -eq 0) {
    Write-Host "Using hardcoded movie IDs for testing" -ForegroundColor Yellow
    $movieIds = @(
        "67f7c3e758982e2e79a9a0ed", # The Matrix
        "67f7c3e758982e2e79a9a0ee", # Inception
        "67f7c3e758982e2e79a9a0ef", # The Avengers
        "67f7c3e758982e2e79a9a0f0", # The Dark Knight
        "67f7c3e758982e2e79a9a0f1", # Titanic
        "67f7c3e758982e2e79a9a0f2", # The Notebook
        "67f7c3e858982e2e79a9a0f3", # LOTR
        "67f7c3e858982e2e79a9a0f4", # Forrest Gump
        "67f7c3e858982e2e79a9a0f5", # Shawshank Redemption
        "67f7c3e858982e2e79a9a0f6", # Pulp Fiction
        "67f7c3e858982e2e79a9a0f7", # Grand Budapest Hotel
        "67f7c3e858982e2e79a9a0f8", # La La Land
        "67f7c3e858982e2e79a9a0f9", # Interstellar
        "67f7c3e858982e2e79a9a0fa", # The Godfather
        "67f7c3e858982e2e79a9a0fb"  # Jurassic Park
    )
    
    # Create dummy movies objects
    for ($i = 0; $i -lt $movies.Count; $i++) {
        $createdMovies += [PSCustomObject]@{
            id = $movieIds[$i]
            title = $movies[$i].title
        }
    }
}

# 3. Create Reviews
Write-Host "`nCreating test reviews..." -ForegroundColor Green

# User 1 reviews (Action/Sci-Fi fan)
$user1Reviews = @(
    @{
        userId = $testUserId1
        movieId = $createdMovies[0].id  # The Matrix
        rating = 5
        comment = "One of the best sci-fi movies ever made!"
    },
    @{
        userId = $testUserId1
        movieId = $createdMovies[1].id  # Inception
        rating = 5
        comment = "Mind-blowing concept and execution!"
    },
    @{
        userId = $testUserId1
        movieId = $createdMovies[2].id  # The Avengers
        rating = 4
        comment = "Great action and humor!"
    },
    @{
        userId = $testUserId1
        movieId = $createdMovies[3].id  # The Dark Knight
        rating = 5
        comment = "Heath Ledger's Joker is unforgettable!"
    },
    @{
        userId = $testUserId1
        movieId = $createdMovies[6].id  # LOTR
        rating = 5
        comment = "Epic fantasy adventure!"
    },
    @{
        userId = $testUserId1
        movieId = $createdMovies[12].id  # Interstellar
        rating = 4
        comment = "Amazing visuals and concept!"
    },
    @{
        userId = $testUserId1
        movieId = $createdMovies[14].id  # Jurassic Park
        rating = 4
        comment = "Classic adventure film!"
    }
)

# User 2 reviews (Romance/Drama/Comedy fan)
$user2Reviews = @(
    @{
        userId = $testUserId2
        movieId = $createdMovies[4].id  # Titanic
        rating = 5
        comment = "The love story gets me every time!"
    },
    @{
        userId = $testUserId2
        movieId = $createdMovies[5].id  # The Notebook
        rating = 5
        comment = "The most romantic movie ever!"
    },
    @{
        userId = $testUserId2
        movieId = $createdMovies[7].id  # Forrest Gump
        rating = 5
        comment = "Heartwarming and beautiful story!"
    },
    @{
        userId = $testUserId2
        movieId = $createdMovies[8].id  # Shawshank Redemption
        rating = 4
        comment = "Powerful and inspiring!"
    },
    @{
        userId = $testUserId2
        movieId = $createdMovies[10].id  # Grand Budapest Hotel
        rating = 4
        comment = "Quirky and charming!"
    },
    @{
        userId = $testUserId2
        movieId = $createdMovies[11].id  # La La Land
        rating = 5
        comment = "Beautiful music and romance!"
    }
)

# Submit all reviews
foreach ($review in $user1Reviews) {
    $createdReview = Invoke-ApiCall -Method "POST" -Endpoint "/api/reviews" -Body $review
    Write-Host "Created review for movie ID: $($review.movieId) by user ID: $($review.userId) with rating: $($review.rating)"
    # Add to watch history
    Invoke-ApiCall -Method "POST" -Endpoint "/api/users/$($review.userId)/watchhistory/$($review.movieId)"
}

foreach ($review in $user2Reviews) {
    $createdReview = Invoke-ApiCall -Method "POST" -Endpoint "/api/reviews" -Body $review
    Write-Host "Created review for movie ID: $($review.movieId) by user ID: $($review.userId) with rating: $($review.rating)"
    # Add to watch history
    Invoke-ApiCall -Method "POST" -Endpoint "/api/users/$($review.userId)/watchhistory/$($review.movieId)"
}

Write-Host "`nTest data creation complete!" -ForegroundColor Green
Write-Host "You can now test recommendations using the following endpoints:" -ForegroundColor Yellow
Write-Host "- API Gateway: $API_BASE/api/recommendations/users/$testUserId1"
Write-Host "- Direct access: $DIRECT_RECOMMENDATION_API/api/recommendations/users/$testUserId1"
Write-Host "- Detailed recommendations: $DIRECT_RECOMMENDATION_API/api/recommendations/users/$testUserId1/detailed"
Write-Host "- Genre recommendations: $DIRECT_RECOMMENDATION_API/api/recommendations/users/$testUserId1/genre"
Write-Host "- Similar user recommendations: $DIRECT_RECOMMENDATION_API/api/recommendations/users/$testUserId2/similar-users"

# Test direct access to recommendations
Write-Host "`nTesting direct access to recommendation service..." -ForegroundColor Cyan
$testRecommendation = Invoke-ApiCall -Method "GET" -Endpoint "/api/recommendations/users/$testUserId1" -BaseUrl $DIRECT_RECOMMENDATION_API
if ($testRecommendation) {
    Write-Host "Recommendation test successful!" -ForegroundColor Green
} else {
    Write-Host "Recommendation test failed!" -ForegroundColor Red
}

# Test genre recommendations
$genreRecommendations = Invoke-ApiCall -Method "GET" -Endpoint "/api/recommendations/users/$testUserId1/genre" -BaseUrl $DIRECT_RECOMMENDATION_API
if ($genreRecommendations) {
    Write-Host "Got $($genreRecommendations.Count) genre-based recommendations" -ForegroundColor Green
} 