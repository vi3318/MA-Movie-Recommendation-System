# Movie Recommendation System - Kubernetes Deployment

This directory contains Kubernetes manifests for deploying the Movie Recommendation System microservices.

## Prerequisites

- Kubernetes cluster (local like Minikube/Docker Desktop or cloud-based)
- kubectl CLI configured
- Docker to build and push images

## Services

- **Eureka Server**: Service discovery
- **API Gateway**: Entry point for all services
- **User Service**: Handles user authentication and management
- **Movie Catalog Service**: Provides movie information
- **Review Service**: Manages movie reviews
- **Recommendation Service**: Generates movie recommendations

## Build and Push Docker Images

Before deploying to Kubernetes, build and tag all service images:

```bash
# For each service directory:
cd service-directory
./mvnw clean package -DskipTests
docker build -t service-name:latest .
docker tag service-name:latest your-registry/service-name:latest
docker push your-registry/service-name:latest
```

Note: Update image names in Kubernetes manifests if using a different registry.

## Deployment Steps

1. Create namespace:
   ```bash
   kubectl apply -f namespace.yaml
   ```

2. Create ConfigMap and Secrets:
   ```bash
   kubectl apply -f configmap.yaml
   kubectl apply -f secrets.yaml
   ```

3. Deploy Eureka Server:
   ```bash
   kubectl apply -f eureka-server.yaml
   ```

4. Wait for Eureka to be ready:
   ```bash
   kubectl wait --for=condition=ready pod -l app=eureka-server -n movie-reco-system --timeout=180s
   ```

5. Deploy remaining services:
   ```bash
   kubectl apply -f api-gateway.yaml
   kubectl apply -f user-service.yaml
   kubectl apply -f movie-catalog-service.yaml
   kubectl apply -f review-service.yaml
   kubectl apply -f recommendation-service.yaml
   ```

6. Apply Horizontal Pod Autoscalers:
   ```bash
   kubectl apply -f hpa.yaml
   ```

7. Deploy Ingress:
   ```bash
   kubectl apply -f ingress.yaml
   ```

## Accessing the Application

- For NodePort access: http://node-ip:30080
- With Ingress: Configure your hosts file to map `movie-reco.local` to your cluster's IP, then access http://movie-reco.local

## Monitoring and Scaling

- Check service status: `kubectl get pods -n movie-reco-system`
- View logs: `kubectl logs -f <pod-name> -n movie-reco-system`
- Scale manually: `kubectl scale deployment/<deployment-name> --replicas=<count> -n movie-reco-system`

## HPA (Horizontal Pod Autoscaler)

The system uses HPA for automatic scaling based on CPU utilization. You can monitor the HPA status with:

```bash
kubectl get hpa -n movie-reco-system
``` 