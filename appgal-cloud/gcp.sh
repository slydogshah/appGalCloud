#GCP Deployment
mvn clean package -DskipTests
gcloud builds submit --tag gcr.io/appgallabs-271922/appgal-cloud:v1
gcloud run deploy --image gcr.io/appgallabs-271922/appgal-cloud:v1 --platform managed