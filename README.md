<p>Trailblazer</p>

1. Environment Variables
* POSTGRESQL_ADDR
* POSTGRESQL_USERNAME
* POSTGRESQL_PASSWORD
* EXTERNAL_URL
* KEYCLOAK_URL
* KEYCLOAK_PORT
* KEYCLOAK_ADMIN_ID
* KEYCLOAK_ADMIN_PW

2. RUN

```
sudo chmod +x ./mvnw
./mvnw spring-boot:run
```

3. Build (.jar)
```
sudo chmod +x ./mvnw
./mvnw clean package -DskipTests
```