# API Endpoints Test Results
Test Date: 04/10/2025 20:25:50

## POST /api/auth/login
- Description: Login user
- Status: âŒ Fail (Error: The remote server returned an error: (401) Unauthorized.)
- Response: 
`
System.Net.HttpWebResponse
`

## GET /api/movies
- Description: Get all movies
- Status: âœ… Pass (Status: 200)
- Response: 
`
[{"id":"67f7d63f9329fd8539a02292","title":"3 Idiots","description":"Two friends are searching for their long lost companion. They revisit their college days and recall the memories of their friend who inspired them to think differently.","genres":["Comedy","Drama"],"releaseYear":2009,"director":"Rajkumar Hirani","actors":["Aamir Khan","R. Madhavan","Sharman Joshi","Kareena Kapoor"],"posterUrl":"https://m.media-amazon.com/images/M/MV5BNTkyOGVjMGEtNmQzZi00NzFlLTlhMzQtNmMzNjRmNjQ4MjM1XkEyXkFqcGdeQX... (truncated)
`

## GET /api/movies/67f7d63f9329fd8539a02292
- Description: Get movie by ID (3 Idiots)
- Status: âœ… Pass (Status: 200)
- Response: 
`
{"id":"67f7d63f9329fd8539a02292","title":"3 Idiots","description":"Two friends are searching for their long lost companion. They revisit their college days and recall the memories of their friend who inspired them to think differently.","genres":["Comedy","Drama"],"releaseYear":2009,"director":"Rajkumar Hirani","actors":["Aamir Khan","R. Madhavan","Sharman Joshi","Kareena Kapoor"],"posterUrl":"https://m.media-amazon.com/images/M/MV5BNTkyOGVjMGEtNmQzZi00NzFlLTlhMzQtNmMzNjRmNjQ4MjM1XkEyXkFqcGdeQXV... (truncated)
`

## GET /api/movies/search?title=Dangar
- Description: Search movies by title
- Status: âœ… Pass (Status: 200)
- Response: 
`
[]
`

## GET /api/reviews
- Description: Get all reviews
- Status: âœ… Pass (Status: 200)
- Response: 
`
[{"id":"67f7d7779329fd8539a022c1","movieId":"67f7d63f9329fd8539a02292","userId":"67f7cde591b50e155f293879","username":"testuser1","rating":5,"comment":"3 Idiots is hilarious! One of the best comedies I've watched.","createdAt":"2025-04-05T14:30:00"},{"id":"67f7d7779329fd8539a022c2","movieId":"67f7d63f9329fd8539a02296","userId":"67f7cde591b50e155f293879","username":"testuser1","rating":4,"comment":"DDLJ is a classic romantic comedy, really enjoyed it!","createdAt":"2025-04-06T10:15:00"},{"id":"67... (truncated)
`

## GET /api/reviews/movie/67f7d63f9329fd8539a02292
- Description: Get reviews for movie (3 Idiots)
- Status: âœ… Pass (Status: 200)
- Response: 
`
[{"id":"67f7d7779329fd8539a022c1","movieId":"67f7d63f9329fd8539a02292","userId":"67f7cde591b50e155f293879","username":"testuser1","rating":5,"comment":"3 Idiots is hilarious! One of the best comedies I've watched.","createdAt":"2025-04-05T14:30:00"}]
`

## GET /api/reviews/user/67f7cde591b50e155f293879
- Description: Get reviews by user (testuser1)
- Status: âœ… Pass (Status: 200)
- Response: 
`
[{"id":"67f7d7779329fd8539a022c1","movieId":"67f7d63f9329fd8539a02292","userId":"67f7cde591b50e155f293879","username":"testuser1","rating":5,"comment":"3 Idiots is hilarious! One of the best comedies I've watched.","createdAt":"2025-04-05T14:30:00"},{"id":"67f7d7779329fd8539a022c2","movieId":"67f7d63f9329fd8539a02296","userId":"67f7cde591b50e155f293879","username":"testuser1","rating":4,"comment":"DDLJ is a classic romantic comedy, really enjoyed it!","createdAt":"2025-04-06T10:15:00"},{"id":"67... (truncated)
`

## GET /api/reviews/movie/67f7d63f9329fd8539a02292/average-rating
- Description: Get average rating for movie (3 Idiots)
- Status: âœ… Pass (Status: 200)
- Response: 
`
5.0
`

## GET /api/recommendations/users/67f7cde591b50e155f293879
- Description: Get recommendations for testuser1
- Status: âœ… Pass (Status: 200)
- Response: 
`
{"userId":"67f7cde591b50e155f293879","recommendedMovies":[]}
`

## GET /api/recommendations/users/67f7cde591b50e155f293879/genre
- Description: Get genre-based recommendations for testuser1
- Status: âœ… Pass (Status: 200)
- Response: 
`
[]
`

## GET /api/recommendations/test-communication/67f7cde591b50e155f293879
- Description: Test inter-service communication
- Status: âœ… Pass (Status: 200)
- Response: 
`
{"success":false,"error":"Connect timed out executing GET http://user-service/api/users/67f7cde591b50e155f293879","message":"Inter-service communication test failed"}
`

