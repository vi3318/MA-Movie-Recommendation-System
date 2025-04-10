   # create-all.ps1
   kubectl apply -f k8s/eureka-server.yaml
   kubectl apply -f k8s/api-gateway.yaml
   kubectl apply -f k8s/user-service.yaml
   kubectl apply -f k8s/movie-catalog-service.yaml
   kubectl apply -f k8s/recommendation-service.yaml
   kubectl apply -f k8s/review-service.yaml