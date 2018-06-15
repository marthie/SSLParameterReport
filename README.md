# SSLReport for servers
based on the code of [TestSSLServer](http://www.bolet.org/TestSSLServer/) by Thomas Pornin 

## Introduction

SSLReport is fetching public SSL/TLS information by connecting to a given host. Following data is fetched by SSLReport:

* Protocol versions
* Support for compression
* Cipher suites
* Certificates

The public SSL/TLS handshake information and the certificate within the host response is displayed on a web interface. The information won't be evaluated by SSLreport but is free for the evaluation by individuals.

## Build prerequisite

* Java JDK 1.8
* Node.js Version 8 & npm 5.6

## Build Overview

1. Run `npm install` in `SSLReport\src\main\resource\static\react`
2. Run `npm run build` in `SSLReport\src\main\resource\static\react`
3. Run `mvn clean package` in `SSLReport\`


## Run SSLReport App

The service is based on Spring Boot. Start the service with:

```
java -jar sslreport-<Version>.jar
```

## Access SSLReport App

Access the SSLReport REST service by using Swagger-UI:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)


Access the React-based web interface:

[http://localhost:8080/sslReportAppV2/index.html](http://localhost:8080/sslReportAppV2/index.html)


