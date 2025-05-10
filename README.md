[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/mochacr0/mongodb-shop-backend)
# MongoDB-Shop-Backend
An e-commerce backend application using Spring Boot, leveraging AWS S3 for media storage, Giaohangtietkiem for delivery services, and integrating Momo wallet for seamless payment processing.
## Technologies
- Java 17
- Spring Boot 3.1.0
- Spring Security
- Spring JPA
- MongoDB
- OAuth
## Testing
### Requirements
- Any IDE of your choice
- Docker
### Step-by-step Guide
1. Clone the latest code:
```bash
git clone https://github.com/mochacr0/mongodb-shop-backend.git
```
2. Open your preferred IDE and provide your own application.yml file, including application properties, secrets, and API keys:
``` yaml
spring:
  data:
    mongodb:
      uri: <YOUR MONGODB CONNECTION STRING>
      auto-index-creation: true
  cloud:
    aws:
      credentials:
        access-key: <YOUR AWS ACCESS KEY>
        secret-key: <YOUR AWS SECRET KEY>
      region:
        static: <YOUR PREFERRED DATA STORAGE REGION>
server:
  port: 5000
  servlet:
    encoding:
      charset: UTF-8
      force: true
jwt:
  settings:
    accessTokenExpiryTimeSeconds: 1800
    refreshTokenExpiryTimeSeconds: 2592000
    tokenSigningKeyString: <YOUR 256-BIT JWT TOKEN SIGNING KEY>
    tokenIssuer: localhost #Token issuer
logging:
  level:
    org:
      springframework:
        security: TRACE
        data:
          mongodb:
            core:
              MongoTemplate: DEBUG
security:
  oauth2:
    googleClientId: <YOUR GOOGLE CLIENT ID>
    googleClientSecret: <YOUR GOOGLE CLIENT SECRET>
  passwordPolicy:
    isWhitespacesAllowed: false
    repeatedPasswordAllowed: false
    minimumLength: 6
    minimumLowerCharacters: 0
    minimumUpperCharacters: 1
    minimumSpecialCharacters: 0
    passwordReuseFrequencyDays: 0
  maxFailedLoginAttempts: 3
  failedLoginLockExpirationMillis: 5000 # 5 second
  activationTokenExpirationMillis: 300000 # 5 minutes
  passwordResetTokenExpirationMillis: 300000 # 5 minutes
  failedLoginIntervalMillis: 10000 # 10 seconds
mailing:
  host: "smtp.gmail.com"
  port: 587
  username: <YOUR ADMIN GMAIL>
  password: <YOUR ADMIN GMAIL APP PASSWORD>
  timeout: 10000
payment:
  momo:
    partnerCode: <YOUR MOMO PARTNER CODE>
    accessKey: <YOUR MOMO ACCESS KEY>
    secretKey: <YOUR MOMO SECRET KEY>
shipping:
  ghtk:
    apiToken: <YOUR GIAOHANGTIETKIEM TOKEN API>
```
3. Navigate to the project's main directory and run the following command to start the container:
```bash
docker compose up -d
```
4. Once the container is running, access the Swagger documentation page at: http://localhost:5000/swagger-ui.html
5. Use the following accounts to access authorized endpoints:

    |          |   User   |   Admin  |
    | :-------: | :------: | :------: | 
    | Username | `user00` |  `admin` |
    | Password | `String` | `String` |
   
6. Stop and remove the container by using:
```bash
docker compose down
```
## Screenshots
**Swagger Documentation Page**

![Screenshot 2023-12-13 111616](https://github.com/mochacr0/mongodb-shop-backend/assets/64319905/69842aba-9e64-4529-888b-f97144ec8c2c)

![Screenshot 2023-12-13 111634](https://github.com/mochacr0/mongodb-shop-backend/assets/64319905/33fdfb08-fa5d-4bb4-9a88-0ef297a9f683)

**User-related APIs**

![image](https://github.com/mochacr0/mongodb-shop-backend/assets/64319905/1d273a83-e6a6-45ca-a5f8-9d460c7b22e9)

**Response example**

![image](https://github.com/mochacr0/mongodb-shop-backend/assets/64319905/397e7412-b766-4187-94f1-7d7ffaf75743)
